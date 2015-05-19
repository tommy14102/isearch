package com.zznode.opentnms.isearch.otnRouteService.api.webservice;

import javax.jws.WebService;

@WebService(endpointInterface = "com.zznode.opentnms.isearch.otnRouteService.api.webservice.HelloService")
public class HelloServiceImpl implements HelloService{

	public String say(String name) {
		System.out.println(123);
		return "adsf";
	}

}