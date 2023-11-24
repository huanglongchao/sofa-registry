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
package com.alipay.sofa.registry.common.model.metaserver;

import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.util.ConcurrentUtils;
import com.alipay.sofa.registry.util.JsonUtils;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author xiaojian.xj
 * @version : NodeServerOperateInfoTest.java, v 0.1 2022年02月15日 10:52 xiaojian.xj Exp $
 */
public class NodeServerOperateInfoTest {

    private static final String CELL = "TEST_CELL";

    private static final Logger LOG = LoggerFactory.getLogger(NodeServerOperateInfoTest.class);

    @Test
    public void test() {
        NodeServerOperateInfo info = new NodeServerOperateInfo();

        // meta
        Assert.assertEquals(0, info.metasSize());
        Assert.assertEquals(0, info.metaLastOperateTs());
        Assert.assertTrue(info.addMetas(CELL, "1.1.1.1"));
        Assert.assertFalse(info.addMetas(CELL, "1.1.1.1"));
        info.addMetas(CELL, "1.1.1.2");
        long timeMillis = System.currentTimeMillis();
        ConcurrentUtils.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        Assert.assertTrue(info.addMetas(CELL, "1.1.1.3"));
        Assert.assertTrue(info.removeMetas(CELL, "1.1.1.2"));
        Assert.assertFalse(info.removeMetas(CELL, "1.1.1.2"));
        Assert.assertEquals(2, info.metasSize());
        Assert.assertTrue(info.metaLastOperateTs() > timeMillis);

        // data
        Assert.assertEquals(0, info.datasSize());
        Assert.assertEquals(0, info.dataLastOperateTs());
        Assert.assertTrue(info.addDatas(CELL, "1.1.1.1"));
        Assert.assertFalse(info.addDatas(CELL, "1.1.1.1"));
        Assert.assertTrue(info.addDatas(CELL, "1.1.1.2"));
        timeMillis = System.currentTimeMillis();
        ConcurrentUtils.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        Assert.assertTrue(info.addDatas(CELL, "1.1.1.3"));
        Assert.assertTrue(info.removeDatas(CELL, "1.1.1.2"));
        Assert.assertFalse(info.removeDatas(CELL, "1.1.1.2"));
        Assert.assertEquals(2, info.datasSize());
        Assert.assertTrue(info.dataLastOperateTs() > timeMillis);

        // session
        Assert.assertEquals(0, info.sessionSize());
        Assert.assertEquals(0, info.sessionLastOperateTs());
        Assert.assertTrue(info.addSessions(CELL, "1.1.1.1"));
        Assert.assertFalse(info.addSessions(CELL, "1.1.1.1"));
        Assert.assertTrue(info.addSessions(CELL, "1.1.1.2"));
        timeMillis = System.currentTimeMillis();
        ConcurrentUtils.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        Assert.assertTrue(info.addSessions(CELL, "1.1.1.3"));
        Assert.assertTrue(info.removeSessions(CELL, "1.1.1.2"));
        Assert.assertFalse(info.removeSessions(CELL, "1.1.1.2"));
        Assert.assertEquals(2, info.sessionSize());
        Assert.assertTrue(info.sessionLastOperateTs() > timeMillis);
    }

    @Test
    public void benchMarkJsonReadTest() {
        String testData =
                "{\"metas\":[],\"datas\":[{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1684231576626},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1680549233524},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1684302055095},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1694747512650},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1680102185863},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1682432970365},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1694958448061},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1679556000825},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1691614528084},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1679554028040},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1699894025865},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1700708828419},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1699102925113},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1665404250703},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1699128157104},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1682433354340},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1683548641733},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1665356135945},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1699859373576},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1678773931611},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1680833515398},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1679216349094},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1700708214857},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1682396360849},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1682401254094},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1682399448259},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1700708544999},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1678786654512},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1700709090410},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1679216077960},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1682480983281},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1681147387197},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1683511760881},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1680496772612},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1669321845534},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1680148932665},{\"nodeType\":\"DATA\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1694491707297}],\"sessions\":{\"CZ50C\":[{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1666454852804},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1699512721207},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1700641126216},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1698394892989},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1666700789822},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1677146179560},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1681123156893},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1700561646792},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1680582927788},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1683512869487},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1677141808080},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1700645903401},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1680234067309},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1693683493636},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1693235858899},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1675823327791},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1680243128168},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1682400714705},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1680501633288},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1665159978380},{\"nodeType\":\"SESSION\",\"cell\":\"CZ50C\",\"address\":\"127.0.0.1\",\"operateTs\":1680233403254}],\"RZ55A\":[{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1700641954244},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1681326891063},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1682140010053},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1700646095778},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1680233343187},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1690982041621},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1680242772672},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1681540892918},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1700646300859},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1681393305154},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1667802290510},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1682568229073},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1685027318487},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1678781853346},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1680837828765},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1671023529196},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1676278413378},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1668579767035},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1688659110407},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1671680482522},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1681098450266},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1682050154941},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1669021988650},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1696608314348},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1684819943636},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1681192397380},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1681390653546},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1699468565936},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1700625791210},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1683875134290},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1680102117254},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1665505688885},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1679982802489},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1694508110999},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1684643159351},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1680851932912},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1681397448191},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1679982983328},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1679220391635},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1700709616572},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55A\",\"address\":\"127.0.0.1\",\"operateTs\":1699278064854}],\"RZ55B\":[{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1678774844176},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1666876361187},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1700739835405},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1680890216034},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1679495633220},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1680774773542},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1681265389591},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1676522969581},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1681204540580},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1679553932211},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1682396913882},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1681984751646},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1695794328317},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1679215760974},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1678774736134},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1682049496967},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1682290871012},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1682102529751},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1682202396681},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1682098791479},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1681237043759},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1682327126365},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1679219183239},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1678774599339},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1700641532991},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1678786556381},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1671319379798},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1695267391512},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1681272012908},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1677037824950},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1700641746991},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1681490881046},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1680163238947},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1672180528023},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1681098933887},{\"nodeType\":\"SESSION\",\"cell\":\"RZ55B\",\"address\":\"127.0.0.1\",\"operateTs\":1681098144308}]}}";
        int times = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            JsonUtils.read(testData, NodeServerOperateInfo.class);
        }
        double duration = System.currentTimeMillis() - start;
        LOG.info("jsonRead total: %s, per: %s\n", duration, duration / times);

        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            JsonUtils.gsonRead(testData, NodeServerOperateInfo.class);
        }
        duration = System.currentTimeMillis() - start;
        LOG.info("gsonRead total: %s, per: %s\n", duration, duration / times);
    }
}
