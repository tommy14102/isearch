package com.zznode.opentnms.isearch.routeAlgorithm.core.cache;


public class MemcachedInfo {

	private String address;

	private String bytes;

	private String version;

	private String time;

	private String threads;

	private String limitMaxBytes;

	private String bytesRead;

	private String bytesWritten;

	private String currConnections;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBytes() {
		return bytes;
	}

	public void setBytes(String bytes) {
		this.bytes = bytes;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getThreads() {
		return threads;
	}

	public void setThreads(String threads) {
		this.threads = threads;
	}

	public String getLimitMaxBytes() {
		return limitMaxBytes;
	}

	public void setLimitMaxBytes(String limitMaxBytes) {
		this.limitMaxBytes = limitMaxBytes;
	}

	public String getBytesRead() {
		return bytesRead;
	}

	public void setBytesRead(String bytesRead) {
		this.bytesRead = bytesRead;
	}

	public String getBytesWritten() {
		return bytesWritten;
	}

	public void setBytesWritten(String bytesWritten) {
		this.bytesWritten = bytesWritten;
	}

	public String getCurrConnections() {
		return currConnections;
	}

	public void setCurrConnections(String currConnections) {
		this.currConnections = currConnections;
	}
}
