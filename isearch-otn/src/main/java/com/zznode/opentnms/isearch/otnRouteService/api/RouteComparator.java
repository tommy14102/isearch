package com.zznode.opentnms.isearch.otnRouteService.api;

import java.util.Comparator;

import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResult;

public class RouteComparator implements Comparator<RouteCalculationResult> {


	public int compare(RouteCalculationResult o1, RouteCalculationResult o2) {
		
		return o1.getZdCount() - o1.getZdCount() ;
		
	}


}
