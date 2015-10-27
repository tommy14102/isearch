package com.zznode.opentnms.isearch.otnRouteService.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.ResponseWrapper;

import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.otnRouteService.api.model.CardUsedPtpInput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.CardUsedPtpOutput;

@WebService
@SOAPBinding(style=Style.RPC)
@Component
public interface CardUsedPtplistCalculation {

	@WebMethod(operationName="cardUsedPtp")
	@ResponseWrapper()
	public @WebResult(name="CardUsedPtpResponse",partName="outputs") CardUsedPtpOutput calculate( @WebParam(name="CardUsedPtpRequest",partName="inputs") CardUsedPtpInput input) throws RouteCalculationException;
	
}
