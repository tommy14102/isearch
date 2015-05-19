package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import java.util.ArrayList;
import java.util.List;

public class CaculatorResultWayRoute {

	private Integer routeseq ; 
	private String nodeid ; 
	private List<String> lefteageid = new ArrayList<String>(); 
	private List<String> righteageid = new ArrayList<String>(); 
	
	
	public Integer getRouteseq() {
		return routeseq;
	}
	public void setRouteseq(Integer routeseq) {
		this.routeseq = routeseq;
	}
	public String getNodeid() {
		return nodeid;
	}
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	public List<String> getLefteageid() {
		return lefteageid;
	}
	public void setLefteageid(List<String> lefteageid) {
		this.lefteageid = lefteageid;
	}
	public List<String> getRighteageid() {
		return righteageid;
	}
	public void setRighteageid(List<String> righteageid) {
		this.righteageid = righteageid;
	}
	@Override
	public String toString() {
		return "CaculatorResultWayRoute [routeseq=" + routeseq + ", nodeid="
				+ nodeid +  ", lefteageid=" + lefteageid
				+ ", righteageid=" + righteageid + "]";
	}
	 
	
	
	
	

}
