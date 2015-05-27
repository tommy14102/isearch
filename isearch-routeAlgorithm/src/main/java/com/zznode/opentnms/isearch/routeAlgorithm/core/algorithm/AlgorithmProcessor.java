package com.zznode.opentnms.isearch.routeAlgorithm.core.algorithm;

import java.util.List;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;

import com.zznode.opentnms.isearch.routeAlgorithm.App;
import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResult;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResultWay;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;
import com.zznode.opentnms.isearch.routeAlgorithm.core.cache.SPtnMemcachedClient;
import com.zznode.opentnms.isearch.routeAlgorithm.core.matrix.Matrix;

public abstract class AlgorithmProcessor {
	
	private static Logger log = Logger.getLogger(AlgorithmProcessor.class);
	private SPtnMemcachedClient testobj = (SPtnMemcachedClient)App.factory.getBean("SPtnMemcachedClient");
	
	
	protected Matrix matrixholder;
	protected Section[][] matrix;
	protected Policy policy ; 
	
	//节点与矩阵元素的对应关系
	protected BidiMap pointMap = new DualHashBidiMap();

	 public CaculatorResult caculate(String businessAvatorKey , CaculatorParam param){
		 
		 Matrix matrix = (Matrix)testobj.get(businessAvatorKey);
		 
		 if( matrix ==null ){
			 log.error("memcached中未查询到资源信息");
			 return null;
		 }
		 
		 this.policy = param.getPolicy();
	     this.matrixholder = matrix ; 
	     this.matrix = matrix.getMatrix();
	     this.pointMap = matrix.getPointMap();
	    	
		 
		 long starttime = System.currentTimeMillis();
		 
		 List<CaculatorResultWay> ways =  doCaculate( param );
		 //log.info( "搜索路径完成:" + matrix.showSearchInfo());

		 CaculatorResult result = new CaculatorResult();
		 result.setResultCode("0");
		 result.setResultMessage("OK");
		 result.setTimecost( System.currentTimeMillis() - starttime );
		 result.setWays(ways);
		 
		 return result ; 
	 }
	 
	  
	 
	 protected  abstract List<CaculatorResultWay> doCaculate( CaculatorParam param ) ;

}
