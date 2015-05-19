package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSnc;

public class DbWdmSncMapper implements RowMapper<DbWdmSnc> {

	public DbWdmSnc mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		DbWdmSnc wdmSnc = new DbWdmSnc();
		
		wdmSnc.objectId          =resultset.getString("ObjectId");       
        wdmSnc.emsObjectId       =resultset.getString("EmsObjectId");    
        wdmSnc.direction         =resultset.getInt   ("Direction");      
        wdmSnc.sncName           =resultset.getString("SncName");        
        wdmSnc.sncId             =resultset.getString("SncId");          
        wdmSnc.rate              =resultset.getInt   ("Rate");          
        wdmSnc.aEndMeObjectId    =resultset.getString("AEndMeObjectId"); 
        wdmSnc.aEndPtpObjectId   =resultset.getString("AEndPtpObjectId");
        wdmSnc.aEndCtpId         =resultset.getString("AEndCtpId");      
        wdmSnc.zEndMeObjectId    =resultset.getString("ZEndMeObjectId"); 
        wdmSnc.zEndPtpObjectId   =resultset.getString("ZEndPtpObjectId");
        wdmSnc.zEndCtpId         =resultset.getString("ZEndCtpId");      
        wdmSnc.aEndFrequency     =resultset.getString("AEndFrequency");  
        wdmSnc.zEndFrequency     =resultset.getString("ZEndFrequency");  
        wdmSnc.aEndClientRate    =resultset.getString("AEndClientRate"); 
        wdmSnc.zEndClientRate    =resultset.getString("ZEndClientRate"); 
        wdmSnc.aEndClientType    =resultset.getString("AEndClientType"); 
        wdmSnc.zEndClientType    =resultset.getString("ZEndClientType");
        
        wdmSnc.activeState       =resultset.getInt("ActiveState");
        wdmSnc.serviceState      =resultset.getInt("ServiceState");
        wdmSnc.rateDesc          =resultset.getString("RateDesc");
        wdmSnc.sncType           =resultset.getString("SncType");
        wdmSnc.a2EndMeObjectId   =resultset.getString("A2EndMeObjectId");
        wdmSnc.a2EndPtpObjectId  =resultset.getString("A2EndPtpObjectId");
        wdmSnc.a2EndCtpId        =resultset.getString("A2EndCtpId");
        wdmSnc.z2EndMeObjectId   =resultset.getString("Z2EndMeObjectId");
        wdmSnc.z2EndPtpObjectId  =resultset.getString("Z2EndPtpObjectId");
        wdmSnc.z2EndCtpId        =resultset.getString("Z2EndCtpId");
        wdmSnc.waveLength        =resultset.getString("WaveLength");
        wdmSnc.lambda            =resultset.getString("Lambda");
        wdmSnc.reverseObjectId   =resultset.getString("ReverseObjectId");
        wdmSnc.routeInfo         =resultset.getString("RouteInfo");
        wdmSnc.reverseRouteInfo  =resultset.getString("ReverseRouteInfo");
        wdmSnc.aEndLayer         =resultset.getString("AEndLayer");
        wdmSnc.zEndLayer         =resultset.getString("ZEndLayer");
        wdmSnc.a2EndLayer        =resultset.getString("A2EndLayer");
        wdmSnc.z2EndLayer        =resultset.getString("Z2EndLayer");
        wdmSnc.ratedesc          =resultset.getString("cmRateDesc");
        
		return wdmSnc  ; 
		
	}

}
