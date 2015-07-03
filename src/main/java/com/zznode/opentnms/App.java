package com.zznode.opentnms;

import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationFault;
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
    	RouteCalculationInput routeCalculationInput = new RouteCalculationInput();
    	routeCalculationInput.setAendme("UUID:11d80a82-6c54-11e2-9812-e006e6ca1f18");
    	routeCalculationInput.setZendme("UUID:76cd8702-01ca-11e4-bd57-9439e54d13ef");
    	routeCalculationInput.setRate(77);
    	
    	RouteCalculationServiceLocator rl = new RouteCalculationServiceLocator();
    	
    	try{
    	RouteCalculationOutput s = rl.getRouteCalculationPort().calculate(routeCalculationInput);
    	System.out.println(s);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
}
