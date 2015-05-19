package com.zznode.opentnms.isearch.otnRouteService.api.model;

import java.util.ArrayList;

public class RouteCalculationResult {

	private String freeOdu;
	private ArrayList<RouteCalculationResultRoute> route ;

	
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
