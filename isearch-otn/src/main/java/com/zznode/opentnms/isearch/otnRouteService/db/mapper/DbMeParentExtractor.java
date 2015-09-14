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

public class DbMeParentExtractor implements ResultSetExtractor<Map<String,String> > {


	public Map<String,String>   extractData(ResultSet rs) throws SQLException,	DataAccessException {

		Map<String,String> meParentMap = new HashMap<String,String>();
		while(rs.next())
		{
			String meobjectid = rs.getString( "objectId") ; 
			String parentmeid = rs.getString( "parentmeobjectid") ; 
			if( parentmeid==null || parentmeid.length()==0 ){
				meParentMap.put( meobjectid , meobjectid );
			}
			else{
				meParentMap.put(meobjectid, parentmeid);
			}
			
			
		}
		return meParentMap; 
		
	}

}
