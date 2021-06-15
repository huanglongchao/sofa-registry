/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.registry.server.session.push;

import static com.alipay.sofa.registry.server.session.push.PushMetrics.Push.*;

import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.util.ConcurrentUtils;
import com.alipay.sofa.registry.util.StringFormatter;
import com.alipay.sofa.registry.util.WakeUpLoopRunnable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class PushTaskBuffer {
  private static final Logger LOGGER = LoggerFactory.getLogger(PushTaskBuffer.class);

  final BufferWorker[] workers;
  final PushSwitchService pushSwitchService;

  PushTaskBuffer(int buckets, PushSwitchService pushSwitchService) {
    this.pushSwitchService = pushSwitchService;
    this.workers = new BufferWorker[buckets];
    for (int i = 0; i < buckets; i++) {
      BufferWorker worker = new BufferWorker();
      worker.t = ConcurrentUtils.createDaemonThread("PushTaskBuffer-" + i, worker);
      this.workers[i] = worker;
    }
  }

  void start() {
    for (BufferWorker w : workers) {
      w.t.start();
    }
  }

  boolean buffer(PushTask pushTask) {
    if (!pushSwitchService.canIpPush(pushTask.addr.getAddress().getHostAddress())) {
      return false;
    }
    final BufferTaskKey key = bufferTaskKey(pushTask);
    final BufferWorker worker = workerOf(key);
    if (worker.bufferMap.putIfAbsent(key, pushTask) == null) {
      // fast path
      wakeup(worker, pushTask);
      PENDING_NEW_COUNTER.inc();
      return true;
    }

    for (; ; ) {
      final PushTask prev = worker.bufferMap.get(key);
      if (prev == null) {
        if (worker.bufferMap.putIfAbsent(key, pushTask) == null) {
          // prev has remove at this time
          wakeup(worker, pushTask);
          PENDING_NEW_COUNTER.inc();
          return true;
        }
      } else {
        if (pushTask.afterThan(prev)) {
          final long originExpireTimestamp = pushTask.expireTimestamp;
          // update the expireTimestamp as prev's, avoid the push block by the continues fire
          pushTask.expireTimestamp = prev.expireTimestamp;
          if (worker.bufferMap.replace(key, prev, pushTask)) {
            wakeup(worker, pushTask);
            PENDING_REPLACE_COUNTER.inc();
            return true;
          } else {
            // put failed, recover the expireTimestamp
            pushTask.expireTimestamp = originExpireTimestamp;
          }
        } else {
          PENDING_SKIP_COUNTER.inc();
          LOGGER.info(
              "[SkipBuffer]key={},prev={},ver={}, now={},ver={}, retry={}",
              key,
              prev.taskID,
              prev.datum.getVersion(),
              pushTask.taskID,
              pushTask.datum.getVersion(),
              pushTask.retryCount);
          return false;
        }
      }
    }
  }

  private void wakeup(BufferWorker worker, PushTask pushTask) {
    if (pushTask.trace.pushCause.pushType.noDelay) {
      worker.wakeup();
    }
  }

  final class BufferWorker extends WakeUpLoopRunnable {
    final Map<BufferTaskKey, PushTask> bufferMap = new ConcurrentHashMap<>(4096);
    Thread t;

    @Override
    public void runUnthrowable() {
      watchBuffer(this);
    }

    @Override
    public int getWaitingMillis() {
      return 200;
    }
  }

  private List<PushTask> transferAndMerge(BufferWorker worker) {
    List<PushTask> pending = Lists.newArrayListWithCapacity(1024);
    final long now = System.currentTimeMillis();
    for (Map.Entry<BufferTaskKey, PushTask> e : worker.bufferMap.entrySet()) {
      final PushTask task = e.getValue();
      // no delay or expire, push immediately
      if (task.trace.pushCause.pushType.noDelay || task.expireTimestamp <= now) {
        pending.add(task);
        // the task maybe update
        worker.bufferMap.remove(e.getKey(), task);
      }
    }
    return pending;
  }

  int watchBuffer(BufferWorker worker) {
    if (!pushSwitchService.canPush()) {
      // stop push, clean the task
      worker.bufferMap.clear();
      return 0;
    }
    List<PushTask> pending = transferAndMerge(worker);
    int count = 0;
    for (PushTask task : pending) {
      if (task.commit()) {
        count++;
      }
    }
    LOGGER.info("buffers={}, commits={}", pending.size(), count);
    return count;
  }

  BufferTaskKey bufferTaskKey(PushTask task) {
    return new BufferTaskKey(
        task.datum.getDataCenter(),
        task.addr,
        task.subscriber.getDataInfoId(),
        task.subscriberMap.keySet());
  }

  static final class BufferTaskKey {
    final String dataCenter;
    final String dataInfoId;
    final InetSocketAddress addr;
    final Set<String> subscriberIds;
    final int hashCode;

    BufferTaskKey(
        String dataCenter, InetSocketAddress addr, String dataInfoId, Set<String> subscriberIds) {
      this.dataCenter = dataCenter;
      this.dataInfoId = dataInfoId;
      this.addr = addr;
      this.subscriberIds = subscriberIds;
      if (subscriberIds.size() <= 1) {
        this.hashCode = Objects.hash(dataCenter, addr, dataInfoId, subscriberIds);
      } else {
        // sort the subscriberIds
        this.hashCode = Objects.hash(dataCenter, addr, dataInfoId, Sets.newTreeSet(subscriberIds));
      }
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      BufferTaskKey that = (BufferTaskKey) o;
      return hashCode == that.hashCode
          && Objects.equals(dataInfoId, that.dataInfoId)
          && Objects.equals(addr, that.addr)
          && Objects.equals(dataCenter, that.dataCenter)
          && Objects.equals(subscriberIds, that.subscriberIds);
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public String toString() {
      return StringFormatter.format(
          "Pending{{},{},{},subIds={}}", dataInfoId, dataCenter, addr, subscriberIds);
    }
  }

  private BufferWorker workerOf(Object key) {
    int n = (key.hashCode() & 0x7fffffff) % workers.length;
    return workers[n];
  }

  public int size() {
    int size = 0;
    for (BufferWorker w : workers) {
      size += w.bufferMap.size();
    }
    return size;
  }

  void suspend() {
    for (BufferWorker w : workers) {
      w.suspend();
    }
  }

  void resume() {
    for (BufferWorker w : workers) {
      w.resume();
    }
  }
}