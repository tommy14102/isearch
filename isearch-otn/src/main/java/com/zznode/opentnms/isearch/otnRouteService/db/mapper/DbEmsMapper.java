package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.DbEms;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbTsn;

public class DbEmsMapper implements RowMapper<DbEms> {

	public DbEms mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		DbEms ems = new DbEms();
		ems.setObjectId( resultset.getString("ObjectId"));             
        ems.setEmsName( resultset.getString("EmsName"));                
        ems.setEmsType( resultset.getString("EmsType"));                
        ems.setManagementDomain( resultset.getString("ManagementDomain"));       
        ems.setVendor( resultset.getString("Vendor"));                 
        ems.setUserLabel( resultset.getString("UserLabel"));              
        ems.setEmsId( resultset.getString("EmsId"));
        ems.setEmsModel( resultset.getString("EmsModel"));
		
		return ems ; 
		
	}

}
