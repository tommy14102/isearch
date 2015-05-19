package com.zznode.opentnms.isearch.routeAlgorithm.core.cache;

import java.util.concurrent.ExecutionException;

import net.spy.memcached.spring.MemcachedClientFactoryBean;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application.xml","classpath:application-memcached.xml"})
public class Tester {

	private static Logger log = Logger.getLogger(Tester.class);
	
	@Autowired
	@Qualifier("SPtnMemcachedClient")
	private   SPtnMemcachedClient testobj;
	
	@Autowired
	@Qualifier("primaryMemcachedClient")
	private   MemcachedClientFactoryBean primaryMemcachedClient;
	
	 
	
	@Test
	public void testSet() throws InterruptedException, ExecutionException{
		testobj.testSet();
	}
	
	public void testGet(){
		testobj.testGet();
	}

}
