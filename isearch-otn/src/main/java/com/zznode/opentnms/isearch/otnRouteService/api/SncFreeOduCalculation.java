package com.zznode.opentnms.isearch.otnRouteService.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.ResponseWrapper;

import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.otnRouteService.api.model.SncFreeOduInput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.SncFreeOduOutput;

@WebService
@SOAPBinding(style=Style.RPC)
@Component
public interface SncFreeOduCalculation {

	@WebMethod(operationName="sncFreeOdu")
	@ResponseWrapper()
	public @WebResult(name="SncFreeOduResponse",partName="outputs") SncFreeOduOutput calculate( @WebParam(name="SncFreeOduRequest",partName="inputs") SncFreeOduInput input) throws RouteCalculationException;
	
}
