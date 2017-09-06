package org.hyperledger.fabric.mysdkuse;

import org.hyperledger.fabric.sdk.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Transaction {
    /// Send transaction proposal to all peers
    static  Collection<ProposalResponse> responses=ResponseMsg.responses;
    static Collection<ProposalResponse> successful = ResponseMsg.successful;
    static Collection<ProposalResponse> failed = ResponseMsg.failed;
    private static final Config testConfig = Config.getConfig();

    public  void SendtTansactionToPeers(HFClient client, Channel channel, ChaincodeID chaincodeID,String[] TransMsg){
        try {
            TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
            transactionProposalRequest.setChaincodeID(chaincodeID);
            transactionProposalRequest.setFcn("invoke");
            transactionProposalRequest.setProposalWaitTime(testConfig.getProposalWaitTime());
            transactionProposalRequest.setArgs(TransMsg);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); //Just some extra junk in transient map
            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
            tm2.put("result", ":)".getBytes(UTF_8));  // This should be returned see chaincode why.
            tm2.put(testConfig.EXPECTED_EVENT_NAME,testConfig. EXPECTED_EVENT_DATA);  //This should trigger an event see chaincode why.

            transactionProposalRequest.setTransientMap(tm2);

            out("sending transactionProposal to all peers with arguments: move(a,b,100)");

            Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
            for (ProposalResponse response : transactionPropResp) {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    out("Successful transaction proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName());
                    successful.add(response);
                } else {
                    failed.add(response);
                }
            }

            // Check that all the proposals are consistent with each other. We should have only one set
            // where all the proposals above are consistent. Note the when sending to Orderer this is done automatically.
            //  Shown here as an example that applications can invoke and select.
            // See org.hyperledger.fabric.sdk.proposal.consistency_validation config property.
            Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(transactionPropResp);
            if (proposalConsistencySets.size() != 1) {
                //fail(format("Expected only one set of consistent proposal responses but got %d", proposalConsistencySets.size()));
            }

            out("Received %d transaction proposal responses. Successful+verified: %d . Failed: %d",
                    transactionPropResp.size(), successful.size(), failed.size());
            if (failed.size() > 0) {
                ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
//                        fail("Not enough endorsers for invoke(move a,b,100):" + failed.size() + " endorser error: " +
//                                firstTransactionProposalResponse.getMessage() +
//                                ". Was verified: " + firstTransactionProposalResponse.isVerified());
            }
            out("Successfully received transaction proposal responses.");

            ProposalResponse resp = transactionPropResp.iterator().next();
            byte[] x = resp.getChaincodeActionResponsePayload(); // This is the data returned by the chaincode.
            String resultAsString = null;
            if (x != null) {
                resultAsString = new String(x, "UTF-8");
            }
//                    assertEquals(":)", resultAsString);
//
//                    assertEquals(200, resp.getChaincodeActionResponseStatus()); //Chaincode's status.

            TxReadWriteSetInfo readWriteSetInfo = resp.getChaincodeActionResponseReadWriteSetInfo();
            //See blockwalker below how to transverse this
//                    assertNotNull(readWriteSetInfo);
//                    assertTrue(readWriteSetInfo.getNsRwsetCount() > 0);

            ChaincodeID cid = resp.getChaincodeID();
//                    assertNotNull(cid);
//                    assertEquals(CHAIN_CODE_PATH, cid.getPath());
//                    assertEquals(CHAIN_CODE_NAME, cid.getName());
//                    assertEquals(CHAIN_CODE_VERSION, cid.getVersion());
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
