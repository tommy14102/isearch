package com.zznode.opentnms.isearch.otnRouteService.api.impl;

import java.util.ArrayList;

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

import com.zznode.opentnms.isearch.otnRouteService.api.ResourceRelease;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationException;
import com.zznode.opentnms.isearch.otnRouteService.cache.CachedClient;

@WebService
@SOAPBinding(style=Style.RPC)
@Component
public class ResourceReleaseImpl implements ResourceRelease{

	private static final Logger logger = LoggerFactory.getLogger(ResourceReleaseImpl.class);   
	
	@Autowired
	public CachedClient cachedClient;
	
	
	@WebMethod(operationName="resourceRelease")
	@ResponseWrapper()
	public @WebResult(name="ReleaseResponse",partName="outputs") String resourceRelease( @WebParam(name="ReleaseRequest",partName="inputs") String id) throws RouteCalculationException
	{
			
			if( id==null ){
				throw new RouteCalculationException("-1", "参数校验失败，参数值：null");
			}
			 
			logger.info("参数id："+ id );
			
			
			String state = (String)cachedClient.get(id);
			if(state==null){
				throw new RouteCalculationException("-2", "id未找到");
			}
			
			int exptime = 24*60*60;
			
			try {
				
			if( !state.equals("1") ){
				cachedClient.set( id, exptime, "0" );
			}
			
			ArrayList<String> snclist = (ArrayList<String>)cachedClient.get( id+"|sncs"  );
			for (int i = 0; i < snclist.size(); i++) {
				String sncid = snclist.get(i);
				cachedClient.set( sncid+"|state", exptime, "0" );
			}
			
			ArrayList<String> zlptplist = (ArrayList<String>)cachedClient.get( id+"|zlptps"  );
			for (int i = 0; i < zlptplist.size(); i++) {
				String ptpid = zlptplist.get(i);
				cachedClient.set( ptpid+"|state", exptime, "0" );
			}
			} catch ( Exception e) {
				
				logger.error("error", e);
				
				return "释放出现异常";
			}  
			
			
			return "释放完成";
			
		
	}
	
	public static void main(String[] args) {
	}
	
}
