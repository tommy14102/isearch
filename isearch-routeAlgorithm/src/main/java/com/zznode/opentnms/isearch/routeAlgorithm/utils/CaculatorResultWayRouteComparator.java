package com.zznode.opentnms.isearch.routeAlgorithm.utils;

import java.util.Comparator;

import com.zznode.opentnms.isearch.model.bo.DSR;
import com.zznode.opentnms.isearch.model.bo.OCH;
import com.zznode.opentnms.isearch.model.bo.ODU;
import com.zznode.opentnms.isearch.model.bo.ODU0;
import com.zznode.opentnms.isearch.model.bo.ODU1;
import com.zznode.opentnms.isearch.model.bo.ODU2;
import com.zznode.opentnms.isearch.model.bo.ODU3;
import com.zznode.opentnms.isearch.model.bo.ODU4;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;


public class CaculatorResultWayRouteComparator implements Comparator<Link> {


	public int compare(Link o1, Link o2) {
		
		ODU o1odu = o1.getZdResult().getOdu();
		ODU o2odu = o2.getZdResult().getOdu();
		
		int o1index = 0 ;
		int o2index = 0 ; 
		
		if( o1odu instanceof DSR){
			o1index = 0 ;
		}
		else if(o1odu instanceof ODU0){
			o1index = 1 ;
		}
		else if(o1odu instanceof ODU1){
			o1index = 2 ;
		}
		else if(o1odu instanceof ODU2){
		   o1index = 3 ;
		}
		else if(o1odu instanceof ODU3){
			o1index = 4 ;
		}
		else if(o1odu instanceof ODU4){
			o1index = 5 ;
		}
		else if(o1odu instanceof OCH){
			o1index = 6 ;
		}
		
		if( o2odu instanceof DSR){
			o2index = 0 ;
		}
		else if(o2odu instanceof ODU0){
			o2index = 1 ;
		}
		else if(o2odu instanceof ODU1){
			o2index = 2 ;
		}
		else if(o2odu instanceof ODU2){
			o2index = 3 ;
		}
		else if(o2odu instanceof ODU3){
			o2index = 4 ;
		}
		else if(o2odu instanceof ODU4){
			o2index = 5 ;
		}
		else if(o2odu instanceof OCH){
			o2index = 6 ;
		}
		
		
		 
		
		return o1index - o2index ;
		
	}


}
