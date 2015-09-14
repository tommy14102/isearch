package com.zznode.opentnms.isearch.otnRouteService.api;

import java.util.Comparator;

import com.zznode.opentnms.isearch.model.bo.ZdResult;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResult;

public class ZdResultComparator implements Comparator<ZdResult> {


	public int compare(ZdResult o1, ZdResult o2) {
		
		return o1.getSncid().compareTo( o2.getSncid() );
		
	}


}
