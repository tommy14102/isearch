package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;


public class CaculatorParam {

	private String aend ; 
	private String zend ; 
	
	private Policy policy ; 
	private Integer count ;
	private Integer routeCount;
	
	private String aendme ; 
	private String zendme ;
	private Integer rate ;
	
	
	public Integer getRate() {
		return rate;
	}
	public void setRate(Integer rate) {
		this.rate = rate;
	}
	public String getAendme() {
		return aendme;
	}
	public void setAendme(String aendme) {
		this.aendme = aendme;
	}
	public String getZendme() {
		return zendme;
	}
	public void setZendme(String zendme) {
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
