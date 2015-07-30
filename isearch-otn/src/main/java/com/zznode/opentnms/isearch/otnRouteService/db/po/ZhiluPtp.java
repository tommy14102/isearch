package com.zznode.opentnms.isearch.otnRouteService.db.po;

import java.sql.ResultSet;

public class ZhiluPtp 
{
    public String ptpobjectid = "";        //VARCHAR(128),              /*波分通道路由对象ID*/
    public String cardobjectid = "";     //VARCHAR(128),              /*波分通道对象ID*/
    public String cardmodel = "";     //VARCHAR(128),
	public String getPtpobjectid() {
		return ptpobjectid;
	}
	public void setPtpobjectid(String ptpobjectid) {
		this.ptpobjectid = ptpobjectid;
	}
	public String getCardobjectid() {
		return cardobjectid;
	}
	public void setCardobjectid(String cardobjectid) {
		this.cardobjectid = cardobjectid;
	}
	public String getCardmodel() {
		return cardmodel;
	}
	public void setCardmodel(String cardmodel) {
		this.cardmodel = cardmodel;
	}
    
    
    
    
}
