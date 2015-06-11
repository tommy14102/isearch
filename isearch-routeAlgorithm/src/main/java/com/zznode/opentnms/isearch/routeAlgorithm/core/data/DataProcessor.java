package com.zznode.opentnms.isearch.routeAlgorithm.core.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.zznode.opentnms.isearch.routeAlgorithm.App;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.core.cache.SPtnMemcachedClient;
import com.zznode.opentnms.isearch.routeAlgorithm.core.matrix.Matrix;

@Service("DataProcessor")
public abstract class DataProcessor<T extends Link> {

	 private static Logger log = Logger.getLogger(DataProcessor.class);
	 private SPtnMemcachedClient cacheClient = (SPtnMemcachedClient)App.factory.getBean("SPtnMemcachedClient");
	
	 public void importdata( String businessAvatorKey ){
		 
		 List<T> linklist = argnizeData();
		 doimport(businessAvatorKey , linklist);
	 }
	 
	 private void doimport(String businessAvatorKey , List<T> linklist){
		 
		 Set<String> nodecount = new HashSet<String>();
	     for (Iterator<T> iter = linklist.iterator(); iter.hasNext();) {
	    	 Link topo =  iter.next();
	    	 nodecount.add(topo.getAendnode().getId());
	    	 nodecount.add(topo.getZendnode().getId());
	     }
	     
	     Matrix matrix = new Matrix(nodecount.size());
	    	 
	     log.info("开始组装邻接矩阵");
	     for (Iterator<T> iter = linklist.iterator(); iter.hasNext();) {
	    	 Link topo =  iter.next();
	         log.info("组装邻接矩阵,aend：" + topo.getAendnode() + ",zend: "+ topo.getZendnode());
	         matrix.addArc(topo.getAendnode(), topo.getZendnode(), topo.getDirection(), topo);
	     }
	        
	     log.info( "组装邻接矩阵完成:" + matrix.show() );
	        
	     cacheClient.set(businessAvatorKey , 0, matrix);
	     log.error("存贮邻接矩阵数据完成"  );
	 }
	 
	 protected  abstract  List<T> argnizeData() ;

}
