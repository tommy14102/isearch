package com.zznode.opentnms.isearch.otnRouteService.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSncAll;
import com.zznode.opentnms.isearch.otnRouteService.db.po.WdmSncRoute;

public class DbWdmOchZdExtractor implements ResultSetExtractor<Map<String,List<DbWdmSncAll>> > {


	public Map<String,List<DbWdmSncAll>>  extractData(ResultSet rs) throws SQLException,	DataAccessException {

		Map<String,List<DbWdmSncAll>> ochData = new HashMap<String,List<DbWdmSncAll>>();
		
		int i = 1 ; 
		while(rs.next())
		{
			
			DbWdmSncAll wdmSnc = new DbWdmSncAll();
			wdmSnc.objectId = rs.getString( "objectId");
			wdmSnc.direction =rs.getInt("direction");      
	        wdmSnc.sncName =rs.getString("sncname");        
	        wdmSnc.rate =rs.getInt   ("rate");  
	        String headjz = rs.getString("juzhan")  ;
	        
	        System.out.println("处理第"+i++ +"条snc数据:" + wdmSnc.objectId);
	        
	        String key = headjz ;
	        
	        if( ochData.containsKey(key) ){
	        	ochData.get(key).add(wdmSnc);
			}
			else{
				List<DbWdmSncAll> snclist = new ArrayList<DbWdmSncAll>();
				snclist.add(wdmSnc);
				ochData.put(key, snclist);
			}
			
		}
		return ochData; 
		
	}

}
