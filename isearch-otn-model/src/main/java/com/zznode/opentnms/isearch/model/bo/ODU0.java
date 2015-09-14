package com.zznode.opentnms.isearch.model.bo;


public class ODU0 extends ODU{

	private static final long serialVersionUID = 8336332184907258020L;

	private DSR dsr ;
	
	public void assignOchSncid(String ochsncobjectid){
		this.setOchSncid(ochsncobjectid);
		dsr.assignOchSncid(ochsncobjectid);
	}

	public String getFreeODU(Integer rate_i){
		Integer ratelevel = ConstBusiness.rateMap.get(rate_i);
		if( ratelevel!=null && ratelevel.intValue() == 0 ){
			if( dsr==null ){
				return "/odu0" + index ; 
			}
		}
		
		Integer odulevel = ConstBusiness.odukMap.get(rate_i);
		if( odulevel!=null && ratelevel.intValue() == 1 ){
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
