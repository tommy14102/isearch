package com.zznode.opentnms.isearch.otnRouteService.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationInput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationOutput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResult;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResultRoute;
import com.zznode.opentnms.isearch.otnRouteService.cache.CachedClient;
import com.zznode.opentnms.isearch.otnRouteService.consts.ConstBusiness;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ZdResult;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ZdResultSingle;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbMe;
import com.zznode.opentnms.isearch.otnRouteService.manage.ResourceManager;
import com.zznode.opentnms.isearch.otnRouteService.service.BusiAnalyser;
import com.zznode.opentnms.isearch.otnRouteService.util.PropertiesHander;
import com.zznode.opentnms.isearch.otnRouteService.util.UuidUtil;
import com.zznode.opentnms.isearch.routeAlgorithm.api.ISearch;
import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.BusinessAvator;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResult;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWayRoute;
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_dijkstra.Dijkstra;

@WebService
@SOAPBinding(style=Style.RPC)
@Component
public class RouteCalculation {

	private static final Logger logger = LoggerFactory.getLogger(RouteCalculation.class);   
	
	@Autowired
	public CachedClient cachedClient;
	
	@Autowired
	public ResourceManager resourceManager;
	
	private final String memcacTag =  PropertiesHander.getProperty("memcacTag");
	
	@WebMethod(operationName="calculate")
	@ResponseWrapper()
	public @WebResult(name="calculateResponse",partName="outputs") RouteCalculationOutput calculate( @WebParam(name="calculateRequest",partName="inputs") RouteCalculationInput input) throws RouteCalculationException{
		
		try{
			
			//0.参数校验
			if( input==null ){
				throw new RouteCalculationException("-1", "参数校验失败，参数值：null");
			}
			String checkinfo = "";
			if( checkinfo.equals("") ){
				throw new RouteCalculationException("-2", "参数校验失败，" + checkinfo );
			}
			
			//1.查询两端网元，站点
			String aendzd = input.getAendzd();
			String zendzd = input.getZendzd();
			
			String aendme = input.getAendme();
			String zendme = input.getZendme();
			
			if(!StringUtils.isEmpty(aendme)){
				DbMe ame = resourceManager.getMeById(aendme);
				aendzd = ame.getJuzhanobjectid(); 
			}
			
			if(!StringUtils.isEmpty(zendme)){
				DbMe zme = resourceManager.getMeById(zendme);
				zendzd = zme.getJuzhanobjectid(); 
			}

			
			//2.查询站点间的直达路径
			RouteCalculationOutput output = findThroughpath(input, aendzd, zendzd, aendme, zendme);
			if( output !=null ){
				return output ; 
			}
		
			//3.查询非直达的最短路径
			output = findJumpedpath(input, aendzd, zendzd, aendme, zendme);
			
			return output;
			
		}
		catch (Exception e) {
			
			logger.error("路径计算处理失败",e);
			throw new RouteCalculationException("-1", "路径计算处理失败");
			
		}
		
	}
	
	/**
	 * 查询两个网元之间的直达路径
	 * @param input
	 * @param aendzd
	 * @param zendzd
	 * @param aendme
	 * @param zendme
	 * @return
	 */
	private RouteCalculationOutput findThroughpath(RouteCalculationInput input , String aendzd , String zendzd, String aendme, String zendme){
		String key = memcacTag + aendzd + "|" + zendzd ;
		Object obj = cachedClient.get(key);
		if( obj != null ){
			@SuppressWarnings("unchecked")
			List<ZdResult> resultlist = (List<ZdResult>)obj;
			
			//根据az端网元进行过滤
			filterZDResult(resultlist, aendme , zendme );
			if( resultlist.size() == 0 ){
				return null ; 
			}
			
			//根据速率进行过滤
			filterZDResultByRate(resultlist, input.getRate() );
			if( resultlist.size() == 0 ){
				return null ; 
			}
			
			//定义返回结果
			RouteCalculationOutput output = new RouteCalculationOutput();
			output.setRoutePlanobjectid( UuidUtil.randomUUID() );
			output.setRate(input.getRate());
			output.setProtectionType(input.getProtectionType());
			ArrayList<RouteCalculationResult> routeCalculationResultlist = new ArrayList<RouteCalculationResult>();
			output.setRouteCalculationResult(routeCalculationResultlist);
			
			//组装所有的路由结构
			for (int i = 0; i < resultlist.size(); i++) {
				ZdResult zdResult = resultlist.get(i);
				
				RouteCalculationResult routeCalculationResult = new RouteCalculationResult(); 
				routeCalculationResult.setFreeOdu(zdResult.getODUinfo());
				ArrayList<RouteCalculationResultRoute> routelist = new ArrayList<RouteCalculationResultRoute>();
				
				Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
				Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
				
				for (LinkedList<ZdResultSingle> linkedList : allzd) {
					for (int j = 0; j < linkedList.size(); j++) {
						ZdResultSingle zdResultSingle = linkedList.get(j);

						RouteCalculationResultRoute route = new RouteCalculationResultRoute(); 
						route.setAendmeobjectid( zdResultSingle.getAendmeid() );
						route.setAendptpobjectid( zdResultSingle.getAendptpid() );
						route.setAtimeSlots(zdResultSingle.getAendctp());
						route.setZendmeobjectid(zdResultSingle.getZendmeid());
						route.setZendptpobjectid(zdResultSingle.getZendptpid());
						route.setZtimeSlots(zdResultSingle.getZendctp());
						
						routelist.add(route);
						
					}
				}
				routeCalculationResult.setRoute(routelist);
				routeCalculationResultlist.add(routeCalculationResult);
			}
			return output ; 
		}
		
		return null;
	}
	
