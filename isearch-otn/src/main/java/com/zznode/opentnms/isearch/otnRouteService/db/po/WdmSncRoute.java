package com.zznode.opentnms.isearch.otnRouteService.db.po;

import java.io.Serializable;


public class WdmSncRoute extends DbWdmSncRoute implements Serializable
{
	private static final long serialVersionUID = 4763283924419782109L;
	public String acardmodel = "";             
	public String zcardmodel = "";  
	private String aendjuzhan = "";             
	private String zendjuzhan = ""; 
    
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
	public String getAendjuzhan() {
		return aendjuzhan;
	}
	public void setAendjuzhan(String aendjuzhan) {
		this.aendjuzhan = aendjuzhan;
	}
	public String getZendjuzhan() {
		return zendjuzhan;
	}
	public void setZendjuzhan(String zendjuzhan) {
		this.zendjuzhan = zendjuzhan;
	}

    
    
}
