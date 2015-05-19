package com.zznode.opentnms.isearch.routeAlgorithm.core.cache;

import java.util.List;

public class MemcachedInfoWrapper{

	private String currentVersion;
	
	private String usabledVersion;

	private List<MemcachedInfo> memcachedList;
	
	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public List<MemcachedInfo> getMemcachedList() {
		return memcachedList;
	}

	public void setMemcachedList(List<MemcachedInfo> memcachedList) {
		this.memcachedList = memcachedList;
	}

	public String getUsabledVersion() {
		return usabledVersion;
	}

	public void setUsabledVersion(String usabledVersion) {
		this.usabledVersion = usabledVersion;
	}
}
