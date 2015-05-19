package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;


public class CaculatorParam {

	private String aend ; 
	private String zend ; 
	
	private Policy policy ; 
	private Integer count ;
	
	
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
