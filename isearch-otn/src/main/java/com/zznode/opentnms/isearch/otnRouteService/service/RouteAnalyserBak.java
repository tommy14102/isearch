package com.zznode.opentnms.isearch.otnRouteService.service;

import java.util.ArrayList;
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

import com.zznode.opentnms.isearch.model.bo.EmsGraph;
import com.zznode.opentnms.isearch.model.bo.TsnGraph;
import com.zznode.opentnms.isearch.model.bo.ZdResult;
import com.zznode.opentnms.isearch.model.bo.Zhandian;
import com.zznode.opentnms.isearch.otnRouteService.cache.CachedClient;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbEms;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbTsn;
import com.zznode.opentnms.isearch.otnRouteService.db.po.TsnMe;
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
public class RouteAnalyserBak {
	
	private static final Logger logger = LoggerFactory.getLogger(RouteAnalyserBak.class);   
	
	@Autowired
	public ResourceManager resourceManager;
	
	@Autowired
	public SubnetManager subnetManager;
	
	@Autowired
	public BusiAnalyser busiAnalyser ;
	
	@Autowired
	public CachedClient cachedClient;
	
	private final String memcacTag =  PropertiesHander.getProperty("memcacTag");
	
	protected List<TsnGraph> tsnGlist = new ArrayList<TsnGraph>();
	protected EmsGraph emsGraph = new EmsGraph();
	
	public void analyseAllRoute() throws Exception{
		
		logger.info("网络结构抽象开始");
		
		//1、查询所有ems
		List<DbEms> allems = resourceManager.getAllEms();
		logger.info("全网ems数量：" + allems.size());
		
		//2.循环每个ems，处理站点链接关系
		analyseEms(allems);
		
		//3.组装数据
		saveAnalyseEmsData();
	    
	}
	
	/**
	 * 循环所有ems，组装数据结构。
	 * TsnGraph：每个tsn的包含的站点信息。
	 * emsGraph：全网所有的站点信息，每个站点所属的tsn，和包含的网元。
	 * @param allems
	 */
	private void analyseEms(List<DbEms> allems){
		
		tsnGlist = new ArrayList<TsnGraph>();
		emsGraph = new EmsGraph();
		
		//循环所有ems
		for (int i = 0; i < allems.size(); i++) {
			DbEms ems = allems.get(i);
			
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
				
				for (int k = 0; k < tsnmes.size(); k++) {
					TsnMe me = tsnmes.get(k);
					if( me.getZhandianid()==null || me.getZhandianid().equals("")){
						continue ;
					}
					
					tsnGraph.getZdids().add(me.getZhandianid());
					if( emsGraph.getZdmap().get(me.getZhandianid())!=null ){
						
						Zhandian zhandian = emsGraph.getZdmap().get(me.getZhandianid());
						zhandian.getMeids().add(me.getId());
						zhandian.getTsnids().add(me.getTsnid());
						
					}else{
						
						Zhandian zhandian = new Zhandian();
						zhandian.setZhandianid(me.getZhandianid());
						
						Set<String> meids = new HashSet<String>() ;
						Set<String> tsnids = new HashSet<String>() ;
						
						meids.add(me.getId());
						tsnids.add(me.getTsnid());
						
						zhandian.setMeids(meids);
						zhandian.setTsnids(tsnids);
						
						emsGraph.getZdmap().put(me.getZhandianid(), zhandian);
					}
				}
				tsnGlist.add(tsnGraph);
			}
		}
	}
	
	/**
	 * 根据tsn分析站点间的链接关系。
	 * 组装图的链路信息。
	 * 边：OtnLink
	 * 点：OtnNode
	 * @throws Exception 
	 */
	private void saveAnalyseEmsData() throws Exception{
		List<OtnLink> linklist  = new ArrayList<OtnLink>();
		for (Iterator<TsnGraph> iter = tsnGlist.iterator(); iter.hasNext();) {
			TsnGraph tsnGraph = iter.next();
			String[] zdlist = tsnGraph.getZdids().toArray(new String[]{});
			int zdcount = zdlist.length;
			for (int i = 0; i < zdcount; i++) {
				
				String aend = zdlist[i];
				OtnNode anode = new OtnNode();
				anode.setId(aend);
				//anode.setTsnids(emsGraph.getZdmap().get(aend).getTsnids());
				
				for (int j = i+1; j < zdlist.length; j++) {
					
					String zend = zdlist[j];
					OtnNode znode = new OtnNode();
					znode.setId(zend);
					//znode.setTsnids(emsGraph.getZdmap().get(zend).getTsnids());
					
					List<ZdResult> resultlist = busiAnalyser.analyseOtnResource(anode.getId(), znode.getId());
					for (int k = 0; k < resultlist.size(); k++) {
						ZdResult zdResult = resultlist.get(k);
						OtnLink otnLink = new OtnLink();
						otnLink.setAendnode(anode);
						otnLink.setZendnode(znode);
						if( zdResult.getDirection().intValue() == 0 ){
							otnLink.setDirection(Direction.SINGLE);
						}else{
							otnLink.setDirection(Direction.DOUBLE);
						}
						otnLink.setJump( (long)zdResult.getZdmap().size() );
						Map attrMap = new HashMap();
						attrMap.put("ZdResultInfo", zdResult);
						otnLink.setAttrMap(attrMap);
						linklist.add(otnLink);
					}
					
					//String key = anode.getId() + "|" + znode.getId() ;
					//cachedClient.set( memcacTag +key, 0, resultlist );
				}
			}
		}
		
		ISearch isearch = new ISearch();
	    
		BusinessAvator businessAvator = new BusinessAvator();
		businessAvator.setKey(PropertiesHander.getProperty("BusinessAvator"));
		isearch.regist(businessAvator);
		BaseImporter<OtnLink> baseImporter = new BaseImporter<OtnLink>(linklist);
	    isearch.refreshdata(businessAvator.getKey(), baseImporter);
	}

}
