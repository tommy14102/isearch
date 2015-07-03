package com.zznode.opentnms.isearch.otnRouteService.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.model.bo.ConstBusiness;
import com.zznode.opentnms.isearch.model.bo.DSR;
import com.zznode.opentnms.isearch.model.bo.OCH;
import com.zznode.opentnms.isearch.model.bo.ODU;
import com.zznode.opentnms.isearch.model.bo.ODU0;
import com.zznode.opentnms.isearch.model.bo.ODU1;
import com.zznode.opentnms.isearch.model.bo.ODU2;
import com.zznode.opentnms.isearch.model.bo.ODU3;
import com.zznode.opentnms.isearch.model.bo.ODU4;
import com.zznode.opentnms.isearch.model.bo.ZdResult;
import com.zznode.opentnms.isearch.model.bo.ZdResultSingle;
import com.zznode.opentnms.isearch.otnRouteService.Main;
import com.zznode.opentnms.isearch.otnRouteService.cache.CachedClient;
import com.zznode.opentnms.isearch.otnRouteService.consts.enums.Rate;
import com.zznode.opentnms.isearch.otnRouteService.db.DBUtil;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSnc;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSncAll;
import com.zznode.opentnms.isearch.otnRouteService.db.po.WdmSncRoute;
import com.zznode.opentnms.isearch.otnRouteService.manage.ResourceManager;
import com.zznode.opentnms.isearch.otnRouteService.util.PropertiesHander;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.BusinessAvator;

@Component
public class BusiAnalyser {

	private static final Logger logger = LoggerFactory.getLogger(BusiAnalyser.class);   
	
	@Autowired
	public DBUtil dbUtil = (DBUtil)Main.factory.getBean("dbUtil");
	
	@Autowired
	public CachedClient cachedClient = (CachedClient)Main.factory.getBean("cachedClient"); ;
	
	@Autowired
	public ResourceManager resourceManager = (ResourceManager)Main.factory.getBean("resourceManager"); ;
	
	private final String memcacTag =  PropertiesHander.getProperty("memcacTag");
	
	private List<String>  mCardmodel = new ArrayList<String>(); 
	
	private Map<String,List<DbWdmSncAll>> emsDataMap = new HashMap<String,List<DbWdmSncAll>>();
	
	public BusiAnalyser(){
	}
	
	public BusiAnalyser(String emsid){
		//查询ems的所有的波道和波道路由。
		//queryAllWdmsnc(emsid);
		queryZdWmdsnc(emsid);
	}
	
	
	private void queryAllWdmsnc(String emsid) {
		
		Collection<DbWdmSncAll> allsnc = resourceManager.getWdmSncAll(emsid);
		for (Iterator<DbWdmSncAll> iter = allsnc.iterator(); iter.hasNext();) {
			DbWdmSncAll dbWdmSnc = iter.next();
			String aendjz = dbWdmSnc.getAendjz();
			String zendjz = dbWdmSnc.getZendjz();
			String key = aendjz + "|" + zendjz ; 
			if( emsDataMap.containsKey(key) ){
				emsDataMap.get(key).add(dbWdmSnc);
			}
			else{
				List<DbWdmSncAll> snclist = new ArrayList<DbWdmSncAll>();
				snclist.add(dbWdmSnc);
				emsDataMap.put(key, snclist);
			}
		}
	}
	
