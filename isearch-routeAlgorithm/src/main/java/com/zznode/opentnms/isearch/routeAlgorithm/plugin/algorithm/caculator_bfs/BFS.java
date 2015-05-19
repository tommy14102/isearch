package com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_bfs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWay;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWayRoute;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;
import com.zznode.opentnms.isearch.routeAlgorithm.core.algorithm.AlgorithmProcessor;

public class BFS extends AlgorithmProcessor {

    private static Logger log = Logger.getLogger(BFS.class);
    
	List<CaculatorResultWay> ways = new ArrayList<CaculatorResultWay>();
	private int wayseq = 0 ; 
		
	
	//计算路径使用，保存路径信息
	private List<Integer> routelist = new ArrayList<Integer>();
	
	  Queue<Integer> bfsQueue = new LinkedList<Integer>();
	  boolean[] bfsFlag;
	  int bsfPre[];
    
    @Override
	protected List<CaculatorResultWay> doCaculate(CaculatorParam param ) {
    	
    	String aendid = param.getAend();
    	String zendid = param.getZend();
    	
    	return bfs(aendid, zendid);
    	
	}
 
	
	/**
	 * 深度搜索算法，找到所有可能路径
	 * 
	 * 注意，此方法返回的数据为单节点数据
	 * 
	 * @param aendid
	 * @param zendid
	 * @return
	 */
	public List<CaculatorResultWay> bfs( String aendid, String zendid ) {
		
	    log.info("开始进行深度搜索算法，aendid: "+ aendid + ",zendid:" + zendid);
	    
		//两端的节点序号
		Integer startindex = (Integer)pointMap.get(aendid);
		Integer endindex = (Integer)pointMap.get(zendid);
		
		//添加第一个网元序号
		routelist.add( startindex );
		log.info("添加节点（起始）："+ startindex + ",rout:" +  routelist );
		//visitedset.add( startindex ) ; 
		//addStartInfo(matrix[startindex][startindex]);
		
		ways.clear();
		wayseq = 0 ; 
		bfsfindSingle(startindex,endindex);
		
		return ways ; 
				
	}
	
	/**
	 * 深度搜索算法，找到所有可能路径
	 * 
	 * 参数为两段NCD序号，注意此方法不包含头结点。
	 * 
	 * @param aendid
	 * @param zendid
	 * @return
	 */
	public void bfsfindSingle( Integer aendindex, Integer zendindex ) {
		
		
		bsfPre = new int[matrix.length];
	    bfsQueue.clear();
	    bfsFlag = new boolean[matrix.length];
	    for(int i=0;i<matrix.length;i++)
	    {
	      bfsFlag[i] = false;
	      bsfPre[i] = -1;
	    }
	    

	    bfsQueue.offer(aendindex);
	    bfsFlag[aendindex] = true;
	    
	    outer:while(!bfsQueue.isEmpty())
	    {
	      int current = bfsQueue.poll();
	      for(int index=0;index<matrix.length;index++)
	      {
	        if(current == index) continue;
	        if(matrix[current][index] !=null ) //两者相连
	        {
	          if(index == zendindex)//找到目标了
	          {
	            bfsFlag[index] = true;
	            bsfPre[index] = current;
	            break outer ; 
	          }
	          if(bfsFlag[index] == false)//如果未访问过
	          {
	            bfsFlag[index] = true;
	            bsfPre[index] = current;
	            bfsQueue.offer(index);
	          }
	        }
	      }
	    }
	    
	    List<Integer> routelist = new ArrayList<Integer>(); 
	    
	    int index = zendindex;
	    
	    do
	    {
	    	routelist.add(index);
	    	index = bsfPre[index];
	    }while(index != -1);

	    
	    Collections.reverse(routelist);
	    
		
	        	    
	        	    CaculatorResultWay way = new CaculatorResultWay();
	        	    way.setWayseq(wayseq++);
	        	    way.setRouteCount(routelist.size());
	        	    LinkedList<CaculatorResultWayRoute> routs = new LinkedList<CaculatorResultWayRoute>();
	        	    
	        		for(int j=0; j< routelist.size(); j++)
	                {
	        			Integer nodeindex = routelist.get(j) ; 
	        			Section beforeSectioninfo = null ;
	        			Section nextSectioninfo = null ;
	        			
	        			if( j-1 >= 0 ){
	        				beforeSectioninfo = matrix[routelist.get(j-1)][nodeindex];
	        			}
	        			if( j+1 < routelist.size() ){
	        				nextSectioninfo = matrix[nodeindex][routelist.get(j+1)];
	        			}
	        			
	        			CaculatorResultWayRoute route = new CaculatorResultWayRoute();
	        			route.setRouteseq(j);
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



	public List<CaculatorResultWay> getWays() {
		return ways;
	}


	public void setWays(List<CaculatorResultWay> ways) {
		this.ways = ways;
	}
	
	

}
