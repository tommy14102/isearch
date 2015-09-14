package com.zznode.opentnms.isearch.otnRouteService.api.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = {"sncid","ochsncid", "freeOdu", "layerDesc","zdCountInSnc","direction","route" })
public class RouteCalculationResultRouteWrapper {

	private String freeOdu;
	private Integer zdCountInSnc;
	private String sncid;
	private String layerDesc;
	private Integer direction;
	private String ochsncid;
	
	private ArrayList<RouteCalculationResultRoute> route = new ArrayList<RouteCalculationResultRoute>() ;

	
	public String getOchsncid() {
		return ochsncid;
	}

	public void setOchsncid(String ochsncid) {
		this.ochsncid = ochsncid;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public Integer getZdCountInSnc() {
		return zdCountInSnc;
	}

	public void setZdCountInSnc(Integer zdCountInSnc) {
		this.zdCountInSnc = zdCountInSnc;
	}

	public String getLayerDesc() {
		return layerDesc;
	}

	public void setLayerDesc(String layerDesc) {
		this.layerDesc = layerDesc;
	}

	public String getSncid() {
		return sncid;
	}

	public void setSncid(String sncid) {
		this.sncid = sncid;
	}



	public String getFreeOdu() {
		return freeOdu;
	}

	public void setFreeOdu(String freeOdu) {
		this.freeOdu = freeOdu;
	}

	public ArrayList<RouteCalculationResultRoute> getRoute() {
		return route;
	}

	public void setRoute(ArrayList<RouteCalculationResultRoute> route) {
		this.route = route;
	}
	
	
	

	
}
