package com.zznode.opentnms.isearch.otnRouteService.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.otnRouteService.cache.CachedClient;
import com.zznode.opentnms.isearch.otnRouteService.consts.ConstBusiness;
import com.zznode.opentnms.isearch.otnRouteService.consts.enums.Rate;
import com.zznode.opentnms.isearch.otnRouteService.db.DBUtil;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.DSR;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.OCH;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU0;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU1;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU2;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU3;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU4;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ZdResult;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ZdResultSingle;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSnc;
import com.zznode.opentnms.isearch.otnRouteService.db.po.WdmSncRoute;
import com.zznode.opentnms.isearch.otnRouteService.manage.ResourceManager;
import com.zznode.opentnms.isearch.otnRouteService.util.PropertiesHander;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.BusinessAvator;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;
import com.zznode.opentnms.isearch.routeAlgorithm.core.matrix.Matrix;

@Component
public class BusiAnalyser {

	private static final Logger logger = LoggerFactory.getLogger(BusiAnalyser.class);   
	
	@Autowired
	public DBUtil dbUtil;
	
	@Autowired
	public CachedClient cachedClient;
	
	@Autowired
	public ResourceManager resourceManager;
	
	private final String memcacTag =  PropertiesHander.getProperty("memcacTag");
	
	List<String>  mCardmodel = new ArrayList<String>(); 
	
	public BusiAnalyser(){}
	
	public void analyseAllBusi() throws Exception {

		//所有合波设备
	    mCardmodel = resourceManager.getMCardModel();
	    
		//1.查询出topo矩阵
		BusinessAvator businessAvator = new BusinessAvator();
		businessAvator.setKey(PropertiesHander.getProperty("BusinessAvator"));
		Object graph = cachedClient.get(businessAvator.getKey());
		if( graph==null){
			throw new RuntimeException("未找到链接图信息，无法计算站点间空闲资源");
		}
		
		//2.拓扑中的每两个点都需要进行计算
		/**
		Matrix matrix = (Matrix)graph;
		Section[][] route =  matrix.getMatrix();
		for (int i = 0; i < route.length; i++) {
			for (int j = 0; j < route.length; j++) {
					Section section = route[i][j];
					if( section!= null){
						List<ZdResult> resultlist = analyseOtnResource( section.getAendNode(), section.getZendNode() );
						String key = section.getAendNode() + "|" + section.getZendNode() ;
						cachedClient.set( memcacTag +key, 0, resultlist );
					}
				}
		}
		*/
		List<ZdResult> resultlist = analyseOtnResource( "16020002", "16040002" );
		String key = "16020002" + "|" + "16040002" ;
		cachedClient.set( memcacTag +key, 0, resultlist );
		
		List<ZdResult> resultlist2 = analyseOtnResource( "16040002", "16030001" );
		String key2 = "16040002" + "|" + "16030001" ;
		cachedClient.set( memcacTag +key2, 0, resultlist2 );
	}
	
	private String getCtpInfo( List<WdmSncRoute>  routelist , boolean isforwardorder){
		
		if(isforwardorder){
			for (int i = 0; i < routelist.size(); i++) {
	    		WdmSncRoute route =routelist.get(i);
	    		if( ConstBusiness.passedProtectedCardSet.contains( route.getAcardmodel() )
	    				|| ConstBusiness.passedProtectedCardSet.contains( route.getZcardmodel() ) ){
	    			continue;
	    		}
	    		return  routelist.get(i).m_ZEndCtpId;
			}
		}
		else{
			for (int i = routelist.size()-1; i > -1; i--) {
	    		WdmSncRoute route =routelist.get(i);
	    		if( ConstBusiness.passedProtectedCardSet.contains( route.getAcardmodel() )
	    				|| ConstBusiness.passedProtectedCardSet.contains( route.getZcardmodel() ) ){
	    			continue;
	    		}
	    		return  routelist.get(i).m_AEndCtpId;
			}
		}
		
		return null;
		
	}

