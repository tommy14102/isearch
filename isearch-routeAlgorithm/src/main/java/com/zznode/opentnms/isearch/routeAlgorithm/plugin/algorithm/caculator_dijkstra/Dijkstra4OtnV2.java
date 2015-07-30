package com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_dijkstra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.zznode.opentnms.isearch.model.bo.DSR;
import com.zznode.opentnms.isearch.model.bo.OCH;
import com.zznode.opentnms.isearch.model.bo.ODU0;
import com.zznode.opentnms.isearch.model.bo.ODU1;
import com.zznode.opentnms.isearch.model.bo.ODU2;
import com.zznode.opentnms.isearch.model.bo.ODU3;
import com.zznode.opentnms.isearch.model.bo.ODU4;
import com.zznode.opentnms.isearch.model.bo.ZdResult;
import com.zznode.opentnms.isearch.model.bo.ZdResultSingle;
import com.zznode.opentnms.isearch.routeAlgorithm.App;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWay;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWayRoute;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.otn.ExCluseBean;
import com.zznode.opentnms.isearch.routeAlgorithm.core.algorithm.AlgorithmProcessor;
import com.zznode.opentnms.isearch.routeAlgorithm.core.cache.SPtnMemcachedClient;
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.data.importor_dbbase.DBUtil;

public class Dijkstra4OtnV2 extends AlgorithmProcessor {

	 private static Logger log = Logger.getLogger(Dijkstra4OtnV2.class);
	 
	 private SPtnMemcachedClient cacheClient = (SPtnMemcachedClient)App.factory.getBean("SPtnMemcachedClient");
	 
	 private DBUtil dbClient = (DBUtil)App.factory.getBean("DBUtil");
	    
	 	Set<Integer> openSet =new HashSet<Integer>();   // 未处理过的节点
	    Set<Integer> closedSet =new HashSet<Integer>();  //已经处理过的节点
	    HashMap<Integer, List<Integer>> path=new HashMap<Integer,List<Integer>>();//封装路径信息  
	    // 用来存放起始点到其它点当前的距离  
	    HashMap<Integer, Long> distanceMap = new HashMap<Integer, Long>();  
	    Long unreachable = Long.MAX_VALUE ; 
	    
	    
	    @Override
		protected List<CaculatorResultWay> doCaculate( CaculatorParam param ) {
	    	
	    	List<CaculatorResultWay> rtnlist = new ArrayList<CaculatorResultWay>();
	    	int i = 1;
	    	while( i<6 ){
	    		
	    		CaculatorResultWay way = doSingleCaculate(param);
	    		if( way == null){
	    			break;
	    		}
	    		i++;
	    		rtnlist.add(way);
	    	}
	    	return rtnlist;
	    	
		}
	    
		private CaculatorResultWay doSingleCaculate( CaculatorParam param ) {
	    	
	    	String aendid = param.getAend();
	    	String zendid = param.getZend();
	    	List<String> aendme = param.getAendme();
	    	List<String> zendme = param.getZendme();
	    	
	    	CaculatorResultWay  mainRoute =  dijkstra( aendid, zendid, aendme , zendme ,param ) ;
	    	if(mainRoute==null){
	    		return null;
	    	}
	    	
	    	LinkedList<CaculatorResultWayRoute>  sway  = mainRoute.getRouts();
	    	for (int j = 1; j < sway.size()-1; j++) {
	    		String nodeid = sway.get(j).getNodeid();
	    		Integer nodeindex = (Integer)pointMap.get(nodeid);
	    		//matrix中，这个节点全部链路删除
	    	   	int size = matrix.length;
	    	   	for (int k = 0; k < size; k++) {
	    	  		for (int l = 0; l < size; l++) {
	    	   			if( k==nodeindex.intValue() || l== nodeindex.intValue()){
	    	   				matrix[k][l] = null;
	    	   				matrix[l][k] = null;
	    	   			}
	    	   		}
	    		}
			}
	    	
	    	
	    	
	    	openSet.clear();
	    	closedSet.clear();
	    	distanceMap.clear();
	    	path.clear();
	    	
	    	
	    	
	    	return mainRoute;
	    	
		}

	    // 返回当前最小距离的点(必须不包含在findedSet中)  
	    private int currentMinIndex() {  
	        Iterator<Entry<Integer, Long>> it = distanceMap.entrySet().iterator();  
	        Long min = Long.MAX_VALUE;  
	        int minIndex = -1;  
	        while (it.hasNext()) {  
	            Entry<Integer, Long> entry = it.next();  
	            if ( openSet.contains(entry.getKey()) && entry.getValue() < min) {  
	                 min = entry.getValue();  
	                 minIndex = entry.getKey();  
	            }  
	        }  
	        return minIndex;  
	    } 
	    
