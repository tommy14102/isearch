package com.zznode.opentnms.isearch.routeAlgorithm;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Hello world!
 *
 */
public class App 
{

	public static ApplicationContext factory ; 
	
	static{
		
		factory = new ClassPathXmlApplicationContext("applicationR.xml","application-memcached.xml","application-jdbc.xml");
		
	}

	
}
