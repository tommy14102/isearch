package com.zznode.opentnms.isearch.otnRouteService.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.model.bo.TsnGraph;
import com.zznode.opentnms.isearch.model.bo.ZdResult;
import com.zznode.opentnms.isearch.otnRouteService.cache.CachedClient;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbEms;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbTsn;
import com.zznode.opentnms.isearch.otnRouteService.db.po.TsnMe;
import com.zznode.opentnms.isearch.otnRouteService.manage.ObjectPool;
import com.zznode.opentnms.isearch.otnRouteService.manage.ResourceManager;
import com.zznode.opentnms.isearch.otnRouteService.manage.SubnetManager;
import com.zznode.opentnms.isearch.otnRouteService.util.PropertiesHander;
import com.zznode.opentnms.isearch.routeAlgorithm.api.ISearch;
import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Direction;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.BusinessAvator;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.otn.OtnLink;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.otn.OtnNode;
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.data.importor_base.BaseImporter;

@Component
public class RouteAnalyser {
	
	private static final Logger logger = LoggerFactory.getLogger(RouteAnalyser.class);   
	
	@Autowired
	public ResourceManager resourceManager;
	
	@Autowired
	public SubnetManager subnetManager;
	
	public ObjectPool pool = new ObjectPool();
	
	@Autowired
	public CachedClient cachedClient;
	
	
	private final String memcacTag =  PropertiesHander.getProperty("memcacTag");
	
	
	public void analyseAllRoute() throws Exception{
		
		logger.info("网络结构抽象开始");
		
		cachedClient.set( memcacTag  , 0, new String("sdf") );
		
		//1、查询所有ems
		List<DbEms> allems = resourceManager.getAllEms();
		logger.info("全网ems数量：" + allems.size());
		
		//2.循环每个ems，处理站点链接关系
		List<TsnGraph> tsnGlist = new ArrayList<TsnGraph>();
		for (int i = 0; i < allems.size(); i++) {
			DbEms ems = allems.get(i);
			logger.info("处理ems：" + ems.getObjectId());
			List<TsnGraph> tsnGraphlist = analyseEms(ems);
			if( tsnGraphlist.size() > 0  ){
				tsnGlist.addAll(tsnGraphlist);
			}
		}
		
		//3.组装数据
		saveAnalyseEmsData(tsnGlist);
	    
	}
	
	/**
	 * 循环所有ems，组装数据结构。
	 * TsnGraph：每个tsn的包含的站点信息。
	 * emsGraph：全网所有的站点信息，每个站点所属的tsn，和包含的网元。
	 * @param allems
	 */
	private List<TsnGraph> analyseEms(DbEms ems){
		
		List<TsnGraph> tsnGlist = new ArrayList<TsnGraph>();
		
			logger.info("处理ems：" + ems.getObjectId());
			
			//1.查询ems的tsn信息。
			List<DbTsn> tsnlist = subnetManager.getTsnByEms(ems.getObjectId());
			
			logger.info("处理ems,tsn数量：" + tsnlist.size());
			
			//2.查询tsn关联的tsnlink信息。
			for (int j = 0; j < tsnlist.size(); j++) {
				DbTsn tsn = tsnlist.get(j);
				String tsnid = tsn.getObjectId();
				List<TsnMe> tsnmes = subnetManager.getTsnMeByEms(tsnid);
				
				TsnGraph tsnGraph = new TsnGraph();
				tsnGraph.setTsnid(tsnid);
				tsnGraph.setEmsid(ems.getObjectId());
				
				for (int k = 0; k < tsnmes.size(); k++) {
					TsnMe me = tsnmes.get(k);
					if( me.getZhandianid()==null || me.getZhandianid().equals("")){
						continue ;
					}
					tsnGraph.getZdids().add(me.getZhandianid());
				}
				tsnGlist.add(tsnGraph);
			}
			
			return tsnGlist;
	}
	
