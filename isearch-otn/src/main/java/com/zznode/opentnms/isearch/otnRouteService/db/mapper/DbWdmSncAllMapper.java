package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSnc;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSncAll;

public class DbWdmSncAllMapper implements RowMapper<DbWdmSncAll> {

	public DbWdmSncAll mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		DbWdmSncAll wdmSnc = new DbWdmSncAll();
		
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
        
        wdmSnc.activeState       =resultset.getInt("ActiveState");
        wdmSnc.serviceState      =resultset.getInt("ServiceState");
        wdmSnc.rateDesc          =resultset.getString("RateDesc");
        wdmSnc.sncType           =resultset.getString("SncType");
        wdmSnc.ratedesc          =resultset.getString("cmRateDesc");
        
		return wdmSnc  ; 
		
	}

}
