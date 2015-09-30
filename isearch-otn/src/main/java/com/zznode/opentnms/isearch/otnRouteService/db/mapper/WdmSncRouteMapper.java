package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSncRoute;
import com.zznode.opentnms.isearch.otnRouteService.db.po.WdmSncRoute;

public class WdmSncRouteMapper implements RowMapper<WdmSncRoute> {

	
	public WdmSncRoute mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		DbWdmSncRoute dbwdmSncRoute = new DbWdmSncRouteMapper().mapRow(resultset, arg1); 
		
		WdmSncRoute wdmSncRoute = new WdmSncRoute();

		//BeanCopier copier = BeanCopier.create(DbWdmSncRoute.class, WdmSncRoute.class, false); 
		//copier.copy(dbwdmSncRoute, wdmSncRoute, null);
		BeanUtils.copyProperties(dbwdmSncRoute, wdmSncRoute);
		
		wdmSncRoute.setAcardmodel( resultset.getString( "acardmodel"));
	    wdmSncRoute.setZcardmodel( resultset.getString( "zcardmodel"));
	    
	    wdmSncRoute.setAendjuzhan( resultset.getString("ajuzhan") );
	    wdmSncRoute.setZendjuzhan( resultset.getString("zjuzhan") );
		return wdmSncRoute  ; 
		
	}

}
