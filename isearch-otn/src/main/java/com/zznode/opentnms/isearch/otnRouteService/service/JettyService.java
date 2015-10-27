package com.zznode.opentnms.isearch.otnRouteService.service;

import javax.xml.ws.Endpoint;

import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.otnRouteService.Main;
import com.zznode.opentnms.isearch.otnRouteService.util.PropertiesHander;

@Component
public class JettyService {

	public void runJetty() {
		
		//Endpoint.publish( "http://localhost:8088/isearch-otnRouteService/services/RouteCalculation",  Main.factory.getBean("routeCalculation"));   
		Endpoint.publish(PropertiesHander.getProperty("webserviceURI"), Main.factory.getBean("routeCalculationImpl"));   
		Endpoint.publish(PropertiesHander.getProperty("sncFreeOduServiceURI"), Main.factory.getBean("sncFreeOduCalculationImpl"));   
		Endpoint.publish(PropertiesHander.getProperty("cardUsedPtplistURI"), Main.factory.getBean("cardUsedPtplistCalculationImpl")); 
		/**
	    Server server = new Server();
	    SelectChannelConnector connector = new SelectChannelConnector();
	    //connector.setMaxIdleTime(30000);
	    connector.setPort(8088);//jetty的端口
	    connector.setHost("127.0.0.1");
	    server.addConnector(connector);
	    
	    
	    WebAppContext context = new WebAppContext("D:\\workspaceNew\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\isearch-otnRouteService","/isearch-otnRouteService");
	    server.setHandler(context);
	    server.setStopAtShutdown(true);
	    server.setSendServerVersion(true);
	    
	    */
		
		
	    
/**
	    ServletHolder axisServletholder = new ServletHolder(new AxisServlet());
	    ServletHolder axisAdminServletholder = new ServletHolder(new AdminServlet());
	    WebAppContext  root = new WebAppContext("isearch-otnRouteService","./WebContent");
	    //root.setContextPath("./WebContent/WEB-INF");//WEB资源目录，./web/WEB-INF/server-config.wsdd
	    //root.setDefaultsDescriptor("./WebContent/WEB-INF/web.xml");
	    //root.setResourceBase("./WebContent/");
	    root.setResourceBase("D:\\workspaceNew\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\isearch-otnRouteService");
	    System.out.println(root.getBaseResource().getFile());
	    root.addServlet(axisServletholder, "/servlet/AxisServlet");
	    root.addServlet(axisServletholder, "/services/*");//设置webservice的发布目录
	    root.addServlet(axisServletholder, "*.jws");
	    root.addServlet(axisAdminServletholder, "/servlet/AdminServlet");

	    try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	    
	}
	
	public static void main(String[] args) {
		new JettyService().runJetty();
	}
}