	/**
	 * 根据速率过滤
	 * @param resultlist
	 * @param rate
	 */
	private void filterZDResultByRate(List<ZdResult> resultlist, Integer rate) {

		
		for (Iterator<ZdResult> zditerator = resultlist.iterator(); zditerator.hasNext();) {
			ZdResult zdResult =  zditerator.next();
			
			int inputrateOrder = ConstBusiness.rateMap.get(rate).intValue();
			int rateOrder = ConstBusiness.rateMap.get(Integer.valueOf(zdResult.getRate())).intValue();
			
			if( rateOrder < inputrateOrder ){
				zditerator.remove();
				continue;
			}
			if(!zdResult.getODUinfo().equals("")){
				zditerator.remove();
				continue;
			}
		}
		
	}

	/**
	 * 对两个站点间的所有业务路径，按az网元进行过滤。
	 * @param resultlist
	 * @param aendmeobjectid
	 * @param zendmeobjectid
	 */
	private void filterZDResult( List<ZdResult> resultlist , String aendmeobjectid, String zendmeobjectid ){
		
		for (Iterator<ZdResult> zditerator = resultlist.iterator(); zditerator.hasNext();) {
			ZdResult zdResult =  zditerator.next();
			
			Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
			
			//判断是否经过az网元
			Iterator<LinkedList<ZdResultSingle>>  iter = allzd.iterator();
			LinkedList<ZdResultSingle> firstelement = iter.next();
			
			LinkedList<ZdResultSingle> lastelement = firstelement;
			while(iter.hasNext()){
				lastelement = iter.next();
			}
			
			if( aendmeobjectid!=null && !firstelement.getFirst().getAendmeid().equals(aendmeobjectid)){
				zditerator.remove();
				continue;
			}
			
			if( zendmeobjectid!=null && !lastelement.getLast().getZendmeid().equals(zendmeobjectid)){
				zditerator.remove();
				continue;
			}
		}
	}
	
	/**
	 * 非直达路径的搜索
	 * @param input
	 * @param aendzd
	 * @param zendzd
	 * @param aendme
	 * @param zendme
	 * @return
	 */
	private  RouteCalculationOutput findJumpedpath(RouteCalculationInput input , String aendzd , String zendzd , String aendme, String zendme){
		
		ISearch isearch = new ISearch();
		
		BusinessAvator businessAvator = new BusinessAvator();
		businessAvator.setKey( PropertiesHander.getProperty("BusinessAvator") );
		    
		CaculatorParam param = new CaculatorParam();
	    param.setAend(aendzd);
	    param.setZend(zendzd);
	    param.setCount(1);
	    param.setPolicy(Policy.LESS_JUMP);
	    
	    CaculatorResult result = isearch.search(businessAvator.getKey(), param, new Dijkstra());
	    LinkedList<CaculatorResultWayRoute> routelist = result.getWays().get(0).getRouts();
	    
	    RouteCalculationOutput ways = new RouteCalculationOutput();
	    int i = 1 ;
	    while( i <= routelist.size()-2 ){
	    	ArrayList<ArrayList<Integer>> seppointlist =  sepratedfind(routelist.size(), i);
	    	ways = sepfindWay(seppointlist , routelist ,aendme , zendme);
	    	if( ways.getRouteCalculationResult().size() > 0  ){
	    		break;
	    	}
	    	i++ ; 
	    }
	    
	    ways.setRoutePlanobjectid( UuidUtil.randomUUID());
	    ways.setRate(input.getRate());
	    ways.setProtectionType(input.getProtectionType());
	    
	    return ways;
	}
	
