package com.zznode.opentnms.isearch.otnRouteService.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.otnRouteService.db.DBUtil;

@Component
public class CucirtManager {

	@Autowired
	public DBUtil dbUtil;
	
	public boolean hasCucirtBySncid(String sncid){
		
		String  sql = " select count(objectid) from circuit where objectid in ( select circuitobjectid from circuitroute where relatedrouteobjectid = '"+sncid +"' ) ";
		
		String cucirtid = dbUtil.getJdbcTemplate().queryForObject(sql, String.class);
				
		return !cucirtid.equals("0");
				
	}
}
