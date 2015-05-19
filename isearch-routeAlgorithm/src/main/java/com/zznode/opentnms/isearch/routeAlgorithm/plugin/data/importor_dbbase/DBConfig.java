package com.zznode.opentnms.isearch.routeAlgorithm.plugin.data.importor_dbbase;

import java.util.Map;

import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Direction;

public class DBConfig {

	 private String sql ; 
	 private String aendnode ; 
	 private String aendeage ;
	 private String zendnode ; 
	 private String zendeage ;
	 private String direction ;
	 private Map<String , Direction> directionMap ;
	 
	 
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getAendnode() {
		return aendnode;
	}
	public void setAendnode(String aendnode) {
		this.aendnode = aendnode;
	}
	public String getAendeage() {
		return aendeage;
	}
	public void setAendeage(String aendeage) {
		this.aendeage = aendeage;
	}
	public String getZendnode() {
		return zendnode;
	}
	public void setZendnode(String zendnode) {
		this.zendnode = zendnode;
	}
	public String getZendeage() {
		return zendeage;
	}
	public void setZendeage(String zendeage) {
		this.zendeage = zendeage;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public Map<String, Direction> getDirectionMap() {
		return directionMap;
	}
	public void setDirectionMap(Map<String, Direction> directionMap) {
		this.directionMap = directionMap;
	} 
	 
	 

}
