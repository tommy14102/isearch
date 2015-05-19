package com.zznode.opentnms.isearch.otnRouteService.db.bo;

import java.util.HashSet;
import java.util.Set;

public class TsnGraph {
	
	private String tsnid ;
	private HashSet<String> zdids = new HashSet<String>();


	public String getTsnid() {
		return tsnid;
	}

	public void setTsnid(String tsnid) {
		this.tsnid = tsnid;
	}

	public HashSet<String> getZdids() {
		return zdids;
	}

	public void setZdids(HashSet<String> zdids) {
		this.zdids = zdids;
	}

	
	
	
	

}
