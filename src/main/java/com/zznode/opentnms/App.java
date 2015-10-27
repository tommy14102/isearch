package com.zznode.opentnms;

import com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInput;
import com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpOutput;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationInput;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationOutput;
import com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationServiceLocator;
import com.zznode.opentnms.isearch.otnRouteService.api.impl.CardUsedPtplistCalculationImplServiceLocator;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	
    	CardUsedPtpInput input = new CardUsedPtpInput();
    	input.setCardobjectid("UUID:079ecf30-feed-11e4-8259-005056844447");
    	
    	CardUsedPtplistCalculationImplServiceLocator r = new CardUsedPtplistCalculationImplServiceLocator();
    	CardUsedPtpOutput output = r.getCardUsedPtplistCalculationImplPort().cardUsedPtp(input);
    	System.out.println(output.getUnusedPtplist()[0].getPortname());
    	
    }
    
}
