package com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWay;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWayRoute;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;
import com.zznode.opentnms.isearch.routeAlgorithm.core.algorithm.AlgorithmProcessor;

public class Dijkstra extends AlgorithmProcessor {

	 private static Logger log = Logger.getLogger(Dijkstra.class);
	    
	 	Set<Integer> openSet =new HashSet<Integer>();   // 未处理过的节点
	    Set<Integer> closedSet =new HashSet<Integer>();  //已经处理过的节点
	    HashMap<Integer, List<Integer>> path=new HashMap<Integer,List<Integer>>();//封装路径信息  
	    // 用来存放起始点到其它点当前的距离  
	    HashMap<Integer, Long> distanceMap = new HashMap<Integer, Long>();  
	    Long unreachable = Long.MAX_VALUE ; 
	    
	    
	    @Override
		protected List<CaculatorResultWay> doCaculate(CaculatorParam param ) {
	    	
	    	String aendid = param.getAend();
	    	String zendid = param.getZend();
	    	
	    	return dijkstra(aendid, zendid);
	    	
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
	    
		private List<CaculatorResultWay> dijkstra(String aendid, String zendid) {
			   
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
	        		distanceMap.put(i, matrix[startindex][i].getMinWeightLink(policy));  
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
						
						if( distanceMap.get(openNodeindex) == unreachable ){
							distanceMap.put(openNodeindex,  matrix[currentMinIndex][openNodeindex].getMinWeightLink( policy ) + distanceMap.get(currentMinIndex)); 
							List<Integer> pathset = new ArrayList<Integer>();
							pathset.addAll( path.get(currentMinIndex)) ;
			        		pathset.add(openNodeindex);
							path.put(openNodeindex, pathset);
						}
						
						if( matrix[currentMinIndex][openNodeindex].getMinWeightLink( policy ) + distanceMap.get(currentMinIndex) < distanceMap.get(openNodeindex)){
							distanceMap.put(openNodeindex,  matrix[currentMinIndex][openNodeindex].getMinWeightLink( policy ) + distanceMap.get(currentMinIndex));  
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
        			   Section beforeSectioninfo = null ;
        			   Section nextSectioninfo = null ;
        			
        			   if( l-1 >= 0 ){
        				   beforeSectioninfo = matrix[alllist.get(l-1)][nodeindex];
        			   }
        			   if( l+1 < alllist.size() ){
        				   nextSectioninfo = matrix[nodeindex][alllist.get(l+1)];
        			   }
        			
        			   CaculatorResultWayRoute route = new CaculatorResultWayRoute();
        			   route.setRouteseq(l);
        			   route.setNodeid((String)pointMap.getKey(nodeindex));
        			   
        			   if( beforeSectioninfo!= null ){
        				   List<Link> linklist = beforeSectioninfo.getLinklist();
        				   for (int k = 0; k < linklist.size(); k++) {
        					   Link link = linklist.get(k);
        					   //route.getLefteageid().add(link.getZendeage());
        					   //route.setTsnid(link.getRighttsnid());
        				   }
        			   }
        			   if( nextSectioninfo!= null ){
        				   List<Link> linklist = nextSectioninfo.getLinklist();
        				   for (int k = 0; k < linklist.size(); k++) {
        					   Link link = linklist.get(k);
        					   //route.getRighteageid().add(link.getAendeage());
        					   //route.setTsnid(link.getLefttsnid());
        				   }
        			   }

        			   routs.add(route);
       	    	   	}
       	    	   
       	    	   	way.setRouts(routs);
       	    	   	ways.add(way);
		       
       		   return ways ;
		       
		}
 
		
}
