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
package org.bcia.javachain.core.ssc.qssc;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.aclmgmt.AclManagement;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.node.NodeTool;
import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.core.ssc.essc.ESSC;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询系统智能合约　Query System Smart Contract,CSSC
 * CSSC implements the ledger query functions, including:
 * -GetChainInfo returns BlockchainInfo
 * -GetBlockByNumber returns a block
 * -GetBlockByHash returns a block
 * -GetTransactionByID returns a transaction
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
@Component
public class QSSC extends SystemSmartContractBase {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ESSC.class);
    // These are function names from Invoke first parameter
    public final static String GET_GROUP_INFO="GetGroupInfo";

    public final static String GET_BLOCK_BY_NUMBER="GetBlockByNumber";

    public final static String GET_BLOCK_BY_HASH="GetBlockByHash";

    public final static String GET_TRANSACTION_BY_ID="GetTransactionByID";

    public final static String GET_BLOCK_BY_TX_ID="GetBlockByTxID";

    // Init is called once per chain when the chain is created.
    // This allows the chaincode to initialize any variables on the ledger prior
    // to any transaction execution on the chain.
    @Override
    public Response init(ISmartContractStub stub) {
        log.info("Successfully initialized QSSC");
        return newSuccessResponse();
    }

    // Invoke is called with args[0] contains the query function name, args[1]
    // contains the chain ID, which is temporary for now until it is part of stub.
    // Each function requires additional parameters as described below:
    // # GetChainInfo: Return a BlockchainInfo object marshalled in bytes
    // # GetBlockByNumber: Return the block specified by block number in args[2]
    // # GetBlockByHash: Return the block specified by block hash in args[2]
    // # GetTransactionByID: Return the transaction specified by ID in args[2]
    @Override
    public Response invoke(ISmartContractStub stub) {
        log.debug("Enter QSSC invoke function");
        List<String> args = stub.getStringArgs();
        int size=args.size();
        if(size<2){
            return newErrorResponse(String.format("Incorrect number of arguments, %d)",args.size()));
        }
        String function=args.get(0);
        String groupID=args.get(1);
        if(function!=GET_GROUP_INFO && size<3){
            return newErrorResponse(String.format("Missing 3rd argument for %s",function));
        }
        INodeLedger targetLedger = NodeTool.getLedger(groupID);
        if(targetLedger==null){
            return newErrorResponse(String.format("Invalid group ID %s",groupID));
        }
        log.debug("Invoke function:%s on group:%s",function,groupID);

        // Handle ACL:
        // 1. get the signed proposal
        ProposalPackage.SignedProposal sp = stub.getSignedProposal();

        // 2. check the channel reader policy
        String res=getACLResource(function);
        if(AclManagement.getACLProvider().checkACL(function,groupID,sp)==false){
            newErrorResponse(String.format("Authorization request for [%s][%s] failed",function,groupID));
        }

        switch(function){
            case GET_TRANSACTION_BY_ID:
                ;
            case GET_BLOCK_BY_NUMBER:
                ;
            case GET_BLOCK_BY_HASH:
                ;
            case GET_GROUP_INFO:
                ;
            case GET_BLOCK_BY_TX_ID:
                ;
            default:
                return newErrorResponse(String.format("Request function %s not found",function));
        }
    }

    @Override
    public String getSmartContractStrDescription() {
        return "与查询相关的系统智能合约";
    }

    private String getACLResource(String function){
        return "QSSC."+function;
    }

    private ProposalResponsePackage.Response getTransactionByID(INodeLedger vledger,byte[] tid){
        return null;
    }

    private ProposalResponsePackage.Response getBlockByNumber(INodeLedger vledger,byte[] number){
        return null;
    }

    private ProposalResponsePackage.Response getBlockByHash(INodeLedger vledger,byte[] hash){
        return null;
    }

    private ProposalResponsePackage.Response getGroupInfo(INodeLedger vledger){
        return null;
    }

    private ProposalResponsePackage.Response getBlockByTxID(INodeLedger vledger,byte[] rawTxID){
        return null;
    }


}