	/**
	 * 计算两个站点间的业务资源
	 * @param aendZDid
	 * @param zendZDid
	 * @return
	 * @throws Exception
	 */
	private List<ZdResult> analyseOtnResource(String aendZDid, String zendZDid) throws Exception {
		
		logger.info("start Analyser::" + aendZDid +" ---" + zendZDid);
		//1.查询两个站点之间的正向单向波道。
	  	List<DbWdmSnc> wdmsnclist = resourceManager.getWdmSncByAZZhandian(aendZDid, zendZDid,"0");
	  	
	  	//查询正向波道的波道路由
	  	for (Iterator<DbWdmSnc> iter = wdmsnclist.iterator(); iter.hasNext();) {
	  		DbWdmSnc wdmsnc = iter.next();
	  		List<WdmSncRoute> wdmsncroutelist = resourceManager.queryForRoute(wdmsnc.getObjectId(),"0");
	    	if( wdmsncroutelist ==null || wdmsncroutelist.size()==0){
	    		iter.remove() ; 
	    		continue ;
	    	}
	    	wdmsnc.setWdmsncroutelist( wdmsncroutelist );
	  	}
	  	
	  	//2.查询两个站点之间的正向双向波道。
	  	List<DbWdmSnc> wdmsncDoublelist = resourceManager.getWdmSncByAZZhandian(aendZDid, zendZDid,"1");
	  	
	  	//查询正向波道的波道路由
	  	for (Iterator<DbWdmSnc> iter = wdmsncDoublelist.iterator(); iter.hasNext();) {
	  		DbWdmSnc wdmsnc = iter.next();
	  		List<WdmSncRoute> wdmsncroutelist = resourceManager.queryForRoute(wdmsnc.getObjectId(),"0");
	    	if( wdmsncroutelist ==null || wdmsncroutelist.size()==0){
	    		iter.remove() ; 
	    		continue ;
	    	}
	    	wdmsnc.setWdmsncroutelist( wdmsncroutelist );
	  	}
	  	
	  	
	  	//3.查询反向波道
	  	List<DbWdmSnc> wdmsncReverselist = resourceManager.getWdmSncByAZZhandian( zendZDid , aendZDid,"1");
	  	
	  	//4.查询反向波道的波道路由
	  	for (Iterator<DbWdmSnc> iter = wdmsncReverselist.iterator(); iter.hasNext();) {
	  		DbWdmSnc wdmsnc = iter.next();
	  		List<WdmSncRoute> wdmsncroutelist = resourceManager.queryForRoute(wdmsnc.getObjectId(),"1");
	    	if( wdmsncroutelist ==null || wdmsncroutelist.size()==0){
	    		iter.remove() ; 
	    		continue ;
	    	}
	    	wdmsnc.setWdmsncroutelist( wdmsncroutelist );
	    	wdmsnc.setIsReverse(Boolean.TRUE);
	  	}
	  		
	  	wdmsnclist.addAll(wdmsncDoublelist);
	  	wdmsnclist.addAll(wdmsncReverselist);
	  	logger.info("wdmsnc count::" + wdmsnclist.size());
	  	
	  	//5.循环所有波道，分析oduk的承载关系
	  	for (Iterator<DbWdmSnc> iter = wdmsnclist.iterator(); iter.hasNext();) {
	  		DbWdmSnc wdmsnc = iter.next();
	  		Integer rate = wdmsnc.getRate();
	  		if( ConstBusiness.rateMap.keySet().contains( rate ) ){	
    	  		wdmsnc.setLayerdesc( Rate.client.getName() );
    	  	}else if( ConstBusiness.odu0rateList.contains( rate ) ){	
    	  		wdmsnc.setLayerdesc( Rate.odu0.getName() );
    	  	}else if( ConstBusiness.odu1rateList.contains( rate ) ){	
    	  		wdmsnc.setLayerdesc( Rate.odu1.getName() );
    	  	}else if( ConstBusiness.odu2rateList.contains( rate ) ){	
    	  		wdmsnc.setLayerdesc( Rate.odu2.getName() );
    	  	}else if( ConstBusiness.odu3rateList.contains( rate ) ){	
    	  		wdmsnc.setLayerdesc( Rate.odu3.getName() );
    	  	}else if( ConstBusiness.odu4rateList.contains( rate ) ){	
    	  		wdmsnc.setLayerdesc( Rate.odu4.getName() );
    	  	}else if( ConstBusiness.ochrateList.contains( rate ) ){	
    	  		wdmsnc.setLayerdesc( Rate.och.getName() );
    	  	}else{
    	  		logger.error("missing layerdesc , wdmsncid::" + wdmsnc.getObjectId());
    	  	}
	    	
	    	String headptpStr = wdmsnc.getWdmsncroutelist().get(0).m_AEndMeObjectId + "|" + wdmsnc.getWdmsncroutelist().get(0).m_AEndPtpObjectId ; 
	    	wdmsnc.setHeadptpStr(headptpStr);
	    	
	    	String headctpStr =  getCtpInfo(wdmsnc.getWdmsncroutelist(), true);  
	    	if(StringUtils.isEmpty(headctpStr)){
	    		headctpStr = getCtpInfo(wdmsnc.getWdmsncroutelist(), false);
	    	}
	    	if( wdmsnc.getIsReverse() ){
	    		headctpStr = getCtpInfo(wdmsnc.getWdmsncroutelist(), false);
	    		if(StringUtils.isEmpty(headctpStr)){
		    		headctpStr = getCtpInfo(wdmsnc.getWdmsncroutelist(), true);  
		    	}
	    	}
	    	wdmsnc.setHeadctpSer(headctpStr);
		    	
		    for (int j = 0; j < wdmsnc.getWdmsncroutelist().size(); j++) {
		    	WdmSncRoute route = wdmsnc.getWdmsncroutelist().get(j);
		    	if( mCardmodel.contains(route.acardmodel)){
		    		break;
		    	}
		    	String ptpStr = route.m_AEndMeObjectId + "|" + route.m_AEndPtpObjectId ; 
		    	wdmsnc.getPassedPtplist().add(ptpStr);
		    }
	  	}
	  	
	  	
	  	//6.分析各层数据情况
	  	List<DbWdmSnc> wdmsncClientResult = new ArrayList<DbWdmSnc>();
	  	List<DbWdmSnc> wdmsncOdu0Result = new ArrayList<DbWdmSnc>();
	  	List<DbWdmSnc> wdmsncOdu1Result = new ArrayList<DbWdmSnc>();
	  	List<DbWdmSnc> wdmsncOdu2Result = new ArrayList<DbWdmSnc>();
	  	List<DbWdmSnc> wdmsncOdu3Result = new ArrayList<DbWdmSnc>();
	  	List<DbWdmSnc> wdmsncOdu4Result = new ArrayList<DbWdmSnc>();
	  	List<DbWdmSnc> wdmsncOchResult = new ArrayList<DbWdmSnc>();
	  	
	  	//7.划分各层数据
	  	for (Iterator<DbWdmSnc> iter = wdmsnclist.iterator(); iter.hasNext();) {
			DbWdmSnc wdmsnc = iter.next();
			if( !wdmsnc.getLayerdesc().equals( Rate.och.getName()) ){
				
				ODU odu = ConstBusiness.getOduByCtp(wdmsnc.getHeadctpSer());
				if(odu==null){
					logger.error("分析CTP失败,ctp:" + wdmsnc.getHeadctpSer() + ",sncid:"+ wdmsnc.getObjectId());
					continue;
				}
				
				ODU sncodu = ConstBusiness.getOduByCtp(wdmsnc.getHeadctpSer());
				sncodu.setRate(wdmsnc.getRate());
				sncodu.setSncobjectid(wdmsnc.getObjectId());
				wdmsnc.setOdu(sncodu);
			}
	   		
			if( wdmsnc.getLayerdesc().equals( Rate.client.getName() )){
		   		wdmsncClientResult.add(wdmsnc);
		   	}
			else if( wdmsnc.getLayerdesc().equals( Rate.odu0.getName())){
				wdmsncOdu0Result.add(wdmsnc);
			}
			else if( wdmsnc.getLayerdesc().equals( Rate.odu1.getName())){
				wdmsncOdu1Result.add(wdmsnc);
			}
			else if( wdmsnc.getLayerdesc().equals( Rate.odu2.getName())){
				wdmsncOdu2Result.add(wdmsnc);
			}
			else if( wdmsnc.getLayerdesc().equals( Rate.odu3.getName())){
				wdmsncOdu3Result.add(wdmsnc);
			}
			else if( wdmsnc.getLayerdesc().equals( Rate.odu4.getName())){
				wdmsncOdu4Result.add(wdmsnc);
			}
			else if( wdmsnc.getLayerdesc().equals( Rate.och.getName())){
				wdmsncOchResult.add(wdmsnc);
			}
		}
	  	
	  	logger.info("wdmsncClientResult count::" + wdmsncClientResult.size());
	  	logger.info("wdmsncOdu0Result count::" + wdmsncOdu0Result.size());
	  	logger.info("wdmsncOdu1Result count::" + wdmsncOdu1Result.size());
	  	logger.info("wdmsncOdu2Result count::" + wdmsncOdu2Result.size());
	  	logger.info("wdmsncOdu3Result count::" + wdmsncOdu3Result.size());
	  	logger.info("wdmsncOdu4Result count::" + wdmsncOdu4Result.size());
	  	logger.info("wdmsncOchResult count::" + wdmsncOchResult.size());
	  	
	    //8.处理odu0层路径
	    for (int i = 0; i < wdmsncOdu0Result.size(); i++) {
	    	DbWdmSnc wdmsnc = wdmsncOdu0Result.get(i);
	    	ODU0 odu0 = (ODU0)wdmsnc.getOdu();  
    		//查询所有client层数据
    		for (int j = 0; j < wdmsncClientResult.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
					ODU odu = wdmsncin.getOdu();
					odu0.setDsr((DSR)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    }
	    	
	    //9.处理odu1层路径
	    for (int i = 0; i < wdmsncOdu1Result.size(); i++) {
	    	DbWdmSnc wdmsnc = wdmsncOdu1Result.get(i);
			ODU1 odu1 = (ODU1)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu1.getDsrlist().add((DSR)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu0Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu1.getOdu0list().add((ODU0)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    }
	    
	    //10.处理odu2层路径
	    for (int i = 0; i < wdmsncOdu2Result.size(); i++) {
	    	DbWdmSnc wdmsnc = wdmsncOdu2Result.get(i);
	    	ODU2 odu2 = (ODU2)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu2.getDsrlist().add((DSR)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu0Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu2.getOdu0list().add((ODU0)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu1Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu2.getOdu1list().add((ODU1)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    }
	    
	    //11.处理odu3层路径
	    for (int i = 0; i < wdmsncOdu3Result.size(); i++) {
	    	DbWdmSnc wdmsnc = wdmsncOdu3Result.get(i);
	    	ODU3 odu3 = (ODU3)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu3.getDsrlist().add((DSR)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu0Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu3.getOdu0list().add((ODU0)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu1Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu3.getOdu1list().add((ODU1)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu2Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu2Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu3.getOdu2list().add((ODU2)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    }
	    
	    //12.处理odu4层路径
	    for (int i = 0; i < wdmsncOdu4Result.size(); i++) {
	    	DbWdmSnc wdmsnc = wdmsncOdu4Result.get(i);
	    	ODU4 odu4 = (ODU4)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu4.getDsrlist().add((DSR)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu0Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu4.getOdu0list().add((ODU0)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu1Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu4.getOdu1list().add((ODU1)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu2Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu2Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu4.getOdu2list().add((ODU2)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu3Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu3Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					odu4.getOdu3list().add((ODU3)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    }
	    
	    //13.处理och层路径
	    for (int i = 0; i < wdmsncOchResult.size(); i++) {
	    	DbWdmSnc wdmsnc = wdmsncOchResult.get(i);
	    	OCH och = new OCH();
	    	wdmsnc.setOdu(och);
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					och.getDsrlist().add((DSR)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu0Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					och.getOdu0list().add((ODU0)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu1Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					och.getOdu1list().add((ODU1)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu2Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu2Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					och.getOdu2list().add((ODU2)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu3Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu3Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					och.getOdu3list().add((ODU3)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    	for (int j = 0; j < wdmsncOdu4Result.size(); j++) {
    			DbWdmSnc wdmsncin = wdmsncOdu4Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					och.getOdu4list().add((ODU4)odu);
					wdmsncin.setIsCaculated(Boolean.TRUE);
				}
			}
	    }

	    List<DbWdmSnc> wdmsncAllResult = new ArrayList<DbWdmSnc>();
	    wdmsncAllResult.addAll(wdmsncClientResult);
	    wdmsncAllResult.addAll(wdmsncOdu0Result);
	    wdmsncAllResult.addAll(wdmsncOdu1Result);
	    wdmsncAllResult.addAll(wdmsncOdu2Result);
	    wdmsncAllResult.addAll(wdmsncOdu3Result);
	    wdmsncAllResult.addAll(wdmsncOdu4Result);
	    wdmsncAllResult.addAll(wdmsncOchResult);
	  	
	    
	    //14.整合查询结果
	    List<ZdResult> allrst = new ArrayList<ZdResult>();
	  	for (Iterator<DbWdmSnc> iter = wdmsncAllResult.iterator(); iter.hasNext();) {
	  		
	  		DbWdmSnc wdmsnc = iter.next();
	  		ZdResult zdResult = new ZdResult();
	  		zdResult.setLayerdesc( wdmsnc.getLayerdesc() );
	    	zdResult.setRate( wdmsnc.getRate()+"");
	    	zdResult.setRatedesc( wdmsnc.getRatedesc() );
	    	zdResult.setSncid(wdmsnc.getObjectId());
	    	zdResult.setSncname( wdmsnc.getSncName() );
	    	zdResult.setOdu( wdmsnc.getOdu());
	    	
	    	Map<String,LinkedList<ZdResultSingle>> zdmap = new LinkedHashMap<String,LinkedList<ZdResultSingle>>();
	    	
	    	for (int j = 0; j < wdmsnc.getWdmsncroutelist().size(); j++) {
	    		WdmSncRoute route = wdmsnc.getWdmsncroutelist().get(j);
	    	  		
	    		ZdResultSingle zda = new ZdResultSingle();
	    		zda.setAendzdid(route.ajuzhan);
	    		zda.setAendmeid(route.m_AEndMeObjectId);
	    		zda.setAendptpid(route.m_AEndPtpObjectId);
	    		zda.setAendcardmodel(route.acardmodel);
	    		zda.setAendctp(route.m_AEndCtpId);
	    	  		
	    		zda.setZendzdid(route.zjuzhan);
	    		zda.setZendmeid(route.m_ZEndMeObjectId);
	    		zda.setZendptpid(route.m_ZEndPtpObjectId);
	    		zda.setZendcardmodel(route.zcardmodel);
	    		zda.setZendctp(route.m_ZEndCtpId);
	    	  		
	    		if( zdmap.get( zda.getAendzdid() )!=null ){
	    			LinkedList<ZdResultSingle>  zdlist = zdmap.get( zda.getAendzdid() );
	    			zdlist.add(zda);
	    		}
	    		else{
	    			LinkedList<ZdResultSingle> zdlist = new LinkedList<ZdResultSingle>();
	    			zdlist.add(zda);
	    	  		zdmap.put(zda.getAendzdid(), zdlist);
	    	  	}
	    	  		
	    	}
	    	zdResult.setZdmap(zdmap);
	    	allrst.add(zdResult);
	    	logger.info("zdResult info::" + zdResult);
	  	}
	  	
	  	return allrst ; 
	  	
	}

	
}