	private void queryZdWmdsnc(String emsid) {
		
		emsDataMap = resourceManager.getZdWmdsnc(emsid);
	}

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
	public List<ZdResult> analyseOtnResource(String aendZDid, String zendZDid) throws Exception {
		
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
	  	logger.info( "单向波道数量:" + wdmsnclist.size() );
	  	
	  	if(true){
			return new ArrayList<ZdResult>();
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
	  	logger.info( "双向波道数量:" + wdmsncDoublelist.size() );
	  	
	  	//3.查询反向波道
	  	List<DbWdmSnc> wdmsncReverselist = resourceManager.getWdmSncByAZZhandian( zendZDid , aendZDid,"1");
	  	//查询反向波道的波道路由
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
	  	logger.info( "双向反向波道数量:" + wdmsncReverselist.size() );
	  	
	  	wdmsnclist.addAll(wdmsncReverselist);
	  	wdmsnclist.addAll(wdmsncDoublelist);
	  	
	  	logger.info("wdmsnc count::" + wdmsnclist.size());
	  	
	  	//5.循环所有波道，分析oduk的承载关系
	  	for (Iterator<DbWdmSnc> iter = wdmsnclist.iterator(); iter.hasNext();) {
	  		DbWdmSnc wdmsnc = iter.next();
	  		Integer rate = wdmsnc.getRate();
	  		if( ConstBusiness.rateDescMap.keySet().contains( rate ) ){	
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
	  		if( wdmsnc.isReverse ){
	  			continue ;
	  		}
	  		ZdResult zdResult = new ZdResult();
	  		zdResult.setLayerdesc( wdmsnc.getLayerdesc() );
	    	zdResult.setRate( wdmsnc.getRate()+"");
	    	zdResult.setRatedesc( wdmsnc.getRatedesc() );
	    	zdResult.setSncid(wdmsnc.getObjectId());
	    	zdResult.setSncname( wdmsnc.getSncName() );
	    	zdResult.setOdu( wdmsnc.getOdu());
	    	zdResult.setDirection(wdmsnc.getDirection());
	    	
	    	Map<String,LinkedList<ZdResultSingle>> zdmap = new LinkedHashMap<String,LinkedList<ZdResultSingle>>();
	    	
	    	for (int j = 0; j < wdmsnc.getWdmsncroutelist().size(); j++) {
	    		WdmSncRoute route = wdmsnc.getWdmsncroutelist().get(j);
	    	  		
	    		ZdResultSingle zda = new ZdResultSingle();
	    		zda.setAendmeid(route.m_AEndMeObjectId);
	    		zda.setAendptpid(route.m_AEndPtpObjectId);
	    		zda.setAendcardmodel(route.acardmodel);
	    		zda.setAendctp(route.m_AEndCtpId);
	    	  		
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
	
	
	/**
	 * 计算两个站点间的业务资源
	 * @param aendZDid
	 * @param zendZDid
	 * @return
	 * @throws Exception
	 */
	public List<ZdResult> analyseOtnResourceV2(String aendZDid, String zendZDid) throws Exception {
		
		logger.info("start Analyser::" + aendZDid +" ---" + zendZDid);
		
		//1.查询两个站点之间的正向单向波道。
		String key = aendZDid + "|" + zendZDid ;
		List<DbWdmSncAll> wdmsnclist =  emsDataMap.get(key);
		if(wdmsnclist!=null){
			logger.info( "正向波道数量:" + wdmsnclist.size() );
		}else{
			wdmsnclist = new ArrayList<DbWdmSncAll>();
		}
		
		
	  	//2.查询反向波道
		String keyReverse = zendZDid + "|" + aendZDid ;
		List<DbWdmSncAll> sncReverselist =  emsDataMap.get(keyReverse);
		if(sncReverselist!=null){
			for (int i = 0; i < sncReverselist.size(); i++) {
				DbWdmSncAll snc =  sncReverselist.get(i);
				if( snc.getDirection().intValue()== 1 ){
					snc.setWdmsncroutelist(snc.getWdmsncrouteReverselist());
					snc.setIsReverse(Boolean.TRUE);
					wdmsnclist.add(snc);
				}
			}
		}
		
		if( wdmsnclist==null||wdmsnclist.size()==0 ){
			return new ArrayList<ZdResult>() ;
		}
		else{
			logger.info( "波道总数量:" + wdmsnclist.size() );
		}
		
	  	//5.循环所有波道，分析oduk的承载关系
	  	for (Iterator<DbWdmSncAll> iter = wdmsnclist.iterator(); iter.hasNext();) {
	  		DbWdmSncAll wdmsnc = iter.next();
	  		Integer rate = wdmsnc.getRate();
	  		if( ConstBusiness.rateDescMap.keySet().contains( rate ) ){	
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
    	  		logger.error("missing layerdesc , wdmsncid::" + wdmsnc.getObjectId() +", rate::" + rate);
    	  		iter.remove();
    	  		continue;
    	  		
    	  	}
	    	
	    	String headptpStr = wdmsnc.getWdmsncroutelist().get(0).m_AEndMeObjectId + "|" + wdmsnc.getWdmsncroutelist().get(0).m_AEndPtpObjectId ; 
	    	wdmsnc.setHeadptpStr(headptpStr);
	    	
	    	String headctpStr =  getCtpInfo(wdmsnc.getWdmsncroutelist(), true);  
	    	if(StringUtils.isEmpty(headctpStr)){
	    		headctpStr = getCtpInfo(wdmsnc.getWdmsncroutelist(), false);
	    	}
	    	/**
	    	if( wdmsnc.getIsReverse() ){
	    		headctpStr = getCtpInfo(wdmsnc.getWdmsncroutelist(), false);
	    		if(StringUtils.isEmpty(headctpStr)){
		    		headctpStr = getCtpInfo(wdmsnc.getWdmsncroutelist(), true);  
		    	}
	    	}
	    	*/
	    	wdmsnc.setHeadctpSer(headctpStr);
	    	if( (headctpStr==null|| headctpStr.length()==0) && !wdmsnc.getLayerdesc().equals( Rate.och.getName() )  ){
	    		logger.error("missing headctpStr , wdmsncid::" + wdmsnc.getObjectId() +", rate::" + rate);
    	  		iter.remove();
    	  		continue;
	    	}
		    	
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
	  	List<DbWdmSncAll> wdmsncClientResult = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOdu0Result = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOdu1Result = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOdu2Result = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOdu3Result = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOdu4Result = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOchResult = new ArrayList<DbWdmSncAll>();
	  	
	  	//7.划分各层数据
	  	for (Iterator<DbWdmSncAll> iter = wdmsnclist.iterator(); iter.hasNext();) {
	  		DbWdmSncAll wdmsnc = iter.next();
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
	  	
	  	/**
	    //8.处理odu0层路径
	    for (int i = 0; i < wdmsncOdu0Result.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOdu0Result.get(i);
	    	ODU0 odu0 = (ODU0)wdmsnc.getOdu();  
    		//查询所有client层数据
    		for (int j = 0; j < wdmsncClientResult.size(); j++) {
    			DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof DSR){
						odu0.setDsr((DSR)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据0，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
					}
				}
			}
	    }
	    	
	    //9.处理odu1层路径
	    for (int i = 0; i < wdmsncOdu1Result.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOdu1Result.get(i);
			ODU1 odu1 = (ODU1)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof DSR){
						odu1.getDsrlist().add((DSR)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据1，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu0Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU0){
						odu1.getOdu0list().add((ODU0)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据1，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
					}
				}
			}
	    }
	    
	    //10.处理odu2层路径
	    for (int i = 0; i < wdmsncOdu2Result.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOdu2Result.get(i);
	    	ODU2 odu2 = (ODU2)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof DSR){
						odu2.getDsrlist().add((DSR)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据2，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu0Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU0){
						odu2.getOdu0list().add((ODU0)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据2，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu1Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU1){
						odu2.getOdu1list().add((ODU1)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据2，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    }
	    
	    //11.处理odu3层路径
	    for (int i = 0; i < wdmsncOdu3Result.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOdu3Result.get(i);
	    	ODU3 odu3 = (ODU3)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof DSR){
						odu3.getDsrlist().add((DSR)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据3，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu0Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU0){
						odu3.getOdu0list().add((ODU0)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据3，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu1Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU1){
						odu3.getOdu1list().add((ODU1)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据3，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu2Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu2Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU2){
						odu3.getOdu2list().add((ODU2)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据3，转换异常,odu2:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    }
	    
	    //12.处理odu4层路径
	    for (int i = 0; i < wdmsncOdu4Result.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOdu4Result.get(i);
	    	ODU4 odu4 = (ODU4)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof DSR){
						odu4.getDsrlist().add((DSR)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据4，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu0Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU0){
						odu4.getOdu0list().add((ODU0)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据4，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu1Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU1){
						odu4.getOdu1list().add((ODU1)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据4，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu2Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu2Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU2){
						odu4.getOdu2list().add((ODU2)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据4，转换异常,odu2:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu3Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu3Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU3){
						odu4.getOdu3list().add((ODU3)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据4，转换异常,odu3:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    }
	    
	    //13.处理och层路径
	    for (int i = 0; i < wdmsncOchResult.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOchResult.get(i);
	    	OCH och = new OCH();
	    	wdmsnc.setOdu(och);
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof DSR){
						och.getDsrlist().add((DSR)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据och，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu0Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU0){
						och.getOdu0list().add((ODU0)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据och，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu1Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU1){
						och.getOdu1list().add((ODU1)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据och，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu2Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu2Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU2){
						och.getOdu2list().add((ODU2)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据och，转换异常,odu2:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu3Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu3Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU3){
						och.getOdu3list().add((ODU3)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据och，转换异常,odu3:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    	for (int j = 0; j < wdmsncOdu4Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu4Result.get(j);
				if( !wdmsncin.isCaculated && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
					ODU odu = wdmsncin.getOdu();
					if( odu instanceof ODU4){
						och.getOdu4list().add((ODU4)odu);
						wdmsncin.setIsCaculated(Boolean.TRUE);
					}
					else{
						logger.error("关联下层数据och，转换异常,odu4:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
					}
				}
			}
	    }

	    List<DbWdmSncAll> wdmsncAllResult = new ArrayList<DbWdmSncAll>();
	    wdmsncAllResult.addAll(wdmsncClientResult);
	    wdmsncAllResult.addAll(wdmsncOdu0Result);
	    wdmsncAllResult.addAll(wdmsncOdu1Result);
	    wdmsncAllResult.addAll(wdmsncOdu2Result);
	    wdmsncAllResult.addAll(wdmsncOdu3Result);
	    wdmsncAllResult.addAll(wdmsncOdu4Result);
	    wdmsncAllResult.addAll(wdmsncOchResult);
	  	
	    
	    //14.整合查询结果
	    List<ZdResult> allrst = new ArrayList<ZdResult>();
	  	for (Iterator<DbWdmSncAll> iter = wdmsncAllResult.iterator(); iter.hasNext();) {
	  		
	  		DbWdmSncAll wdmsnc = iter.next();
	  		if( wdmsnc.isReverse ){
	  			continue ;
	  		}
	  		ZdResult zdResult = new ZdResult();
	  		zdResult.setLayerdesc( wdmsnc.getLayerdesc() );
	    	zdResult.setRate( wdmsnc.getRate()+"");
	    	zdResult.setRatedesc( wdmsnc.getRatedesc() );
	    	zdResult.setSncid(wdmsnc.getObjectId());
	    	zdResult.setSncname( wdmsnc.getSncName() );
	    	zdResult.setOdu( wdmsnc.getOdu());
	    	zdResult.setDirection(wdmsnc.getDirection());
	    	
	    	Map<String,LinkedList<ZdResultSingle>> zdmap = new LinkedHashMap<String,LinkedList<ZdResultSingle>>();
	    	
	    	for (int j = 0; j < wdmsnc.getWdmsncroutelist().size(); j++) {
	    		WdmSncRoute route = wdmsnc.getWdmsncroutelist().get(j);
	    	  		
	    		ZdResultSingle zda = new ZdResultSingle();
	    		zda.setAendmeid(route.m_AEndMeObjectId);
	    		zda.setAendptpid(route.m_AEndPtpObjectId);
	    		zda.setAendcardmodel(route.acardmodel);
	    		zda.setAendctp(route.m_AEndCtpId);
	    	  		
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
	  	
	  	*/
	  	
	  	return null ; 
	  	
	}
	

	/**
	 * 计算两个站点间的业务资源
	 * @param aendZDid
	 * @param zendZDid
	 * @return
	 * @throws Exception
	 */
	public List<ZdResult> analyseOtnResourceV3(String aendZDid, String zendZDid) throws Exception {
		
		logger.info("start Analyser::" + aendZDid +" ---" + zendZDid);
		
		List wdmsnclist = new ArrayList<DbWdmSncAll>();
		
		//1.查询两个站点之间的正向单向波道。
		String key = aendZDid + "|" + zendZDid ;
		List<DbWdmSncAll> wdmsnclistTp =  emsDataMap.get(key);
		if( wdmsnclistTp!=null){
			logger.info( "正向波道数量:" + wdmsnclistTp.size() );
			for (Iterator<DbWdmSncAll> iter = wdmsnclistTp.iterator(); iter.hasNext();) {
				DbWdmSncAll wdmsnc = iter.next();
				
				
		  		List<WdmSncRoute> wdmsncroutelist = resourceManager.queryForRoute(wdmsnc.getObjectId(),"0");
		    	if( wdmsncroutelist ==null || wdmsncroutelist.size()==0){
		    		iter.remove() ; 
		    		logger.info( "正向波道无路由，舍弃:" + wdmsnc.getObjectId() );
		    		continue ;
		    	}
		    	
		    	wdmsnc.setWdmsncroutelist( wdmsncroutelist );
		    	wdmsnc.setIsReverse(Boolean.FALSE);
		    	wdmsnclist.add(wdmsnc);
		  	}
		} 
		
	  	//2.查询反向波道
		String keyReverse = zendZDid + "|" + aendZDid ;
		List<DbWdmSncAll> sncReverselist =  emsDataMap.get(keyReverse);
		if(sncReverselist!=null){
			logger.info( "反向波道数量:" + sncReverselist.size() );
			//查询反向波道的波道路由
		  	for (Iterator<DbWdmSncAll> iter = sncReverselist.iterator(); iter.hasNext();) {
		  		DbWdmSncAll wdmsnc = iter.next();
		  		
		  		if( wdmsnc.getDirection().intValue() == 0 ){
		  			//单向舍弃
		  			iter.remove() ; 
		    		logger.info( "反向波道为单向路由，舍弃:" + wdmsnc.getObjectId() );
		    		continue ;
		  		}
		  		
		  		List<WdmSncRoute> wdmsncroutelist = resourceManager.queryForRoute(wdmsnc.getObjectId(),"1");
		    	if( wdmsncroutelist ==null || wdmsncroutelist.size()==0){
		    		iter.remove() ; 
		    		logger.info( "反向波道无路由，舍弃:" + wdmsnc.getObjectId() );
		    		continue ;
		    	}
		    	wdmsnc.setWdmsncroutelist( wdmsncroutelist );
		    	wdmsnc.setIsReverse(Boolean.TRUE);
		    	wdmsnclist.add(wdmsnc);
		    	
		    	/**
		    	if(wdmsnc.getObjectId().equals("UUID:b545d64c-210a-11e4-9365-005056862639")){
					System.out.println("why1");
				}
				*/
		    	
		  	}
		}
		
		if( wdmsnclist==null||wdmsnclist.size()==0 ){
			return new ArrayList<ZdResult>() ;
		}
		else{
			logger.info( "波道总数量:" + wdmsnclist.size() );
		}
		
	  	//5.循环所有波道，分析oduk的承载关系
	  	for (Iterator<DbWdmSncAll> iter = wdmsnclist.iterator(); iter.hasNext();) {
	  		DbWdmSncAll wdmsnc = iter.next();
	  		
	  		Integer rate = wdmsnc.getRate();
	  		if( ConstBusiness.rateDescMap.keySet().contains( rate ) ){	
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
    	  		logger.error("missing layerdesc , wdmsncid::" + wdmsnc.getObjectId() +", rate::" + rate);
    	  		iter.remove();
    	  		continue;
    	  		
    	  	}
	    	
	    	String headptpStr = wdmsnc.getWdmsncroutelist().get(0).m_AEndMeObjectId + "|" + wdmsnc.getWdmsncroutelist().get(0).m_AEndPtpObjectId ; 
	    	wdmsnc.setHeadptpStr(headptpStr);
	    	
	    	String headctpStr =  getCtpInfo(wdmsnc.getWdmsncroutelist(), true);  
	    	if(StringUtils.isEmpty(headctpStr)){
	    		headctpStr = getCtpInfo(wdmsnc.getWdmsncroutelist(), false);
	    	}
	    	/**
	    	if( wdmsnc.getIsReverse() ){
	    		headctpStr = getCtpInfo(wdmsnc.getWdmsncroutelist(), false);
	    		if(StringUtils.isEmpty(headctpStr)){
		    		headctpStr = getCtpInfo(wdmsnc.getWdmsncroutelist(), true);  
		    	}
	    	}
	    	*/
	    	wdmsnc.setHeadctpSer(headctpStr);
	    	if( (headctpStr==null|| headctpStr.length()==0) && !wdmsnc.getLayerdesc().equals( Rate.och.getName() )  ){
	    		logger.warn("warning: missing headctpStr , wdmsncid::" + wdmsnc.getObjectId() +", rate::" + rate);
    	  		//iter.remove();
    	  		//continue;
	    	}
		    	
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
	  	List<DbWdmSncAll> wdmsncClientResult = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOdu0Result = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOdu1Result = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOdu2Result = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOdu3Result = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOdu4Result = new ArrayList<DbWdmSncAll>();
	  	List<DbWdmSncAll> wdmsncOchResult = new ArrayList<DbWdmSncAll>();
	  	
	  	//7.划分各层数据
	  	for (Iterator<DbWdmSncAll> iter = wdmsnclist.iterator(); iter.hasNext();) {
	  		DbWdmSncAll wdmsnc = iter.next();
	  		
	  		if( wdmsnc.getObjectId().equals("UUID:515f196c-10da-11e5-9c2d-005056862639")){
	  			System.out.println(1234);
	  		}
	  		
			if( !wdmsnc.getLayerdesc().equals( Rate.och.getName()) ){
				
				if( wdmsnc.getLayerdesc().equals( Rate.odu4.getName() ) ){
					ODU4 odu4 = new ODU4();
					odu4.setIndex(1);
					
					odu4.setRate(wdmsnc.getRate());
					odu4.setSncobjectid(wdmsnc.getObjectId());
					wdmsnc.setOdu(odu4);
				}
				else{
					ODU sncodu = null;
					try {
						sncodu = ConstBusiness.getOduByCtp(wdmsnc.getHeadctpSer());
					} catch (Exception e) {
						logger.error("分析CTP失败,ctp:" + wdmsnc.getHeadctpSer() + ",sncid:"+ wdmsnc.getObjectId());
					}
					if( sncodu==null){
						//continue;
						if( wdmsnc.getLayerdesc().equals( Rate.odu2.getName() ) ){
							ODU2 odu2 = new ODU2();
							odu2.setIndex(1);
							
							odu2.setRate(wdmsnc.getRate());
							odu2.setSncobjectid(wdmsnc.getObjectId());
							wdmsnc.setOdu(odu2);
						}
						
					}
					else{
						sncodu.setRate(wdmsnc.getRate());
						sncodu.setSncobjectid(wdmsnc.getObjectId());
						wdmsnc.setOdu(sncodu);
					}
					
				}
			}
	   		
			if( wdmsnc.getLayerdesc().equals( Rate.client.getName() ) ){
				if(wdmsnc.getOdu() instanceof DSR){
					wdmsncClientResult.add(wdmsnc);
				}
				else{
					logger.warn("warning: clientrate, bad ctp route. sncid:"+ wdmsnc.getObjectId());
					DSR dsr = new DSR();
					dsr.setIndex(wdmsnc.getOdu().getIndex());
					dsr.setRate(wdmsnc.getRate());
					dsr.setSncobjectid(wdmsnc.getObjectId());
					wdmsnc.setOdu(dsr);
					wdmsncClientResult.add(wdmsnc);
				}
		   	}
			else if( wdmsnc.getLayerdesc().equals( Rate.odu0.getName())){
				if(wdmsnc.getOdu() instanceof ODU0){
					wdmsncOdu0Result.add(wdmsnc);
				}
				else{
					logger.error("odu0rate, bad ctp route. sncid:"+ wdmsnc.getObjectId());
				}
			}
			else if( wdmsnc.getLayerdesc().equals( Rate.odu1.getName())){
				if(wdmsnc.getOdu() instanceof ODU1){
					wdmsncOdu1Result.add(wdmsnc);
				}
				else{
					logger.error("odu1rate, bad ctp route. sncid:"+ wdmsnc.getObjectId());
				}
			}
			else if( wdmsnc.getLayerdesc().equals( Rate.odu2.getName())){
				if(wdmsnc.getOdu() instanceof ODU2){
					wdmsncOdu2Result.add(wdmsnc);
				}
				else{
					logger.error("odu2rate, bad ctp route. sncid:"+ wdmsnc.getObjectId());
				}
			}
			else if( wdmsnc.getLayerdesc().equals( Rate.odu3.getName())){
				if(wdmsnc.getOdu() instanceof ODU3){
					wdmsncOdu3Result.add(wdmsnc);
				}
				else{
					logger.error("odu3rate, bad ctp route. sncid:"+ wdmsnc.getObjectId());
				}
			}
			else if( wdmsnc.getLayerdesc().equals( Rate.odu4.getName())){
				if(wdmsnc.getOdu() instanceof ODU4){
					wdmsncOdu4Result.add(wdmsnc);
				}
				else{
					logger.error("odu4rate, bad ctp route. sncid:"+ wdmsnc.getObjectId());
				}
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
	    	DbWdmSncAll wdmsnc = wdmsncOdu0Result.get(i);
	    	ODU0 odu0 = (ODU0)wdmsnc.getOdu();  
    		//查询所有client层数据
    		for (int j = 0; j < wdmsncClientResult.size(); j++) {
    			DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
    			if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
    				if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						odu0.setDsr((DSR)odu);
    						wdmsncin.setIsCaculatedBid(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据0，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
    			else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
    				if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						odu0.setDsr((DSR)odu);
    						wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据0，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
			}
	    }
	    	
	    //9.处理odu1层路径
	    for (int i = 0; i < wdmsncOdu1Result.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOdu1Result.get(i);
			ODU1 odu1 = (ODU1)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
    				if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						odu1.getDsrlist().add((DSR)odu);
    						wdmsncin.setIsCaculatedBid(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据1，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
    			else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
    				if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						odu1.getDsrlist().add((DSR)odu);
    						wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据1，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu0Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU0){
							odu1.getOdu0list().add((ODU0)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据1，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU0){
							odu1.getOdu0list().add((ODU0)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据1，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    }
	    
	    //10.处理odu2层路径
	    for (int i = 0; i < wdmsncOdu2Result.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOdu2Result.get(i);
	    	ODU2 odu2 = (ODU2)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
	    		
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
    				if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						odu2.getDsrlist().add((DSR)odu);
    						wdmsncin.setIsCaculatedBid(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据2，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
    			else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
    				if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						odu2.getDsrlist().add((DSR)odu);
    						wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据2，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu0Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU0){
							odu2.getOdu0list().add((ODU0)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据2，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU0){
							odu2.getOdu0list().add((ODU0)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据2，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu1Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU1){
							odu2.getOdu1list().add((ODU1)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据2，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU1){
							odu2.getOdu1list().add((ODU1)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据2，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    }
	    
	    //11.处理odu3层路径
	    for (int i = 0; i < wdmsncOdu3Result.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOdu3Result.get(i);
	    	ODU3 odu3 = (ODU3)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
	    		
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
    				if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						odu3.getDsrlist().add((DSR)odu);
    						wdmsncin.setIsCaculatedBid(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据3，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
    			else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
    				if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						odu3.getDsrlist().add((DSR)odu);
    						wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据3，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu0Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU0){
							odu3.getOdu0list().add((ODU0)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据3，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU0){
							odu3.getOdu0list().add((ODU0)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据3，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu1Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU1){
							odu3.getOdu1list().add((ODU1)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据3，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU1){
							odu3.getOdu1list().add((ODU1)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据3，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    	
	    	for (int j = 0; j < wdmsncOdu2Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu2Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU2){
							odu3.getOdu2list().add((ODU2)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据3，转换异常,odu2:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU2){
							odu3.getOdu2list().add((ODU2)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据3，转换异常,odu2:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    }
	    
	    //12.处理odu4层路径
	    for (int i = 0; i < wdmsncOdu4Result.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOdu4Result.get(i);
	    	ODU4 odu4 = (ODU4)wdmsnc.getOdu();  
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
	    		
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
    				if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						odu4.getDsrlist().add((DSR)odu);
    						wdmsncin.setIsCaculatedBid(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据4，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
    			else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
    				if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						odu4.getDsrlist().add((DSR)odu);
    						wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据4，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu0Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU0){
							odu4.getOdu0list().add((ODU0)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据4，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU0){
							odu4.getOdu0list().add((ODU0)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据4，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu1Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU1){
							odu4.getOdu1list().add((ODU1)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据4，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU1){
							odu4.getOdu1list().add((ODU1)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据4，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    	
	    	for (int j = 0; j < wdmsncOdu2Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu2Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU2){
							odu4.getOdu2list().add((ODU2)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据4，转换异常,odu2:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU2){
							odu4.getOdu2list().add((ODU2)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据4，转换异常,odu2:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    	for (int j = 0; j < wdmsncOdu3Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu3Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU3){
							odu4.getOdu3list().add((ODU3)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据4，转换异常,odu3:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU3){
							odu4.getOdu3list().add((ODU3)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据4，转换异常,odu3:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    }
	    
	    //13.处理och层路径
	    for (int i = 0; i < wdmsncOchResult.size(); i++) {
	    	DbWdmSncAll wdmsnc = wdmsncOchResult.get(i);
	    	OCH och = new OCH();
	    	wdmsnc.setOdu(och);
	    	
	    	for (int j = 0; j < wdmsncClientResult.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncClientResult.get(j);
	    		
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
    				if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						och.getDsrlist().add((DSR)odu);
    						wdmsncin.setIsCaculatedBid(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据och，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
    			else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
    				if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr())  ){
    					ODU odu = wdmsncin.getOdu();
    					if( odu instanceof DSR){
    						och.getDsrlist().add((DSR)odu);
    						wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
    					}
    					else{
    						logger.error("关联下层数据och，转换异常,oduc:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
    					}
    				}
    			}
			}
	    	for (int j = 0; j < wdmsncOdu0Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu0Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU0){
							och.getOdu0list().add((ODU0)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据och，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU0){
							och.getOdu0list().add((ODU0)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据och，转换异常,odu0:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    	for (int j = 0; j < wdmsncOdu1Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu1Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU1){
							och.getOdu1list().add((ODU1)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据och，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU1){
							och.getOdu1list().add((ODU1)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据och，转换异常,odu1:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    	
	    	for (int j = 0; j < wdmsncOdu2Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu2Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU2){
							och.getOdu2list().add((ODU2)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据och，转换异常,odu2:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU2){
							och.getOdu2list().add((ODU2)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据och，转换异常,odu2:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    	for (int j = 0; j < wdmsncOdu3Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu3Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU3){
							och.getOdu3list().add((ODU3)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据och，转换异常,odu3:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU3){
							och.getOdu3list().add((ODU3)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据och，转换异常,odu3:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    	for (int j = 0; j < wdmsncOdu4Result.size(); j++) {
	    		DbWdmSncAll wdmsncin = wdmsncOdu4Result.get(j);
	    		if( wdmsnc.getIsReverse().equals(Boolean.FALSE)){
	    			if( !wdmsncin.getIsCaculatedBid() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU4){
							och.getOdu4list().add((ODU4)odu);
							wdmsncin.setIsCaculatedBid(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据och，转换异常,odu4:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsnc.getObjectId());
						}
					}
	    		}
	    		else if( wdmsnc.getIsReverse().equals(Boolean.TRUE)){
	    			if( !wdmsncin.getIsCaculatedReverse() && wdmsncin.getPassedPtplist().contains(wdmsnc.getHeadptpStr()) ){
						ODU odu = wdmsncin.getOdu();
						if( odu instanceof ODU4){
							och.getOdu4list().add((ODU4)odu);
							wdmsncin.setIsCaculatedReverse(Boolean.TRUE);
						}
						else{
							logger.error("关联下层数据och，转换异常,odu4:" + odu.getClass().getSimpleName() + ",sncid:"+ wdmsncin.getObjectId());
						}
					}
	    		}
			}
	    }

	    List<DbWdmSncAll> wdmsncAllResult = new ArrayList<DbWdmSncAll>();
	    wdmsncAllResult.addAll(wdmsncClientResult);
	    wdmsncAllResult.addAll(wdmsncOdu0Result);
	    wdmsncAllResult.addAll(wdmsncOdu1Result);
	    wdmsncAllResult.addAll(wdmsncOdu2Result);
	    wdmsncAllResult.addAll(wdmsncOdu3Result);
	    wdmsncAllResult.addAll(wdmsncOdu4Result);
	    wdmsncAllResult.addAll(wdmsncOchResult);
	  	
	    logger.info("wdmsncAllResult info::" + wdmsncAllResult.size());
	    
	    //14.整合查询结果
	    List<ZdResult> allrst = new ArrayList<ZdResult>();
	  	for (Iterator<DbWdmSncAll> iter = wdmsncAllResult.iterator(); iter.hasNext();) {
	  		
	  		DbWdmSncAll wdmsnc = iter.next();
	  		if( wdmsnc.isReverse ){
	  			//logger.info("isReverse drop::" + wdmsnc.getObjectId());
	  			//wdmsnc.setIsReverse(Boolean.FALSE);
	  			//continue ;
	  		}
	  		ZdResult zdResult = new ZdResult();
	  		zdResult.setLayerdesc( wdmsnc.getLayerdesc() );
	    	zdResult.setRate( wdmsnc.getRate()+"");
	    	zdResult.setRatedesc( wdmsnc.getRatedesc() );
	    	zdResult.setSncid(wdmsnc.getObjectId());
	    	zdResult.setSncname( wdmsnc.getSncName() );
	    	zdResult.setOdu( wdmsnc.getOdu());
	    	zdResult.setDirection(wdmsnc.getDirection());
	    	
	    	Map<String,LinkedList<ZdResultSingle>> zdmap = new LinkedHashMap<String,LinkedList<ZdResultSingle>>();
	    	
	    	for (int j = 0; j < wdmsnc.getWdmsncroutelist().size(); j++) {
	    		WdmSncRoute route = wdmsnc.getWdmsncroutelist().get(j);
	    	  		
	    		ZdResultSingle zda = new ZdResultSingle();
	    		zda.setAendmeid(route.m_AEndMeObjectId);
	    		zda.setAendptpid(route.m_AEndPtpObjectId);
	    		zda.setAendcardmodel(route.acardmodel);
	    		zda.setAendctp(route.m_AEndCtpId);
	    	  		
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
