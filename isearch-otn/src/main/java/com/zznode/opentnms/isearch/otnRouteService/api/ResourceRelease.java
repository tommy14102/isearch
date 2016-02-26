package com.zznode.opentnms.isearch.otnRouteService.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.ResponseWrapper;

import org.springframework.stereotype.Component;

@WebService
@SOAPBinding(style=Style.RPC)
@Component
public interface ResourceRelease {

	@WebMethod(operationName="resourceRelease")
	@ResponseWrapper()
	public @WebResult(name="ReleaseResponse",partName="outputs") String resourceRelease( @WebParam(name="ReleaseRequest",partName="inputs") String id) throws RouteCalculationException;
	
}
