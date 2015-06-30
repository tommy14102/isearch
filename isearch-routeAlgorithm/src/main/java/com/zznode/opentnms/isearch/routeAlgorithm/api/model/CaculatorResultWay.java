package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CaculatorResultWay {

	private Integer Wayseq ; 
	private Integer routeCount ; 
	private LinkedList<CaculatorResultWayRoute>  routs = new LinkedList<CaculatorResultWayRoute>();
	
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
