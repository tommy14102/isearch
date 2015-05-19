package com.zznode.opentnms;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationInput;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationOutput;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationServiceLocator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	 
        System.out.println( "Hello World!" );
        String endpoint_1="http://localhost:8088/isearch-otnRouteService/services/RouteCalculation";
        Service service = new Service();
        Call call=(Call)service.createCall();
        call.setOperationName(new QName(endpoint_1,"calculate"));
        call.setTargetEndpointAddress(new URL(endpoint_1));
        String result=(String)call.invoke(new Object[]{});
        System.out.println("result="+result); 
        
    	RouteCalculationInput routeCalculationInput = new RouteCalculationInput();
    	routeCalculationInput.setAendme("UUID:11d80a82-6c54-11e2-9812-e006e6ca1f18");
    	routeCalculationInput.setZendme("UUID:76cd8702-01ca-11e4-bd57-9439e54d13ef");
    	routeCalculationInput.setRate(77);
    	
    	RouteCalculationServiceLocator rl = new RouteCalculationServiceLocator();
    	RouteCalculationOutput s = rl.getRouteCalculationPort().calculate(routeCalculationInput);
    	System.out.println(s);
    }
}
