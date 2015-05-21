package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.DbMe;

public class DbMeMapper implements RowMapper<DbMe> {

	public DbMe mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		DbMe me = new DbMe();
		me.setObjectId( resultset.getString("ObjectId"));             
		me.setMeId( resultset.getString("MeId"));                
		me.setModel(  resultset.getString("Model"));       
		me.setVendor( resultset.getString("Vendor"));                 
		me.setManagementDomain(  resultset.getString("ManagementDomain"));              
		me.setJuzhanobjectid( resultset.getString("Juzhanobjectid"));
		me.setJifangobjectid(  resultset.getString("Jifangobjectid"));
		
		return me ; 
		
	}

}
