package com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_dfs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWay;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWayRoute;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;
import com.zznode.opentnms.isearch.routeAlgorithm.core.algorithm.AlgorithmProcessor;

public class DFS extends AlgorithmProcessor {

    private static Logger log = Logger.getLogger(DFS.class);
    
	List<CaculatorResultWay> ways = new ArrayList<CaculatorResultWay>();
	private int wayseq = 0 ; 
		
	
	//计算路径使用，保存路径信息
	private List<Integer> routelist = new ArrayList<Integer>();
	
	//深度遍历时，已经访问过的节点id
    //private Set<Integer> visitedset = new HashSet<Integer>();
	
    
    @Override
	protected List<CaculatorResultWay> doCaculate(CaculatorParam param ) {
    	
    	String aendid = param.getAend();
    	String zendid = param.getZend();
    	
    	return dfsSingle(aendid, zendid);
    	
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
	public List<CaculatorResultWay> dfsSingle( String aendid, String zendid ) {
		
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
		dfsfindSingle(startindex,endindex);
		
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
	public void dfsfindSingle( Integer aendindex, Integer zendindex ) {
		
		//循环所有其他顶点，如果有边
	    for(int i=0; i<matrix.length; i++)
	    {
	    	if( matrix[aendindex][i] != null &&  aendindex != i  && !routelist.contains(i))
	        {
	        	if(i == zendindex.intValue())//如果深搜到了终点，就输出刚才经过的路径
	            {
	        		routelist.add(i);
	        		//addEndInfo(matrix[aendindex][i]);
	        		//matrix[aendindex][i].setVisited(Boolean.TRUE);
	        	    log.info("添加节点（终点）："+ i + ",rout:" +  routelist );
	        	    
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
	        		routelist.remove(routelist.size()-1);
                    
	            }
	            else///如果该点不是终点
	            {
	                routelist.add(i);
	                log.info("添加节点："+ i + ",rout:" +  routelist);
	            	//visitedset.add( i ) ; 
	                //matrix[aendindex][i].setVisited(Boolean.TRUE);
	            	addStepInfo(matrix[aendindex][i]);
	            	dfsfindSingle( i , zendindex  );//接着深搜
	            	routelist.remove(routelist.size()-1);
	            }
	        }
	    }
	}

	private void addEndInfo(Section section) {

		section.getAttrMap().put("showinfo", "z");
		
	}

	private void addStartInfo(Section section) {

		section.getAttrMap().put("showinfo", "a");
	}

	private void addStepInfo(Section section) {

		if(section.getAttrMap().get("showinfo")==null){
			section.getAttrMap().put("showinfo",1);
		}
		else{
			section.getAttrMap().put("showinfo", (Integer)section.getAttrMap().get("showinfo") +1 );
		}
	}


	public List<CaculatorResultWay> getWays() {
		return ways;
	}


	public void setWays(List<CaculatorResultWay> ways) {
		this.ways = ways;
	}
	
	

}
