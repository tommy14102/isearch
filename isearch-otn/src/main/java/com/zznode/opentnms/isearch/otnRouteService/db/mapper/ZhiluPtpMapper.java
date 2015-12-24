package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.otnRouteService.db.po.ZhiluPtp;

public class ZhiluPtpMapper implements RowMapper<ZhiluPtp> {

	
	public ZhiluPtp mapRow(ResultSet resultset, int arg1) throws SQLException {
		
		
		ZhiluPtp zhiluPtp = new ZhiluPtp();

		zhiluPtp.ptpobjectid  = resultset.getString( "ptpobjectid");
		zhiluPtp.cardobjectid  = resultset.getString( "cardobjectid");
		zhiluPtp.cardmodel  = resultset.getString( "cardmodel");
		zhiluPtp.tptype = resultset.getString( "tptype");
		zhiluPtp.ptpid = resultset.getString( "ptpid");
		
		return zhiluPtp  ; 
		
	}

}
