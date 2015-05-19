package com.zznode.opentnms.isearch.otnRouteService.api;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebFault;
import javax.xml.ws.soap.SOAPFaultException;

@WebFault(faultBean = "RouteCalculationFault", name = "RouteCalculationException" )
public class RouteCalculationException extends Exception {
	private static final long serialVersionUID = -3607154010022389749L;
	private RouteCalculationFault fault;

	public RouteCalculationException() {
		super();
		this.fault = new RouteCalculationFault();
	}

	public RouteCalculationException(String message, RouteCalculationFault fault) {
		super(message);
		this.fault = fault;
	}

	public RouteCalculationException(RouteCalculationFault fault) {
		super();
		this.fault = fault;
	}
	
	public RouteCalculationException(String errorCode, String errorMessage) {
		super();
		this.fault = new RouteCalculationFault(errorCode, errorMessage);
	}

	public RouteCalculationException(String errorCode, String errorMessage , String errorDesc) {
		super();
		this.fault = new RouteCalculationFault(errorCode, errorMessage, errorDesc);
	}

	public RouteCalculationFault getFaultInfo() {
		return this.fault;
	}

	@Override
	public Throwable getCause() {
		SOAPFault faults;
		try {
			SOAPFactory face = SOAPFactory.newInstance();
			System.out.println(face.getClass());
			faults = face.createFault(); 
			faults.setFaultCode(this.getFaultInfo().getErrorCode());
			faults.setFaultString(this.getFaultInfo().toString());
			SOAPFaultException ex = new SOAPFaultException(faults);
			return ex;
		} catch (SOAPException e) {
			e.printStackTrace();
			return super.getCause();
		}
	}
}
