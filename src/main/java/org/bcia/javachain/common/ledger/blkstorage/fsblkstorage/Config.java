/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.javachain.common.ledger.blkstorage.fsblkstorage;

/**
 * 类描述
 *
 * @author
 * @date 2018/3/7
 * @company Dingxuan
 */
public class Config {

    /**
     * ChainsDir is the name of the directory containing the channel ledgers.
     */
    public static final String CHAINS_DIR = "chains";

    /**
     * IndexDir is the name of the directory containing all block indexes across ledgers.
     */
    public static final String INDEX_DIR = "index";

    public static final Integer DEFAULT_MAX_BLOCKFILE_SIZE = 64 * 1024 * 1024;

    public Conf newConf(String blockStorageDir, Integer maxBlockfileSize) {
        if(maxBlockfileSize <= 0) {
            maxBlockfileSize = DEFAULT_MAX_BLOCKFILE_SIZE;
        }
        Conf conf = new Conf();
        conf.setBlockStorageDir(blockStorageDir);
        conf.setMaxBlockfileSize(maxBlockfileSize);
        return conf;
    }

}
