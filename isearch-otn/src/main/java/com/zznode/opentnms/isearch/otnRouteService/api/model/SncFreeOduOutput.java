package com.zznode.opentnms.isearch.otnRouteService.api.model;

import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = {"sncid","freeOdu0"})
public class SncFreeOduOutput {

	private String sncid;
	private String freeOdu0;
	
	public String getSncid() {
		return sncid;
	}
	public void setSncid(String sncid) {
		this.sncid = sncid;
	}
	public String getFreeOdu0() {
		return freeOdu0;
	}
	public void setFreeOdu0(String freeOdu0) {
		this.freeOdu0 = freeOdu0;
	}
	
	

	
}
