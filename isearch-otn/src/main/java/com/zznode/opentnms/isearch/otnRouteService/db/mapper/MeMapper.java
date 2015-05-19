package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.TsnMe;

public class MeMapper implements RowMapper<TsnMe> {

	public TsnMe mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		TsnMe me = new TsnMe();
		me.setTsnid( resultset.getString("tsnobjectid") );
		me.setId( resultset.getString("meobjectid") );
		me.setZhandianid(  resultset.getString("juzhanobjectid") );
		
		return me  ; 
		
	}

}
