package com.zznode.opentnms.isearch.otnRouteService.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = {"usedPtplist","unusedPtplist"})
public class CardUsedPtpOutput {

	private List<CardUsedPtpInfo> usedPtplist = new ArrayList<CardUsedPtpInfo>();
	private List<CardUsedPtpInfo> unusedPtplist = new ArrayList<CardUsedPtpInfo>();
	
	public List<CardUsedPtpInfo> getUsedPtplist() {
		return usedPtplist;
	}
	public void setUsedPtplist(List<CardUsedPtpInfo> usedPtplist) {
		this.usedPtplist = usedPtplist;
	}
	public List<CardUsedPtpInfo> getUnusedPtplist() {
		return unusedPtplist;
	}
	public void setUnusedPtplist(List<CardUsedPtpInfo> unusedPtplist) {
		this.unusedPtplist = unusedPtplist;
	}
		
}
