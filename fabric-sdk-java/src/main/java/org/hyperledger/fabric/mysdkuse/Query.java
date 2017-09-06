package org.hyperledger.fabric.mysdkuse;

import org.hyperledger.fabric.sdk.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Query {
    public static void SendQuryToPeers(HFClient client, Channel channel, ChaincodeID chaincodeID,String[] query_args){
        try {


            QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
            queryByChaincodeRequest.setArgs(query_args);
            queryByChaincodeRequest.setFcn("invoke");
            queryByChaincodeRequest.setChaincodeID(chaincodeID);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
            queryByChaincodeRequest.setTransientMap(tm2);

            Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());
            for (ProposalResponse proposalResponse : queryProposals) {
                if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
//                            fail("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " + proposalResponse.getStatus() +
//                                    ". Messages: " + proposalResponse.getMessage()
//                                    + ". Was verified : " + proposalResponse.isVerified());
                } else {
                    String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                     out ("Query payload of b from peer %s returned %s",proposalResponse.getPeer().getName(), payload);
                    //  assertEquals(payload, expect);
                }
            }
        }catch (Exception e){
            e.printStackTrace();

        }

    }
    static void out(String format, Object... args) {

        System.err.flush();
        System.out.flush();

        System.out.println(format(format, args));
        System.err.flush();
        System.out.flush();

    }
}
