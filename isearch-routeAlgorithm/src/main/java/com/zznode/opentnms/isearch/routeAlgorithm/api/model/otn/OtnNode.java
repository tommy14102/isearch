package com.zznode.opentnms.isearch.routeAlgorithm.api.model.otn;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Node;

public class OtnNode extends Node implements Serializable{

	public OtnNode() {
	}
	
	public OtnNode(String id) {
		super(id);
	}

	private static final long serialVersionUID = -3120075992921834138L;

	
	/**
	private Set<String> tsnids = new HashSet<String>();


	public Set<String> getTsnids() {
		return tsnids;
	}

	public void setTsnids(Set<String> tsnids) {
		this.tsnids = tsnids;
	}
	*/

	@Override
	public String toString() {
		return "OtnNode [id=" + super.getId() + "]";
	}

	
	
}
