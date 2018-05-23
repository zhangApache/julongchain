/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.statebasedval;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.IResultsIterator;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.RangeQueryResultsHelper;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.RwSetUtil;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.IQueryResult;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;

/**
 * range query hash 验证器
 * 用于验证返回读集的hash
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public class RangeQueryHashValidator implements IRangeQueryValidator {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(RangeQueryHashValidator.class);

    private KvRwset.RangeQueryInfo rqInfo;
    private IResultsIterator itr;
    private RangeQueryResultsHelper resultsHelper;

    @Override
    public void init(KvRwset.RangeQueryInfo rqInfo, IResultsIterator itr) throws LedgerException {
        this.rqInfo = rqInfo;
        this.itr = itr;
        this.resultsHelper = RangeQueryResultsHelper.newRangeQueryResultsHelper(true, rqInfo.getReadsMerkleHashes().getMaxDegree());
    }

    @Override
    public boolean validate() throws LedgerException {
        int lastMatchedIndex = -1;
        KvRwset.QueryReadsMerkleSummary inMerkle = rqInfo.getReadsMerkleHashes();
        KvRwset.QueryReadsMerkleSummary merkle = null;
        logger.debug("inMerkle");
        while(true){
            IQueryResult result = null;
            result = itr.next();
            logger.debug("Processing result = " + result);
            if(result == null){
                merkle = resultsHelper.done().getValue();
                boolean equals = inMerkle.equals(merkle);
                logger.debug("Combined interator exhausted.");
                return equals;
            }
            resultsHelper.addResult(RwSetUtil.newKVRead(((VersionedKV) result).getCompositeKey().getKey(), ((VersionedKV) result).getVersionedValue().getVersion()));
            merkle = resultsHelper.getMerkleSummary();

            if(merkle.getMaxLevel() < inMerkle.getMaxLevel()){
                logger.debug("Hashes still under construction. Noting to compare yet. Need more results. Continuing...");
                continue;
            }
            if(lastMatchedIndex == merkle.getMaxLevelHashesList().size() - 1){
                logger.debug(String.format("Need more results to build next entry [index=%d] at level [%d]. Continuing...", lastMatchedIndex + 1, merkle.getMaxLevel()));
                continue;
            }
            if(merkle.getMaxLevelHashesList().size() > inMerkle.getMaxLevelHashesList().size()){
                logger.debug("Entries exceeded from what are present in the incoming merkleSummary. Validation failed");
                return false;
            }
            lastMatchedIndex++;
            if(!merkle.getMaxLevelHashes(lastMatchedIndex).equals(inMerkle.getMaxLevelHashes(lastMatchedIndex))){
                logger.debug(String.format("Hashes does not match at index [%d]. Validation failed", lastMatchedIndex));
                return false;
            }
        }
    }
}
