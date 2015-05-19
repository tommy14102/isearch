package com.zznode.opentnms.isearch.routeAlgorithm.plugin.data.importor_base;

import java.util.List;

import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.core.data.DataProcessor;

public class BaseImporter<T extends Link>  extends DataProcessor<T>{

	private List<T> linklist ;
	
	public BaseImporter(List<T> linklist2){
		this.linklist = linklist2 ; 
	}
	
	@Override
	public List<T> argnizeData() {
		return  linklist;
	}


}
