package com.zznode.opentnms.isearch.otnRouteService.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.ResponseWrapper;

import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationInput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationOutput;

@WebService
@SOAPBinding(style=Style.RPC)
@Component
public interface RouteCalculation {

	@WebMethod(operationName="calculate")
	@ResponseWrapper()
	public @WebResult(name="calculateResponse",partName="outputs") RouteCalculationOutput calculate( @WebParam(name="calculateRequest",partName="inputs") RouteCalculationInput input) throws RouteCalculationException;
	
}
