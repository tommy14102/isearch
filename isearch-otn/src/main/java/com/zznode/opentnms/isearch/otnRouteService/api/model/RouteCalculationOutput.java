package com.zznode.opentnms.isearch.otnRouteService.api.model;

import java.util.ArrayList;
import java.util.List;


public class RouteCalculationOutput {

	private String routePlanobjectid;
	private String routePlanseq;
	private String routeseq;
	private Integer rate;
	private String ProtectionType;
	private List<RouteCalculationResult> routeCalculationResult ;
	
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
	public List<RouteCalculationResult> getRouteCalculationResult() {
		return routeCalculationResult;
	}
	public void setRouteCalculationResult(
			List<RouteCalculationResult> routeCalculationResult) {
		this.routeCalculationResult = routeCalculationResult;
	}
	
	

	
}
