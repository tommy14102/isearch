package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;


public class CaculatorParam {

	private String aend ; 
	private String zend ; 
	
	private Policy policy ; 
	private Integer count ;
	private Integer routeCount;
	
	private List<String> aendme ; 
	private List<String> zendme ;
	private Integer rate ;
	protected Map attrMap = new HashMap();
	
	
	public Map getAttrMap() {
		return attrMap;
	}
	public void setAttrMap(Map attrMap) {
		this.attrMap = attrMap;
	}
	public Integer getRate() {
		return rate;
	}
	public void setRate(Integer rate) {
		this.rate = rate;
	}
	public List<String> getAendme() {
		return aendme;
	}
	public void setAendme(List<String> aendme) {
		this.aendme = aendme;
	}
	public List<String> getZendme() {
		return zendme;
	}
	public void setZendme(List<String> zendme) {
		this.zendme = zendme;
	}
	public Integer getRouteCount() {
		return routeCount;
	}
	public void setRouteCount(Integer routeCount) {
		this.routeCount = routeCount;
	}
	public String getAend() {
		return aend;
	}
	public void setAend(String aend) {
		this.aend = aend;
	}
	public String getZend() {
		return zend;
	}
	public void setZend(String zend) {
		this.zend = zend;
	}
	public Policy getPolicy() {
		return policy;
	}
	public void setPolicy(Policy policy) {
		this.policy = policy;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	} 
	

	
}
