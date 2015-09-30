package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CaculatorResultWayRoute {

	private Integer routeseq ; 
	private String nodeid ; 
	private List<String> lefteageid = new ArrayList<String>(); 
	private List<String> righteageid = new ArrayList<String>(); 
	
	private LinkedList<Link> clientrouts = new LinkedList<Link>();
	private LinkedList<Link> odu0routs = new LinkedList<Link>();
	private LinkedList<Link> odu1routs = new LinkedList<Link>();
	private LinkedList<Link> odu2routs = new LinkedList<Link>();
	private LinkedList<Link> odu3routs = new LinkedList<Link>();
	private LinkedList<Link> odu4routs = new LinkedList<Link>();
	private LinkedList<Link> ochrouts = new LinkedList<Link>();
	
	private LinkedList<Link> passedallroutes = new LinkedList<Link>();
	private LinkedList<Link> notpassedallroutes = new LinkedList<Link>();
	
	private Map attrMap = new HashMap();
	
	
	
	public Map getAttrMap() {
		return attrMap;
	}

	public void setAttrMap(Map attrMap) {
		this.attrMap = attrMap;
	}
	public int getAllRouteCount(){
		return clientrouts.size() + odu0routs.size() + odu1routs.size() + odu2routs.size() + odu3routs.size() + odu4routs.size();
	}

	public LinkedList<Link> getRouteByType(int layer){
		if( layer==0){
			return clientrouts;
		}
		else if( layer==1){
			return odu0routs;
		}
		else if( layer==2){
			return odu1routs;
		}
		else if( layer==3){
			return odu2routs;
		}
		else if( layer==4){
			return odu3routs;
		}
		else if( layer==5){
			return odu4routs;
		}
		else if( layer==6){
			return ochrouts;
		}
		return null ;
	}
	
	public LinkedList<Link> getClientrouts() {
		return clientrouts;
	}
	public void setClientrouts(LinkedList<Link> clientrouts) {
		this.clientrouts = clientrouts;
	}
	public LinkedList<Link> getOdu0routs() {
		return odu0routs;
	}
	public void setOdu0routs(LinkedList<Link> odu0routs) {
		this.odu0routs = odu0routs;
	}
	public LinkedList<Link> getOdu1routs() {
		return odu1routs;
	}
	public void setOdu1routs(LinkedList<Link> odu1routs) {
		this.odu1routs = odu1routs;
	}
	public LinkedList<Link> getOdu2routs() {
		return odu2routs;
	}
	public void setOdu2routs(LinkedList<Link> odu2routs) {
		this.odu2routs = odu2routs;
	}
	public LinkedList<Link> getOdu3routs() {
		return odu3routs;
	}
	public void setOdu3routs(LinkedList<Link> odu3routs) {
		this.odu3routs = odu3routs;
	}
	public LinkedList<Link> getOdu4routs() {
		return odu4routs;
	}
	public void setOdu4routs(LinkedList<Link> odu4routs) {
		this.odu4routs = odu4routs;
	}
	public LinkedList<Link> getOchrouts() {
		return ochrouts;
	}
	public void setOchrouts(LinkedList<Link> ochrouts) {
		this.ochrouts = ochrouts;
	}
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
	public LinkedList<Link> getPassedallroutes() {
		return passedallroutes;
	}
	public void setPassedallroutes(LinkedList<Link> passedallroutes) {
		this.passedallroutes = passedallroutes;
	}
	public LinkedList<Link> getNotpassedallroutes() {
		return notpassedallroutes;
	}
	public void setNotpassedallroutes(LinkedList<Link> notpassedallroutes) {
		this.notpassedallroutes = notpassedallroutes;
	}

	@Override
	public String toString() {
		return "CaculatorResultWayRoute [routeseq=" + routeseq + ", nodeid="
				+ nodeid +  ", lefteageid=" + lefteageid
				+ ", righteageid=" + righteageid + "]";
	}
	 
	
	
	
	

}
