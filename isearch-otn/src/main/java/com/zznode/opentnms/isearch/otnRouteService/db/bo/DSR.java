package com.zznode.opentnms.isearch.otnRouteService.db.bo;

import com.zznode.opentnms.isearch.otnRouteService.consts.ConstBusiness;


public class DSR extends ODU{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4256325299670390989L;
	
	private Integer index ;
	
	public String getFreeODU(Integer rate){
		int ratelevel = ConstBusiness.rateMap.get(rate).intValue();
		return "/dsr" + index ; 
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	} 
	
	
	
	

}
