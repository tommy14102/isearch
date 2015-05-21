package com.zznode.opentnms.isearch.otnRouteService.api.model;

import java.util.ArrayList;

public class RouteCalculationResult {

	private String freeOdu;
	private Integer zdCount;
	private Integer busiCount;
	
	private ArrayList<RouteCalculationResultRoute> route ;

	
	public Integer getZdCount() {
		return zdCount;
	}

	public void setZdCount(Integer zdCount) {
		this.zdCount = zdCount;
	}

	public Integer getBusiCount() {
		return busiCount;
	}

	public void setBusiCount(Integer busiCount) {
		this.busiCount = busiCount;
	}

	public String getFreeOdu() {
		return freeOdu;
	}

	public void setFreeOdu(String freeOdu) {
		this.freeOdu = freeOdu;
	}

	public ArrayList<RouteCalculationResultRoute> getRoute() {
		return route;
	}

	public void setRoute(ArrayList<RouteCalculationResultRoute> route) {
		this.route = route;
	}
	
	
	

	
}
