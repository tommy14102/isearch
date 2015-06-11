package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSncAll;
import com.zznode.opentnms.isearch.otnRouteService.db.po.WdmSncRoute;

public class DbWdmSncAllExtractor implements ResultSetExtractor<Collection<DbWdmSncAll> > {


	public Collection<DbWdmSncAll>  extractData(ResultSet rs) throws SQLException,	DataAccessException {

		Map<String , DbWdmSncAll> sncMap = new HashMap<String , DbWdmSncAll>();
		int i = 1 ; 
		while(rs.next())
		{
			System.out.println("处理第"+i++ +"条路由数据");
			DbWdmSncAll wdmSnc = new DbWdmSncAll();
			wdmSnc.objectId = rs.getString("sncobjectid");   
			
			WdmSncRoute wdmSncRoute = new WdmSncRoute();
			wdmSncRoute.m_ObjectId = rs.getString( "ObjectId");
		    wdmSncRoute.m_SncObjectId = rs.getString( "SncObjectId");
		    wdmSncRoute.m_EmsObjectId = rs.getString( "EmsObjectId");
		    wdmSncRoute.m_RouteSeq = rs.getInt( "RouteSeq");
		    wdmSncRoute.m_WaySeq = rs.getInt( "WaySeq");
		    wdmSncRoute.m_AEndMeObjectId = rs.getString( "AEndMeObjectId");
		    wdmSncRoute.m_AEndPtpObjectId = rs.getString( "AEndPtpObjectId");
		    wdmSncRoute.m_AEndCtpId = rs.getString( "AEndCtpId");
		    wdmSncRoute.m_ZEndMeObjectId = rs.getString( "ZEndMeObjectId");
		    wdmSncRoute.m_ZEndPtpObjectId = rs.getString( "ZEndPtpObjectId");
		    wdmSncRoute.m_ZEndCtpId = rs.getString( "ZEndCtpId");
		    wdmSncRoute.m_LinkType = rs.getInt( "LinkType");
		    wdmSncRoute.m_Direction = rs.getInt( "Direction");
		    wdmSncRoute.m_ProtectionRole = rs.getInt( "ProtectionRole");
		    wdmSncRoute.acardmodel = rs.getString( "acardmodel");
		    wdmSncRoute.zcardmodel = rs.getString( "zcardmodel");
			
			if( sncMap.containsKey(wdmSnc.objectId)){
				
				wdmSnc = sncMap.get(wdmSnc.objectId);
			}
			else{
		        wdmSnc.direction =rs.getInt   ("sncdirection");      
		        wdmSnc.sncName =rs.getString("sncname");        
		        wdmSnc.rate =rs.getInt   ("sncrate");  
		        wdmSnc.setAendjz( rs.getString("ajuzhan") );
		        wdmSnc.setZendjz( rs.getString("zjuzhan") );
		        sncMap.put(wdmSnc.objectId, wdmSnc);
			}
		        
			if( wdmSncRoute.m_Direction.intValue() == 0 ){
			  	wdmSnc.getWdmsncroutelist().add(wdmSncRoute);
			}
			else if( wdmSncRoute.m_Direction.intValue() == 1 ){
			   	wdmSnc.getWdmsncrouteReverselist().add(wdmSncRoute);
			}
			
		}
		return sncMap.values(); 
		
	}

}
