package com.zznode.opentnms.isearch.otnRouteService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zznode.opentnms.isearch.otnRouteService.service.BusiAnalyser;
import com.zznode.opentnms.isearch.otnRouteService.service.JettyService;
import com.zznode.opentnms.isearch.otnRouteService.service.RouteAnalyser;
import com.zznode.opentnms.isearch.otnRouteService.util.PropertiesHander;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);   
	
	public static ApplicationContext factory ; 
	public static RouteAnalyser routeAnalyser ;
	public static BusiAnalyser busiAnalyser ;
	public static JettyService jettyService ;
	
	static{
		factory = new ClassPathXmlApplicationContext("application-ors.xml","application-ors-memcached.xml","application-ors-jdbc.xml");
		routeAnalyser =  (RouteAnalyser)factory.getBean("routeAnalyser");
		busiAnalyser =  (BusiAnalyser)factory.getBean("busiAnalyser");
		jettyService = (JettyService)factory.getBean("jettyService");
	}
	
	public static void start() throws Exception{
		//1.根据配置重新加载所有topo资源
		if(PropertiesHander.getProperty("needRefreshTopo").equals("true")){
			routeAnalyser.analyseAllRoute();
			//PropertiesHander.setProperty("needRefreshTopo", "false");
		}
		
		//2.根据配置重新加载所有业务资源
		if(PropertiesHander.getProperty("needRefreshBusi").equals("true")){
			//busiAnalyser.analyseAllBusi();
			//PropertiesHander.setProperty("needRefreshBusi", "false");
		}
		
		//3.开放webservice服务
		jettyService.runJetty();
		
	}
	
	public static void main(String[] args) {
		
		try {
			logger.info("启动程序");
			Main.start();
		} catch (Exception e) {
			logger.error("启动异常", e);
		}
		
	}
}
