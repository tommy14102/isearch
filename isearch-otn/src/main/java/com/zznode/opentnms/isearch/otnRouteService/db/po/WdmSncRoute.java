package com.zznode.opentnms.isearch.otnRouteService.db.po;

import java.io.Serializable;


public class WdmSncRoute extends DbWdmSncRoute implements Serializable
{
	private static final long serialVersionUID = 4763283924419782109L;
	public String acardmodel = "";             
    public String zcardmodel = "";            
    public String ajuzhan = "";     
    public String zjuzhan = "";             
    public String tsnid = "";
    
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
	public String getAjuzhan() {
		return ajuzhan;
	}
	public void setAjuzhan(String ajuzhan) {
		this.ajuzhan = ajuzhan;
	}
	public String getZjuzhan() {
		return zjuzhan;
	}
	public void setZjuzhan(String zjuzhan) {
		this.zjuzhan = zjuzhan;
	}
	public String getTsnid() {
		return tsnid;
	}
	public void setTsnid(String tsnid) {
		this.tsnid = tsnid;
	}  		 

    
    
}
