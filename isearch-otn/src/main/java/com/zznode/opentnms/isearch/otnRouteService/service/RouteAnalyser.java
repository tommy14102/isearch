package com.zznode.opentnms.isearch.otnRouteService.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.model.bo.DSR;
import com.zznode.opentnms.isearch.model.bo.ODU;
import com.zznode.opentnms.isearch.model.bo.ODU0;
import com.zznode.opentnms.isearch.model.bo.ODU1;
import com.zznode.opentnms.isearch.model.bo.ODU2;
import com.zznode.opentnms.isearch.model.bo.ODU3;
import com.zznode.opentnms.isearch.model.bo.ODU4;
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
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.otn.OtnLink;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.otn.OtnNode;
import com.zznode.opentnms.isearch.routeAlgorithm.core.matrix.Matrix;
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
		cachedClient.set( memcacTag  , 0, new String("test") );
		
		/**
		 * 
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
		*/
		
		//4.组装完成后对于小段snc上承载长snc的情况，再进一步进行分析
		analyserSeprateData();
	    
	}
	
	private void analyserSeprateData() throws Exception {
		
		//取出邻接矩阵
		BusinessAvator businessAvator = new BusinessAvator();
		businessAvator.setKey( PropertiesHander.getProperty("BusinessAvator") );
		Matrix matrix = (Matrix)cachedClient.get(businessAvator.getKey());
		
		Section[][] sections = matrix.getMatrix();
		int size = sections.length;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Section sec = sections[i][j];
				if( sec!=null ){
					dealSec(sections,sec,i,j);
				}
				
			}
		}
		
	}

	private void dealSec(Section[][] sections, Section sec, int i, int j) throws  Exception {
		
		List<ZdResult> sourceZdResultOdu4 = new ArrayList<ZdResult>();
		List<ZdResult> sourceZdResultOdu3 = new ArrayList<ZdResult>();
		List<ZdResult> sourceZdResultOdu2 = new ArrayList<ZdResult>();
		List<ZdResult> sourceZdResultOdu1 = new ArrayList<ZdResult>();
		
		List<Link> sourcelinks =  sec.getLinklist();
		for (Iterator<Link> iter = sourcelinks.iterator(); iter.hasNext();) {
			Link link = iter.next();
			
			String key = "OTN_RESOURECE_OTNLink" + "|" +sec.getAendNode()+"|"+sec.getZendNode()+"|"+ link.getLinkindex();
			ZdResult zdResult = (ZdResult)cachedClient.get(key);
			logger.info("getkey:"+ key +",value:" + zdResult);
			zdResult.setStoreKey(key);
			ODU odu = zdResult.getOdu();
			if (odu instanceof ODU4) {
				sourceZdResultOdu4.add(zdResult);
			}
			else if (odu instanceof ODU3) {
				sourceZdResultOdu3.add(zdResult);
			}
			else if (odu instanceof ODU2) {
				sourceZdResultOdu2.add(zdResult);
			}
			else if (odu instanceof ODU1) {
				sourceZdResultOdu1.add(zdResult);
			}
		}

		
		List<ZdResult> lineZdResultOdu3 = new ArrayList<ZdResult>();
		List<ZdResult> lineZdResultOdu2 = new ArrayList<ZdResult>();
		List<ZdResult> lineZdResultOdu1 = new ArrayList<ZdResult>();
		List<ZdResult> lineZdResultOdu0 = new ArrayList<ZdResult>();
		List<ZdResult> lineZdResultDsr = new ArrayList<ZdResult>();
		
		List<ZdResult> colonumZdResultOdu3 = new ArrayList<ZdResult>();
		List<ZdResult> colonumZdResultOdu2 = new ArrayList<ZdResult>();
		List<ZdResult> colonumZdResultOdu1 = new ArrayList<ZdResult>();
		List<ZdResult> colonumZdResultOdu0 = new ArrayList<ZdResult>();
		List<ZdResult> colonumZdResultDsr = new ArrayList<ZdResult>();
		
		int size = sections.length;
		for (int k = 0; k < size; k++) {
			
			Section lineSec =  sections[i][k];
			if( lineSec!=null && k != j ){
				List<Link> linklist = lineSec.getLinklist();
				for (Iterator<Link> iter = linklist.iterator(); iter.hasNext();) {
					Link link = iter.next();
					
					String key = "OTN_RESOURECE_OTNLink" + "|" +lineSec.getAendNode()+"|"+lineSec.getZendNode()+"|"+ link.getLinkindex();
					ZdResult zdResult = (ZdResult)cachedClient.get(key);
					ODU odu = zdResult.getOdu();
					if (odu instanceof ODU3) {
						lineZdResultOdu3.add(zdResult);
					}
					else if (odu instanceof ODU2) {
						lineZdResultOdu2.add(zdResult);
					}
					else if (odu instanceof ODU1) {
						lineZdResultOdu1.add(zdResult);
					}
					else if (odu instanceof ODU0) {
						lineZdResultOdu0.add(zdResult);
					}
					else if (odu instanceof DSR) {
						lineZdResultDsr.add(zdResult);
					}
				}
			}
				
			Section colSec =  sections[k][j];	
			if( colSec!=null && k != i ){
				List<Link> linklist = colSec.getLinklist();
				for (Iterator<Link> iter = linklist.iterator(); iter.hasNext();) {
					Link link = iter.next();
					
					String key = "OTN_RESOURECE_OTNLink" + "|" +colSec.getAendNode()+"|"+colSec.getZendNode()+"|"+ link.getLinkindex();
					ZdResult zdResult = (ZdResult)cachedClient.get(key);
					ODU odu = zdResult.getOdu();
					if (odu instanceof ODU3) {
						colonumZdResultOdu3.add(zdResult);
					}
					else if (odu instanceof ODU2) {
						colonumZdResultOdu2.add(zdResult);
					}
					else if (odu instanceof ODU1) {
						colonumZdResultOdu1.add(zdResult);
					}
					else if (odu instanceof ODU0) {
						colonumZdResultOdu0.add(zdResult);
					}
					else if (odu instanceof DSR) {
						colonumZdResultDsr.add(zdResult);
					}
				}
			}
			
		}
		
		//判断占用，之判断下一层的。
		for (int l = 0; l < sourceZdResultOdu4.size(); l++) {
			ZdResult source = sourceZdResultOdu4.get(l);
			boolean needfresh = false ; 
			for (int m = 0; m < lineZdResultOdu3.size(); m++) {
				ZdResult line = lineZdResultOdu3.get(m);
				if( line.getPassedPtplist().contains(source.getHeadptpStr()) ){
					ODU4 odu4 = (ODU4)source.getOdu();
					ODU3 odu3 = (ODU3)line.getOdu();
					odu4.getOdu3list().add(odu3);
					needfresh =true ; 
				}
			}
			for (int m = 0; m < colonumZdResultOdu3.size(); m++) {
				ZdResult line = colonumZdResultOdu3.get(m);
				if( line.getPassedPtplist().contains(source.getHeadptpStr()) ){
					ODU4 odu4 = (ODU4)source.getOdu();
					ODU3 odu3 = (ODU3)line.getOdu();
					odu4.getOdu3list().add(odu3);
					needfresh =true ; 
				}
			}
			for (int m = 0; m < lineZdResultOdu2.size(); m++) {
				ZdResult line = lineZdResultOdu2.get(m);
				if( line.getPassedPtplist().contains(source.getHeadptpStr()) ){
					ODU4 odu4 = (ODU4)source.getOdu();
					ODU2 odu2 = (ODU2)line.getOdu();
					odu4.getOdu2list().add(odu2);
					needfresh =true ; 
				}
			}
			for (int m = 0; m < colonumZdResultOdu2.size(); m++) {
				ZdResult line = colonumZdResultOdu2.get(m);
				if( line.getPassedPtplist().contains(source.getHeadptpStr()) ){
					ODU4 odu4 = (ODU4)source.getOdu();
					ODU2 odu2 = (ODU2)line.getOdu();
					odu4.getOdu2list().add(odu2);
					needfresh =true ; 
				}
			}
			if(needfresh){
				cachedClient.set( source.getStoreKey(), 0, source );
				logger.error("刷新存贮两点间链路的路由数据："+ source.getStoreKey()+":" + source );
			}
		}
		
		//判断占用，之判断下一层的。
		for (int l = 0; l < sourceZdResultOdu3.size(); l++) {
			ZdResult source = sourceZdResultOdu3.get(l);
			boolean needfresh = false ; 
			for (int m = 0; m < lineZdResultOdu2.size(); m++) {
				ZdResult line = lineZdResultOdu2.get(m);
				if( line.getPassedPtplist().contains(source.getHeadptpStr()) ){
					ODU3 odu3 = (ODU3)source.getOdu();
					ODU2 odu2 = (ODU2)line.getOdu();
					odu3.getOdu2list().add(odu2);
					needfresh =true ; 
				}
			}
			for (int m = 0; m < colonumZdResultOdu2.size(); m++) {
				ZdResult line = colonumZdResultOdu2.get(m);
				if( line.getPassedPtplist().contains(source.getHeadptpStr()) ){
					ODU3 odu3 = (ODU3)source.getOdu();
					ODU2 odu2 = (ODU2)line.getOdu();
					odu3.getOdu2list().add(odu2);
					needfresh =true ; 
				}
			}
			if(needfresh){
				cachedClient.set( source.getStoreKey(), 0, source );
				logger.error("刷新存贮两点间链路的路由数据："+ source.getStoreKey()+":" + source );
			}
		}
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
		
		Set<Integer> allinputtype = new HashSet<Integer>();
		allinputtype.add(87);
		allinputtype.add(76);
		allinputtype.add(77);
		allinputtype.add(78);
		allinputtype.add(8043);
		allinputtype.add(8031);
		allinputtype.add(104);
		allinputtype.add(105);
		allinputtype.add(106);
		allinputtype.add(8041);
		
		//BusiAnalyser busiAnalyser = new BusiAnalyser("Huawei/NanJingJWZ2");
		
		List<OtnLink> linklist  = new ArrayList<OtnLink>();
		int linkindex = 1 ; 
			
		logger.info("全网tsn数量：" + tsnGlist.size());
		Set<String> dealedSet = new HashSet<String>();
		Set<String> dealedFreeSet = new HashSet<String>();
		//Map<String,Integer> dealedIndex = new HashMap<String,Integer>();
		
		Map<String,ArrayList<String>> zjMap = new HashMap<String,ArrayList<String>>();
		
		for (Iterator<TsnGraph> alliter = tsnGlist.iterator(); alliter.hasNext();) {
			TsnGraph tsnGraph = alliter.next();
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
					if( anode.getId().equals("16080002") && znode.getId().equals("16090001") ){
						
					}else if ( anode.getId().equals("16090001") && znode.getId().equals("16080002") ){
						System.out.println(123);
					}else{
						continue ;
					}
					*/
					
					String dealedkey = tsnGraph.getEmsid() + "|" + anode.getId() + "|" + znode.getId() ; 
					if( dealedSet.contains( dealedkey )){
						logger.info("处理tsn,站点已经处理过 ：" + dealedkey);
						continue ;
					}
					dealedSet.add( dealedkey );
					List<ZdResult> resultlist = doAnalyserZdRoute(busiAnalyser, anode.getId(), znode.getId());
					for (Iterator<ZdResult> iters = resultlist.iterator(); iters.hasNext();) {
						ZdResult zdResult = iters.next();
						OtnLink otnLink = new OtnLink();
						otnLink.setAendnode(anode);
						otnLink.setZendnode(znode);
						otnLink.setDirection(Direction.SINGLE);
						otnLink.setJump( (long)zdResult.getZdmap().size() );
						otnLink.setLinkindex(linkindex++);
						linklist.add(otnLink);
						
						//客户侧路径，根据占用进行判断
						if( zdResult.getOdu() instanceof DSR ){
							String sncid = zdResult.getSncid();
							if( resourceManager.hasCucirtBySncid(sncid) ){
								logger.error("移除客户侧路径："+ sncid );
								iters.remove();
								continue;
							}
						}
						
						String key = "OTNLink" + "|" + anode.getId() + "|" + znode.getId() + "|" + otnLink.getLinkindex();
						cachedClient.set( memcacTag +key, 0, zdResult );
						logger.error("存贮两点间链路的路由数据："+ memcacTag +key );
						
					}
					if( resultlist!=null && resultlist.size() > 0 ){
						String key = anode.getId() + "|" + znode.getId() ;
						logger.error("存贮两点间波道数据："+ memcacTag +key + ",size():" + resultlist.size() );
						cachedClient.set( memcacTag +key, 0, resultlist );
					}
					
					
					for (Iterator<Integer> iterin = allinputtype.iterator(); iterin.hasNext();) {
						Integer rate = iterin.next();
						String keyfree = "OTNLink_isFress" + "|" + rate + "|" + anode.getId() + "|" + znode.getId()  ;
						if(dealedFreeSet.contains(keyfree)){
							continue;
						}
						for (int k = 0; k < resultlist.size(); k++) {
							ZdResult zdResult = resultlist.get(k);
							if( !zdResult.getODUinfo(rate).equals("")){
								cachedClient.set( memcacTag +keyfree, 0, "true" );
								dealedFreeSet.add(keyfree);
								logger.error("存贮两点间链路的路由数据空闲标记："+ memcacTag +keyfree + ":true"  );
								break;
				    		}
						}
					}
					
					String dealedkeyReverse = tsnGraph.getEmsid() + "|" +znode.getId() + "|" + anode.getId() ; 
					if( dealedSet.contains( dealedkeyReverse )){
						logger.info("处理tsn,站点已经处理过2 ：" + dealedkeyReverse);
						continue ;
					}
					dealedSet.add( dealedkeyReverse );
					List<ZdResult> resultlistReverse = doAnalyserZdRoute(busiAnalyser, znode.getId(), anode.getId());
					
					for (Iterator<ZdResult> iters = resultlistReverse.iterator(); iters.hasNext();) {
						ZdResult zdResult = iters.next();
						OtnLink otnLink = new OtnLink();
						otnLink.setAendnode(znode);
						otnLink.setZendnode(anode);
						otnLink.setDirection(Direction.SINGLE);
						otnLink.setJump( (long)zdResult.getZdmap().size() );
						otnLink.setLinkindex(linkindex++);
						linklist.add(otnLink);
						
						//客户侧路径，根据占用进行判断
						if( zdResult.getOdu() instanceof DSR ){
							String sncid = zdResult.getSncid();
							if( resourceManager.hasCucirtBySncid(sncid) ){
								logger.error("移除客户侧路径2："+ sncid );
								iters.remove();
								continue;
							}
						}
						
						String key = "OTNLink" + "|" + znode.getId() + "|" + anode.getId() +"|"+ otnLink.getLinkindex();
						cachedClient.set( memcacTag +key, 0, zdResult );
						logger.error("存贮两点间链路的反向路由数据："+ memcacTag +key );
						
					}
					if( resultlistReverse!=null && resultlistReverse.size() > 0 ){
						String keyReverse = znode.getId() + "|" + anode.getId() ;
						logger.error("存贮两点间波道数据："+ memcacTag +keyReverse + ",size():" + resultlistReverse.size() );
						cachedClient.set( memcacTag +keyReverse, 0, resultlistReverse );
					}
					
					for (Iterator<Integer> iterin = allinputtype.iterator(); iterin.hasNext();) {
						Integer rate = iterin.next();
						String keyfree = "OTNLink_isFress" + "|" + rate + "|" + znode.getId() + "|" + anode.getId()  ;
						if(dealedFreeSet.contains(keyfree)){
							continue;
						}
						for (int k = 0; k < resultlistReverse.size(); k++) {
							ZdResult zdResult = resultlistReverse.get(k);
							if( !zdResult.getODUinfo(rate).equals("")){
								cachedClient.set( memcacTag +keyfree, 0, "true" );
								dealedFreeSet.add(keyfree);
								logger.error("存贮两点间链路的路由数据空闲标记2："+ memcacTag +keyfree + ":true"  );
								break;
				    		}
						}
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
