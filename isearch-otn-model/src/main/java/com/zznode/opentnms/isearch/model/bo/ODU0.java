package com.zznode.opentnms.isearch.model.bo;


public class ODU0 extends ODU{

	private static final long serialVersionUID = 8336332184907258020L;

	private DSR dsr ;
	

	public String getFreeODU(Integer rate_i){
		int ratelevel = ConstBusiness.rateMap.get(rate_i).intValue();
		if( ratelevel == 0 ){
			if( dsr==null ){
				return "/odu0" + index ; 
			}
		}
		return "";
	}
	

	public DSR getDsr() {
		return dsr;
	}

	public void setDsr(DSR dsr) {
		this.dsr = dsr;
	}
	
}
