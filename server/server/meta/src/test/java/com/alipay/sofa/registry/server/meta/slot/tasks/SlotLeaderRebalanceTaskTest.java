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
package com.alipay.sofa.registry.server.meta.slot.tasks;

import com.alipay.sofa.registry.common.model.metaserver.nodes.DataNode;
import com.alipay.sofa.registry.common.model.slot.DataNodeSlot;
import com.alipay.sofa.registry.server.meta.AbstractTest;
import com.alipay.sofa.registry.server.meta.bootstrap.config.NodeConfig;
import com.alipay.sofa.registry.server.meta.lease.data.DefaultDataServerManager;
import com.alipay.sofa.registry.server.meta.slot.SlotManager;
import com.alipay.sofa.registry.server.meta.slot.impl.LocalSlotManager;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SlotLeaderRebalanceTaskTest extends AbstractTest {

    private SlotLeaderRebalanceTask  task;

    private SlotManager              raftSlotManager;

    private LocalSlotManager         localSlotManager;

    @Mock
    private DefaultDataServerManager dataServerManager;

    @Before
    public void beforeInitReshardingTaskTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        List<DataNode> dataNodes = Lists.newArrayList(new DataNode(randomURL(randomIp()), getDc()),
            new DataNode(randomURL(randomIp()), getDc()), new DataNode(randomURL(randomIp()),
                getDc()), new DataNode(randomURL(randomIp()), getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        NodeConfig nodeConfig = mock(NodeConfig.class);
        when(nodeConfig.getLocalDataCenter()).thenReturn(getDc());
        raftSlotManager = localSlotManager = new LocalSlotManager(nodeConfig);
    }

    @Test
    public void testRun() throws TimeoutException, InterruptedException {
        makeRaftLeader();
        localSlotManager.refresh(new SlotTableGenerator(dataServerManager.getClusterMembers()).createLeaderUnBalancedSlotTable());
        int average = localSlotManager.getSlotNums() / dataServerManager.getClusterMembers().size();
        int lowWaterMark = average / 2;
        AtomicBoolean unbalance = new AtomicBoolean(false);
        dataServerManager.getClusterMembers().forEach(dataNode -> {
            DataNodeSlot dataNodeSlot = localSlotManager.getDataNodeManagedSlot(dataNode, true);
            if(dataNodeSlot.getLeaders() == null || dataNodeSlot.getLeaders().size() <= lowWaterMark) {
                unbalance.set(true);
            }
        });
        printSlotTable(localSlotManager.getSlotTable());
        Assert.assertTrue(unbalance.get());


        task = new SlotLeaderRebalanceTask(localSlotManager, raftSlotManager, dataServerManager);
        task.run();

        unbalance.set(false);
        dataServerManager.getClusterMembers().forEach(dataNode -> {
            DataNodeSlot dataNodeSlot = localSlotManager.getDataNodeManagedSlot(dataNode, true);
            if(dataNodeSlot.getLeaders() == null || dataNodeSlot.getLeaders().size() <= lowWaterMark) {
                unbalance.set(true);
            }
        });
        printSlotTable(localSlotManager.getSlotTable());
        Assert.assertFalse(unbalance.get());
    }
}