	    private Long getDistance(int aendindex , int zendindex , int headindex , int tailindex , List<String> aendmelist , List<String> zendmelist , CaculatorParam param ){
	    	
	    	log.info( " 计算两点间距离 ，aendindex:"+ aendindex  +" ，zendindex:"+ zendindex );
	    	
	    	String aendptp = (String)param.getAttrMap().get("aendptp");
	    	String aendctp = (String)param.getAttrMap().get("aendctp");
	    	String zendptp = (String)param.getAttrMap().get("zendptp");
	    	String zendctp = (String)param.getAttrMap().get("zendctp");
	    	
	    	//必经网元
	    	List<String> excludeMelist = (List<String>)param.getAttrMap().get("excludeMelist");
	    	//必经端口
	    	List<String> excludePtplist = (List<String>)param.getAttrMap().get("excludePtplist");
	    	excludeMelist = excludeMelist==null ? new ArrayList<String>() : excludeMelist ; 
	    	excludePtplist = excludePtplist==null ? new ArrayList<String>() : excludePtplist ; 
	    	
	    	//禁止点最近原则
	    	Map<String ,List<ExCluseBean> > inclusemap = (Map<String ,List<ExCluseBean> >)param.getAttrMap().get("inclusemap");
	    	
	    	Section sec = matrix[aendindex][zendindex];
	    	if(sec==null){
	    		return null;
	    	}
	    	List<Link> linklist = sec.getLinklist();
	    	log.info( " 计算两点间距离 ，linklist.size:"+ linklist.size() );
	    	
	    	if(aendindex== headindex){
	    		
	    		for (Iterator<Link> iter = linklist.iterator(); iter.hasNext();) {
					Link link = iter.next();
					
					String key = "OTN_RESOURECE_OTNLink" + "|" +sec.getAendNode()+"|"+sec.getZendNode()+"|"+ link.getLinkindex();
					ZdResult zdResult = (ZdResult)cacheClient.get(key);
					
					//ZdResult zdResult = (ZdResult)link.getAttrMap().get("ZdResultInfo");
					Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
					Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
					
					//判断是否经过a网元
					Iterator<LinkedList<ZdResultSingle>>  iters = allzd.iterator();
					LinkedList<ZdResultSingle> firstelement = iters.next();
					
					if(	aendmelist.size()>0 && !aendmelist.contains(firstelement.getFirst().getAendmeid())){
						iter.remove();
						log.debug( " 计算两点间距离 ，按网元过滤:"+ zdResult.getSncid() );
						continue;
					}
					
					//判断是否经过a端口
					if(	!StringUtils.isEmpty(aendptp) && !firstelement.getFirst().getAendptpid().equals(aendptp)){
						iter.remove();
						log.debug( " 计算两点间距离 ，按端口过滤:"+ zdResult.getSncid() );
						continue;
					}
					
					//判断是否经过a时隙
					if(	!StringUtils.isEmpty(aendctp) && !firstelement.getFirst().getAendctp().equals(aendctp)){
						iter.remove();
						log.debug( " 计算两点间距离 ，按时隙过滤:"+ zdResult.getSncid() );
						continue;
					}
					
				}
	    		if(linklist.size()==0){
	    			log.info( " 计算两点间距离 ，无结果返回" );
	    			return null;
	    		}
	    		
	    	}

	    	if(zendindex== tailindex){
	    		
	    		for (Iterator<Link> iter = linklist.iterator(); iter.hasNext();) {
					Link link = iter.next();
					
					String key = "OTN_RESOURECE_OTNLink" + "|" +sec.getAendNode()+"|"+sec.getZendNode()+"|"+  link.getLinkindex();
					ZdResult zdResult = (ZdResult)cacheClient.get(key);
					
					//ZdResult zdResult = (ZdResult)link.getAttrMap().get("ZdResultInfo");
					Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
					Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
					
					//判断是否经过az网元
					Iterator<LinkedList<ZdResultSingle>>  iters = allzd.iterator();
					LinkedList<ZdResultSingle> lastelement = iters.next();
					while(iters.hasNext()){
						lastelement = iters.next();
					}
					
					if( zendmelist.size()>0 && !zendmelist.contains(lastelement.getLast().getZendmeid())){
						iter.remove();
						log.debug( " 计算两点间距离 ，按z网元过滤:"+ zdResult.getSncid() );
						continue;
					}
					
					//判断是否经过z端口
					if(	!StringUtils.isEmpty(zendptp) && !lastelement.getLast().getZendptpid().equals(zendptp)){
						iter.remove();
						log.debug( " 计算两点间距离 ，按z端口过滤:"+ zdResult.getSncid() );
						continue;
					}
					
					//判断是否经过z时隙
					if(	!StringUtils.isEmpty(zendctp) && !lastelement.getLast().getZendctp().equals(zendctp)){
						iter.remove();
						log.debug( " 计算两点间距离 ，按z时隙过滤:"+ zdResult.getSncid() );
						continue;
					}
					
				}
	    		if(linklist.size()==0){
	    			log.info( " 计算两点间距离 ，无结果返回" );
	    			return null;
	    		}
	    	}
	    	
	    	
	    	outer:for (Iterator<Link> iter = linklist.iterator(); iter.hasNext();) {
	    		Link link = iter.next();
	    		String key = "OTN_RESOURECE_OTNLink" + "|" +sec.getAendNode()+"|"+sec.getZendNode()+"|"+  link.getLinkindex();
	    		ZdResult zdResult = (ZdResult)cacheClient.get(key);
	    		//ZdResult zdResult = (ZdResult)link.getAttrMap().get("ZdResultInfo");
	    		//判断是否有可用资源
	    		if(zdResult.getSncid().equals("UUID:516a8b47-10da-11e5-9c2d-005056862639")  ){
	    			System.out.println(7788);
	    		}
	    		if(zdResult.getODUinfo(param.getRate()).equals("")){
	    			iter.remove();
	    			log.debug( " 计算两点间距离 ，按资源过滤:"+ zdResult.getSncid() );
	    			continue ;
	    		}
	    		
	    		//客户侧路径，根据占用进行判断
				if( zdResult.getOdu() instanceof DSR ){
					String sncid = zdResult.getSncid();
					if( hasCucirtBySncid(sncid) ){
						log.info("计算两点间距离 ，根据客户层路径已占用过滤:" + zdResult.getSncid());
						continue;
					}
				}
	    		
	    		if( excludeMelist.size()>0 || excludePtplist.size()>0 ){
	    			Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
	    			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
	    			
	    			for (LinkedList<ZdResultSingle> zdsinglelist : allzd) {
	    				for (int i = 0; i < zdsinglelist.size(); i++) {
	   					ZdResultSingle zd = zdsinglelist.get(i);
	   					if( excludeMelist.contains(zd.getAendmeid()) || excludeMelist.contains(zd.getZendmeid()) ){
	   						iter.remove();
	   		    			log.info( " 计算两点间距离 ，按禁止网元过滤:"+ zdResult.getSncid() );
	   		    			continue outer;
	   					}
	   					if( excludePtplist.contains(zd.getAendptpid()) || excludePtplist.contains(zd.getZendptpid()) ){
	   						iter.remove();
	   		    			log.info( " 计算两点间距离 ，按禁止端口过滤:"+ zdResult.getSncid() );
	   		    			continue outer;
	   					}
	    				}
	    			}
	    		}
	    		
	    		//必经点包含在a端站点内
	    		if( inclusemap.containsKey( sec.getAendNode())  ){
	    			
	    			Set<String> cmelist = new HashSet<String>();
	    			Set<String> cptplist = new HashSet<String>();
	    			
	    			List<ExCluseBean> beans = inclusemap.get(sec.getAendNode());
	    			for (int i = 0; i < beans.size(); i++) {
	    				if(!StringUtils.isEmpty(beans.get(i).getPtpid())){
	    					cptplist.add(beans.get(i).getPtpid());
	    				}
	    				else{
	    					cmelist.add(beans.get(i).getMeid());
	    				}
					}
	    			
	    			Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
	    			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
	    			
	    			for (LinkedList<ZdResultSingle> zdsinglelist : allzd) {
	    				for (int i = 0; i < zdsinglelist.size(); i++) {
	    					ZdResultSingle zd = zdsinglelist.get(i);
	    					if( cmelist.contains(zd.getAendmeid())   ){
	    						cmelist.remove(zd.getAendmeid());
	    					}
	    					if( cmelist.contains(zd.getZendmeid())   ){
	    						cmelist.remove(zd.getZendmeid());
	    					}
	    					if( cptplist.contains(zd.getAendptpid())   ){
	    						cptplist.remove(zd.getAendptpid());
	    					}
	    					if( cptplist.contains(zd.getZendptpid())   ){
	    						cptplist.remove(zd.getZendptpid());
	    					}
	    				}
	    				if(cmelist.size()>0 || cptplist.size()>0){
	    					iter.remove();
	    					log.info( " 计算两点间距离 ，按必经点过滤:"+ zdResult.getSncid() );
	    					continue outer;
	    				}
	    			}
	    		}
	    		
	    		//必经点包含在z端站点内
	    		if( inclusemap.containsKey( sec.getZendNode())  ){
	    			
	    			Set<String> cmelist = new HashSet<String>();
	    			Set<String> cptplist = new HashSet<String>();
	    			
	    			List<ExCluseBean> beans = inclusemap.get(sec.getZendNode());
	    			for (int i = 0; i < beans.size(); i++) {
	    				if(!StringUtils.isEmpty(beans.get(i).getPtpid())){
	    					cptplist.add(beans.get(i).getPtpid());
	    				}
	    				else{
	    					cmelist.add(beans.get(i).getMeid());
	    				}
					}
	    			
	    			Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
	    			Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
	    			
	    			for (LinkedList<ZdResultSingle> zdsinglelist : allzd) {
	    				for (int i = 0; i < zdsinglelist.size(); i++) {
	    					ZdResultSingle zd = zdsinglelist.get(i);
	    					if( cmelist.contains(zd.getAendmeid())   ){
	    						cmelist.remove(zd.getAendmeid());
	    					}
	    					if( cmelist.contains(zd.getZendmeid())   ){
	    						cmelist.remove(zd.getZendmeid());
	    					}
	    					if( cptplist.contains(zd.getAendptpid())   ){
	    						cptplist.remove(zd.getAendptpid());
	    					}
	    					if( cptplist.contains(zd.getZendptpid())   ){
	    						cptplist.remove(zd.getZendptpid());
	    					}
	    				}
	    				if(cmelist.size()>0 || cptplist.size()>0){
	    					iter.remove();
	    					log.info( " 计算两点间距离 ，按必经点过滤:"+ zdResult.getSncid() );
	    					continue outer;
	    				}
	    			}
	    		}
	    	}
	    		
	    	if(linklist.size()==0){
	    		log.info( " 计算两点间距离 ，无结果返回" );
	    		return null;
	    	}
	    	
	    	//如果包含必经点，那么返回最短距离
	    	if( inclusemap.containsKey( sec.getAendNode()) ||inclusemap.containsKey( sec.getZendNode()) ){
	    		return Long.MAX_VALUE * -1 ; 
	    	}
	    	
	    	return matrix[aendindex][zendindex].getMinWeightLink(policy);
	    }
	    
