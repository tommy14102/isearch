package com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_bellmanFord;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWay;
import com.zznode.opentnms.isearch.routeAlgorithm.core.algorithm.AlgorithmProcessor;

public class BellmanFord extends AlgorithmProcessor {

    private static Logger log = Logger.getLogger(BellmanFord.class);
    
	List<CaculatorResultWay> ways = new ArrayList<CaculatorResultWay>();
	private int wayseq = 0 ; 
	private double[] D;	
	
	//计算路径使用，保存路径信息
	private List<Integer> routelist = new ArrayList<Integer>();
	
    
    @Override
	protected List<CaculatorResultWay> doCaculate(CaculatorParam param ) {
    	
    	String aendid = param.getAend();
    	String zendid = param.getZend();
    	
    	return bellmanFord(aendid, zendid);
    	
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
	public List<CaculatorResultWay> bellmanFord( String aendid, String zendid ) {
		
	    log.info("开始进行深度搜索算法，aendid: "+ aendid + ",zendid:" + zendid);
	  
	    //两端的节点序号
	  	Integer startindex = (Integer)pointMap.get(aendid);
	  	Integer endindex = (Integer)pointMap.get(zendid);
	  		
	    D = new double[matrix.length];
	    for(int i=0;i<matrix.length;i++)
	    {
	      D[i] = 99999999;//无穷大
	    }
	    D[startindex] = 0;
	    for(int i=0;i<matrix.length;i++)//外层循环次数
	    {
	      for(int j=0;j<matrix.length;j++)
	      {
	        for(int k=0;k<matrix.length;k++)
	        {
	          if(matrix[j][k] != null )
	          {
	            relax(j,k);
	          }
	        }
	      }
	    }
		
		return ways ; 
				
	}
	
	private void relax(int u,int v)
	{
		if(D[v]>D[u]+ matrix[v][u].getMinWeightLink(policy)) {
			D[v]=D[u]+ matrix[v][u].getMinWeightLink(policy);
	    }
	}



	public List<CaculatorResultWay> getWays() {
		return ways;
	}


	public void setWays(List<CaculatorResultWay> ways) {
		this.ways = ways;
	}
	
	

}
