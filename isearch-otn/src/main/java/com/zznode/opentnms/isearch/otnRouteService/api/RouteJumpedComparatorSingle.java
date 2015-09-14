package com.zznode.opentnms.isearch.otnRouteService.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResult;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;

public class RouteJumpedComparatorSingle implements Comparator<Link> {

	ArrayList<String> preMes = new ArrayList<String>();
	ArrayList<String> tailMes = new ArrayList<String>();
	
	public RouteJumpedComparatorSingle(ArrayList<String> preMes , ArrayList<String> tailMes ){
		this.preMes = preMes;
		this.tailMes = tailMes;
	}
	
	public int compare(Link o1, Link o2) {
		
		if(preMes!=null && preMes.size()>0){
			if(preMes.contains(o1.getZdResult().getFirstZdRoute().getFirst().getAendmeid()) && !preMes.contains(o2.getZdResult().getFirstZdRoute().getFirst().getAendmeid())){
				return -1;
			}
			else if(!preMes.contains(o1.getZdResult().getFirstZdRoute().getFirst().getAendmeid()) && preMes.contains(o2.getZdResult().getFirstZdRoute().getFirst().getAendmeid())){ 
				return 1 ; 
			}
			else if(preMes.contains(o1.getZdResult().getFirstZdRoute().getFirst().getAendmeid()) && preMes.contains(o2.getZdResult().getFirstZdRoute().getFirst().getAendmeid())){ 
				if(o1.getZdResult().getFirstZdRoute().getFirst().getAendmeid().equals(o2.getZdResult().getFirstZdRoute().getFirst().getAendmeid())){
					return o1.getZdResult().getSncid().compareTo(o2.getZdResult().getSncid());
				}
				else{
					return o1.getZdResult().getFirstZdRoute().getFirst().getAendmeid().compareTo(o2.getZdResult().getFirstZdRoute().getFirst().getAendmeid()) ; 
				}
				
			}
		}
		
		if(tailMes!=null && tailMes.size()>0){
			if(tailMes.contains(o1.getZdResult().getLastMe()) && !tailMes.contains(o2.getZdResult().getLastMe())){
				return -1;
			}
			else if(!tailMes.contains(o1.getZdResult().getLastMe()) && tailMes.contains(o2.getZdResult().getLastMe())){ 
				return 1 ; 
			}
			else if(tailMes.contains(o1.getZdResult().getLastMe()) && tailMes.contains(o2.getZdResult().getLastMe())){ 
				if( o1.getZdResult().getLastMe().equals(o2.getZdResult().getLastMe()) ){
					return o1.getZdResult().getSncid().compareTo(o2.getZdResult().getSncid());
				}
				else{
					return o1.getZdResult().getLastMe().compareTo(o2.getZdResult().getLastMe()) ; 
				}
				
			}
		}
		
		
		if(o1.getJump() == o2.getJump()){
			String o1sncid = o1.getZdResult().getSncid();
			String o2sncid = o2.getZdResult().getSncid();
			return o1sncid.compareTo(o2sncid);
		}
		
		return (int)(o1.getJump() - o2.getJump()) ; 
		
		}

}
