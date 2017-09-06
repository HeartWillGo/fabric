package org.hyperledger.fabric.mysdkuse;

import org.hyperledger.fabric.sdk.ProposalResponse;

import java.util.Collection;
import java.util.LinkedList;

public class ResponseMsg {
    static final Collection<ProposalResponse> responses=new LinkedList<>();
    static final Collection<ProposalResponse> successful=new LinkedList<>()  ;
    static final Collection<ProposalResponse> failed =new LinkedList<>();

}
