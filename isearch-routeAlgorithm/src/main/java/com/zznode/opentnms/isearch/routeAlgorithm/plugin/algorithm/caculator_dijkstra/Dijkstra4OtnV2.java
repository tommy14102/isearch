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

import com.zznode.opentnms.isearch.model.bo.ZdResult;
import com.zznode.opentnms.isearch.model.bo.ZdResultSingle;
import com.zznode.opentnms.isearch.routeAlgorithm.App;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWay;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWayRoute;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;
import com.zznode.opentnms.isearch.routeAlgorithm.core.algorithm.AlgorithmProcessor;
import com.zznode.opentnms.isearch.routeAlgorithm.core.cache.SPtnMemcachedClient;

public class Dijkstra4OtnV2 extends AlgorithmProcessor {

	 private static Logger log = Logger.getLogger(Dijkstra4OtnV2.class);
	 
	 private SPtnMemcachedClient cacheClient = (SPtnMemcachedClient)App.factory.getBean("SPtnMemcachedClient");
	    
	 	Set<Integer> openSet =new HashSet<Integer>();   // 未处理过的节点
	    Set<Integer> closedSet =new HashSet<Integer>();  //已经处理过的节点
	    HashMap<Integer, List<Integer>> path=new HashMap<Integer,List<Integer>>();//封装路径信息  
	    // 用来存放起始点到其它点当前的距离  
	    HashMap<Integer, Long> distanceMap = new HashMap<Integer, Long>();  
	    Long unreachable = Long.MAX_VALUE ; 
	    
	    
	    @Override
		protected List<CaculatorResultWay> doCaculate( CaculatorParam param ) {
	    	
	    	String aendid = param.getAend();
	    	String zendid = param.getZend();
	    	String aendme = param.getAendme();
	    	String zendme = param.getZendme();
	    	
	    	return dijkstra( aendid, zendid, aendme , zendme ,param );
	    	
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
	    
	    private Long getDistance(int aendindex , int zendindex , int headindex , int tailindex , String aendme , String zendme , CaculatorParam param ){
	    	
	    	String aendptp = (String)param.getAttrMap().get("aendptp");
	    	String aendctp = (String)param.getAttrMap().get("aendctp");
	    	String zendptp = (String)param.getAttrMap().get("zendptp");
	    	String zendctp = (String)param.getAttrMap().get("zendctp");
	    	
	    	if(aendindex== headindex){
	    		
	    		Section sec = matrix[aendindex][zendindex];
	    		List<Link> linklist = sec.getLinklist();
	    		for (Iterator<Link> iter = linklist.iterator(); iter.hasNext();) {
					Link link = iter.next();
					
					String key = "OTN_RESOURECE_OTNLink" + "|" + link.getLinkindex();
					ZdResult zdResult = (ZdResult)cacheClient.get(key);
					
					//ZdResult zdResult = (ZdResult)link.getAttrMap().get("ZdResultInfo");
					Map<String, LinkedList<ZdResultSingle>> zdmap =  zdResult.getZdmap();
					Collection<LinkedList<ZdResultSingle>> allzd =  zdmap.values();
					
					//判断是否经过a网元
					Iterator<LinkedList<ZdResultSingle>>  iters = allzd.iterator();
					LinkedList<ZdResultSingle> firstelement = iters.next();
					if(	!firstelement.getFirst().getAendmeid().equals(aendme)){
						iter.remove();
						continue;
					}
					
					//判断是否经过a端口
					if(	!StringUtils.isEmpty(aendptp) && !firstelement.getFirst().getAendptpid().equals(aendptp)){
						iter.remove();
						continue;
					}
					
					//判断是否经过a时隙
					if(	!StringUtils.isEmpty(aendctp) && !firstelement.getFirst().getAendctp().equals(aendctp)){
						iter.remove();
						continue;
					}
					
					//判断是否有可用资源
					if(zdResult.getODUinfo(param.getRate()).equals("")){
						iter.remove();
						continue ;
					}
				}
	    		if(linklist.size()==0){
	    			return null;
	    		}
	    		
	    	}else if(zendindex== tailindex){
	    		
	    		Section sec = matrix[aendindex][zendindex];
	    		List<Link> linklist = sec.getLinklist();
	    		for (Iterator<Link> iter = linklist.iterator(); iter.hasNext();) {
					Link link = iter.next();
					
					String key = "OTN_RESOURECE_OTNLink" + "|" + link.getLinkindex();
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
					
					if( !lastelement.getLast().getZendmeid().equals(zendme)){
						iter.remove();
						continue;
					}
					
					//判断是否经过z端口
					if(	!StringUtils.isEmpty(zendptp) && !lastelement.getLast().getZendptpid().equals(zendptp)){
						iter.remove();
						continue;
					}
					
					//判断是否经过z时隙
					if(	!StringUtils.isEmpty(zendctp) && !lastelement.getLast().getZendctp().equals(zendctp)){
						iter.remove();
						continue;
					}
					
					//判断是否有可用资源
					if(zdResult.getODUinfo(param.getRate()).equals("")){
						iter.remove();
						continue ;
					}
				}
	    		if(linklist.size()==0){
	    			return null;
	    		}
	    		
	    	}else{
	    		
	    		Section sec = matrix[aendindex][zendindex];
	    		List<Link> linklist = sec.getLinklist();
	    		for (Iterator<Link> iter = linklist.iterator(); iter.hasNext();) {
					Link link = iter.next();
					String key = "OTN_RESOURECE_OTNLink" + "|" + link.getLinkindex();
					ZdResult zdResult = (ZdResult)cacheClient.get(key);
					//ZdResult zdResult = (ZdResult)link.getAttrMap().get("ZdResultInfo");
					//判断是否有可用资源
					if(zdResult.getODUinfo(param.getRate()).equals("")){
						iter.remove();
						continue ;
					}
	    		}
	    		if(linklist.size()==0){
	    			return null;
	    		}
	    	}
	    	
	    	return matrix[aendindex][zendindex].getMinWeightLink(policy);
	    }
	    
		private List<CaculatorResultWay> dijkstra(String aendid, String zendid, String aendme , String zendme, CaculatorParam param) {
			   
			//起始点的序号
			Integer startindex = (Integer)pointMap.get(aendid);
			Integer zendindex = (Integer)pointMap.get(zendid) ; 
			
			closedSet.add(startindex);
			
	        for (int i = 0; i < matrix.length; i++) {  
	        	
	        	// 用start相邻的点初始化distanceMap  
	        	if( matrix[startindex][i] ==null   ){
	        		distanceMap.put(i, unreachable);  
	        	}
	        	else{
	        		distanceMap.put(i,  getDistance(startindex, i, startindex, zendindex, aendme, zendme, param) );  
	        		List<Integer> pathset = new ArrayList<Integer>();
	        		pathset.add(startindex);
	        		pathset.add(i);
	        		path.put(i, pathset);
	        	}
	        	
	        	//所有的节点都缴入到未处理节点中
	        	if( i != startindex ){
	        		openSet.add(i);
	        	}
	        } 
	        
	        while ( closedSet.size() != matrix.length) {  
	            int currentMinIndex = currentMinIndex();  
	            if (currentMinIndex == -1 ){
	            	break ;
	            }
	            closedSet.add(currentMinIndex);  
	            openSet.remove(currentMinIndex);
	            
	            // 用此结点更新其它未处理结点的距离  
	            for (Integer openNodeindex : openSet) {
					if( matrix[currentMinIndex][openNodeindex]!=null  ){
						
						long nowdistance = getDistance(currentMinIndex, openNodeindex, startindex, zendindex, aendme, zendme, param);
								
						if( distanceMap.get(openNodeindex) == unreachable ){
							distanceMap.put(openNodeindex,  nowdistance + distanceMap.get(currentMinIndex)); 
							List<Integer> pathset = new ArrayList<Integer>();
							pathset.addAll( path.get(currentMinIndex)) ;
			        		pathset.add(openNodeindex);
							path.put(openNodeindex, pathset);
						}
						
						if( nowdistance + distanceMap.get(currentMinIndex) < distanceMap.get(openNodeindex)){
							distanceMap.put(openNodeindex,  nowdistance + distanceMap.get(currentMinIndex));  
							List<Integer> pathset = new ArrayList<Integer>();
							pathset.addAll( path.get(currentMinIndex)) ;
			        		pathset.add(openNodeindex);
							path.put(openNodeindex, pathset);
						}
					}
				}
	        }
	        
	        //没有找到，返回null
	        if( path.get(zendindex)==null ||path.get(zendindex).size()==0 ){
	            return null ; 
	        }
			
	        List<CaculatorResultWay> ways = new ArrayList<CaculatorResultWay>();
	        List<Integer> alllist =  path.get(zendindex);
       	    	   
       	    CaculatorResultWay way = new CaculatorResultWay();
        	way.setWayseq(0);
        	way.setRouteCount(alllist.size());
        	LinkedList<CaculatorResultWayRoute> routs = new LinkedList<CaculatorResultWayRoute>();
        	       
       	    for (int l = 0; l < alllist.size(); l++) {
       	    	Integer nodeindex = alllist.get(l) ; 
        		CaculatorResultWayRoute route = new CaculatorResultWayRoute();
        		route.setRouteseq(l);
        		route.setNodeid((String)pointMap.getKey(nodeindex));
        		
        		if( l+1 < alllist.size() ){
        		   Section nextSectioninfo = matrix[nodeindex][alllist.get(l+1)];
 				   route.getAttrMap().put("section", nextSectioninfo);
 			   }
        		
        		routs.add(route);
       	    }
       	    	   
       	    way.setRouts(routs);
       	    ways.add(way);
		       
       		return ways ;
		       
		}
		
		
}
