package com.zznode.opentnms.isearch.otnRouteService.api.impl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.ResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.model.bo.OCH;
import com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtplistCalculation;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationException;
import com.zznode.opentnms.isearch.otnRouteService.api.model.CardUsedPtpInput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.CardUsedPtpOutput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.SncFreeOduInput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.SncFreeOduOutput;
import com.zznode.opentnms.isearch.otnRouteService.cache.CachedClient;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSncAll;
import com.zznode.opentnms.isearch.otnRouteService.manage.ResourceManager;

@WebService
@SOAPBinding(style=Style.RPC)
@Component
public class CardUsedPtplistCalculationImpl implements CardUsedPtplistCalculation{

	private static final Logger logger = LoggerFactory.getLogger(CardUsedPtplistCalculationImpl.class);   
	
	@Autowired
	public ResourceManager resourceManager;

	@WebMethod(operationName = "cardUsedPtp")
	@ResponseWrapper
	public @WebResult(name = "CardUsedPtpResponse", partName = "outputs")
	CardUsedPtpOutput calculate(
			@WebParam(name = "CardUsedPtpRequest", partName = "inputs") CardUsedPtpInput input)
			throws RouteCalculationException {

		if( input==null ){
			throw new RouteCalculationException("-1", "参数校验失败，参数值：null");
		}
		if( !input.checkmyself() ){
			throw new RouteCalculationException("-2", "参数校验失败" );
		}
		
		logger.info("参数cardid："+ input.getCardobjectid() );
		
		CardUsedPtpOutput output = resourceManager.queryCardptpinfo(input.getCardobjectid());
				
		return output;
	}
	
	
	public static void main(String[] args) {
	}
	
}
