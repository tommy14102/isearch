package com.zznode.opentnms.isearch.otnRouteService.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.ResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.model.bo.ConstBusiness;
import com.zznode.opentnms.isearch.model.bo.OCH;
import com.zznode.opentnms.isearch.model.bo.ODU;
import com.zznode.opentnms.isearch.model.bo.ODU0;
import com.zznode.opentnms.isearch.model.bo.ODU1;
import com.zznode.opentnms.isearch.model.bo.ODU2;
import com.zznode.opentnms.isearch.model.bo.ODU3;
import com.zznode.opentnms.isearch.model.bo.ODU4;
import com.zznode.opentnms.isearch.model.bo.ZdResult;
import com.zznode.opentnms.isearch.model.bo.ZdResultSingle;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationException;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteComparator;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteJumpedComparator;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteJumpedComparatorSingle;
import com.zznode.opentnms.isearch.otnRouteService.api.SncFreeOduCalculation;
import com.zznode.opentnms.isearch.otnRouteService.api.ZdResultComparator;
import com.zznode.opentnms.isearch.otnRouteService.api.model.ClusionBean;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationInput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationOutput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResult;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResultRoute;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResultRouteWrapper;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResultWrapper;
import com.zznode.opentnms.isearch.otnRouteService.api.model.SncFreeOduInput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.SncFreeOduOutput;
import com.zznode.opentnms.isearch.otnRouteService.cache.CachedClient;
import com.zznode.opentnms.isearch.otnRouteService.consts.enums.Rate;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbMe;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSncAll;
import com.zznode.opentnms.isearch.otnRouteService.db.po.ZhiluPtp;
import com.zznode.opentnms.isearch.otnRouteService.manage.CucirtManager;
import com.zznode.opentnms.isearch.otnRouteService.manage.ResourceManager;
import com.zznode.opentnms.isearch.otnRouteService.util.PropertiesHander;
import com.zznode.opentnms.isearch.otnRouteService.util.UuidUtil;
import com.zznode.opentnms.isearch.routeAlgorithm.api.ISearch;
import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.BusinessAvator;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResult;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWay;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWayRoute;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.otn.ExCluseBean;
import com.zznode.opentnms.isearch.routeAlgorithm.core.matrix.Matrix;
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_dijkstra.Dijkstra4OtnV2;

@WebService
@SOAPBinding(style=Style.RPC)
@Component
public class SncFreeOduCalculationImpl implements SncFreeOduCalculation{

	private static final Logger logger = LoggerFactory.getLogger(SncFreeOduCalculationImpl.class);   
	
	@Autowired
	public CachedClient cachedClient;
	
	@Autowired
	public ResourceManager resourceManager;
	
	
	@WebMethod(operationName="sncFreeOdu")
	@ResponseWrapper()
	public @WebResult(name="SncFreeOduResponse",partName="outputs") SncFreeOduOutput calculate( @WebParam(name="SncFreeOduRequest",partName="inputs") SncFreeOduInput input) throws RouteCalculationException
	{
			
			if( input==null ){
				throw new RouteCalculationException("-1", "参数校验失败，参数值：null");
			}
			if( !input.checkmyself() ){
				throw new RouteCalculationException("-2", "参数校验失败" );
			}
			
			logger.info("参数sncid："+ input.getSncid() );
			
			//1.查询两端站点
			String zhandianStr = resourceManager.queryZhandianBySncid(input.getSncid());
			if( zhandianStr==null ){
				throw new RouteCalculationException("-3", "没有查询到两端站点信息" );
			}
			
			String[] zds = zhandianStr.split("-");
			if( zds.length != 2 ){
				throw new RouteCalculationException("-4", "没有查询到两端站点信息" );
			}
			
			DbWdmSncAll wdmsnc = (DbWdmSncAll)cachedClient.get("OCH-RESOURCE-"+ input.getSncid());
			OCH och = (OCH)wdmsnc.getOdu();
			String freeodu0Str = "";
			if( och.getOdu4list().size()>0){
				freeodu0Str = och.getOdu4list().get(0).getFreeODU(87);
			}
			if( och.getOdu2list().size()>0){
				freeodu0Str = och.getOdu2list().get(0).getFreeODU(87);
			}
			if( och.getOdu1list().size()>0){
				freeodu0Str = och.getOdu1list().get(0).getFreeODU(87);
			}
			if( och.getOdu1list().size()>0){
				freeodu0Str = och.getOdu1list().get(0).getFreeODU(87);
			}
			
			
			SncFreeOduOutput output = new SncFreeOduOutput();
			output.setSncid(input.getSncid());
			output.setFreeOdu0(freeodu0Str);
			return output;
			
		
	}
	
	public static void main(String[] args) {
	}
	
}