	/**
	 * 根据tsn分析站点间的链接关系。
	 * 组装图的链路信息。
	 * 边：OtnLink
	 * 点：OtnNode
	 * @throws Exception 
	 */
	private void saveAnalyseEmsData( List<TsnGraph> tsnGlist ) throws Exception{
		
		//BusiAnalyser busiAnalyser = new BusiAnalyser("Huawei/NanJingJWZ2");
		
		List<OtnLink> linklist  = new ArrayList<OtnLink>();
		int linkindex = 1 ; 
			
		logger.info("全网tsn数量：" + tsnGlist.size());
		Set<String> dealedSet = new HashSet<String>();
		Map<String,ArrayList<String>> zjMap = new HashMap<String,ArrayList<String>>();
		
		for (Iterator<TsnGraph> iter = tsnGlist.iterator(); iter.hasNext();) {
			TsnGraph tsnGraph = iter.next();
			logger.info("处理tsn,emsid:"+ tsnGraph.getEmsid() +" tsnid ：" + tsnGraph.getTsnid());
			logger.info("处理tsn,站点数量 ：" + tsnGraph.getZdids().size());
			if(tsnGraph.getZdids().size()==0){
				continue ;
			}
			
			BusiAnalyser busiAnalyser = (BusiAnalyser)pool.getPool().borrowObject(tsnGraph.getEmsid());
			
			String[] zdlist = tsnGraph.getZdids().toArray(new String[]{});
			int zdcount = zdlist.length;
			for (int i = 0; i < zdcount; i++) {
				
				//a端站点
				String aend = zdlist[i];
				OtnNode anode = new OtnNode();
				anode.setId(aend);
				
				if(zjMap.containsKey(aend)){
					if(  !zjMap.get(aend).contains(tsnGraph.getEmsid()) ){
						zjMap.get(aend).add(tsnGraph.getEmsid());
					}
				}
				else{
					ArrayList<String> emslist = new ArrayList<String>();
					emslist.add(tsnGraph.getEmsid());
					zjMap.put(aend, emslist);
				}
				
				//循环其余站点，作为z端站点
				for (int j = i+1; j < zdlist.length; j++) {
					
					String zend = zdlist[j];
					OtnNode znode = new OtnNode();
					znode.setId(zend);
					
					/**
					if( anode.getId().equals("16040008") && znode.getId().equals("16060003") ){
						
					}else if ( anode.getId().equals("16060003") && znode.getId().equals("16040008") ){
						
					}else{
						continue ;
					}
					*/
					
					String dealedkey = anode.getId() + "|" + znode.getId() ; 
					if( dealedSet.contains( dealedkey )){
						continue ;
					}
					dealedSet.add( dealedkey );
					List<ZdResult> resultlist = doAnalyserZdRoute(busiAnalyser, anode.getId(), znode.getId());
					//List<ZdResult> resultlist = busiAnalyser.analyseOtnResourceV3(anode.getId(), znode.getId());
					int tagi = 1;
					for (int k = 0; k < resultlist.size(); k++) {
						ZdResult zdResult = resultlist.get(k);
						OtnLink otnLink = new OtnLink();
						otnLink.setAendnode(anode);
						otnLink.setZendnode(znode);
						otnLink.setDirection(Direction.SINGLE);
						otnLink.setJump( (long)zdResult.getZdmap().size() );
						otnLink.setLinkindex(linkindex++);
						//Map attrMap = new HashMap();
						//attrMap.put("ZdResultInfo", zdResult);
						//otnLink.setAttrMap(attrMap);
						linklist.add(otnLink);
						
						String key = "OTNLink" + "|" + anode.getId() + "|" + znode.getId() + "|" + otnLink.getLinkindex();
						cachedClient.set( memcacTag +key, 0, zdResult );
						logger.error("存贮两点间链路的路由数据："+ key );
						
						
						//String key = anode.getId() + "|" + znode.getId() + "|" +tagi++ ;
						//cachedClient.set( memcacTag +key, 0, zdResult );
						//logger.error("存贮两点间波道数据："+ key );
						
					}
					if(resultlist!=null && resultlist.size() > 0 ){
						String key = anode.getId() + "|" + znode.getId() ;
						//cachedClient.set( memcacTag +key, 0, tagi-1 );
						logger.error("存贮两点间波道数据："+ key + ",size():" + resultlist.size() );
						cachedClient.set( memcacTag +key, 0, resultlist );
					}
					
					String dealedkeyReverse = znode.getId() + "|" + anode.getId() ; 
					if( dealedSet.contains( dealedkeyReverse )){
						continue ;
					}
					dealedSet.add( dealedkeyReverse );
					//List<ZdResult> resultlistReverse = busiAnalyser.analyseOtnResourceV3(znode.getId(), anode.getId());
					List<ZdResult> resultlistReverse = doAnalyserZdRoute(busiAnalyser, znode.getId(), anode.getId());
					int tagireverse = 1;
					for (int k = 0; k < resultlistReverse.size(); k++) {
						ZdResult zdResult = resultlistReverse.get(k);
						OtnLink otnLink = new OtnLink();
						otnLink.setAendnode(znode);
						otnLink.setZendnode(anode);
						otnLink.setDirection(Direction.SINGLE);
						otnLink.setJump( (long)zdResult.getZdmap().size() );
						otnLink.setLinkindex(linkindex++);
						
						//Map attrMap = new HashMap();
						//attrMap.put("ZdResultInfo", zdResult);
						//otnLink.setAttrMap(attrMap);
						linklist.add(otnLink);
						
						String key = "OTNLink" + "|" + znode.getId() + "|" + anode.getId() +"|"+ otnLink.getLinkindex();
						cachedClient.set( memcacTag +key, 0, zdResult );
						logger.error("存贮两点间链路的反向路由数据："+ key );
						
						//String key  = znode.getId() + "|" + anode.getId() + "|" +tagireverse++ ;
						//cachedClient.set( memcacTag +key, 0, zdResult );
						//logger.error("存贮两点间反向波道数据："+ key );
						
					}
					if( resultlistReverse!=null && resultlistReverse.size() > 0 ){
						String keyReverse = znode.getId() + "|" + anode.getId() ;
						//cachedClient.set( memcacTag +keyReverse, 0, tagireverse-1 );
						logger.error("存贮两点间波道数据："+ keyReverse + ",size():" + resultlistReverse.size() );
						cachedClient.set( memcacTag +keyReverse, 0, resultlistReverse );
					}
					
				}
			}
			
			pool.getPool().returnObject(tsnGraph.getEmsid(), busiAnalyser);
		}
		
		//busiAnalyser = null ;
		
		for(Map.Entry<String,ArrayList<String>> zds : zjMap.entrySet()){
			if( zds.getValue().size()> 1){
				logger.info("转接站点 ：" + zds.getKey() +", ems列表：" + Arrays.deepToString(zds.getValue().toArray()));
			}
		}
		
		ISearch isearch = new ISearch();
	    
		BusinessAvator businessAvator = new BusinessAvator();
		businessAvator.setKey(PropertiesHander.getProperty("BusinessAvator"));
		isearch.regist(businessAvator);
		BaseImporter<OtnLink> baseImporter = new BaseImporter<OtnLink>(linklist);
	    isearch.refreshdata(businessAvator.getKey(), baseImporter);
	}
	
	private List<ZdResult>  doAnalyserZdRoute( BusiAnalyser busiAnalyser  ,String aendzd , String zendzd){
		
		List<ZdResult> rtnlist = new ArrayList<ZdResult>();
		
		try{
			rtnlist = busiAnalyser.analyseOtnResourceV3( aendzd , zendzd );
		}
		catch(Exception e){
			logger.error(aendzd+"|"+zendzd+", err accoured",e);
		}
		
		return rtnlist;
	}

}