	/**
	 * 将站点分段后需找路径
	 * @param seppointlist:切割点列表
	 * @param routerlist
	 * @param aendme
	 * @param zendme
	 * @return
	 */
	private  RouteCalculationOutput sepfindWay(ArrayList<ArrayList<Integer>> seppointlist, LinkedList<CaculatorResultWayRoute> routerlist , String aendme, String zendme) {
		
		RouteCalculationOutput output = new RouteCalculationOutput();
		ArrayList<RouteCalculationResult> routeCalculationResultlist = new ArrayList<RouteCalculationResult>();
		output.setRouteCalculationResult(routeCalculationResultlist);
		
		outer: for (int i = 0; i < seppointlist.size(); i++) {
			ArrayList<Integer> singlepointcutlist =  seppointlist.get(i);
			
			LinkedList<List<ZdResult>> singeResult = new LinkedList<List<ZdResult>>();
			
			int startindex = 0;
			while( !singlepointcutlist.isEmpty()){
				int index = singlepointcutlist.remove(0);
				String key = memcacTag + routerlist.get(startindex).getNodeid() + "|" + routerlist.get(index).getNodeid(); 
				Object obj = cachedClient.get(key);
				if( obj != null ){
					List<ZdResult> resultlist = (List<ZdResult>)obj;
					singeResult.add(resultlist);
				}else{
					continue outer;
				}
				startindex = index ; 
				
			}
			
			int endindex = routerlist.size()-1;
			String endkey = memcacTag + routerlist.get(startindex).getNodeid() + "|" + routerlist.get(endindex).getNodeid(); 
			Object obj = cachedClient.get(endkey);
			if( obj != null ){
				List<ZdResult> resultlist = (List<ZdResult>)obj;
				singeResult.add(resultlist);
			}else{
				continue outer;
			}
			
			//筛选a端网元。并按最优排序？
			List<ZdResult> firstsection =  singeResult.getFirst();
			filterZDResult(firstsection, aendme, null);
			
			//筛选z端网元。并按最优排序？
			List<ZdResult> lastsection =  singeResult.getLast();
			filterZDResult(lastsection, null ,zendme);
			
			//合并选取结果
			for (int j = 0; j < singeResult.size(); j++) {
				List<ZdResult> zdlist = singeResult.get(j);
				ZdResult zdResult = zdlist.get(0);
				if(zdResult.getODUinfo().equals("")){
					continue outer;
				}
				
				RouteCalculationResult routeCalculationResult = new RouteCalculationResult(); 
				routeCalculationResult.setFreeOdu(zdResult.getODUinfo());
				ArrayList<RouteCalculationResultRoute> routelist = new ArrayList<RouteCalculationResultRoute>();
				Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
				Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
				
				for (LinkedList<ZdResultSingle> linkedList : allzd) {
					for (int k = 0; k < linkedList.size(); k++) {
						ZdResultSingle zdResultSingle = linkedList.get(k);

						RouteCalculationResultRoute route = new RouteCalculationResultRoute(); 
						route.setAendmeobjectid( zdResultSingle.getAendmeid() );
						route.setAendptpobjectid( zdResultSingle.getAendptpid() );
						route.setAtimeSlots(zdResultSingle.getAendctp());
						route.setZendmeobjectid(zdResultSingle.getZendmeid());
						route.setZendptpobjectid(zdResultSingle.getZendptpid());
						route.setZtimeSlots(zdResultSingle.getZendctp());
						
						routelist.add(route);
					}
				}
				routeCalculationResult.setRoute(routelist);
				routeCalculationResultlist.add(routeCalculationResult);
			}
		}
		
		return output ; 
	}

	/**
	 * 将一段路由多个站点，按sepratecount切割
	 * 比如total=5，sepratecount=1
	 * 那么返回结果为[2],[3],[4]
	 * 如果total=5，sepratecount=2
	 * 那么返回结果为[2,3][2,4][3,4]
	 * 
	 * @param totalcount
	 * @param sepratecount
	 * @return
	 */
	private static ArrayList<ArrayList<Integer>>  sepratedfind(int totalcount , int sepratecount ){
		
		ArrayList<ArrayList<Integer>> rtnlist = new ArrayList<ArrayList<Integer>>();
		
		int counts = totalcount -2;
		
		ArrayList<Integer[]> totallist = new ArrayList<Integer[]>();
		for (int i = 0; i < sepratecount; i++) {
			Integer[] att = new Integer[counts];
			for (int j = 1; j <=counts ; j++) {
				att[j-1] = j ;
			}
			totallist.add(att);
		}
		
		doit(totallist, 0, rtnlist, -1,new ArrayList<Integer>());
		
		return rtnlist;
		
	}
	
	private static void doit(ArrayList<Integer[]> totallist, int index , ArrayList<ArrayList<Integer>> finalRssultlist,int beforeElemenvale ,ArrayList<Integer> beforpointlist){
		
		Integer[] sw =  totallist.get(index);
		for (int i = 0; i < sw.length; i++) {
			int first = sw[i];
			if( first <= beforeElemenvale ){
				continue;
			}
			
			if(totallist.size()>index+1){
				
				beforpointlist.add(first);
				doit( totallist, index+1, finalRssultlist , first, beforpointlist);
				
			}else{
				ArrayList<Integer> yui = new ArrayList<Integer>();
				yui.addAll(beforpointlist);
				yui.add(first);
				finalRssultlist.add(yui);
			}
		}
		if( beforpointlist.size()-1>=0){
			beforpointlist.remove(beforpointlist.size()-1);
		}
	}
	
}