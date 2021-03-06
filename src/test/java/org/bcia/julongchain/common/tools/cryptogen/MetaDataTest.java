/**
 * Copyright BCIA. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.common.tools.cryptogen;

import org.bcia.julongchain.common.tools.cryptogen.bean.MetaData;
import org.junit.Assert;
import org.junit.Test;


/**
 * MetaData 测试类
 *
 * @author chenhao, liuxifeng
 * @date 2018/7/12
 * @company Excelsecu
 */
public class MetaDataTest {

    @Test
    public void getVersionInfo() {

        String testVersion="TestVersion";
        MetaData.mVersion=testVersion;
        String expected = MetaData.PROGRAM_NAME +
                ":\n Version: " +
                testVersion +
                "\n Java version: " +
                System.getProperty("java.version") +
                "\n OS/Arch: " +
                System.getProperty("os.name") +
                "/" +
                System.getProperty("os.arch");
        Assert.assertEquals(expected, MetaData.getVersionInfo());

    }
}