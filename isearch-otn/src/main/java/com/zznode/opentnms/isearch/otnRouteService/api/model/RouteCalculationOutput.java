package com.zznode.opentnms.isearch.otnRouteService.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = {"routePlanobjectid","routePlanseq","routeseq" ,"rate", "protectionType","routeCalculationResultWrapper"  })
public class RouteCalculationOutput {

	private String routePlanobjectid;
	private String routePlanseq;
	private String routeseq;
	private Integer rate;
	private String protectionType;
	
	//@XmlElementWrapper(name="rcResult")  
	//@XmlElement(name="rc-work") 
	private List<RouteCalculationResultWrapper> routeCalculationResultWrapper = new ArrayList<RouteCalculationResultWrapper>();
	
	public String getRoutePlanobjectid() {
		return routePlanobjectid;
	}
	public void setRoutePlanobjectid(String routePlanobjectid) {
		this.routePlanobjectid = routePlanobjectid;
	}
	public String getRoutePlanseq() {
		return routePlanseq;
	}
	public void setRoutePlanseq(String routePlanseq) {
		this.routePlanseq = routePlanseq;
	}
	public String getRouteseq() {
		return routeseq;
	}
	public void setRouteseq(String routeseq) {
		this.routeseq = routeseq;
	}
	public Integer getRate() {
		return rate;
	}
	public void setRate(Integer rate) {
		this.rate = rate;
	}
	public String getProtectionType() {
		return protectionType;
	}
	public void setProtectionType(String protectionType) {
		this.protectionType = protectionType;
	}
	public List<RouteCalculationResultWrapper> getRouteCalculationResultWrapper() {
		return routeCalculationResultWrapper;
	}
	public void setRouteCalculationResultWrapper(
			List<RouteCalculationResultWrapper> routeCalculationResultWrapper) {
		this.routeCalculationResultWrapper = routeCalculationResultWrapper;
	}
	
	

	
}
