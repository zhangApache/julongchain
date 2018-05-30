package org.bcia.javachain.events.producer;

import com.google.protobuf.ByteString;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.TransactionPackage;
import org.junit.Test;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/21
 * @company Dingxuan
 */
public class EventHelperTest {

    @Test
    public void createBlockEvents() {
        ProposalResponsePackage.ProposalResponsePayload payload = ProposalResponsePackage.ProposalResponsePayload
                .getDefaultInstance();
        System.out.println("update: " + payload.getProposalHash().toStringUtf8());

        ProposalResponsePackage.ProposalResponsePayload.Builder builder = payload.toBuilder();
        builder.setProposalHash(ByteString.copyFrom(new byte[]{'0', '1', '2'}));

        System.out.println("update: " + payload.getProposalHash().toStringUtf8());
        System.out.println("update2: " + builder.getProposalHash().toStringUtf8());


        TransactionPackage.SmartContractActionPayload.Builder payloadBuilder =
                TransactionPackage.SmartContractActionPayload.newBuilder();
        payloadBuilder.getActionBuilder().setProposalResponsePayload(ByteString.copyFrom(new byte[]{'0', '1', '2'}));
        System.out.println("update3: " + payloadBuilder.build().getAction().getProposalResponsePayload().toStringUtf8());


    }

    @Test
    public void send() {
    }
}