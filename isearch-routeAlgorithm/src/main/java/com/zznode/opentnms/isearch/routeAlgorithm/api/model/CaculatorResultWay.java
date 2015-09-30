package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CaculatorResultWay {

	private Integer Wayseq ; 
	private Integer routeCount ; 
	private LinkedList<CaculatorResultWayRoute>  routs = new LinkedList<CaculatorResultWayRoute>();
	
	private List<Integer> passbjindex = new ArrayList<Integer>();
	private boolean passedOK = true; 
	
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
	public List<Integer> getPassbjindex() {
		return passbjindex;
	}
	public void setPassbjindex(List<Integer> passbjindex) {
		this.passbjindex = passbjindex;
	}
	
	
	public boolean isPassedOK() {
		return passedOK;
	}
	public void setPassedOK(boolean passedOK) {
		this.passedOK = passedOK;
	}
	@Override
	public String toString() {
		return "CaculatorResultWay [Wayseq=" + Wayseq + ", routeCount="
				+ routeCount + ", routs=" + routs + "]";
	}
	

}
