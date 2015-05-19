package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.DbTsn;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbTsnLink;

public class DbTsnLinkMapper implements RowMapper<DbTsnLink> {

	public DbTsnLink mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		DbTsnLink tsnlink = new DbTsnLink();
		tsnlink.setM_ObjectId( resultset.getString("objectid"));
		tsnlink.setM_MeObjectId( resultset.getString("meObjectId"));
		tsnlink.setM_TsnObjectId( resultset.getString("tsnObjectId"));
		
		return tsnlink  ; 
		
	}

}
