package com.zznode.opentnms.isearch.otnRouteService.db.po;

import java.io.Serializable;


public class WdmSncRoute extends DbWdmSncRoute implements Serializable
{
	private static final long serialVersionUID = 4763283924419782109L;
	public String acardmodel = "";             
    public String zcardmodel = "";            
    
	public String getAcardmodel() {
		return acardmodel;
	}
	public void setAcardmodel(String acardmodel) {
		this.acardmodel = acardmodel;
	}
	public String getZcardmodel() {
		return zcardmodel;
	}
	public void setZcardmodel(String zcardmodel) {
		this.zcardmodel = zcardmodel;
	}

    
    
}
