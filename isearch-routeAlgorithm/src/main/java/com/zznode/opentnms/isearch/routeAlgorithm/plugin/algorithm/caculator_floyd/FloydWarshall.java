package com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_floyd;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWay;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWayRoute;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;
import com.zznode.opentnms.isearch.routeAlgorithm.core.algorithm.AlgorithmProcessor;

public class FloydWarshall extends AlgorithmProcessor {

	 private static Logger log = Logger.getLogger(FloydWarshall.class);
	    
		
		
		
		
	    @Override
		protected List<CaculatorResultWay> doCaculate(CaculatorParam param ) {
	    	
	    	String aendid = param.getAend();
	    	String zendid = param.getZend();
	    	
	    	return floyd(aendid, zendid);
	    	
		}

		private List<CaculatorResultWay> floyd(String aendid, String zendid) {
			   
			   long[][] DD = new long[matrix.length][matrix.length];
			   int[][] p = new int[matrix.length][matrix.length];
		       for(int i = 0;i < matrix.length;i++)
		       {
		             for(int j = 0;j < matrix.length;j++)
		             {
		          	   if(matrix[i][j] != null){
		          		   DD[i][j] = matrix[i][j].getMinWeightLink(policy);
		          	   }
		          	   else if(i == j){
		          		   DD[i][j] = 0;
		          	   }  
		          	   else{
		          		   DD[i][j] = Integer.MAX_VALUE;
		          	   } 
		          	   
		          	   p[i][j] = j;
		          	 
		             }
		       }
		       for(int k = 0;k < matrix.length;k++)
		       for(int i = 0;i < matrix.length;i++)
		       for(int j = 0;j < matrix.length;j++)
		       {
		    	   if(DD[i][j] > DD[i][k] + DD[k][j])
		    	   {
		           		DD[i][j] = DD[i][k] + DD[k][j]; 
	                   p[i][j] = p[i][k];//更新下一跳的点
		    	   }
		       }

		       int startindex = (Integer)pointMap.get(aendid) ; 
		       int endindex = (Integer)pointMap.get(zendid) ; 
		       long sorteastWaycount = DD[startindex][endindex];
		       
		       int headindex = startindex ;
		       int tailindex = p[startindex][endindex];
		       
		       List<List<Integer>> alllist = dealOld(headindex,endindex , p );
		       
		       
       	       List<CaculatorResultWay> ways = new ArrayList<CaculatorResultWay>();
       	       for (int i = 0; i < alllist.size(); i++) {
       	    	   List<Integer> innerlist =  alllist.get(i);
       	    	   
       	    	   CaculatorResultWay way = new CaculatorResultWay();
        	       way.setWayseq(0);
        	       way.setRouteCount(innerlist.size());
        	       LinkedList<CaculatorResultWayRoute> routs = new LinkedList<CaculatorResultWayRoute>();
        	       
       	    	   for (int l = 0; l < innerlist.size(); l++) {
       	    	   
       	    		Integer nodeindex = innerlist.get(l) ; 
        			   Section beforeSectioninfo = null ;
        			   Section nextSectioninfo = null ;
        			
        			   if( l-1 >= 0 ){
        				   beforeSectioninfo = matrix[innerlist.get(l-1)][nodeindex];
        			   }
        			   if( l+1 < innerlist.size() ){
        				   nextSectioninfo = matrix[nodeindex][innerlist.get(l+1)];
        			   }
        			
        			   CaculatorResultWayRoute route = new CaculatorResultWayRoute();
        			   route.setRouteseq(l);
        			   route.setNodeid((String)pointMap.getKey(nodeindex));
        			   if( beforeSectioninfo!= null ){
        				   List<Link> linklist = beforeSectioninfo.getLinklist();
        				   for (int k = 0; k < linklist.size(); k++) {
        					   Link link = linklist.get(k);
        					   //route.getLefteageid().add(link.getZendeage());
        				   }
        			   }
        			   if( nextSectioninfo!= null ){
        				   List<Link> linklist = nextSectioninfo.getLinklist();
        				   for (int k = 0; k < linklist.size(); k++) {
        					   Link link = linklist.get(k);
        					   //route.getRighteageid().add(link.getAendeage());
        				   }
        			   }

        			   routs.add(route);
       	    	   	}
       	    	   
       	    	   	way.setRouts(routs);
       	    	   	ways.add(way);
       	       }
		       
       		   return ways ;
		       
		}

		private List<List<Integer>> deal(int headindex,int endindex ,   Vector<Integer>[][] p ) {
			
			Stack<Integer> stack = new Stack<Integer>();
			stack.push(headindex);
			
			List<List<Integer>> routelistall= new ArrayList<List<Integer>>();
			List<Integer> routelist= new ArrayList<Integer>();
			
			while(!stack.isEmpty()){
				Integer index = stack.pop();
				routelist.add(index);
				Vector<Integer> v= p[index][endindex];
				if( v.size()==1 && v.get(0).intValue() == endindex ){
					List<Integer> routelistNew = new ArrayList<Integer>(routelist); 
					routelistNew.add(endindex);
					routelistall.add(routelistNew);
					routelist.remove(routelist.size()-1);
		    	}
				else{
					for (int i = 0; i < v.size(); i++) {
						stack.push(v.get(i));
					}
				}
			}
			
			return routelistall ; 
			
		}
		
		private List<List<Integer>> dealOld(int headindex,int endindex ,   int[][] p ) {
			
			List<Integer> rtnlist = new ArrayList<Integer>();
			rtnlist.add(headindex);
			
			int tailindex = p[headindex][endindex];
			rtnlist.add(tailindex);
			
			while( tailindex != endindex){
				
				tailindex = p[tailindex][endindex];
				rtnlist.add(tailindex);
			}
			
			List<List<Integer>> alllist = new ArrayList<List<Integer>>();
			alllist.add(rtnlist);
			
			return alllist ; 
		}
		
		
}
