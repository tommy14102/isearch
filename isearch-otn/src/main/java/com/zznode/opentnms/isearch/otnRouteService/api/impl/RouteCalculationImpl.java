package com.zznode.opentnms.isearch.otnRouteService.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

import com.zznode.opentnms.isearch.model.bo.ConstBusiness;
import com.zznode.opentnms.isearch.model.bo.ZdResult;
import com.zznode.opentnms.isearch.model.bo.ZdResultSingle;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculation;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationException;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteComparator;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteJumpedComparator;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationInput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationOutput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResult;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResultRoute;
import com.zznode.opentnms.isearch.otnRouteService.cache.CachedClient;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbMe;
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
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_dfs.DFS4Otn;
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_dijkstra.Dijkstra4OtnV2;

@WebService
@SOAPBinding(style=Style.RPC)
@Component
public class RouteCalculationImpl implements RouteCalculation{

	private static final Logger logger = LoggerFactory.getLogger(RouteCalculationImpl.class);   
	
	@Autowired
	public CachedClient cachedClient;
	
	@Autowired
	public ResourceManager resourceManager;
	
	private final String memcacTag =  PropertiesHander.getProperty("memcacTag");
	
	@WebMethod(operationName="calculate")
	@ResponseWrapper()
	public @WebResult(name="calculateResponse",partName="outputs") RouteCalculationOutput calculate( @WebParam(name="calculateRequest",partName="inputs") RouteCalculationInput input) throws RouteCalculationException{
			
			//0.参数校验
			if( input==null ){
				throw new RouteCalculationException("-1", "参数校验失败，参数值：null");
			}
			String checkinfo = input.checkmyself();
			if( !checkinfo.equals("") ){
				throw new RouteCalculationException("-2", "参数校验失败，" + checkinfo );
			}
			
			//1.查询两端网元，站点
			String aendzd = input.getAendzd();
			String zendzd = input.getZendzd();
			logger.info("参数A端站点："+ aendzd );
			logger.info("参数Z端站点："+ zendzd );
			
			String aendme = input.getAendme();
			String zendme = input.getZendme();
			logger.info("参数A端网元："+ aendme );
			logger.info("参数Z端网元："+ zendme );
			
			if(!StringUtils.isEmpty(aendme)){
				DbMe ame = resourceManager.getMeById(aendme);
				if(ame==null){
					throw new RouteCalculationException("-3", "没有找到网元，" + aendme );
				}
				aendzd = ame.getJuzhanobjectid(); 
				logger.info("查询得到A端站点："+ aendzd );
			}
			
			if(!StringUtils.isEmpty(zendme)){
				DbMe zme = resourceManager.getMeById(zendme);
				if(zme==null){
					throw new RouteCalculationException("-3", "没有找到网元，" + zendme );
				}
				zendzd = zme.getJuzhanobjectid(); 
				logger.info("查询得到Z端站点："+ zendzd );
			}
		
		try{	
			//2.查询站点间的直达路径
			RouteCalculationOutput output = findThroughpath(input, aendzd, zendzd, aendme, zendme);
			if( output !=null ){
				return output ; 
			}
		
			//3.查询非直达的最短路径
			if(PropertiesHander.getProperty("newRoutemode").equals("true")){
				output = findJumpedpathV2(input, aendzd, zendzd, aendme, zendme);
			}
			else{
				output = findJumpedpath(input, aendzd, zendzd, aendme, zendme);
			}
			
			if(output==null){
				output = new RouteCalculationOutput();
			}
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
		
		logger.info("查询直达路径" );
		String key = memcacTag + aendzd + "|" + zendzd ;
		Object obj = cachedClient.get(key);
		if( obj != null ){
			@SuppressWarnings("unchecked")
			List<ZdResult> resultlist = (List<ZdResult>)obj;
			
			logger.info("查询直达路径，缓存中结果:" + resultlist);
			
			//根据az端网元进行过滤
			filterZDResult(resultlist, aendme , zendme );
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据az端网元进行过滤后数量:" + resultlist.size());
			
			//根据速率进行过滤
			filterZDResultByRate(resultlist, input.getRate() );
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据az端网元进行过滤后数量:" + resultlist.size());
			
			//根据端口进行过滤
			filterZDResultByPtp(resultlist, input);
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据端口进行过滤后数量:" + resultlist.size());
			
			//根据时隙进行过滤
			filterZDResultByCtp(resultlist, input);
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据时隙进行过滤后数量:" + resultlist.size());
			
			
			List<RouteCalculationResult> routeCalculationResultlist = new ArrayList<RouteCalculationResult>();
			//组装所有的路由结构
			for (int i = 0; i < resultlist.size(); i++) {
				ZdResult zdResult = resultlist.get(i);
				Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
				Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
				
				RouteCalculationResult routeCalculationResult = new RouteCalculationResult(); 
				routeCalculationResult.setFreeOdu(zdResult.getODUinfo(input.getRate()));
				routeCalculationResult.setZdCount(zdmap.size());
				routeCalculationResult.setBusiCount(1);
				ArrayList<RouteCalculationResultRoute> routelist = new ArrayList<RouteCalculationResultRoute>();
				
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
			
			//对资源信息进行排序
			Collections.sort(routeCalculationResultlist, new RouteComparator());
			
			if(routeCalculationResultlist.size()>2){
				routeCalculationResultlist = routeCalculationResultlist.subList(0, 2);
			}
			
			//定义返回结果
			RouteCalculationOutput output = new RouteCalculationOutput();
			output.setRoutePlanobjectid( UuidUtil.randomUUID() );
			output.setRate(input.getRate());
			output.setProtectionType(input.getProtectionType());
			output.setRouteCalculationResult(routeCalculationResultlist);
			return output ; 
		}
		
		return null;
	}
	
	/**
	 * 根据时隙进行过滤
	 * @param resultlist
	 * @param input
	 */
	private void filterZDResultByCtp(List<ZdResult> resultlist, RouteCalculationInput input) {

		if( StringUtils.isEmpty(input.getAtimeSlots()) && StringUtils.isEmpty(input.getZtimeSlots()) ){
			return ; 
		}
		
		String aendctp = input.getAtimeSlots();
		String zendctp = input.getZtimeSlots();

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
			
			if( !StringUtils.isEmpty(aendctp) && !firstelement.getFirst().getAendctp().equals(aendctp)){
				zditerator.remove();
				logger.info("根据A端时隙过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
			
			if( !StringUtils.isEmpty(zendctp) && !lastelement.getLast().getZendctp().equals(zendctp)){
				zditerator.remove();
				logger.info("根据Z端时隙过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
		}
		
	}

	/**
	 * 根据端口进行过滤
	 * @param resultlist
	 * @param input
	 */
	private void filterZDResultByPtp(List<ZdResult> resultlist, RouteCalculationInput input) {
		
		if( StringUtils.isEmpty(input.getAendport()) && StringUtils.isEmpty(input.getZendport()) ){
			return ; 
		}
		
		String aendptp = input.getAendport();
		String zendptp = input.getZendport();

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
			
			if( !StringUtils.isEmpty(aendptp) && !firstelement.getFirst().getAendptpid().equals(aendptp)){
				zditerator.remove();
				logger.info("根据A端端口过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
			
			if( !StringUtils.isEmpty(zendptp) && !lastelement.getLast().getZendptpid().equals(zendptp)){
				zditerator.remove();
				logger.info("根据Z端端口过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
		}
		
	}

	/**
	 * 根据速率过滤
	 * @param resultlist
	 * @param rate
	 */
	private void filterZDResultByRate(List<ZdResult> resultlist, Integer rate) {

		int inputrateOrder = ConstBusiness.rateMap.get(rate).intValue();
		logger.info("根据速率过滤,请求速率:" + rate + ", inputrateOrder：" + inputrateOrder);
		
		for (Iterator<ZdResult> zditerator = resultlist.iterator(); zditerator.hasNext();) {
			ZdResult zdResult =  zditerator.next();
			
			int rateOrder = ConstBusiness.rateMap.get(Integer.valueOf(zdResult.getRate())).intValue();
			
			if( rateOrder < inputrateOrder ){
				zditerator.remove();
				logger.info("根据速率过滤:" + zdResult.getSncid() + "," + zdResult.getSncname() +",rateOrder: " + rateOrder);
				continue;
			}
			if( zdResult.getODUinfo(rate).equals("")){
				zditerator.remove();
				logger.info("根据资源过滤:" + zdResult);
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
			
			if( !StringUtils.isEmpty(aendmeobjectid) && !firstelement.getFirst().getAendmeid().equals(aendmeobjectid)){
				zditerator.remove();
				logger.info("根据A端网元过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
			
			if( !StringUtils.isEmpty(zendmeobjectid) && !lastelement.getLast().getZendmeid().equals(zendmeobjectid)){
				zditerator.remove();
				logger.info("根据Z端网元过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
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
		    
		CaculatorResult result = null;
		int zdcount = 3;
		do{
			
			CaculatorParam param = new CaculatorParam();
		    param.setAend(aendzd);
		    param.setZend(zendzd);
		    param.setCount(1);
		    param.setPolicy(Policy.LESS_JUMP);
		    param.setRouteCount(zdcount);
		    
		    result = isearch.search(businessAvator.getKey(), param, new DFS4Otn());
		    if( result.getWays().size()>0 ){
		    	break;
		    }
		    if( zdcount++ == 10){
		    	break;
		    }
		    
		}while(true);
		
		if( result ==null ){
			return null;
		}
		
		
		List<RouteCalculationResult> routeCalculationResultlist = new ArrayList<RouteCalculationResult>();
		for (int i = 0; i < result.getWays().size(); i++) {
			 LinkedList<CaculatorResultWayRoute> routelist = result.getWays().get(i).getRouts();
			 int j = 1 ;
			 while( j <= routelist.size()-2 ){
				 ArrayList<ArrayList<Integer>> seppointlist =  sepratedfind(routelist.size(), j);
				 ArrayList<RouteCalculationResult>  ways = sepfindWay(input ,seppointlist , routelist ,aendme , zendme);
				 if( ways.size()>0 ){
					 routeCalculationResultlist.addAll(ways);
					 break;
				 }
				 j++ ; 
			 }
		}
	    
		//对资源信息进行排序
		Collections.sort(routeCalculationResultlist, new RouteJumpedComparator());
		if(routeCalculationResultlist.size()>2){
			routeCalculationResultlist = routeCalculationResultlist.subList(0, 2);
		}
		
		RouteCalculationOutput ways = new RouteCalculationOutput();
	    ways.setRoutePlanobjectid( UuidUtil.randomUUID());
	    ways.setRate(input.getRate());
	    ways.setProtectionType(input.getProtectionType());
	    ways.setRouteCalculationResult(routeCalculationResultlist);
	    
	    return ways;
	}
	
	
	/**
	 * 非直达路径的搜索
	 * @param input
	 * @param aendzd
	 * @param zendzd
	 * @param aendme
	 * @param zendme
	 * @return
	 * @throws RouteCalculationException 
	 */
	private  RouteCalculationOutput findJumpedpathV2(RouteCalculationInput input , String aendzd , String zendzd , String aendme, String zendme) throws RouteCalculationException{
		
		logger.info("查询非直达路径" );
		
		ISearch isearch = new ISearch();
		
		BusinessAvator businessAvator = new BusinessAvator();
		businessAvator.setKey( PropertiesHander.getProperty("BusinessAvator") );
		    
		CaculatorParam param = new CaculatorParam();
	    param.setAend(aendzd);
	    param.setZend(zendzd);
	    param.setCount(2);
	    param.setPolicy(Policy.LESS_JUMP);
	    param.setAendme(aendme);
	    param.setZendme(zendme);
	    param.setRate(input.getRate());
	    param.getAttrMap().put("aendptp", input.getAendport());
	    param.getAttrMap().put("zendptp", input.getZendport());
	    param.getAttrMap().put("aendctp", input.getAtimeSlots());
	    param.getAttrMap().put("zendctp", input.getZtimeSlots());
	    
	    logger.info("开始查询最短路径" );
		CaculatorResult result = isearch.search(businessAvator.getKey(), param, new Dijkstra4OtnV2());
		logger.info("查询最短路径结果：" +  result);
		if( result ==null ){
			logger.info("查询最短路径无结果");
			return null;
		}
		if( result.getWays()==null || result.getWays().size()==0 ){
			logger.info("查询最短路径无结果");
			return null;
		}
		
		RouteCalculationResult mainRoute = new RouteCalculationResult();
		mainRoute.setZdCount(0);
		mainRoute.setBusiCount(0);
		RouteCalculationResult bakRoute = new RouteCalculationResult();
		bakRoute.setZdCount(0);
		bakRoute.setBusiCount(0);
		
		ArrayList<RouteCalculationResultRoute> mainRoutelist = new ArrayList<RouteCalculationResultRoute>();
		ArrayList<RouteCalculationResultRoute> bakRoutelist = new ArrayList<RouteCalculationResultRoute>();
		
		mainRoute.setRoute(mainRoutelist);
		bakRoute.setRoute(bakRoutelist);
		
		boolean hasBak = true ;
		
		CaculatorResultWay caculatorResultWay = result.getWays().get(0);
		LinkedList<CaculatorResultWayRoute> routelist = caculatorResultWay.getRouts();
		
		//合并选取结果
		for (int j = 0; j < routelist.size(); j++) {
			CaculatorResultWayRoute caculatorResultWayRoute = routelist.get(j);
			logger.info(" 处理路径结果，序号：" +j +",节点：" +caculatorResultWayRoute.getNodeid());
			Section section = (Section)caculatorResultWayRoute.getAttrMap().get("section");
			if( section==null ){
				if( j!= routelist.size()-1){
					logger.error("路由段缺少section信息");
					throw new RouteCalculationException("-3", "路由段缺少section信息"  );
				}
				continue ;
			}
			
			List<Link> links = section.getLinklist();
			Collections.sort(links, new Comparator<Link>() {

				public int compare(Link o1, Link o2) {
					return (int)(o1.getJump() - o2.getJump()) ;
				}
				
			});
			logger.info(" 处理路径结果，序号：" +j +",节点波道信息：" +links);
			
			if( links.size() == 0  ){
				return null;
			}
			else if( links.size() < 2  ){
				hasBak = false ;
			}
			
			String key = "OTNLink" + "|" + links.get(0).getLinkindex();
			ZdResult zdResult = (ZdResult)cachedClient.get(memcacTag +key);
			
			//ZdResult zdResult = (ZdResult)links.get(0).getAttrMap().get("ZdResultInfo");
			logger.info(" 处理路径结果，序号：" +j +",添加zdResult：" +zdResult);
			
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
					
					mainRoutelist.add(route);
				}
			}
			
			mainRoute.setZdCount( mainRoute.getZdCount() + zdmap.size() );
			mainRoute.setBusiCount( mainRoute.getBusiCount() + 1 );
			
			
			if(hasBak){

				String key1 = "OTNLink" + "|" + links.get(1).getLinkindex();
				ZdResult zdResultbak = (ZdResult)cachedClient.get(memcacTag +key1);
				
				//ZdResult zdResultbak = (ZdResult)links.get(1).getAttrMap().get("ZdResultInfo");
				logger.info(" 处理路径结果，备用，序号：" +j +",添加zdResultbak：" +zdResultbak );
				Map<String, LinkedList<ZdResultSingle>> zdmapbak =  zdResultbak.getZdmap();
				Collection<LinkedList<ZdResultSingle>> allzdbak =  zdmapbak.values();
				for (LinkedList<ZdResultSingle> linkedList : allzdbak) {
					for (int k = 0; k < linkedList.size(); k++) {
						ZdResultSingle zdResultSingle = linkedList.get(k);

						RouteCalculationResultRoute route = new RouteCalculationResultRoute(); 
						route.setAendmeobjectid( zdResultSingle.getAendmeid() );
						route.setAendptpobjectid( zdResultSingle.getAendptpid() );
						route.setAtimeSlots(zdResultSingle.getAendctp());
						route.setZendmeobjectid(zdResultSingle.getZendmeid());
						route.setZendptpobjectid(zdResultSingle.getZendptpid());
						route.setZtimeSlots(zdResultSingle.getZendctp());
						
						bakRoutelist.add(route);
					}
				}
				
				bakRoute.setZdCount( bakRoute.getZdCount() + zdmapbak.size() );
				bakRoute.setBusiCount( bakRoute.getBusiCount() + 1 );
				
			}
		}
		
		ArrayList<RouteCalculationResult> routeCalculationResultlist = new ArrayList<RouteCalculationResult>();
		routeCalculationResultlist.add(mainRoute);
		routeCalculationResultlist.add(bakRoute);
	    
		//对资源信息进行排序
		Collections.sort(routeCalculationResultlist, new RouteJumpedComparator());
		
		RouteCalculationOutput ways = new RouteCalculationOutput();
	    ways.setRoutePlanobjectid( UuidUtil.randomUUID());
	    ways.setRate(input.getRate());
	    ways.setProtectionType(input.getProtectionType());
	    ways.setRouteCalculationResult(routeCalculationResultlist);
	    
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
	private  ArrayList<RouteCalculationResult> sepfindWay(RouteCalculationInput input , ArrayList<ArrayList<Integer>> seppointlist, LinkedList<CaculatorResultWayRoute> routerlist , String aendme, String zendme) {
		
		ArrayList<RouteCalculationResult> routeCalculationResultlist = new ArrayList<RouteCalculationResult>();
		
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
			
			RouteCalculationResult mainRoute = new RouteCalculationResult();
			mainRoute.setZdCount(0);
			mainRoute.setBusiCount(0);
			RouteCalculationResult bakRoute = new RouteCalculationResult();
			bakRoute.setZdCount(0);
			bakRoute.setBusiCount(0);
			
			ArrayList<RouteCalculationResultRoute> mainRoutelist = new ArrayList<RouteCalculationResultRoute>();
			ArrayList<RouteCalculationResultRoute> bakRoutelist = new ArrayList<RouteCalculationResultRoute>();
			
			mainRoute.setRoute(mainRoutelist);
			bakRoute.setRoute(bakRoutelist);
			
			boolean hasBak = true ;
			
			//合并选取结果
			for (int j = 0; j < singeResult.size(); j++) {
				List<ZdResult> zdlist = singeResult.get(j);
				
				List<RouteCalculationResult> sectionRouteCalculationResultlist = new ArrayList<RouteCalculationResult>();
				for (int k1 = 0; k1 < zdlist.size(); k1++) {
					ZdResult zdResult = zdlist.get(k1);
					if(zdResult.getODUinfo(input.getRate()).equals("")){
						continue ;
					}
					
					ArrayList<RouteCalculationResultRoute> sectionRoutelist = new ArrayList<RouteCalculationResultRoute>();
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
							
							sectionRoutelist.add(route);
						}
					}
					
					RouteCalculationResult sectionRoute = new RouteCalculationResult(); 
					sectionRoute.setFreeOdu(zdResult.getODUinfo(input.getRate()));
					sectionRoute.setZdCount(zdmap.size());
					sectionRoute.setBusiCount(1);
					sectionRoute.setRoute(sectionRoutelist);
					sectionRouteCalculationResultlist.add(sectionRoute);
					
				}
				Collections.sort(sectionRouteCalculationResultlist, new RouteJumpedComparator());
				if(sectionRouteCalculationResultlist.size() == 0){
					continue outer;
				}else if(sectionRouteCalculationResultlist.size() < 2){
					hasBak = false;
				}else{
					sectionRouteCalculationResultlist = sectionRouteCalculationResultlist.subList(0, 2);
				}
				
				RouteCalculationResult main = sectionRouteCalculationResultlist.get(0);
				//mainRoute.setFreeOdu(freeOdu);
				mainRoute.setZdCount( mainRoute.getZdCount() + main.getZdCount());
				mainRoute.setBusiCount( mainRoute.getBusiCount() + main.getBusiCount());
				mainRoutelist.addAll(sectionRouteCalculationResultlist.get(0).getRoute());
				
				if(hasBak){
					RouteCalculationResult bak = sectionRouteCalculationResultlist.get(1);
					//mainRoute.setFreeOdu(freeOdu);
					bakRoute.setZdCount( bakRoute.getZdCount() + bak.getZdCount());
					bakRoute.setBusiCount( bakRoute.getBusiCount() + bak.getBusiCount());
					bakRoutelist.addAll(sectionRouteCalculationResultlist.get(0).getRoute());
				}
			}
			
			routeCalculationResultlist.add(mainRoute);
			routeCalculationResultlist.add(bakRoute);
		}
		
		return routeCalculationResultlist ; 
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