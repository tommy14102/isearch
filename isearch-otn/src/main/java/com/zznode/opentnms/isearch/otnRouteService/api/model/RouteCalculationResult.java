package com.zznode.opentnms.isearch.otnRouteService.api.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = {"zdCount", "busiCount", "routeCalculationResultRouteWrapper" })
public class RouteCalculationResult {

	private Integer zdCount;
	private Integer busiCount;
	
	private ArrayList<RouteCalculationResultRouteWrapper> routeCalculationResultRouteWrapper = new ArrayList<RouteCalculationResultRouteWrapper>() ;


	public Integer getZdCount() {
		return zdCount;
	}

	public void setZdCount(Integer zdCount) {
		this.zdCount = zdCount;
	}

	public Integer getBusiCount() {
		return busiCount;
	}

	public void setBusiCount(Integer busiCount) {
		this.busiCount = busiCount;
	}

	public ArrayList<RouteCalculationResultRouteWrapper> getRouteCalculationResultRouteWrapper() {
		return routeCalculationResultRouteWrapper;
	}

	public void setRouteCalculationResultRouteWrapper(
			ArrayList<RouteCalculationResultRouteWrapper> routeCalculationResultRouteWrapper) {
		this.routeCalculationResultRouteWrapper = routeCalculationResultRouteWrapper;
	}


	
}
