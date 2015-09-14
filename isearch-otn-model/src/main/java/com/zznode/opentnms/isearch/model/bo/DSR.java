package com.zznode.opentnms.isearch.model.bo;



public class DSR extends ODU{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4256325299670390989L;
	
	private Integer index ;
	
	public void assignOchSncid(String ochsncobjectid){
		this.setOchSncid(ochsncobjectid);
	}
	
	public String getFreeODU(Integer rate){
		return "/dsr" + index ; 
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	} 
	
	
	
	

}
