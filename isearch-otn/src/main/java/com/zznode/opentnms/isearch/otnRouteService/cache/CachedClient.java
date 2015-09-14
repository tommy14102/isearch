package com.zznode.opentnms.isearch.otnRouteService.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.spy.memcached.MemcachedClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;


@Component
public class CachedClient {

	private static final String CATEGORY_CURRENT_VERSION = "categoryCurrentVersion"; // 当前版本key

	private static final Integer VERSION_CACHED_TIME = 60 * 60 * 24 ;// 当前版本缓存时间1天

	private static final String VERSION_SUFFIX = "_V_"; // 带版本号的key标识

    @Autowired
    @Qualifier("primaryMemcachedClient")
	private MemcachedClient primaryMemcachedClient;

    @Autowired
    @Qualifier("backupMemcachedClient")
	private MemcachedClient backupMemcachedClient;
	
	private boolean isPrimaryClient = true; // 标识当前是否使用主缓存
	
	private Integer failCount = 0; // 记录某个缓存失败次数
	
	private static final Integer switchFailCount = 3;
	
	public CachedClient(){}
	
	public void testSet() throws InterruptedException, ExecutionException{
		final String key = "TOPOLOGY" + "_" + "1";
		Link topo = new Link();
		topo.setId("1");
		Future<Boolean> fu = primaryMemcachedClient.set(key , 0, "sdf");
		while( !fu.isDone()){
			
		}
		System.out.println(fu.get());
		System.out.println(primaryMemcachedClient.get(key));
	}
	
	public void testGet(){
		final String key = "TOPOLOGY" + "_" + "1";
		Object obj = primaryMemcachedClient.get(key);
		System.out.println(obj);
	}
	
	

