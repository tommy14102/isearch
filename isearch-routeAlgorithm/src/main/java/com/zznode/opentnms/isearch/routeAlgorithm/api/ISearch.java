package com.zznode.opentnms.isearch.routeAlgorithm.api;

import com.zznode.opentnms.isearch.routeAlgorithm.api.model.BusinessAvator;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResult;
import com.zznode.opentnms.isearch.routeAlgorithm.core.algorithm.AlgorithmProcessor;
import com.zznode.opentnms.isearch.routeAlgorithm.core.data.DataProcessor;

public class ISearch {

	
	public boolean regist(BusinessAvator businessAvator){
		
		return true ;
		
	}
	
	public boolean refreshdata(String businessAvatorKey , DataProcessor<?> dataProcessor){
		
		dataProcessor.importdata( businessAvatorKey );
		
		return true;
		
	}
	
	public CaculatorResult search(String businessAvatorKey ,CaculatorParam param , AlgorithmProcessor algorithmProcessor){
		
		
		return algorithmProcessor.caculate(businessAvatorKey ,param);
	}

}
