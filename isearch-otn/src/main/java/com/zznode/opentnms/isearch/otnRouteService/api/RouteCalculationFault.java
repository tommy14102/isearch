package com.zznode.opentnms.isearch.otnRouteService.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType( name = "RouteCalculationFault", propOrder = {"errorCode", "errorMessage", "errorDesc" })
public class RouteCalculationFault {
	private String errorCode;
	private String errorMessage;
	private String errorDesc;

	public RouteCalculationFault() {
		super();
	}

	public RouteCalculationFault(String errorCode, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public RouteCalculationFault(String errorCode, String errorMessage , String errorDesc) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.errorDesc = errorDesc;
	}


	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("errorCode: ").append(this.errorCode).append("/r/n ");
		buffer.append("errorMessage: ").append(this.errorMessage).append("/r/n ");
		buffer.append("errorDesc: ").append(this.errorDesc).append("/r/n ");
		return buffer.toString();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	
	
}
