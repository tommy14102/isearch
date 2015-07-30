package com.zznode.opentnms.isearch.otnRouteService.api;

import java.util.Comparator;

import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResult;

public class RouteComparator implements Comparator<RouteCalculationResult> {


	public int compare(RouteCalculationResult o1, RouteCalculationResult o2) {
		
		if( o1.getZdCount().equals(o1.getZdCount())){
			String o1sncid = o1.getRouteCalculationResultRouteWrapper().get(0).getSncid();
			String o2sncid = o2.getRouteCalculationResultRouteWrapper().get(0).getSncid();
			return o1sncid.compareTo(o2sncid);
		}
		
		return o1.getZdCount() - o1.getZdCount() ;
		
	}


}
