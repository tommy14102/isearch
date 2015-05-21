package com.zznode.opentnms.isearch.otnRouteService.api;

import java.util.Comparator;

import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResult;

public class RouteJumpedComparator implements Comparator<RouteCalculationResult> {


	public int compare(RouteCalculationResult o1, RouteCalculationResult o2) {
		
		int zdplus =  o1.getZdCount() - o1.getZdCount() ;
		
		if( zdplus == 0 ){
			
			return o1.getBusiCount() - o2.getBusiCount() ; 
		}
		
		return zdplus ; 
		
	}


}
