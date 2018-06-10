package org.bcia.julongchain.core.ssc.essc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.Utils;
import org.bcia.julongchain.common.util.proto.ProposalUtils;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.julongchain.core.smartcontract.shim.impl.MockStub;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.node.entity.MockCrypto;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.Smartcontract;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * ESSC的单元测试类
 *
 * @author sunianle
 * @date 3/8/18
 * @company Dingxuan
 */
public class ESSCTest extends BaseJunit4Test {
    @Autowired
    private ESSC essc;
    @Mock
    private ISmartContractStub stub;

    @Test
    public void init() {
        ISmartContract.SmartContractResponse smartContractResponse = essc.init(stub);
        assertThat(smartContractResponse.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void invoke() {
        try {
            ProposalResponsePackage.Response successResponse = ProposalResponsePackage.Response.newBuilder().
                    setStatus(200).setMessage("OK").setPayload(ByteString.copyFromUtf8("payload")).build();

            MockStub mockStub = new MockStub(CommConstant.ESSC, essc);
            // Initialize ESCC supplying the identity of the signer
            List<ByteString> args0 = new LinkedList<ByteString>();
            args0.add(ByteString.copyFromUtf8("DEFAULT"));
            args0.add(ByteString.copyFromUtf8("PEER"));
            ISmartContract.SmartContractResponse res = mockStub.mockInit("1", args0);
            if (res.getStatus() != ISmartContract.SmartContractResponse.Status.SUCCESS) {
                System.out.printf("Init failded,%s\n", res.getMessage());
            }


            //success test 1: invocation with mandatory args only
            Smartcontract.SmartContractID smartContractID = Smartcontract.SmartContractID.newBuilder().setName("foo").setVersion("1.0").build();
            Smartcontract.SmartContractInput input = Smartcontract.SmartContractInput.newBuilder().addArgs(ByteString.copyFromUtf8("some"))
                    .addArgs(ByteString.copyFromUtf8("args")).build();
            Smartcontract.SmartContractSpec smartContractSpec = Smartcontract.SmartContractSpec.newBuilder().
                    setSmartContractId(smartContractID).
                    setType(Smartcontract.SmartContractSpec.Type.JAVA).
                    setInput(input).
                    build();
            Smartcontract.SmartContractInvocationSpec invocationSpec = Smartcontract.SmartContractInvocationSpec.newBuilder().
                    setSmartContractSpec(smartContractSpec).build();
            ISigningIdentity sId = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();
            byte[] sIdBytes = sId.serialize();
            byte[] nonce = MockCrypto.getRandomNonce();
            String txID=ProposalUtils.computeProposalTxID(sIdBytes, nonce);
            ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType.ENDORSER_TRANSACTION,
                    Utils.getTestGroupID(), txID, invocationSpec, nonce, sIdBytes, null);
            String simRes="simulation_result";
            

            List<ByteString> args1 = new LinkedList<ByteString>();
            args1.add(0, ByteString.copyFromUtf8(""));
            args1.add(1,proposal.getHeader());
            args1.add(2,proposal.getPayload());
            args1.add(3,smartContractID.toByteString());
            args1.add(4,successResponse.toByteString());
            args1.add(5,ByteString.copyFromUtf8(simRes));
            ISmartContract.SmartContractResponse res1 = mockStub.mockInvoke("1", args1);
            if (res1.getStatus() != ISmartContract.SmartContractResponse.Status.SUCCESS) {
                System.out.printf("Invoke failded,%s\n", res.getMessage());
            }
            assertThat(res1.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));


            //Failed path: Not enough parameters
            List<ByteString> args2 = new LinkedList<ByteString>();
            args2.add(0, ByteString.copyFromUtf8("test"));
            ISmartContract.SmartContractResponse res2=mockStub.mockInvoke("1", args2);
            ISmartContract.SmartContractResponse.Status status2 = res2.getStatus();
            assertThat(status2,is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));

            // Failed path: header is empty
            List<ByteString> args3 = new LinkedList<ByteString>();
            args3.add(0, ByteString.copyFromUtf8("test"));
            args3.add(1,ByteString.copyFromUtf8(""));
            args3.add(2,proposal.getPayload());
            args3.add(3,smartContractID.toByteString());
            args3.add(4,successResponse.toByteString());
            args3.add(5,ByteString.copyFromUtf8(simRes));
            ISmartContract.SmartContractResponse res3=mockStub.mockInvoke("1", args3);
            ISmartContract.SmartContractResponse.Status status3= res3.getStatus();
            assertThat(status3,is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));

            // Failed path: header is null
            List<ByteString> args4 = new LinkedList<ByteString>();
            args4.add(0, ByteString.copyFromUtf8("test"));
            //预期将在invoke时抛出空指针异常,用户调用不应传入null
            args4.add(1,null);
            args4.add(2,proposal.getPayload());
            args4.add(3,smartContractID.toByteString());
            args4.add(4,successResponse.toByteString());
            args4.add(5,ByteString.copyFromUtf8(simRes));
            try {
                ISmartContract.SmartContractResponse res4 = mockStub.mockInvoke("1", args4);
            }catch(Exception e){
                //stub.getArgs()将抛出NullPointerException异常
                assertThat(e.getClass().toString(),is("class java.lang.NullPointerException"));
                //e.printStackTrace();
            }
        } catch (JavaChainException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getSmartContractStrDescription() {
        String description = essc.getSmartContractStrDescription();
        String expectedResult = "与背书相关的系统智能合约";
        assertThat(description, is(expectedResult));
    }

    @Test
    public void transformProtobuf() {
        try {
            ProposalResponsePackage.Response successResponse = ProposalResponsePackage.Response.newBuilder().
                    setStatus(200).setMessage("OK").setPayload(ByteString.copyFromUtf8("payload")).build();
            ByteString byteString = successResponse.toByteString();
            ProposalResponsePackage.Response transformedResponse = ProposalResponsePackage.Response.parseFrom(byteString);
            assertThat(transformedResponse.getStatus(),is(200));

            ProposalResponsePackage.Response successResponse2 = ProposalResponsePackage.Response.newBuilder().
                    setStatus(200).setMessage("OK").setPayload(ByteString.copyFromUtf8("payload")).build();
            ByteString byteString2 = successResponse.toByteString();
            String string2=byteString2.toStringUtf8();
            ByteString transformByteString=ByteString.copyFromUtf8(string2);
            ProposalResponsePackage.Response transformedResponse2 = ProposalResponsePackage.Response.parseFrom(transformByteString);
            assertThat(transformedResponse2.getStatus(),not(200));

            ProposalResponsePackage.Response successResponse3= ProposalResponsePackage.Response.newBuilder().
                    setStatus(200).setMessage("OK").setPayload(ByteString.copyFromUtf8("payload")).build();
            ByteString byteString3 = successResponse.toByteString();
            byte[] byteArray3 = byteString3.toByteArray();
            ProposalResponsePackage.Response transformedResponse3 = ProposalResponsePackage.Response.parseFrom(byteArray3);
            assertThat(transformedResponse3.getStatus(),is(200));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }


}