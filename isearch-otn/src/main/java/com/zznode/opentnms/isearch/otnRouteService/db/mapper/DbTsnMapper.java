package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.DbTsn;

public class DbTsnMapper implements RowMapper<DbTsn> {

	public DbTsn mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		DbTsn tsn = new DbTsn();
		tsn.setObjectId(resultset.getString("objectid"));
		tsn.setEmsObjectId( resultset.getString("emsObjectId"));
		tsn.setUserLabel( resultset.getString("userLabel"));
		tsn.setParentObjectId( resultset.getString("parentObjectId"));
		tsn.setCreateUserName( resultset.getString("createUserName"));
		tsn.setxOffSet( resultset.getInt("xOffSet"));
		tsn.setyOffSet( resultset.getInt("yOffSet"));
		
		return tsn  ; 
		
	}

}
