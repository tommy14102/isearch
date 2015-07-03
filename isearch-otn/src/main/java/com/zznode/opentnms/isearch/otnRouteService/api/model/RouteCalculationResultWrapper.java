package com.zznode.opentnms.isearch.otnRouteService.api.model;

import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = {"work", "work_reverse", "protect", "protect_reverse" })
public class RouteCalculationResultWrapper {

	private  RouteCalculationResult work ;
	private  RouteCalculationResult work_reverse ;
	private  RouteCalculationResult protect ;
	private  RouteCalculationResult protect_reverse ;
	
	public RouteCalculationResult getWork() {
		return work;
	}
	public void setWork(RouteCalculationResult work) {
		this.work = work;
	}
	public RouteCalculationResult getWork_reverse() {
		return work_reverse;
	}
	public void setWork_reverse(RouteCalculationResult work_reverse) {
		this.work_reverse = work_reverse;
	}
	public RouteCalculationResult getProtect() {
		return protect;
	}
	public void setProtect(RouteCalculationResult protect) {
		this.protect = protect;
	}
	public RouteCalculationResult getProtect_reverse() {
		return protect_reverse;
	}
	public void setProtect_reverse(RouteCalculationResult protect_reverse) {
		this.protect_reverse = protect_reverse;
	}

	

	
}
