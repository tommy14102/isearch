package com.zznode.opentnms.isearch.otnRouteService.api.model;


public class SncFreeOduInput {

	private String sncid;

	public String getSncid() {
		return sncid;
	}

	public void setSncid(String sncid) {
		this.sncid = sncid;
	}

	public boolean checkmyself() {
		
		return sncid!=null && sncid.length()>0;
		
	}
	
	
}
