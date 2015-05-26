package com.zznode.opentnms.isearch.model.bo;

import java.util.Set;

public class Zhandian {

	private String zhandianid ; 
	private Set<String> meids ;
	private Set<String> tsnids ;
	
	public boolean isCrossDomain(){
		return tsnids.size() > 1 ;
	}
	
	public String getZhandianid() {
		return zhandianid;
	}
	public void setZhandianid(String zhandianid) {
		this.zhandianid = zhandianid;
	}
	public Set<String> getMeids() {
		return meids;
	}
	public void setMeids(Set<String> meids) {
		this.meids = meids;
	}
	public Set<String> getTsnids() {
		return tsnids;
	}
	public void setTsnids(Set<String> tsnids) {
		this.tsnids = tsnids;
	}
	
	
	
}
