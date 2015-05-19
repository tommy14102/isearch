package com.zznode.opentnms.isearch.otnRouteService.api.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface HelloService {

	@WebMethod
    String say(String name);
}