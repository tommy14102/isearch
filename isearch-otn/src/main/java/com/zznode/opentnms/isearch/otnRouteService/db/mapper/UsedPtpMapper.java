package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.UsedPtp;

public class UsedPtpMapper implements RowMapper<UsedPtp> {

	
	public UsedPtp mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		
		UsedPtp uUsedPtp = new UsedPtp();

		uUsedPtp.ptpobjectid  = resultset.getString( "ptpobjectid" );
		uUsedPtp.ptpname  = resultset.getString( "ptpname" );
		uUsedPtp.awdmtrailid  = resultset.getString( "awdmtrailid" );
		uUsedPtp.zwdmtrailid  = resultset.getString( "zwdmtrailid" );
		
		return uUsedPtp  ; 
		
	}

}
