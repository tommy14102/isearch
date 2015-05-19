package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;

public class Section implements Serializable{

	private static final long serialVersionUID = 4452172743722411302L;
	
	private String id ;
	private String aendNode ; 
	private String zendNode ;
	
	private List<Link> linklist ;
	private Map<String , Object> attrMap = new HashMap<String , Object>(); 
	private Boolean visited = false  ;

	public long getMinWeightLink(Policy policy){
		long minweight = -1 ; 
		for (int i = 0; i < linklist.size(); i++) {
			Link link =  linklist.get(i);
			long weight = link.getWeight(policy);
			if( minweight == -1 || minweight > weight ){
				minweight = weight ; 
			}
		}
		return minweight ; 
	}
	
	public Boolean isVisited() {
		return visited;
	}

	public void setVisited(Boolean visited) {
		this.visited = visited;
	}

	public Map<String , Object> getAttrMap() {
		return attrMap;
	}

	public void setAttrMap(Map<String , Object> attrMap) {
		this.attrMap = attrMap;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAendNode() {
		return aendNode;
	}

	public void setAendNode(String aendNode) {
		this.aendNode = aendNode;
	}


	public String getZendNode() {
		return zendNode;
	}

	public void setZendNode(String zendNode) {
		this.zendNode = zendNode;
	}


	public List<Link> getLinklist() {
		return linklist;
	}

	public void setLinklist(List<Link> linklist) {
		this.linklist = linklist;
	} 
	
	

}
