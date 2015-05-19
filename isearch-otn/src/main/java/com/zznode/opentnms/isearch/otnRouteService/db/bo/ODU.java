package com.zznode.opentnms.isearch.otnRouteService.db.bo;

import java.io.Serializable;

public class ODU implements Serializable{

	private static final long serialVersionUID = 5195945162610962833L;
	
	private Integer rate ;
	private String sncobjectid;

	
	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}

	public String getSncobjectid() {
		return sncobjectid;
	}

	public void setSncobjectid(String sncobjectid) {
		this.sncobjectid = sncobjectid;
	}
	
	
	
}
