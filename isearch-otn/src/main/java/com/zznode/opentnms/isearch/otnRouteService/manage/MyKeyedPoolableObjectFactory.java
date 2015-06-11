package com.zznode.opentnms.isearch.otnRouteService.manage;

import org.apache.commons.pool.KeyedPoolableObjectFactory;

import com.zznode.opentnms.isearch.otnRouteService.service.BusiAnalyser;

public class MyKeyedPoolableObjectFactory implements KeyedPoolableObjectFactory {

	// 创建对象实例。同时可以分配这个对象适用的资源。
	public Object makeObject(Object key) throws Exception {
		
		return new BusiAnalyser((String)key);
	}

	public void activateObject(Object arg0, Object arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void destroyObject(Object arg0, Object arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void passivateObject(Object arg0, Object arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public boolean validateObject(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}


}
