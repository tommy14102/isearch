package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSnc;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSncRoute;

public class DbWdmSncRouteMapper implements RowMapper<DbWdmSncRoute> {

	
	public DbWdmSncRoute mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		DbWdmSncRoute wdmSncRoute = new DbWdmSncRoute();
		
		wdmSncRoute.m_ObjectId = resultset.getString( "ObjectId");
	    wdmSncRoute.m_SncObjectId = resultset.getString( "SncObjectId");
	    wdmSncRoute.m_EmsObjectId = resultset.getString( "EmsObjectId");
	    wdmSncRoute.m_RouteSeq = resultset.getInt( "RouteSeq");
	    wdmSncRoute.m_WaySeq = resultset.getInt( "WaySeq");
	    wdmSncRoute.m_AEndMeObjectId = resultset.getString( "AEndMeObjectId");
	    wdmSncRoute.m_AEndPtpObjectId = resultset.getString( "AEndPtpObjectId");
	    wdmSncRoute.m_AEndCtpId = resultset.getString( "AEndCtpId");
	    wdmSncRoute.m_ZEndMeObjectId = resultset.getString( "ZEndMeObjectId");
	    wdmSncRoute.m_ZEndPtpObjectId = resultset.getString( "ZEndPtpObjectId");
	    wdmSncRoute.m_ZEndCtpId = resultset.getString( "ZEndCtpId");
	    wdmSncRoute.m_LinkType = resultset.getInt( "LinkType");
	    wdmSncRoute.m_Direction = resultset.getInt( "Direction");
	    wdmSncRoute.m_ProtectionRole = resultset.getInt( "ProtectionRole");
        
		return wdmSncRoute  ; 
		
	}

}
