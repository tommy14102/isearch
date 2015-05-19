package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import java.util.LinkedList;

public class CaculatorResultWay {

	private Integer Wayseq ; 
	private Integer routeCount ; 
	private LinkedList<CaculatorResultWayRoute> routs ;
	
	public Integer getWayseq() {
		return Wayseq;
	}
	public void setWayseq(Integer wayseq) {
		Wayseq = wayseq;
	}
	public Integer getRouteCount() {
		return routeCount;
	}
	public void setRouteCount(Integer routeCount) {
		this.routeCount = routeCount;
	}
	
	public LinkedList<CaculatorResultWayRoute> getRouts() {
		return routs;
	}
	public void setRouts(LinkedList<CaculatorResultWayRoute> routs) {
		this.routs = routs;
	}
	@Override
	public String toString() {
		return "CaculatorResultWay [Wayseq=" + Wayseq + ", routeCount="
				+ routeCount + ", routs=" + routs + "]";
	} 
	
	

}
