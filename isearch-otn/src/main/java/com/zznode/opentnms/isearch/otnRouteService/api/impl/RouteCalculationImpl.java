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

import com.thoughtworks.xstream.XStream;
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
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculation;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationException;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteComparator;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteJumpedComparator;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteJumpedComparatorSingle;
import com.zznode.opentnms.isearch.otnRouteService.api.ZdResultComparator;
import com.zznode.opentnms.isearch.otnRouteService.api.model.ClusionBean;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationInput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationOutput;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResult;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResultRoute;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResultRouteWrapper;
import com.zznode.opentnms.isearch.otnRouteService.api.model.RouteCalculationResultWrapper;
import com.zznode.opentnms.isearch.otnRouteService.cache.CachedClient;
import com.zznode.opentnms.isearch.otnRouteService.consts.enums.Rate;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbMe;
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
public class RouteCalculationImpl implements RouteCalculation{

	private static final Logger logger = LoggerFactory.getLogger(RouteCalculationImpl.class);   
	
	@Autowired
	public CachedClient cachedClient;
	
	@Autowired
	public ResourceManager resourceManager;
	
	@Autowired
	public CucirtManager cucirtManager;
	
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
			
			logger.info("参数rate："+ input.getRate() );
			
			//1.查询两端网元，站点
			String aendzd = input.getAendstationname();
			String zendzd = input.getZendstationname();
			logger.info("参数A端站点："+ aendzd );
			logger.info("参数Z端站点："+ zendzd );
			
			String aendme = input.getAendme();
			String zendme = input.getZendme();
			logger.info("参数A端网元："+ aendme + ",ptp:" + input.getAendport()+",ctp:"+input.getAtimeSlots() );
			logger.info("参数Z端网元："+ zendme + ",ptp:" + input.getZendport()+",ctp:"+input.getZtimeSlots() );
			
			List<String> aednmelist = new ArrayList<String>();
			List<String> zednmelist = new ArrayList<String>();
			
			if(!StringUtils.isEmpty(aendme)){
				DbMe ame = resourceManager.getMeById(aendme);
				if(ame==null){
					throw new RouteCalculationException("-3", "没有找到网元，" + aendme );
				}
				aendzd = ame.getJuzhanobjectid(); 
				logger.info("查询得到A端站点："+ aendzd );
				if( !StringUtils.isEmpty(ame.getParentmeobjectid()) ){
					aednmelist.add(aendme);
				}else{
					//查询小网元
					List<DbMe> melist = resourceManager.getChildMeById(aendme);
					for (int i = 0; i < melist.size(); i++) {
						DbMe me = melist.get(i);
						aednmelist.add(me.getObjectId());
					}
				}
			}
			
