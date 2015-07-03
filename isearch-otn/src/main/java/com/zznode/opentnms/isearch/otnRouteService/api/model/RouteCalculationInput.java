package com.zznode.opentnms.isearch.otnRouteService.api.model;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.zznode.opentnms.isearch.model.bo.ConstBusiness;

public class RouteCalculationInput {

	private String circuitname;
	private String circuitType;
	private Integer rate;
	private String equipmentType;
	private String acity;
	private String aendstationname;
	private String aendbuildingname;
	private String aendme;
	private String aendport;
	private String atimeSlots;
	private String zcity;
	private String zendstationname;
	private String zendbuildingname;
	private String zendme;
	private String zendport;
	private String ztimeSlots;
	private String ProtectionType;
	private String additionalInfo;
	
	private RouteCalculationInputWrapper inclusionResource = new RouteCalculationInputWrapper();
	private RouteCalculationInputWrapper exclusionResource = new RouteCalculationInputWrapper();
	
	
	public String checkmyself(){
		if( StringUtils.isEmpty(aendstationname) && StringUtils.isEmpty(aendme) ){
			return "missing aend info.";
		}
		if( StringUtils.isEmpty(zendstationname) && StringUtils.isEmpty(zendme) ){
			return "missing zend info.";
		}
		if(rate==null||rate.intValue()==0){
			return "missing rate info.";
		}
		if( ConstBusiness.rateMap.get(rate)==null && ConstBusiness.odukMap.get(rate)==null){
			return "bad rate info.";
		}
		return "";
	}
	
	public String getCircuitname() {
		return circuitname;
	}
	public void setCircuitname(String circuitname) {
		this.circuitname = circuitname;
	}
	public String getCircuitType() {
		return circuitType;
	}
	public void setCircuitType(String circuitType) {
		this.circuitType = circuitType;
	}
	public Integer getRate() {
		return rate;
	}
	public void setRate(Integer rate) {
		this.rate = rate;
	}
	public String getEquipmentType() {
		return equipmentType;
	}
	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}
	public String getAcity() {
		return acity;
	}
	public void setAcity(String acity) {
		this.acity = acity;
	}
	public String getAendme() {
		return aendme;
	}
	public void setAendme(String aendme) {
		this.aendme = aendme;
	}
	public String getAendport() {
		return aendport;
	}
	public void setAendport(String aendport) {
		this.aendport = aendport;
	}
	public String getAtimeSlots() {
		return atimeSlots;
	}
	public void setAtimeSlots(String atimeSlots) {
		this.atimeSlots = atimeSlots;
	}
	public String getZcity() {
		return zcity;
	}
	public void setZcity(String zcity) {
		this.zcity = zcity;
	}
	public String getZendme() {
		return zendme;
	}
	public void setZendme(String zendme) {
		this.zendme = zendme;
	}
	public String getZendport() {
		return zendport;
	}
	public void setZendport(String zendport) {
		this.zendport = zendport;
	}
	public String getZtimeSlots() {
		return ztimeSlots;
	}
	public void setZtimeSlots(String ztimeSlots) {
		this.ztimeSlots = ztimeSlots;
	}
	public String getProtectionType() {
		return ProtectionType;
	}
	public void setProtectionType(String protectionType) {
		ProtectionType = protectionType;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getAendstationname() {
		return aendstationname;
	}

	public void setAendstationname(String aendstationname) {
		this.aendstationname = aendstationname;
	}

	public String getAendbuildingname() {
		return aendbuildingname;
	}

	public void setAendbuildingname(String aendbuildingname) {
		this.aendbuildingname = aendbuildingname;
	}

	public String getZendstationname() {
		return zendstationname;
	}

	public void setZendstationname(String zendstationname) {
		this.zendstationname = zendstationname;
	}

	public String getZendbuildingname() {
		return zendbuildingname;
	}

	public void setZendbuildingname(String zendbuildingname) {
		this.zendbuildingname = zendbuildingname;
	}

	public RouteCalculationInputWrapper getInclusionWrapper() {
		return inclusionResource;
	}

	public void setInclusionWrapper(RouteCalculationInputWrapper inclusionWrapper) {
		this.inclusionResource = inclusionWrapper;
	}

	public RouteCalculationInputWrapper getExclusionWrapper() {
		return exclusionResource;
	}

	public void setExclusionWrapper(RouteCalculationInputWrapper exclusionWrapper) {
		this.exclusionResource = exclusionWrapper;
	}

	
	

	
}
