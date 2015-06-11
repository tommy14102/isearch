package com.zznode.opentnms.isearch.otnRouteService.manage;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;


public class ObjectPool {

	private GenericKeyedObjectPool pool ;
	
	public ObjectPool(){
		pool = new GenericKeyedObjectPool(new MyKeyedPoolableObjectFactory());
        pool.setMaxActive(50);
        pool.setMaxIdle(20);
        pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        pool.setMaxWait(Long.MAX_VALUE);
	}

	public GenericKeyedObjectPool getPool() {
		return pool;
	}

	public void setPool(GenericKeyedObjectPool pool) {
		this.pool = pool;
	}
	
	
	

}
