package com.zznode.opentnms.isearch.otnRouteService.api.model;


public class CardUsedPtpInput {

	private String cardobjectid;

	public String getCardobjectid() {
		return cardobjectid;
	}

	public void setCardobjectid(String cardobjectid) {
		this.cardobjectid = cardobjectid;
	}

	public boolean checkmyself() {
		
		return cardobjectid!=null && cardobjectid.length()>0;
		
	}
	
	
}