			if(!StringUtils.isEmpty(zendme)){
				DbMe zme = resourceManager.getMeById(zendme);
				if(zme==null){
					throw new RouteCalculationException("-3", "没有找到网元，" + zendme );
				}
				zendzd = zme.getJuzhanobjectid(); 
				logger.info("查询得到Z端站点："+ zendzd );
				if( !StringUtils.isEmpty(zme.getParentmeobjectid()) ){
					zednmelist.add(zendme);
				}else{
					//查询小网元
					List<DbMe> melist = resourceManager.getChildMeById(zendme);
					for (int i = 0; i < melist.size(); i++) {
						DbMe me = melist.get(i);
						zednmelist.add(me.getObjectId());
					}
				}
			}
			
		
		try{	
			
			//定义返回结果
			RouteCalculationOutput output = new RouteCalculationOutput();
			output.setRoutePlanobjectid( UuidUtil.randomUUID() );
			output.setRate(input.getRate());
			output.setProtectionType(input.getProtectionType());
			List<RouteCalculationResultWrapper> routeCalculationResultWrapper = new ArrayList<RouteCalculationResultWrapper>();
			
			LinkedList<RouteCalculationResult> routeCalculationResult = new LinkedList<RouteCalculationResult>();
			LinkedList<RouteCalculationResult> routeCalculationReverseResult = new LinkedList<RouteCalculationResult>();
			
			int routeCount = Integer.valueOf( PropertiesHander.getProperty("routeCount"));
			
			//1.查询站点间的直达路径
			List<RouteCalculationResult> resultZD = findThroughpath(input, aendzd, zendzd, aednmelist, zednmelist,false);
			if( resultZD !=null ){
				logger.info("resultZD,size: " + resultZD.size());
				routeCalculationResult.addAll(resultZD);
			}
		 
			//2.查询非直达的最短路径
			List<RouteCalculationResult> resultJP = findJumpedpathV3(input, aendzd, zendzd, aednmelist, zednmelist,routeCount-routeCalculationResult.size(),false );
			if(resultJP!=null){
				logger.info("resultJP,size: "+ resultJP.size());
				routeCalculationResult.addAll(resultJP);
			}
			
			//3查询反向路由
			List<RouteCalculationResult> resultZDReverse = findThroughpath(input, zendzd, aendzd, zednmelist, aednmelist,true);
			if( resultZDReverse !=null ){
				logger.info("resultZDReverse,size: " + resultZDReverse.size());
				routeCalculationReverseResult.addAll(resultZDReverse);
			}
		
		 
			//4.查询非直达的最短路径
			List<RouteCalculationResult> resultJPReverse = findJumpedpathV3(input, zendzd, aendzd, zednmelist, aednmelist ,routeCount-routeCalculationReverseResult.size(),true);
			if( resultJPReverse!=null){
				logger.info("resultJPReverse,size: " + resultJPReverse.size());
				routeCalculationReverseResult.addAll( resultJPReverse );
			}
			
			while( routeCalculationResultWrapper.size() < 5 && routeCalculationResult.size()>0 ){
				RouteCalculationResultWrapper result = new RouteCalculationResultWrapper();
				result.setWork( routeCalculationResult.removeFirst());
				if( routeCalculationReverseResult.size() >0){
					result.setWork_reverse( routeCalculationReverseResult.removeFirst());
				}else{
					break ; 
				}
				routeCalculationResultWrapper.add(result);
			} 
			
			logger.info("total,size: " + routeCalculationResultWrapper.size());
			output.setRouteCalculationResultWrapper(routeCalculationResultWrapper);
			
			return output;
			
		}
		catch (Exception e) {
			
			logger.info("路径计算处理失败",e);
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
	private List<RouteCalculationResult> findThroughpath(RouteCalculationInput input , String aendzd , String zendzd, List<String> aendmelist, List<String> zendmelist , boolean isReverse ){
		
		logger.info("查询直达路径" );
		
		List<ZdResult> resultlist = new ArrayList<ZdResult>();
		
		//取出邻接矩阵
		BusinessAvator businessAvator = new BusinessAvator();
		businessAvator.setKey( PropertiesHander.getProperty("BusinessAvator") );
		Matrix matrix = (Matrix)cachedClient.get(businessAvator.getKey());
				
		Section[][] sections = matrix.getMatrix();

		Integer startindex = (Integer)matrix.getPointMap().get(aendzd);
		Integer zendindex = (Integer)matrix.getPointMap().get(zendzd) ; 
		
		if( startindex==null || zendindex==null){
			return null;
		}
		
		Section sec = sections[startindex][zendindex];
		if(sec==null){
			return null;
		}
		
		List<Link> sourcelinks =  sec.getLinklist();
		for (Iterator<Link> iter = sourcelinks.iterator(); iter.hasNext();) {
			Link link = iter.next();
			
			String key = "OTN_RESOURECE_OTNLink" + "|" +sec.getAendNode()+"|"+sec.getZendNode()+"|"+ link.getLinkindex();
			ZdResult zdResult = (ZdResult)cachedClient.get(key);
			if( zdResult==null ){
				System.out.println(111);
			}
			else{
				resultlist.add(zdResult);
			}
			
		}
		
				
		/**		
		String key = memcacTag + aendzd + "|" + zendzd ;
		Object obj = cachedClient.get(key);
		if( obj != null ){
			@SuppressWarnings("unchecked")
			List<ZdResult> resultlist = (List<ZdResult>)obj;
			
			/**
			for (Iterator iterator = resultlist.iterator(); iterator.hasNext();) {
				ZdResult zdResult = (ZdResult) iterator.next();
				if(!zdResult.getSncid().equals("UUID:516a8b1b-10da-11e5-9c2d-005056862639")){
					iterator.remove();
					continue;
				}
			}
			*/
		if( resultlist.size() > 0 ){
			
			logger.info("查询直达路径，缓存中结果:" + resultlist);
			
			//根据az端网元进行过滤
			filterZDResult(resultlist, aendmelist , zendmelist );
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据az端网元进行过滤后数量:" + resultlist.size());
			
			//根据速率进行过滤
			filterZDResultByRate(resultlist, input.getRate() );
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据速率进行过滤后数量:" + resultlist.size());
			
			//根据端口进行过滤
			filterZDResultByPtp(resultlist, input , isReverse);
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据端口进行过滤后数量:" + resultlist.size());
			
			//根据时隙进行过滤
			filterZDResultByCtp(resultlist, input , isReverse );
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据时隙进行过滤后数量:" + resultlist.size());
			
			//根据毕经进行过滤
			filterZDResultByInclusion(resultlist, input);
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据必经进行过滤后数量:" + resultlist.size());
			
			//根据毕不经进行过滤
			filterZDResultByExclusion(resultlist, input);
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据必不经进行过滤后数量:" + resultlist.size());
			
			//客户侧路径，根据占用进行判断
			filterZDResultByClientCucirt(resultlist, input);
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据客户层路径已占用过滤后数量:" + resultlist.size());
			
			
			Collections.sort(resultlist, new ZdResultComparator());
			
			//网元完全相同算一组路径
			filterZDResultByDunlicateMe(resultlist, input);
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据网元相同过滤后数量:" + resultlist.size());
			
			
			Map<Integer,List<RouteCalculationResult>> resultmap = new TreeMap<Integer,List<RouteCalculationResult>>();
			
			//组装所有的路由结构
			for (int i = 0; i < resultlist.size(); i++) {
				ZdResult zdResult = resultlist.get(i);
				Integer rateOrder = ConstBusiness.rateMap.get(Integer.valueOf(zdResult.getRate()));
				if( rateOrder==null ){
					rateOrder = ConstBusiness.odukMap.get(Integer.valueOf(zdResult.getRate()));
					
					//排除och路径
					if( rateOrder.intValue() == 6){
						continue ; 
					}
				}
				else{
					if(!PropertiesHander.getProperty("includeClientResource").equals("true")){
						continue;
					}
				}
				
				boolean dealflag = dealZdResult(zdResult, input);
				if(!dealflag){
					logger.error("直达路径，拼接板卡失败舍弃");
					continue;
				}
				LinkedList<ZdResult> list =  new  LinkedList<ZdResult>();
				list.add(zdResult);
				ArrayList<RouteCalculationResultRouteWrapper> result = convertToOutput( list,input );
				
				RouteCalculationResult routeCalculationResult = new RouteCalculationResult();
				routeCalculationResult.setBusiCount(1);
				routeCalculationResult.setZdCount(0);
				routeCalculationResult.setRouteCalculationResultRouteWrapper(result);
				
				if( resultmap.get(rateOrder) ==null ){
					List<RouteCalculationResult> routeCalculationResultlist = new ArrayList<RouteCalculationResult>();
					routeCalculationResultlist.add(routeCalculationResult);
					resultmap.put(rateOrder, routeCalculationResultlist);
				}
				else{
					resultmap.get(rateOrder).add(routeCalculationResult);
				}
			}
			
			List<RouteCalculationResult> result = new ArrayList<RouteCalculationResult>();
			for ( Map.Entry<Integer,List<RouteCalculationResult>> mp : resultmap.entrySet() ) {
				List<RouteCalculationResult> rlist =  mp.getValue();
				if( rlist.size() >  0 ){
					//对资源信息进行排序
					Collections.sort(rlist, new RouteComparator());
					result.addAll(rlist);
				}
			}
			
			if(result.size() == 0){
				return null;
			}
			
			logger.info("排序后结果:" + result);
			
			/**
			//网元完全相同算一组路径
			filterZDResultBySameMe(result, input);
			if( resultlist.size() == 0 ){
				return null ; 
			}
			logger.info("查询直达路径，根据网元相同过滤后数量:" + result.size());
			*/
			
			return result ;
			
		}
		
		return null;
	}
	
	private void filterZDResultByDunlicateMe(List<ZdResult> resultlist, RouteCalculationInput input) {

		HashSet<TreeSet<String>> passedMes = new HashSet<TreeSet<String>>();
		for (Iterator<ZdResult> zditerator = resultlist.iterator(); zditerator.hasNext();) {
			ZdResult zdResult =  zditerator.next();
			
			String sncid = zdResult.getSncid();
			TreeSet<String>  passedMeSet = new TreeSet<String>();
			
			LinkedHashMap<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
			for (Iterator<LinkedList<ZdResultSingle>> iterator = allzd.iterator(); iterator.hasNext();) {
				LinkedList<ZdResultSingle> linkedList =  iterator.next();
				for (int i = 0; i < linkedList.size(); i++) {
					ZdResultSingle  zdResultSingle = linkedList.get(i);
					passedMeSet.add(resourceManager.meParentMap.get(zdResultSingle.getAendmeid()));
					passedMeSet.add(resourceManager.meParentMap.get(zdResultSingle.getZendmeid()));
				}
				
			}
			if( passedMes.contains(passedMeSet)){
				zditerator.remove();
				logger.info("根据重复网元进行过滤:" + sncid );
				continue;
			}
			passedMes.add(passedMeSet);
		}
		
	}

	/**
	private RouteCalculationResult convertZdResult2Result(ZdResult zdResult , RouteCalculationInput input){
		
		LinkedHashMap<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
		Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
		RouteCalculationResult routeCalculationResult = new RouteCalculationResult(); 
		routeCalculationResult.setBusiCount(1);
		routeCalculationResult.setZdCount(allzd.size());
		RouteCalculationResultRouteWrapper wrapper = convertZdResult2Route(zdResult, input);
		routeCalculationResult.getRouteCalculationResultRouteWrapper().add(wrapper);
		return routeCalculationResult ;
	}
	*/
	
	
	
	
	private void filterZDResultBySameMe(List<RouteCalculationResult> result, RouteCalculationInput input) {

		HashSet<TreeSet<String>> passedMes = new HashSet<TreeSet<String>>();
		for (Iterator<RouteCalculationResult> zditerator = result.iterator(); zditerator.hasNext();) {
			RouteCalculationResult zdResult =  zditerator.next();
			
			TreeSet<String>  passedMeSet = new TreeSet<String>();
			ArrayList<RouteCalculationResultRouteWrapper> snclist = zdResult.getRouteCalculationResultRouteWrapper();
			String sncid = "";
			for (int i = 0; i < snclist.size(); i++) {
				RouteCalculationResultRouteWrapper rw = snclist.get(i);
				sncid = rw.getSncid();
				ArrayList<RouteCalculationResultRoute> rcr =  rw.getRoute();
				for (int j = 0; j < rcr.size(); j++) {
					RouteCalculationResultRoute route =  rcr.get(j);
					passedMeSet.add(resourceManager.meParentMap.get( route.getAendmeobjectid()));
					passedMeSet.add(resourceManager.meParentMap.get( route.getZendmeobjectid()));
				}
			}
			
			System.out.println( zdResult.getRouteCalculationResultRouteWrapper().get(0).getSncid()+":"+ passedMeSet);
			if( passedMes.contains(passedMeSet)){
				zditerator.remove();
				logger.info("根据重复网元进行过滤:" + sncid );
				continue;
			}
			passedMes.add(passedMeSet);
		}
		
	}

	private void filterZDResultByClientCucirt(List<ZdResult> resultlist, RouteCalculationInput input) {
		
		for (Iterator<ZdResult> zditerator = resultlist.iterator(); zditerator.hasNext();) {
			ZdResult zdResult =  zditerator.next();
			
			if(!zdResult.getLayerdesc().equals(Rate.client.getName())){
				continue ;
			}
			
			String sncid = zdResult.getSncid();
			if( cucirtManager.hasCucirtBySncid(sncid)){
				zditerator.remove();
				logger.info("客户侧路径，根据电路占用过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
			if(!PropertiesHander.getProperty("includeClientResource").equals("true")){
				zditerator.remove();
				logger.info("客户侧路径，根据配置过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
		}
		
	}

	/**
	 * 根据时隙进行过滤
	 * @param resultlist
	 * @param input
	 */
	private void filterZDResultByCtp(List<ZdResult> resultlist, RouteCalculationInput input, boolean isReverse) {

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
			
			String headctp = firstelement.getFirst().getAendctp();
			String tailctp = lastelement.getLast().getZendctp();
			if(isReverse){
				String tmp = headctp ; 
				headctp = tailctp ; 
				tailctp = tmp ; 
			}
			
			if( !StringUtils.isEmpty(aendctp) && !headctp.equals(aendctp)){
				zditerator.remove();
				logger.info("根据A端时隙过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
			
			if( !StringUtils.isEmpty(zendctp) && !tailctp.equals(zendctp)){
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
	private void filterZDResultByPtp(List<ZdResult> resultlist, RouteCalculationInput input, boolean isReverse) {
		
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
			
			String headptp = firstelement.getFirst().getAendptpid();
			String tailptp = lastelement.getLast().getZendptpid();
			if(isReverse){
				String tmp = headptp ; 
				headptp = tailptp ; 
				tailptp = tmp ; 
			}
			
			if( !StringUtils.isEmpty(aendptp) && !headptp.equals(aendptp)){
				zditerator.remove();
				logger.info("根据A端端口过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
			
			if( !StringUtils.isEmpty(zendptp) && !tailptp.equals(zendptp)){
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

		Integer inputrateOrder = ConstBusiness.rateMap.get(rate);
		Integer odurateOrder = ConstBusiness.odukMap.get(rate);
		logger.info("根据速率过滤,请求速率:" + rate + ", inputrateOrder：" + inputrateOrder);
		
		for (Iterator<ZdResult> zditerator = resultlist.iterator(); zditerator.hasNext();) {
			ZdResult zdResult =  zditerator.next();
			
			Integer rateOrder = ConstBusiness.rateMap.get(Integer.valueOf(zdResult.getRate()));
			Integer odukOrder = ConstBusiness.odukMap.get(Integer.valueOf(zdResult.getRate()));
			
			if( inputrateOrder!=null ){
				if(rateOrder!=null ){
					if(  rateOrder.intValue() <  inputrateOrder.intValue()  ){
						zditerator.remove();
						logger.info("根据速率过滤:" + zdResult.getSncid() + "," + zdResult.getSncname() +",rateOrder: " + rateOrder);
						continue;
					}
				}
				else{
					if( odukOrder!=null &&  (odukOrder.intValue() < inputrateOrder.intValue() || odukOrder.intValue()==6) ){
						zditerator.remove();
						logger.info("根据速率过滤:" + zdResult.getSncid() + "," + zdResult.getSncname() +",rateOrder: " + rateOrder);
						continue;
					}
				}
			}
			else if( odurateOrder!=null ){
				if(rateOrder!=null ){
					zditerator.remove();
					logger.info("根据速率过滤:" + zdResult.getSncid() + "," + zdResult.getSncname() +",rateOrder: " + rateOrder);
					continue;
				}
				else{
					if( odukOrder!=null &&  (odukOrder.intValue() < odurateOrder.intValue() || odukOrder.intValue()==6) ){
						zditerator.remove();
						logger.info("根据速率过滤:" + zdResult.getSncid() + "," + zdResult.getSncname() +",rateOrder: " + rateOrder);
						continue;
					}
				}
			}
			
			if("UUID:51688f91-10da-11e5-9c2d-005056862639".equals(zdResult.getSncid())){
				System.out.println(123);
			}
			
			if( zdResult.getODUinfo(rate).equals("")){
				zditerator.remove();
				logger.info("根据资源过滤::" + zdResult);
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
	private void filterZDResult( List<ZdResult> resultlist , List<String> aendmelist , List<String> zendmelist ){
		
		for (Iterator<ZdResult> zditerator = resultlist.iterator(); zditerator.hasNext();) {
			ZdResult zdResult =  zditerator.next();
			
			if(zdResult==null){
				System.out.println(000);
				continue;
			}
			Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
			
			//判断是否经过az网元
			Iterator<LinkedList<ZdResultSingle>>  iter = allzd.iterator();
			LinkedList<ZdResultSingle> firstelement = iter.next();
			
			LinkedList<ZdResultSingle> lastelement = firstelement;
			while(iter.hasNext()){
				lastelement = iter.next();
			}
			
			if( aendmelist.size()> 0 && !aendmelist.contains(firstelement.getFirst().getAendmeid())){
				zditerator.remove();
				logger.info("根据A端网元过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
			
			if( zendmelist.size()> 0 && !zendmelist.contains(lastelement.getLast().getZendmeid())){
				zditerator.remove();
				logger.info("根据Z端网元过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
		}
	}
	
	/**
	 * 对两个站点间的所有业务路径，按必经点过滤
	 * @param resultlist
	 * @param aendmeobjectid
	 * @param zendmeobjectid
	 */
	private void filterZDResultByInclusion( List<ZdResult> resultlist , RouteCalculationInput input  ){
		
		if(input.getInclusionWrapper().getClusionBean()==null || input.getInclusionWrapper().getClusionBean().size()==0){
			return ;
		}
		
		
		for (Iterator<ZdResult> zditerator = resultlist.iterator(); zditerator.hasNext();) {
			
			Set<String> meSet = new HashSet<String>();
			Set<String> ptpSet = new HashSet<String>();
			
			for (int i = 0; i < input.getInclusionWrapper().getClusionBean().size(); i++) {
				String meid = input.getInclusionWrapper().getClusionBean().get(i).getMeid();
				String ptpid = input.getInclusionWrapper().getClusionBean().get(i).getPtpid();
				if( !StringUtils.isEmpty(ptpid) ){
					ptpSet.add(ptpid);
					continue;
				}
				if( !StringUtils.isEmpty(meid) ){
					meSet.add(meid);
				}
			}
			
			ZdResult zdResult =  zditerator.next();
			
			Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
			
			//判断是否经过az网元
			for (LinkedList<ZdResultSingle> zdsinglelist : allzd) {

				for (int i = 0; i < zdsinglelist.size(); i++) {
					 ZdResultSingle zd = zdsinglelist.get(i);
					 if( meSet.contains(zd.getAendmeid()) ){
						 meSet.remove(zd.getAendmeid());
					 }
					 if( meSet.contains(zd.getZendmeid()) ){
						 meSet.remove(zd.getZendmeid());
					 }
					 if( ptpSet.contains(zd.getAendptpid()) ){
						 ptpSet.remove(zd.getAendptpid());
					 }
					 if( ptpSet.contains(zd.getZendptpid()) ){
						 ptpSet.remove(zd.getZendptpid());
					 }
				}
			}
			
			if( meSet.size()> 0 ){
				zditerator.remove();
				logger.info("根据必经网元过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
			
			if( ptpSet.size()>0 ){
				zditerator.remove();
				logger.info("根据必经端口过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
				continue;
			}
		}
	}
	
	/**
	 * 对两个站点间的所有业务路径，按禁止点进行过滤。
	 * @param resultlist
	 * @param aendmeobjectid
	 * @param zendmeobjectid
	 */
	private void filterZDResultByExclusion( List<ZdResult> resultlist ,  RouteCalculationInput input  ){
		
		if( input.getExclusionWrapper().getClusionBean()==null || input.getExclusionWrapper().getClusionBean().size()==0){
			return ;
		}
		
		Set<String> meSet = new HashSet<String>();
		Set<String> ptpSet = new HashSet<String>();
		
		for (int i = 0; i < input.getExclusionWrapper().getClusionBean().size(); i++) {
			String meid = input.getExclusionWrapper().getClusionBean().get(i).getMeid();
			String ptpid = input.getExclusionWrapper().getClusionBean().get(i).getPtpid();
			if( !StringUtils.isEmpty(ptpid) ){
				ptpSet.add(ptpid);
				continue;
			}
			if( !StringUtils.isEmpty(meid) ){
				meSet.add(meid);
			}
		}
		
		outer:for (Iterator<ZdResult> zditerator = resultlist.iterator(); zditerator.hasNext();) {
			ZdResult zdResult =  zditerator.next();
			
			Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
			
			//判断是否经过az网元
			for (LinkedList<ZdResultSingle> zdsinglelist : allzd) {

				for (int i = 0; i < zdsinglelist.size(); i++) {
					 ZdResultSingle zd = zdsinglelist.get(i);
					 if( meSet.contains(zd.getAendmeid()) ){
						 zditerator.remove();
						 logger.info("根据禁止网元过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
						 continue outer;
					 }
					 if( meSet.contains(zd.getZendmeid()) ){
						 zditerator.remove();
						 logger.info("根据禁止网元过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
						 continue outer;
					 }
					 if( ptpSet.contains(zd.getAendptpid()) ){
						 zditerator.remove();
						 logger.info("根据禁止端口过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
						 continue outer;
					 }
					 if( ptpSet.contains(zd.getZendptpid()) ){
						 zditerator.remove();
						 logger.info("根据禁止端口过滤:" + zdResult.getSncid() + "," + zdResult.getSncname());
						 continue outer;
					 }
				}
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
	*/
	
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
	private  List<RouteCalculationResult> findJumpedpathV3(RouteCalculationInput input , String aendzd , String zendzd , List<String> aendmelist, List<String> zendmelist,int routeCount,boolean isReverse) throws RouteCalculationException{

		logger.info("查询非直达路径" );
		
		ISearch isearch = new ISearch();
		
		BusinessAvator businessAvator = new BusinessAvator();
		businessAvator.setKey( PropertiesHander.getProperty("BusinessAvator") );
		    
		CaculatorParam param = new CaculatorParam();
	    param.setAend(aendzd);
	    param.setZend(zendzd);
	    param.setCount(2);
	    param.setPolicy(Policy.LESS_JUMP);
	    param.setAendme(aendmelist);
	    param.setZendme(zendmelist);
	    param.setRate(input.getRate());
	    param.getAttrMap().put("aendptp", input.getAendport());
	    param.getAttrMap().put("zendptp", input.getZendport());
	    param.getAttrMap().put("aendctp", input.getAtimeSlots());
	    param.getAttrMap().put("zendctp", input.getZtimeSlots());
	    param.getAttrMap().put("routeCount", routeCount);
	    
	    List<String> excludeMelist = new ArrayList<String>();
	    List<String> excludePtplist = new ArrayList<String>();
	    List<ExCluseBean> excludelist = new ArrayList<ExCluseBean>();
	    
	    List<ClusionBean> incluselist = input.getExclusionWrapper().getClusionBean();
	    for (int i = 0; i < incluselist.size(); i++) {
	    	ClusionBean bean = incluselist.get(i);
	    	if( StringUtils.isEmpty(bean.getMeid())){
	    		continue;
	    	}
	    	if(StringUtils.isEmpty( bean.getPtpid())){
	    		excludeMelist.add( bean.getMeid());
	    	}else{
	    		excludePtplist.add( bean.getPtpid());
	    	}
		}
	    
	    List<ClusionBean> cluselist = input.getInclusionWrapper().getClusionBean();
	    for (int i = 0; i < cluselist.size(); i++) {
	    	ClusionBean bean = cluselist.get(i);
	    	if( StringUtils.isEmpty(bean.getMeid())){
	    		continue;
	    	}
	    	ExCluseBean exCluseBean = new ExCluseBean();
	    	exCluseBean.setMeid(bean.getMeid());
	    	exCluseBean.setPtpid(bean.getPtpid());
	    	DbMe ame = resourceManager.getMeById( bean.getMeid() );
			if(ame==null){
				throw new RouteCalculationException("-3", "没有找到网元，" + bean.getMeid() );
			}
			aendzd = ame.getJuzhanobjectid(); 
			if(StringUtils.isEmpty(aendzd)){
				continue;
			}
			exCluseBean.setZdid(aendzd);
			excludelist.add(exCluseBean);
		}
	    
	    /**
	    Map<String ,List<ExCluseBean> > cluseMap = new HashMap<String ,List<ExCluseBean> >();
	    for (int i = 0; i < excludelist.size(); i++) {
	    	ExCluseBean bean = excludelist.get(i);
	    	if( cluseMap.containsKey( bean.getZdid()) ){
	    		cluseMap.get( bean.getZdid() ).add( bean );
	    	}
	    	else{
	    		List<ExCluseBean>  clist = new ArrayList<ExCluseBean>();
	    		clist.add(bean);
	    		cluseMap.put(bean.getZdid(), clist);
	    	}
		}
		*/
	    List<ExCluseBean> inclusedlist = new ArrayList<ExCluseBean>();
	    for (int i = 0; i < excludelist.size(); i++) {
	    	ExCluseBean bean = excludelist.get(i);
	    	inclusedlist.add(bean);
		}
	    
	    param.getAttrMap().put("excludeMelist", excludeMelist);
	    param.getAttrMap().put("excludePtplist", excludePtplist);
	    param.getAttrMap().put("inclusedlist", inclusedlist);
	    param.getAttrMap().put("meParentMap", resourceManager.meParentMap);
	    
	    logger.info("开始查询最短路径" );
		CaculatorResult result = isearch.search(businessAvator.getKey(), param, new Dijkstra4OtnV2(isReverse));
		logger.info("查询最短路径结果：" +  result);
		if( result ==null ){
			logger.info("查询最短路径无结果");
			return null;
		}
		if( result.getWays()==null || result.getWays().size()==0 ){
			logger.info("查询最短路径无结果");
			return null;
		}
		
		ArrayList<RouteCalculationResult> routeCalculationResultlist = new ArrayList<RouteCalculationResult>();
		for (int i = 0; i < result.getWays().size(); i++) {
			CaculatorResultWay caculatorResultWay = result.getWays().get(i);
			ArrayList<RouteCalculationResult> newways = dealRoute(caculatorResultWay,input);
			if( newways!=null && newways.size()>0){
				routeCalculationResultlist.addAll(newways);
			}
		}
		//对资源信息进行排序
		Collections.sort(routeCalculationResultlist, new RouteJumpedComparator());
		
	    return routeCalculationResultlist;
	}
	
	private ArrayList<RouteCalculationResult> dealRoute( CaculatorResultWay caculatorResultWay,RouteCalculationInput input  ){
		
		//路由列表
		LinkedList<CaculatorResultWayRoute> routelist = caculatorResultWay.getRouts();
		
		Map<Integer , ArrayList<String>> preMes = new HashMap<Integer , ArrayList<String>>();
		
		//先循环一遍，确定边界点是否有同网元的节点
		for (int j = 0; j < routelist.size(); j++) {
			CaculatorResultWayRoute caculatorResultWayRoute = routelist.get(j);
			if(j+1 < routelist.size()){
				
				ArrayList<String> s = new ArrayList<String>();
				preMes.put(j+1, s);
				
				CaculatorResultWayRoute nextWayRoute = routelist.get(j+1);
			
				Set<String> firstMes = new HashSet<String>();
				getlastMe(firstMes ,caculatorResultWayRoute.getClientrouts());
				getlastMe(firstMes ,caculatorResultWayRoute.getOdu0routs());
				getlastMe(firstMes ,caculatorResultWayRoute.getOdu1routs());
				getlastMe(firstMes ,caculatorResultWayRoute.getOdu2routs());
				getlastMe(firstMes ,caculatorResultWayRoute.getOdu3routs());
				getlastMe(firstMes ,caculatorResultWayRoute.getOdu4routs());
			
				Set<String> nextMes = new HashSet<String>();
				getfirstMe(nextMes ,nextWayRoute.getClientrouts());
				getfirstMe(nextMes ,nextWayRoute.getOdu0routs());
				getfirstMe(nextMes ,nextWayRoute.getOdu1routs());
				getfirstMe(nextMes ,nextWayRoute.getOdu2routs());
				getfirstMe(nextMes ,nextWayRoute.getOdu3routs());
				getfirstMe(nextMes ,nextWayRoute.getOdu4routs());
				
				for (Iterator<String> iterator = nextMes.iterator(); iterator.hasNext();) {
					String next = iterator.next();
					if( firstMes.contains(next)){
						preMes.get(j+1).add(next);
					}
				}
			}
		}
		
		//合并选取结果,对路径进行排序
		for (int j = 0; j < routelist.size(); j++) {
			CaculatorResultWayRoute caculatorResultWayRoute = routelist.get(j);
			logger.info(" 处理路径结果，序号：" +j +",节点：" +caculatorResultWayRoute.getNodeid());
			LinkedList<Link> clientlist = caculatorResultWayRoute.getClientrouts();
			LinkedList<Link> odu0list = caculatorResultWayRoute.getOdu0routs();
			LinkedList<Link> odu1list = caculatorResultWayRoute.getOdu1routs();
			LinkedList<Link> odu2list = caculatorResultWayRoute.getOdu2routs();
			LinkedList<Link> odu3list = caculatorResultWayRoute.getOdu3routs();
			LinkedList<Link> odu4list = caculatorResultWayRoute.getOdu4routs();
			LinkedList<Link> ochlist = caculatorResultWayRoute.getOchrouts();
			
			Collections.sort(clientlist, new RouteJumpedComparatorSingle( preMes.get(j),preMes.get(j+1) )); 
			Collections.sort(odu0list, new RouteJumpedComparatorSingle( preMes.get(j),preMes.get(j+1) )); 
			Collections.sort(odu1list, new RouteJumpedComparatorSingle( preMes.get(j),preMes.get(j+1) )); 
			Collections.sort(odu2list, new RouteJumpedComparatorSingle( preMes.get(j),preMes.get(j+1) )); 
			Collections.sort(odu3list, new RouteJumpedComparatorSingle( preMes.get(j),preMes.get(j+1) )); 
			Collections.sort(odu4list, new RouteJumpedComparatorSingle( preMes.get(j),preMes.get(j+1) )); 
			Collections.sort(ochlist, new RouteJumpedComparatorSingle( preMes.get(j),preMes.get(j+1))); 
			
			//网元完全相同算一组路径
			dealSameMePath(clientlist ,preMes.get(j),preMes.get(j+1) );
			dealSameMePath(odu0list  ,preMes.get(j),preMes.get(j+1));
			dealSameMePath(odu1list  ,preMes.get(j),preMes.get(j+1));
			dealSameMePath(odu2list  ,preMes.get(j),preMes.get(j+1));
			dealSameMePath(odu3list  ,preMes.get(j),preMes.get(j+1));
			dealSameMePath(odu4list  ,preMes.get(j),preMes.get(j+1));
			dealSameMePath(ochlist  ,preMes.get(j),preMes.get(j+1));
			
		}
		
		int startRate = 0 ;
		Integer inputRateOrder = ConstBusiness.rateMap.get( input.getRate() );
		if(inputRateOrder==null){
			inputRateOrder = ConstBusiness.odukMap.get( input.getRate() );
		}
		startRate = inputRateOrder.intValue();
		//return grenT( startRate,routelist ,input) ;
		
		return grenTV2( startRate,caculatorResultWay ,input) ;
	}
	
	private void getfirstMe(Set<String> firstMes, LinkedList<Link> links) {
		for (int i = 0; i < links.size(); i++) {
			ZdResult zdresult = links.get(i).getZdResult();
			firstMes.add(zdresult.getFirstZdRoute().getFirst().getAendmeid());
		}
	}

	private void getlastMe(Set<String> preMes , LinkedList<Link> links) {
		for (int i = 0; i < links.size(); i++) {
			ZdResult zdresult = links.get(i).getZdResult();
			preMes.add(zdresult.getLastMe());
		}
	}

	private void dealSameMePath(LinkedList<Link> clientlist, ArrayList<String> headmes, ArrayList<String> tailmes) {

		Set<String> meSets = new HashSet<String>();
		HashSet<TreeSet<String>> passedMes = new HashSet<TreeSet<String>>();
		for (Iterator<Link> zditerator = clientlist.iterator(); zditerator.hasNext();) {
			Link link =  zditerator.next();
			
			TreeSet<String>  passedMeSet = new TreeSet<String>();
			ZdResult zdResult = link.getZdResult();
			String sncid = zdResult.getSncid();
			Map<String, LinkedList<ZdResultSingle>> zdmap = zdResult.getZdmap();
			Collection<LinkedList<ZdResultSingle>> zds = zdmap.values();
			for (Iterator<LinkedList<ZdResultSingle>> iterator = zds.iterator(); iterator.hasNext();) {
				LinkedList<ZdResultSingle> linkedList =  iterator.next();
				for (int i = 0; i < linkedList.size(); i++) {
					ZdResultSingle zhandian = linkedList.get(i);
					passedMeSet.add( resourceManager.meParentMap.get(zhandian.getAendmeid())  );
					passedMeSet.add( resourceManager.meParentMap.get(zhandian.getZendmeid())  );
				}
			}
			
			String headme = zdResult.getFirstZdRoute().getFirst().getAendmeid();
			String tailme = zdResult.getLastMe();
			String key = "";
					
			if( headmes!=null && headmes.size()>0 && headmes.contains(headme) ){
				key = headme +"|";
			}else{
				key = ""+"|";
			}
			
			if( tailmes!=null && tailmes.size()>0 && tailmes.contains(tailme) ){
				key  += tailme;
			}else{
				key += "";
			}
			
			if( passedMes.contains(passedMeSet)){
				if( meSets.contains(key) || (meSets.size()>0 && key.equals("|")) ){
					zditerator.remove();
					logger.info("转接路径根据重复网元进行过滤:" + sncid );
					continue;
				}
			}
			passedMes.add(passedMeSet);
			meSets.add( key );
		}
		
	}

	private ArrayList<RouteCalculationResult> grenTV2( int layer , CaculatorResultWay caculatorResultWay ,RouteCalculationInput input){
		
		
		ArrayList<RouteCalculationResult> rtnValue = new ArrayList<RouteCalculationResult>(); 
		
		LinkedList<ZdResult> allroutes = new LinkedList<ZdResult>();
		ArrayList<RouteCalculationResult> rtnlist = new ArrayList<RouteCalculationResult>();
		
		RouteCalculationResult mainRoute = new RouteCalculationResult();
		mainRoute.setBusiCount(caculatorResultWay.getRouts().size()-1);
		mainRoute.setZdCount(0);
		
		List<Integer> passedindex = caculatorResultWay.getPassbjindex();
		LinkedList<CaculatorResultWayRoute>  routelist = caculatorResultWay.getRouts();

		
		LinkedList<LinkedList<Link>> alllinks =  new LinkedList<LinkedList<Link>>();
		for (int i = 0; i < routelist.size()-1; i++) {
			CaculatorResultWayRoute route = routelist.get(i);
			
			LinkedList<Link>  links = null;
			if( passedindex.contains(i)){
				links = getNextLink(layer, route , true);
			}
			else{
				links = getNextLink(layer, route , false);
			}
			
			if(links ==null || links.size()==0){
				logger.error(" links为空，路径过滤  ");
				return null;
			}else{
				logger.info(" 第 " + i +" 端路径  ,links：" +links);
				alllinks.add(links);
			}
		}
		
		int i = 0 ;
		Link prevlink = null;
		while( i  < alllinks.size() ){
			LinkedList<Link> current = alllinks.get(i);
			
			Link l = getOneLink(current, prevlink);
			if(l==null){
				return null;
			}
			
			ZdResult zdResult = l.getZdResult();
			//logger.info(" 处理路径结果  ,添加zdResult：" +zdResult);
			if(!dealZdResult(zdResult, input)){
				logger.error(" 拼接板卡为空，路径过滤  ");
				return null;
			}
			
			allroutes.offer( zdResult ) ;
			i++;
			prevlink = l;
			
		}
		
		ArrayList<RouteCalculationResultRouteWrapper> outputlist = convertToOutput(allroutes,input);
		
		int zdcount = 0 ;
		for (int s = 0; s < outputlist.size(); s++) {
			RouteCalculationResultRouteWrapper wrapper = outputlist.get(s);
			zdcount += wrapper.getZdCountInSnc() ; 
		}
		mainRoute.setZdCount(zdcount);
		mainRoute.setRouteCalculationResultRouteWrapper(outputlist);;
		
		rtnValue.add(mainRoute);
		
		return rtnValue;
	}
	
	private Link getOneLink(LinkedList<Link> current, Link prev) {
		Link l = current.removeFirst();
		
		//判断回头的情况
		while( true ){
			if( !checkRouteBak(l,prev)){
				if( current.size() > 0 ){
					l = current.removeFirst();
				}
				else{
					return null;
				}
			}
			else{
				break;
			}
		}
		
		return l;
				
	}

	private boolean checkRouteBak(Link l, Link prev) {

		boolean pass = true ; 
		ZdResult zdResult = l.getZdResult();
		Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
		Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
		
		for (LinkedList<ZdResultSingle> zdsinglelist : allzd) {
			for (int i = 0; i < zdsinglelist.size(); i++) {
				ZdResultSingle zd = zdsinglelist.get(i);
				
				String meid = resourceManager.meParentMap.get(zd.getAendmeid());
				if( !l.getPassedbigmelist().contains(meid)){
					l.getPassedbigmelist().add(meid);
				}
				
				if( prev!=null && prev.getPassedbigmelist().contains(meid)){
					if( !prev.getPassedbigmelist().get( prev.getPassedbigmelist().size()-1).equals(meid)){
						pass = false;
					}
					
				}
			}
		}
		
		return pass;
	}

	private ArrayList<RouteCalculationResult> grenT( int layer , LinkedList<CaculatorResultWayRoute> routelist ,RouteCalculationInput input){
		
		ArrayList<RouteCalculationResult> rtnlist = new ArrayList<RouteCalculationResult>();
		
		RouteCalculationResult mainRoute = new RouteCalculationResult();
		mainRoute.setBusiCount(routelist.size()-1);
		mainRoute.setZdCount(0);
		
		int totalrouteCount = Integer.MAX_VALUE ;
		for (int j = 0; j < routelist.size()-1; j++) {
			CaculatorResultWayRoute caculatorResultWayRoute = routelist.get(j);
			int routecount = caculatorResultWayRoute.getAllRouteCount();
			if( routecount < totalrouteCount){
				totalrouteCount = routecount ; 
			}
		}
		
		for (int j = 0; j < totalrouteCount ; j++) {
			RouteCalculationResult routeCalculationResult = grenRoute(layer, routelist, input);
			if( routeCalculationResult!=null){
				rtnlist.add(routeCalculationResult);
			}
		}
		return rtnlist ;
	}
	
	private  LinkedList<Link> getNextLink(int layer , CaculatorResultWayRoute caculatorResultWayRoute, boolean mastPass){
		
		LinkedList<Link> rtnlist  = new LinkedList<Link>();
		while( layer < 6 ){
			logger.info("  层速率  ：" + layer );
			LinkedList<Link> links = caculatorResultWayRoute.getRouteByType(layer);
			
			if(!PropertiesHander.getProperty("includeClientResource").equals("true")){
				for (Iterator<Link> iter = links.iterator(); iter.hasNext();) {
					Link link =  iter.next();
					ZdResult zdResult = link.getZdResult();
					if( ConstBusiness.rateMap.get(Integer.valueOf(zdResult.getRate()))!=null ){
						logger.info("过滤客户侧资源:" + link.getZdResult().getSncid() + "," + link.getZdResult().getSncname()  );
						iter.remove();
					}
				}
			}
			if( mastPass ){
				for (int i = 0; i < links.size(); i++) {
					 Link link = links.get(i);
					 if(link.isPassed()){
						 rtnlist.add(link);
					 }
				}
			}
			else{
				rtnlist.addAll(links);
			}
			
			//if( rtnlist !=null && rtnlist.size() > 0){
			//	return rtnlist ;
			//}
			layer ++ ;
		}
		
		return rtnlist;
		
	}
	
	private RouteCalculationResult grenRoute( int layer , LinkedList<CaculatorResultWayRoute> routelist ,RouteCalculationInput input){
		
		
		LinkedList<ZdResult> allroutes = new LinkedList<ZdResult>();
		
		LinkedList<ZdResult> targetlinklist = new LinkedList<ZdResult>();
		for (int j = 0; j < routelist.size()-1; j++) {
			
			logger.info(" 第 " + j +" 段路径  ");
			
			CaculatorResultWayRoute caculatorResultWayRoute = routelist.get(j);
			
			LinkedList<Link>  links = grenTer(layer, caculatorResultWayRoute);
			
			if(links ==null || links.size()==0){
				logger.error(" links为空，路径过滤  ");
				return null;
			}else{
				logger.info(" 第 " + j +" 端路径  ,links：" +links);
			}
			
			targetlinklist.add( links.removeFirst().getZdResult() );
			
			ZdResult zdresult = gerenRouteLayer(links,input) ;
			if( zdresult==null ){
				logger.error(" zdresult为空，路径过滤  ");
				return null;
			}
			allroutes.offer( zdresult ) ;
			
		}
		
		ArrayList<RouteCalculationResultRouteWrapper> outputlist = convertToOutput(allroutes,input);
		
		RouteCalculationResult mainRoute = new RouteCalculationResult();
		mainRoute.setBusiCount(routelist.size()-1);
		int zdcount = 0 ;
		for (int i = 0; i < outputlist.size(); i++) {
			RouteCalculationResultRouteWrapper wrapper = outputlist.get(i);
			zdcount += wrapper.getZdCountInSnc() ; 
		}
		mainRoute.setZdCount(zdcount);
		mainRoute.setRouteCalculationResultRouteWrapper(outputlist);;
		
		return mainRoute;

	}
	
	private ArrayList<RouteCalculationResultRouteWrapper> convertToOutput( LinkedList<ZdResult> allroutes,RouteCalculationInput input ) {
		
		List<String> mCardmodel = resourceManager.getMCardModel();
		mCardmodel.add("12M40");
		mCardmodel.add("12M40V");
		mCardmodel.add("13M40");
		mCardmodel.add("13M40V");
		
		List<String> dCardmodel = resourceManager.getDCardModel();
		dCardmodel.add("12D40");
		dCardmodel.add("12D40V");
		dCardmodel.add("13D40");
		dCardmodel.add("13D40V");
		
		
		
		String[] nCardmodel = resourceManager.getNCardModel();
		String[] tCardmodel = resourceManager.getTCardModel();
		
		List<String> nCardmodellist =  Arrays.asList(nCardmodel);
		List<String> tCardmodellist =  Arrays.asList(tCardmodel);
		
		
		for (int i = 0; i < allroutes.size(); i++) {
			
			ZdResult zdresult = allroutes.get(i);
			LinkedHashMap<String, LinkedList<ZdResultSingle>> zdmap =  zdresult.getZdmap();
			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
			
			//循环一遍，找到第一个支路板的物理口，最后一个线路板的物理口，并对路由段进行标记
			ZdResultSingle headZl = null;
			ZdResultSingle headXl = null;
			ZdResultSingle tailZl = null;
			ZdResultSingle tailXl = null;
			String headmeid = null;
			String tailmeid = null;
			
			for (Iterator<LinkedList<ZdResultSingle>> iter = allzd.iterator(); iter.hasNext();) {
				LinkedList<ZdResultSingle> singleResult =  iter.next();
				
				for (Iterator<ZdResultSingle> iterator = singleResult.iterator(); iterator.hasNext();) {
					ZdResultSingle zdResultSingle =  iterator.next();
					
					//第一段一定为T板为A端
					if( headZl ==null){
						headZl = zdResultSingle;
					}
					
					//A端线路，Z端合波
					if( headXl==null && nCardmodellist.contains( zdResultSingle.getAendcardmodel() ) && mCardmodel.contains( zdResultSingle.getZendcardmodel() ) ){
						headXl = zdResultSingle;
					}
					
					//最后一段支路
					if( tCardmodellist.contains(zdResultSingle.getZendcardmodel())){
						tailZl = zdResultSingle;
					}
					
					//最后一段线路
					if( nCardmodellist.contains( zdResultSingle.getZendcardmodel() ) && dCardmodel.contains( zdResultSingle.getAendcardmodel() ) ){
						tailXl = zdResultSingle;
					}
					
					if(headmeid==null){
						headmeid = zdResultSingle.getAendmeid();
					}
					tailmeid = zdResultSingle.getZendmeid();
					
					if( tCardmodellist.contains(zdResultSingle.getAendcardmodel()) && tCardmodellist.contains(zdResultSingle.getZendcardmodel())){
						zdResultSingle.setRouteType( ConstBusiness.routeType_zl);
					}
					else if( nCardmodellist.contains(zdResultSingle.getAendcardmodel()) && nCardmodellist.contains(zdResultSingle.getZendcardmodel())){
						zdResultSingle.setRouteType( ConstBusiness.routeType_xl);
					}
					else if( tCardmodellist.contains(zdResultSingle.getAendcardmodel()) && nCardmodellist.contains(zdResultSingle.getZendcardmodel()) ){
						//zdResultSingle.setZendctp( zdresult.getCtpStr());
						zdResultSingle.setRouteType(ConstBusiness.routeType_zxl);
					}
					else  if( tCardmodellist.contains(zdResultSingle.getZendcardmodel()) && nCardmodellist.contains(zdResultSingle.getAendcardmodel()) ){
						//zdResultSingle.setAendctp( zdresult.getCtpStr() );
						zdResultSingle.setRouteType(ConstBusiness.routeType_zxl);
					}
					else{
						zdResultSingle.setRouteType(ConstBusiness.routeType_other);
					}
				}
			}
			if( headZl==null){
				logger.error("headZl分析异常："+ zdresult.getSncid());
			}
			if( headXl==null){
				logger.error("headXl分析异常："+ zdresult.getSncid());
			}
			if( tailZl==null){
				logger.error("tailZl分析异常："+ zdresult.getSncid());
			}
			if( tailXl==null){
				logger.error("tailXl分析异常："+ zdresult.getSncid());
			}
			
			zdresult.setHeadXl(headXl);
			zdresult.setHeadZl(headZl);
			zdresult.setTailXl(tailXl);
			zdresult.setTailZl(tailZl);
			
			zdresult.setHeadme(headmeid);
			zdresult.setTrailme(tailmeid);
			
		}
		
		
		
		//在循环一次，新增T-N板卡的路由
		String previoursMeObjectid = allroutes.peek().getTrailme();
		for (int i = 0; i < allroutes.size(); i++) {
			ZdResult zdResult = allroutes.get(i);
			LinkedHashMap<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
			Iterator<LinkedList<ZdResultSingle>> iter = allzd.iterator(); 
			iter.hasNext();
			LinkedList<ZdResultSingle> singleResult =  iter.next();
				
			
			/**
			//客户层路径不拼接
			Integer clinetOrder = ConstBusiness.rateMap.get(Integer.valueOf(zdResult.getRate()));
			if( clinetOrder!=null ){
				
				LinkedList<ZdResultSingle> newlist = new LinkedList<ZdResultSingle>();
				
					for (int j = 0; j < singleResult.size(); j++) {
						ZdResultSingle zdResultSingleNow = singleResult.get(j);
						if( zdResultSingleNow.getRouteType().equals(ConstBusiness.routeType_zxl) ){
							//T-N
							ZdResultSingle zdResultSingle = new ZdResultSingle();
							zdResultSingle.setAendmeid(zdResultSingleNow.getAendmeid());
							zdResultSingle.setAendzdid(zdResultSingleNow.getAendzdid());
							zdResultSingle.setAendptpid(zdResultSingleNow.getAendptpid());
							zdResultSingle.setAendcardid(zdResultSingleNow.getAendcardid());
							zdResultSingle.setAendcardmodel(zdResultSingleNow.getAendcardmodel());
							zdResultSingle.setAendctp("");
							
							zdResultSingle.setZendmeid(zdResultSingleNow.getZendmeid());
							zdResultSingle.setZendzdid(zdResultSingleNow.getZendzdid());
							zdResultSingle.setZendptpid(zdResultSingleNow.getZendptpid());
							zdResultSingle.setZendcardid(zdResultSingleNow.getZendcardid());
							zdResultSingle.setZendcardmodel(zdResultSingleNow.getZendcardmodel());
							zdResultSingle.setZendctp(zdResult.getCtpStr());
							
							zdResultSingle.setRouteType( ConstBusiness.routeType_zxlwl);
							newlist.add(zdResultSingle);
						}
					}
				
				singleResult.addAll(0, newlist);
				continue;
			}
			*/
			
			if( zdResult.getHeadme().equals(previoursMeObjectid)){
				
				//删除上一段的N-T板路由
				ZdResult previours = allroutes.get(i-1);
				previours.getFirstZdRoute().remove(1);
				
				//新增N-N板卡路由
				ZdResultSingle zdResultSingleLast = new ZdResultSingle();
				zdResultSingleLast.setAendmeid(previours.getTailXl().getZendmeid());
				zdResultSingleLast.setAendzdid(previours.getTailXl().getZendzdid());
				zdResultSingleLast.setAendptpid(previours.getTailXl().getZendptpid());
				zdResultSingleLast.setAendcardid(previours.getTailXl().getZendcardid());
				zdResultSingleLast.setAendcardmodel(previours.getTailXl().getZendcardmodel());
				zdResultSingleLast.setAendctp(previours.getCtpStr());
				
				zdResultSingleLast.setZendmeid(zdResult.getHeadXl().getAendmeid());
				zdResultSingleLast.setZendzdid(zdResult.getHeadXl().getAendzdid());
				zdResultSingleLast.setZendptpid(zdResult.getHeadXl().getAendptpid());
				zdResultSingleLast.setZendcardid(zdResult.getHeadXl().getAendcardid());
				zdResultSingleLast.setZendcardmodel(zdResult.getHeadXl().getAendcardmodel());
				zdResultSingleLast.setZendctp(zdResult.getCtpStr());
				
				zdResultSingleLast.setRouteType( ConstBusiness.routeType_zxlwl);
				singleResult.addFirst(zdResultSingleLast);
				
			}
			else{
				//T-N
				ZdResultSingle zdResultSingle = new ZdResultSingle();
				zdResultSingle.setAendmeid(zdResult.getHeadZl().getAendmeid());
				zdResultSingle.setAendzdid(zdResult.getHeadZl().getAendzdid());
				zdResultSingle.setAendptpid(zdResult.getHeadZl().getAendptpid());
				zdResultSingle.setAendcardid(zdResult.getHeadZl().getAendcardid());
				zdResultSingle.setAendcardmodel(zdResult.getHeadZl().getAendcardmodel());
				zdResultSingle.setAendctp( zdResult.getHeadctpStr() );
				
				zdResultSingle.setZendmeid(zdResult.getHeadXl().getAendmeid());
				zdResultSingle.setZendzdid(zdResult.getHeadXl().getAendzdid());
				zdResultSingle.setZendptpid(zdResult.getHeadXl().getAendptpid());
				zdResultSingle.setZendcardid(zdResult.getHeadXl().getAendcardid());
				zdResultSingle.setZendcardmodel(zdResult.getHeadXl().getAendcardmodel());
				zdResultSingle.setZendctp(zdResult.getCtpStr());
				
				zdResultSingle.setRouteType( ConstBusiness.routeType_zxlwl);
				singleResult.addFirst(zdResultSingle);
			}
				
			//N-T
			ZdResultSingle zdResultSingleLast = new ZdResultSingle();
			zdResultSingleLast.setAendmeid(zdResult.getTailXl().getZendmeid());
			zdResultSingleLast.setAendzdid(zdResult.getTailXl().getZendzdid());
			zdResultSingleLast.setAendptpid(zdResult.getTailXl().getZendptpid());
			zdResultSingleLast.setAendcardid(zdResult.getTailXl().getZendcardid());
			zdResultSingleLast.setAendcardmodel(zdResult.getTailXl().getZendcardmodel());
			zdResultSingleLast.setAendctp(zdResult.getCtpStr());
				
			zdResultSingleLast.setZendmeid(zdResult.getTailZl().getZendmeid());
			zdResultSingleLast.setZendzdid(zdResult.getTailZl().getZendzdid());
			zdResultSingleLast.setZendptpid(zdResult.getTailZl().getZendptpid());
			zdResultSingleLast.setZendcardid(zdResult.getTailZl().getZendcardid());
			zdResultSingleLast.setZendcardmodel(zdResult.getTailZl().getZendcardmodel());
			zdResultSingleLast.setZendctp( zdResult.getHeadctpStr() );
				
			zdResultSingleLast.setRouteType( ConstBusiness.routeType_zxlwl);
			singleResult.add(1, zdResultSingleLast);
				
		}
		
		ArrayList<RouteCalculationResultRouteWrapper> rtnlist = new ArrayList<RouteCalculationResultRouteWrapper>();
		for (int i = 0; i < allroutes.size(); i++) {
			
			ZdResult zdresult = allroutes.get(i);
			LinkedHashMap<String, LinkedList<ZdResultSingle>> zdmap =  zdresult.getZdmap();
			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
			
			RouteCalculationResultRouteWrapper wrapper = new RouteCalculationResultRouteWrapper();
			wrapper.setFreeOdu(zdresult.getODUinfo(input.getRate()));
			wrapper.setZdCountInSnc(zdmap.size());
			wrapper.setSncid(zdresult.getSncid());
			wrapper.setOchsncid(zdresult.getOdu().getOchSncid());
			wrapper.setLayerDesc(zdresult.getOdu().getClass().getSimpleName());
			wrapper.setDirection(zdresult.getDirection());
			ArrayList<RouteCalculationResultRoute> routelist = new ArrayList<RouteCalculationResultRoute>();
			
			for (Iterator<LinkedList<ZdResultSingle>> iter = allzd.iterator(); iter.hasNext();) {
				LinkedList<ZdResultSingle> singleResult =  iter.next();
				
				for (Iterator<ZdResultSingle> iterator = singleResult.iterator(); iterator.hasNext();) {
					ZdResultSingle zdResultSingle =  iterator.next();
					RouteCalculationResultRoute route = new RouteCalculationResultRoute(); 
					
					route.setAendmeobjectid( zdResultSingle.getAendmeid() );
					route.setAendptpobjectid( zdResultSingle.getAendptpid() );
					route.setAtimeSlots(zdResultSingle.getAendctp());
					route.setZendmeobjectid(zdResultSingle.getZendmeid());
					route.setZendptpobjectid(zdResultSingle.getZendptpid());
					route.setZtimeSlots(zdResultSingle.getZendctp());
					route.setRouteType(zdResultSingle.getRouteType());
					
					routelist.add(route);
				}
			}
			
			wrapper.setRoute(routelist);
			rtnlist.add(wrapper);
		}

		return rtnlist;
	}

	private LinkedList<Link> grenTer( int layer , CaculatorResultWayRoute caculatorResultWayRoute){
		
		LinkedList<Link> rtnlist  = new LinkedList<Link>();
		while( layer < 6 ){
			logger.info("  层速率  ：" + layer );
			LinkedList<Link> links = caculatorResultWayRoute.getRouteByType(layer);
			
			if(!PropertiesHander.getProperty("includeClientResource").equals("true")){
			for (Iterator<Link> iter = links.iterator(); iter.hasNext();) {
				Link link =  iter.next();
				ZdResult zdResult = link.getZdResult();
					if( ConstBusiness.rateMap.get(Integer.valueOf(zdResult.getRate()))!=null ){
						logger.info("过滤客户侧资源:" + link.getZdResult().getSncid() + "," + link.getZdResult().getSncname()  );
						iter.remove();
					}
				}
			}
			
			if( links !=null && links.size() > 0){
				return links ;
			}
			layer ++ ;
		}
		
		return rtnlist;
		
	}
	
	private ZdResult gerenRouteLayer( LinkedList<Link> clientlist,RouteCalculationInput input ){
		
		ZdResult zdResult = clientlist.removeFirst().getZdResult();
		//logger.info(" 处理路径结果  ,添加zdResult：" +zdResult);
		if(!dealZdResult(zdResult, input)){
			logger.error(" 拼接板卡为空，路径过滤  ");
			return null;
		}
		
		//RouteCalculationResultRouteWrapper wraper = convertZdResult2Route(zdResult, input);
		return zdResult;
		
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
	/**
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
	    //param.setAendme(aendmelist);
	    //param.setZendme(zendmelist);
	    param.setRate(input.getRate());
	    param.getAttrMap().put("aendptp", input.getAendport());
	    param.getAttrMap().put("zendptp", input.getZendport());
	    param.getAttrMap().put("aendctp", input.getAtimeSlots());
	    param.getAttrMap().put("zendctp", input.getZtimeSlots());
	    
	    List<String> includeMelist = new ArrayList<String>();
	    List<String> includePtplist = new ArrayList<String>();
	    List<String> excludeMelist = new ArrayList<String>();
	    List<String> excludePtplist = new ArrayList<String>();
	    
	    List<ClusionBean> cluselist = input.getInclusionWrapper().getInclusionResource();
	    for (int i = 0; i < cluselist.size(); i++) {
	    	ClusionBean bean = cluselist.get(i);
	    	if(StringUtils.isEmpty( bean.getPtpid())){
	    		includeMelist.add( bean.getMeid());
	    	}else{
	    		includePtplist.add( bean.getPtpid());
	    	}
		}
	    
	    List<ClusionBean> excluselist = input.getExclusionWrapper().getExclusionResource();
	    for (int i = 0; i < excluselist.size(); i++) {
	    	ClusionBean bean = excluselist.get(i);
	    	if(StringUtils.isEmpty( bean.getPtpid())){
	    		excludeMelist.add( bean.getMeid());
	    	}else{
	    		excludePtplist.add( bean.getPtpid());
	    	}
		}
	    
	    param.getAttrMap().put("includeMelist", includeMelist);
	    param.getAttrMap().put("includePtplist", includePtplist);
	    param.getAttrMap().put("excludeMelist", excludeMelist);
	    param.getAttrMap().put("excludePtplist", excludePtplist);
	    
	    
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
			
			String key = "OTNLink" + "|"+section.getAendNode()+"|"+section.getZendNode()+"|" + links.get(0).getLinkindex();
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

				String key1 = "OTNLink" + "|"+section.getAendNode()+"|"+section.getZendNode()+ "|" + links.get(1).getLinkindex();
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
		if(hasBak){
			routeCalculationResultlist.add(bakRoute);
		}
	    
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
	/**
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
			//filterZDResult(firstsection, aendme, null);
			
			//筛选z端网元。并按最优排序？
			List<ZdResult> lastsection =  singeResult.getLast();
			//filterZDResult(lastsection, null ,zendme);
			
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

	*/
	
	private boolean dealZdResult(ZdResult zdResult, RouteCalculationInput input) {

		List<String> mCardmodel = resourceManager.getMCardModel();
		String[] nCardmodel = resourceManager.getNCardModel();
		String[] tCardmodel = resourceManager.getTCardModel();
		
		List<String> nCardmodellist =  Arrays.asList(nCardmodel);
		List<String> tCardmodellist =  Arrays.asList(tCardmodel);
		
		LinkedHashMap<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
		Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
		
		RouteCalculationResultRouteWrapper wrapper = new RouteCalculationResultRouteWrapper();
		wrapper.setFreeOdu(zdResult.getODUinfo(input.getRate()));
		wrapper.setZdCountInSnc(zdmap.size());
		wrapper.setSncid(zdResult.getSncid());
		wrapper.setLayerDesc(zdResult.getOdu().getClass().getSimpleName());
		wrapper.setDirection(zdResult.getDirection());
		ArrayList<RouteCalculationResultRoute> routelist = new ArrayList<RouteCalculationResultRoute>();
		
		Integer oduOrder = ConstBusiness.odukMap.get(Integer.valueOf(zdResult.getRate()));
		if( oduOrder!=null ){
			//odu路径，寻找到一个T板卡拼接到返回的路由中
			Iterator<LinkedList<ZdResultSingle>> iter = allzd.iterator();
			iter.hasNext();
			LinkedList<ZdResultSingle> linkedList = iter.next();
			ZdResultSingle headinfo = linkedList.getFirst();
			String headzd = headinfo.getAendzdid();
			String headmeid = headinfo.getAendmeid();
			String headptp = headinfo.getAendptpid();
			String headcardmodel = headinfo.getAendcardmodel();
			
			LinkedList<ZdResultSingle> nextZd = linkedList;
			while(iter.hasNext()){
				nextZd = iter.next();
			}
			
			ZdResultSingle tailinfo = nextZd.getLast();
			String tailzd = tailinfo.getZendzdid();
			String tailmeid = tailinfo.getZendmeid();
			String tailptp = tailinfo.getZendptpid();
			String tailcardmodel = tailinfo.getZendcardmodel();
			
			List<ZhiluPtp> zlptplist = null ;
			
			//如果是以支路板开始的，那么找到客户侧端口
			if(tCardmodellist.contains(headcardmodel)){
				zlptplist =  resourceManager.queryZLClinetPtp( headptp );
			}
			//如果是以线路板开始的，那么找到一个随机支路板的客户侧端口
			else if(nCardmodellist.contains(headcardmodel)){
				zlptplist =  resourceManager.queryZLPtp( headmeid ,input.getRate());
			}
			
			if(zlptplist!=null && zlptplist.size()>0 ){
				
					ZhiluPtp ptp = zlptplist.get(0);
					
					ZdResultSingle zdResultSingle = new ZdResultSingle();
					zdResultSingle.setAendmeid(headmeid);
					zdResultSingle.setAendzdid(headzd);
					zdResultSingle.setAendptpid(ptp.getPtpobjectid());
					zdResultSingle.setAendcardid(ptp.getCardobjectid());
					zdResultSingle.setAendcardmodel(ptp.getCardmodel());
					zdResultSingle.setAendctp("");
					
					zdResultSingle.setZendmeid(headmeid);
					zdResultSingle.setZendzdid(headzd);
					zdResultSingle.setZendptpid(headptp);
					zdResultSingle.setZendcardid(headinfo.getAendcardid());
					zdResultSingle.setZendcardmodel(headinfo.getAendcardmodel());
					zdResultSingle.setZendctp("");
					
					zdResultSingle.setRouteType("2");
					linkedList.addFirst(zdResultSingle);
			}
			else{
				logger.info("没有查询到支路口信息：sncid："+ zdResult.getSncid()+ ", cardmodel:" + headcardmodel + ", ptpid:" + headptp) ; 
				return false;
			}
			
			
			List<ZhiluPtp> endzlptplist = null ;
			
			//如果是以支路板开始的，那么找到客户侧端口
			if(tCardmodellist.contains(tailcardmodel)){
				endzlptplist =  resourceManager.queryZLClinetPtp( tailptp );
			}
			//如果是以线路板开始的，那么找到一个随机支路板的客户侧端口
			else if(nCardmodellist.contains(tailcardmodel)){
				endzlptplist =  resourceManager.queryZLPtp( tailmeid , input.getRate() );
			}
			
			if( endzlptplist!=null && endzlptplist.size()>0 ){
				
					ZhiluPtp ptp = endzlptplist.get(0);
					
					ZdResultSingle zdResultSingle = new ZdResultSingle();
					zdResultSingle.setAendmeid(tailmeid);
					zdResultSingle.setAendzdid(tailzd);
					zdResultSingle.setAendptpid(tailptp);
					zdResultSingle.setAendcardid(tailinfo.getZendcardid());
					zdResultSingle.setAendcardmodel(tailcardmodel);
					zdResultSingle.setAendctp("");
					
					zdResultSingle.setZendmeid(tailmeid);
					zdResultSingle.setZendzdid(tailzd);
					zdResultSingle.setZendptpid(ptp.getPtpobjectid());
					zdResultSingle.setZendcardid(ptp.getCardobjectid());
					zdResultSingle.setZendcardmodel(ptp.getCardmodel());
					zdResultSingle.setZendctp("");
					
					zdResultSingle.setRouteType("2");
					nextZd.addLast(zdResultSingle);
			}
			else{
				logger.info("没有查询到支路口信息：sncid："+ zdResult.getSncid()+ ", cardmodel:" + headcardmodel + ", ptpid:" + headptp) ; 
				return false;
			}
			
		}
		
			//客户层路径
			ODU0 odu0 = null;
			ODU1 odu1 = null;
			ODU2 odu2 = null;
			ODU3 odu3 = null;
			ODU4 odu4 = null;
			OCH och = null;
			
			boolean passMCard = false;
			Iterator<LinkedList<ZdResultSingle>> iter = allzd.iterator();
			iter.hasNext();
			LinkedList<ZdResultSingle> linkedList = iter.next();
			for (int j = 0; j < linkedList.size(); j++) {
					ZdResultSingle zdResultSingle = linkedList.get(j);
					if(mCardmodel.contains(zdResultSingle.getZendcardmodel())){
						if(!StringUtils.isEmpty(zdResultSingle.getZendctp())){
							ODU odu = ConstBusiness.getOduByCtp(zdResultSingle.getZendctp());
							if( odu!=null && odu instanceof OCH ){
								och = (OCH)odu;
							}
						}
						passMCard = true ;
					}
					else{
						if(passMCard){
							break ;
						}
						else{
							if(!StringUtils.isEmpty(zdResultSingle.getZendctp())){
								ODU odu = ConstBusiness.getOduByCtp(zdResultSingle.getZendctp());
								if( odu!=null && odu instanceof ODU0 ){
									odu0 = (ODU0)odu;
								}
								if( odu!=null && odu instanceof ODU1 ){
									odu1 = (ODU1)odu;
								}
								if( odu!=null && odu instanceof ODU2 ){
									odu2 = (ODU2)odu;
								}
								if( odu!=null && odu instanceof ODU3 ){
									odu3 = (ODU3)odu;
								}
								if( odu!=null && odu instanceof ODU4 ){
									odu4 = (ODU4)odu;
								}
								if( odu!=null && odu instanceof OCH ){
									och = (OCH)odu;
								}
							}
						}
					}
			}
			
			/**
			if( och!=null  &&( odu0!=null || odu1!=null || odu2!=null || odu3!=null)){
				
				//String headctp = "/och="+och.getIndex()+  "/odu4="+ (odu4==null?"1":odu4.getIndex());
				String headctp = "/och=1" +  "/odu4="+ (odu4==null?"1":odu4.getIndex());
				if(odu0!=null){
					headctp += "/odu0="+ odu0.getIndex();
				}
				else if(odu1!=null){
					headctp += "/odu1="+ odu1.getIndex();
				}
				else if(odu2!=null){
					headctp += "/odu2="+ odu2.getIndex();
				}
				else if(odu3!=null){
					headctp += "/odu3="+ odu3.getIndex();
				}
				
				logger.info("2zdResult-id:" + zdResult.getSncid()  + ", rate: " + input.getRate());
				logger.info("2headctp:" + headctp );
				
				int lastSep = headctp.lastIndexOf("/");
				int lastSep2 = headctp.lastIndexOf("=")+1;
				String ctp = headctp.substring(lastSep,lastSep2) + "1";
				
				zdResult.setCtpStr(headctp);
				zdResult.setHeadctpStr(ctp);
				
			}
			else{
			*/
				//String headctp = "/och="+och.getIndex()+  "/odu4=1";
				String headctp = "/och=1" + "/odu4=1";
				
				String oduinfo = zdResult.getODUinfo(input.getRate());
				logger.info("zdResult-id:" + zdResult.getSncid()  + ", rate: " + input.getRate());
				logger.info("oduinfo:" + oduinfo );
				int lastSep = oduinfo.lastIndexOf("/");
				int firstCommor = oduinfo.lastIndexOf("=")+1;
				String ctp = oduinfo.substring(lastSep , firstCommor)+ "1";

				int lastSep2 = oduinfo.lastIndexOf("/");
				int firstCommor2 = oduinfo.indexOf(",") == -1 ? oduinfo.length():  oduinfo.indexOf(",");
				headctp = headctp+= oduinfo.substring(lastSep2, firstCommor2);
				
				zdResult.setCtpStr(headctp);
				zdResult.setHeadctpStr(ctp);
				
				
			//}
			
			
			/**
			String headCtp = "";
			Integer inputoduk = ConstBusiness.odukMap.get( input.getRate() );
			if( inputoduk!=null ){
				if( inputoduk.intValue()==1 ){
					headCtp = "/odu0=1" ; 
				}
				else if( inputoduk.intValue()==2 ){
					headCtp = "/odu1=1" ; 
				}
				else if( inputoduk.intValue()==3 ){
					headCtp = "/odu2=1" ; 
				}
				else if( inputoduk.intValue()==4 ){
					headCtp = "/odu3=1" ; 
				}
				else if( inputoduk.intValue()==5 ){
					headCtp = "/odu4=1" ; 
				}
				zdResult.setHeadctpStr(headCtp);
			}
			else{
				Integer clientoduk = ConstBusiness.rateMap.get( input.getRate() );
				if( clientoduk.intValue()==0 ){
					headCtp = "/odu0=1" ; 
				}
				else if( clientoduk.intValue()==2 ){
					headCtp = "/odu1=1" ; 
				}
				else if( clientoduk.intValue()==3 ){
					headCtp = "/odu2=1" ; 
				}
				else if( clientoduk.intValue()==4 ){
					headCtp = "/odu3=1" ; 
				}
				else if( clientoduk.intValue()==5 ){
					headCtp = "/odu4=1" ; 
				}
				zdResult.setHeadctpStr(headCtp);
			}
			*/
			
			
			return true;
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
	
	public static void main(String[] args) {
	}
	
}