		private  CaculatorResultWay  dijkstra(String aendid, String zendid, List<String> aendme , List<String> zendme, CaculatorParam param) {
			   
			//起始点的序号
			Integer startindex = (Integer)pointMap.get(aendid);
			Integer zendindex = (Integer)pointMap.get(zendid) ; 
			
			log.info("非直达寻路计算请求：" +" aend:"+ aendid +"("+ startindex+")" );
			log.info("非直达寻路计算请求：" +" zend:"+ zendid +"("+ zendindex+")" );
			
			closedSet.add(startindex);
			
	        for (int i = 0; i < matrix.length; i++) {  
	        	
	        	if( i == zendindex.intValue() ){
	        		distanceMap.put(i, unreachable);  
	        	}
	        	// 用start相邻的点初始化distanceMap  
	        	else if( matrix[startindex][i] ==null   ){
	        		distanceMap.put(i, unreachable);  
	        	}
	        	else{
	        		Long distance = getDistance(startindex, i, startindex, zendindex, aendme, zendme, param);
	        		if(distance==null){
	        			distanceMap.put(i, unreachable);  
	        		}
	        		else{
	        			distanceMap.put(i,  distance );  
		        		List<Integer> pathset = new ArrayList<Integer>();
		        		pathset.add(startindex);
		        		pathset.add(i);
		        		path.put(i, pathset);
	        		}
	        		
	        	}
	        	
	        	//所有的节点都缴入到未处理节点中
	        	if( i != startindex ){
	        		openSet.add(i);
	        	}
	        } 
	        
	        log.info("非直达寻路：" +" aendzd:"+ aendid +"("+ startindex+")" );
	        for ( Map.Entry<Integer, List<Integer>> cp : path.entrySet() ) {
	        	log.info("非直达寻路：" +" 可达站点:"+ pointMap.getKey(cp.getKey()) +"("+ cp.getKey()+")" + ",路径：" + cp.getValue() );
			}
	        
	        
	        while ( closedSet.size() != matrix.length) {  
	            int currentMinIndex = currentMinIndex();  
	            if (currentMinIndex == -1 ){
	            	log.info("非直达寻路：没有找到下一跳路径" );
	            	break ;
	            }
	            log.info("非直达寻路：" +" 下一跳最近点:"+ pointMap.getKey(currentMinIndex) +"("+ currentMinIndex +")" );
	            
	            closedSet.add(currentMinIndex);  
	            openSet.remove(currentMinIndex);
	            
	            // 用此结点更新其它未处理结点的距离  
	            for (Integer openNodeindex : openSet) {
					if( matrix[currentMinIndex][openNodeindex]!=null  ){
						
						Long nowdistance = getDistance(currentMinIndex, openNodeindex, startindex, zendindex, aendme, zendme, param);
						if( nowdistance ==null ){
							continue;
						}
								
						if( distanceMap.get(openNodeindex) == unreachable ){
							distanceMap.put(openNodeindex,  nowdistance + distanceMap.get(currentMinIndex)); 
							List<Integer> pathset = new ArrayList<Integer>();
							pathset.addAll( path.get(currentMinIndex)) ;
			        		pathset.add(openNodeindex);
							path.put(openNodeindex, pathset);
							log.info("非直达寻路：" +" 更新距离:"+ pointMap.getKey(openNodeindex) +"("+ openNodeindex +")" + ",路径：" + pathset );
						}
						
						if( nowdistance + distanceMap.get(currentMinIndex) < distanceMap.get(openNodeindex)){
							distanceMap.put(openNodeindex,  nowdistance + distanceMap.get(currentMinIndex));  
							List<Integer> pathset = new ArrayList<Integer>();
							pathset.addAll( path.get(currentMinIndex)) ;
			        		pathset.add(openNodeindex);
							path.put(openNodeindex, pathset);
							log.info("非直达寻路：" +" 更新距离:"+ pointMap.getKey(openNodeindex) +"("+ openNodeindex +")" + ",路径：" + pathset );
						}
					}
				}
	        }
	        
	        //没有找到，返回null
	        if( path.get(zendindex)==null ||path.get(zendindex).size()==0 ){
	            return null ; 
	        }
			
	        List<Integer> alllist =  path.get(zendindex);
       	    	   
       	    CaculatorResultWay way = new CaculatorResultWay();
        	way.setWayseq(0);
        	way.setRouteCount(alllist.size());

       	    for (int l = 0; l < alllist.size(); l++) {
       	    	Integer nodeindex = alllist.get(l) ; 
        		CaculatorResultWayRoute route = new CaculatorResultWayRoute();
        		route.setRouteseq(l);
        		route.setNodeid((String)pointMap.getKey(nodeindex));
        		
        		if( l+1 < alllist.size() ){
        			
        		   Section nextSectioninfo = matrix[nodeindex][alllist.get(l+1)];
        		   for (int i = 0; i < nextSectioninfo.getLinklist().size(); i++) {
        			   Link link = nextSectioninfo.getLinklist().get(i);
        			   String key = "OTN_RESOURECE_OTNLink" + "|"+nextSectioninfo.getAendNode()+"|"+nextSectioninfo.getZendNode()+"|" + link.getLinkindex();
        			   ZdResult zdResult = (ZdResult)cacheClient.get( key );
        			   link.setZdResult(zdResult);
        			   if(zdResult.getOdu() instanceof DSR){
        				   route.getClientrouts().add(link);
        			   }
        			   else if(zdResult.getOdu() instanceof ODU0){
        				   route.getOdu0routs().add(link);
        			   }
        			   else if(zdResult.getOdu() instanceof ODU1){
        				   route.getOdu1routs().add(link);
        			   }
        			   else if(zdResult.getOdu() instanceof ODU2){
        				   route.getOdu2routs().add(link);
        			   }
        			   else if(zdResult.getOdu() instanceof ODU3){
        				   route.getOdu3routs().add(link);
        			   }
        			   else if(zdResult.getOdu() instanceof ODU4){
        				   route.getOdu4routs().add(link);
        			   }
        			   else if(zdResult.getOdu() instanceof OCH){
        				   route.getOchrouts().add(link);
        			   }
        		   }
 			   }
        		
        		way.getRouts().add(route);
       	    }
       	    	   
		       
       		return way ;
		       
		}
		
		
		public boolean hasCucirtBySncid(String sncid){
			
			String  sql = " select count(objectid) from circuit where objectid in ( select circuitobjectid from circuitroute where relatedrouteobjectid = '"+sncid +"' ) ";
			
			String cucirtid = dbClient.getJdbcTemplate().queryForObject(sql, String.class);
					
			return !cucirtid.equals("0");
					
		}
		
		
}
