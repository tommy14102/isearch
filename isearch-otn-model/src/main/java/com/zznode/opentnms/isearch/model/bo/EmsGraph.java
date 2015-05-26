package com.zznode.opentnms.isearch.model.bo;

import java.util.HashMap;
import java.util.Map;


public class EmsGraph {
	
	private Map<String, Zhandian> zdmap = new HashMap<String, Zhandian>();

	public Map<String, Zhandian> getZdmap() {
		return zdmap;
	}

	public void setZdmap(Map<String, Zhandian> zdmap) {
		this.zdmap = zdmap;
	}
	
	

}
