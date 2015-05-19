package com.zznode.opentnms.isearch.otnRouteService.api.model;

import java.util.ArrayList;


public class RouteCalculationOutput {

	private String routePlanobjectid;
	private String routePlanseq;
	private String routeseq;
	private Integer rate;
	private String ProtectionType;
	private ArrayList<RouteCalculationResult> routeCalculationResult ;
	
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
		return ProtectionType;
	}
	public void setProtectionType(String protectionType) {
		ProtectionType = protectionType;
	}
	public ArrayList<RouteCalculationResult> getRouteCalculationResult() {
		return routeCalculationResult;
	}
	public void setRouteCalculationResult(
			ArrayList<RouteCalculationResult> routeCalculationResult) {
		this.routeCalculationResult = routeCalculationResult;
	}
	
	

	
}
