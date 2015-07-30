package com.zznode.opentnms.isearch.otnRouteService.api;

import java.util.Comparator;

import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResult;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;

public class RouteJumpedComparatorSingle implements Comparator<Link> {


	public int compare(Link o1, Link o2) {
		
		if(o1.getJump() == o2.getJump()){
			String o1sncid = o1.getZdResult().getSncid();
			String o2sncid = o2.getZdResult().getSncid();
			return o1sncid.compareTo(o2sncid);
		}
		
		return (int)(o1.getJump() - o2.getJump()) ; 
		
	}


}
