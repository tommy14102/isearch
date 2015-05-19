package com.zznode.opentnms.isearch.otnRouteService.manage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.zznode.opentnms.isearch.otnRouteService.db.DBUtil;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbTsnMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.MeMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbTsn;
import com.zznode.opentnms.isearch.otnRouteService.db.po.TsnMe;

@Component
public class SubnetManager {

	@Autowired
	public DBUtil dbUtil;
	
	public List<DbTsn> getTsnByEms(String emsid){
		
		//1.查询ems的tsn信息。
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM transformsubnetwork WHERE 1=1 and EMSOBJECTID = '").append(emsid).append("' ");
		List<DbTsn> tsnlist = dbUtil.query(sb.toString(), new DbTsnMapper());
				
		return tsnlist;
				
	}
	
	public List<TsnMe> getTsnMeByEms(String tsnid){
		
		//1.查询tsn的me信息。
		List<TsnMe> allmelist = new ArrayList<TsnMe>();
		StringBuilder sqltsnlink = new StringBuilder();
		sqltsnlink.append("SELECT t.tsnobjectid, t.meobjectid, me.juzhanobjectid FROM transformsubnetworklink t , me where t.tsnobjectid= '").append(tsnid).append("' ");
		sqltsnlink.append("and t.meobjectid = me.objectid   ");
		List<TsnMe> mes = dbUtil.query(sqltsnlink.toString(), new MeMapper());
		allmelist.addAll(mes);
		
		return allmelist;
				
	}
}