	/**
	 * 缓存中取值，默认使用当前缓存服务器，如果异常或取值为空，到另一台去取
	 * 默认取最新版本，取不到倒退前一版本
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		
		return primaryMemcachedClient.get(key);
		
		/**
		Object cacheObj = null;
		try {
			cacheObj = get(getCurrentMemcachedClient(), key);
		} catch (Exception e) {
			failCount++;
		}
		if(cacheObj == null){
			try {
				if(isPrimaryClient){
					cacheObj = get(backupMemcachedClient, key);
				} else {
					cacheObj = get(primaryMemcachedClient, key);
				}
			} catch (Exception e) {
			}
		}
		return cacheObj;
		*/
		
	}
	
	/**
	 * 缓存中取值，默认使用当前缓存服务器，如果异常或取值为空，到另一台去取
	 * 获取指定version值
	 * @param key
	 * @return
	 */
	public Object get(String key, Integer version) {
		Object cacheObj = null;
		try {
			cacheObj = get(getCurrentMemcachedClient(), key);
		} catch (Exception e) {
			failCount++;
		}
		if(cacheObj == null){
			try {
				if(isPrimaryClient){
					cacheObj = get(backupMemcachedClient, key, version);
				} else {
					cacheObj = get(primaryMemcachedClient, key, version);
				}
			} catch (Exception e) {
			}
		}
		return cacheObj;
	}

	/**
	 * 缓存中取值，使用当前版本缓存，如果取不到则倒推版本取值
	 * @param memcachedClient
	 * @param key
	 * @return
	 */
	private Object get(MemcachedClient memcachedClient, String key) {
		Integer version = (Integer) memcachedClient.get(CATEGORY_CURRENT_VERSION);
		if (version == null) {
			version = 1;
		}
		Object cacheObj = get(memcachedClient, key, version);
		int count = 0; // 重试另外两个版本数据
		while (count < 2 && cacheObj == null) {
			count++;
			if (version == 1) {
				version = 3;
			} else {
				version--;
			}
			cacheObj = get(memcachedClient, key, version);
		}
		return cacheObj;
	}

	/**
	 * 缓存中指定版本取值
	 * @param memcachedClient
	 * @param key
	 * @param version
	 * @return
	 */
	private Object get(MemcachedClient memcachedClient, String key, Integer version) {
		if(version == null){
			version = 1;
		}
		return memcachedClient.get(key + VERSION_SUFFIX + version);
	}

	/**
	 * 设置缓存内容，使用下一个版本号做key
	 * @param key
	 * @param exp
	 * @param o
	 * @return
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws BaseServiceException 
	 */
	public void set(String key, int exp, Object o) throws InterruptedException, ExecutionException {
		
		 Future<Boolean> rtn = primaryMemcachedClient.set(key , exp, o);
		 while(!rtn.isDone()){
			 
		 }
		 System.out.println("set result:" + rtn.get());
	}
	
	public void setByVersion(String key, int exp, Object o) {
		Integer currentVersion = (Integer) primaryMemcachedClient.get(CATEGORY_CURRENT_VERSION);
		primaryMemcachedClient.set(key + VERSION_SUFFIX + getNextVersion(currentVersion), exp, o);
		currentVersion = (Integer) backupMemcachedClient.get(CATEGORY_CURRENT_VERSION);
		backupMemcachedClient.set(key + VERSION_SUFFIX + getNextVersion(currentVersion), exp, o);
	}
	
	/**
	 * 更新缓存当前版本，主辅缓存各自+1
	 * @return
	 * @throws BaseServiceException 
	 */
	public void updateVersion() {
		Integer currentVersion = (Integer) primaryMemcachedClient.get(CATEGORY_CURRENT_VERSION);
		primaryMemcachedClient.set(CATEGORY_CURRENT_VERSION, VERSION_CACHED_TIME, getNextVersion(currentVersion));
		currentVersion = (Integer) backupMemcachedClient.get(CATEGORY_CURRENT_VERSION);
		backupMemcachedClient.set(CATEGORY_CURRENT_VERSION, VERSION_CACHED_TIME, getNextVersion(currentVersion));
	}

	/**
	 * 获取当前使用缓存
	 * @return
	 */
	private MemcachedClient getCurrentMemcachedClient(){
		if(failCount < switchFailCount){// 失败次数小于3，还是使用主缓存
			if(isPrimaryClient){// 继续使用主缓存
				return primaryMemcachedClient;
			} else {// 当前使用辅缓存
				return backupMemcachedClient;
			}
		} else { // 失败次数大于3，强制切换
			if(isPrimaryClient){
				isPrimaryClient = false;
				failCount = 0;
				return backupMemcachedClient;
			} else {
				isPrimaryClient = true;
				failCount = 0;
				return primaryMemcachedClient;
			}
		}
	}

	/**
	 * 获取当前版本号，给外层获取使用
	 * @param currentVersion
	 * @return
	 */
	public Integer getCurrentVersion() {
		Integer currentVersion = null;
		try {
			MemcachedClient client = getCurrentMemcachedClient();
			currentVersion = (Integer) client.get(CATEGORY_CURRENT_VERSION);
		} catch (Exception e) {
		}
		return currentVersion;
	}
	
	/**
	 * 获取前一个版本号
	 * @param currentVersion
	 * @return
	 */
	public Integer getPreviousVersion(Integer currentVersion) {
		if (currentVersion == null || currentVersion == 1) {
			return 3;
		} else {
			return currentVersion - 1;
		}
	}
	
	/**
	 * 获取下一个版本号
	 * @param currentVersion
	 * @return
	 */
	public Integer getNextVersion(Integer currentVersion) {
		if (currentVersion == null || currentVersion == 3) {
			return 1;
		} else {
			return currentVersion + 1;
		}
	}

	/**
	 * 更新某个缓存的当前版本号
	 * @param primary
	 * @param version
	 * @return
	 */
	public void updateVersion(boolean primary, Integer version) {
		if(version == 1 || version == 2 || version == 3){
			if(primary){
				primaryMemcachedClient.set(CATEGORY_CURRENT_VERSION, VERSION_CACHED_TIME, version);
			} else {
				backupMemcachedClient.set(CATEGORY_CURRENT_VERSION, VERSION_CACHED_TIME, version);
			}
		}
	}

	/**
	 * 切换当前使用缓存
	 * @param primary
	 * @return
	 */
	public void switchMemcachedServer(boolean primary) {
		isPrimaryClient = primary;
		failCount = 0;
	}


	public MemcachedClient getPrimaryMemcachedClient() {
		return primaryMemcachedClient;
	}

	public void setPrimaryMemcachedClient(MemcachedClient primaryMemcachedClient) {
		this.primaryMemcachedClient = primaryMemcachedClient;
	}

	public MemcachedClient getBackupMemcachedClient() {
		return backupMemcachedClient;
	}

	public void setBackupMemcachedClient(MemcachedClient backupMemcachedClient) {
		this.backupMemcachedClient = backupMemcachedClient;
	}
	
	
}

