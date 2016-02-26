package com.zznode.opentnms.isearch.otnRouteService.api.model;

import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = {"work", "work_reverse" })
public class RouteCalculationResultWrapper {

	private  String resultid;
	private  RouteCalculationResult work ;
	private  RouteCalculationResult work_reverse ;
	
	public String getResultid() {
		return resultid;
	}
	public void setResultid(String resultid) {
		this.resultid = resultid;
	}
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

	

	
}
