package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import java.util.List;

public class CaculatorResult {

	private String resultCode ; 
	private String resultMessage ; 
	private Long timecost ; 
	private List<CaculatorResultWay> ways ;
	
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public Long getTimecost() {
		return timecost;
	}
	public void setTimecost(Long timecost) {
		this.timecost = timecost;
	}
	public List<CaculatorResultWay> getWays() {
		return ways;
	}
	public void setWays(List<CaculatorResultWay> ways) {
		this.ways = ways;
	}
	@Override
	public String toString() {
		return "CaculatorResult [resultCode=" + resultCode + ", resultMessage="
				+ resultMessage + ", timecost=" + timecost + ", ways=" + ways
				+ "]";
	} 

}
