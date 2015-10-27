package com.zznode.opentnms.isearch.otnRouteService.api.model;

import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = {"objectid","portname"})
public class CardUsedPtpInfo {

	private String objectid;
	private String portname;
	
	public String getObjectid() {
		return objectid;
	}
	public void setObjectid(String objectid) {
		this.objectid = objectid;
	}
	public String getPortname() {
		return portname;
	}
	public void setPortname(String portname) {
		this.portname = portname;
	}
	
	
	
}
