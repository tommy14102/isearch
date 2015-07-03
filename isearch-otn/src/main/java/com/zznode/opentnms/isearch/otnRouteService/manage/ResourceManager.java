package com.zznode.opentnms.isearch.otnRouteService.manage;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.otnRouteService.db.DBUtil;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbEmsMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbMeMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbWdmSncAllExtractor;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbWdmSncMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbWdmSncZdExtractor;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.WdmSncRouteMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbEms;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbMe;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSnc;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSncAll;
import com.zznode.opentnms.isearch.otnRouteService.db.po.WdmSncRoute;
import com.zznode.opentnms.isearch.otnRouteService.service.BusiAnalyser;
import com.zznode.opentnms.isearch.otnRouteService.util.PropertiesHander;

@Component
public class ResourceManager {

	@Autowired
	public DBUtil dbUtil;
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);   
	
	public List<DbEms> getAllEms() throws SQLException{
		
		//1.查询ems的tsn信息。
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM ems WHERE 1=1 ");
		if(PropertiesHander.getProperty("testmode").equals("true")){
			sb.append(" and emsid = '").append("Huawei/NanJingJWZ2").append("' ");
		}
		List<DbEms> emslist = dbUtil.query(sb.toString(), new DbEmsMapper());
		
		return emslist;
				
	}
	
	public DbMe getMeById(String meobjectid){
		
		//1.查询ems的tsn信息。
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM me WHERE 1=1 and objectid = '").append(meobjectid).append("' ");
		//DbMe me = dbUtil.getJdbcTemplate().queryForObject(sb.toString(), new DbMeMapper());
		List<DbMe> melist = dbUtil.query(sb.toString(), new DbMeMapper());
		if( melist.size()==0 ){
			return null;
		}
		return melist.get(0);
				
	}
	
	public List<DbMe> getChildMeById(String meobjectid){
		
		//1.查询ems的tsn信息。
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM me WHERE 1=1 and parentmeobjectid = '").append(meobjectid).append("' ");
		//DbMe me = dbUtil.getJdbcTemplate().queryForObject(sb.toString(), new DbMeMapper());
		List<DbMe> melist = dbUtil.query(sb.toString(), new DbMeMapper());
		return melist;
				
	}

	
	/**
	 * 根据板卡类型和厂商获取板卡model
	 *
	 * @param cardTypes
	 * @param vendor
	 * @return
	 */
	public List<String>  getMCardModel() {
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select model from cardmodel where 1=1 and cardtype='合波盘' ");
		return dbUtil.getJdbcTemplate().queryForList( sqlBuf.toString(),String.class); 
	}
	
	/**
	 * 查询两个站点之间的所有波道。
	 * @return
	 */
	public List<DbWdmSnc> getWdmSncByAZZhandian(String aendZDid, String zendZDid, String direction){
		
	  	StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT wdmsnc.*, (select ratedesc from enum_vendorlayerrate where  vendor = ems.vendor and layerrate = wdmsnc.rate ) cmRateDesc ");
	  	sb.append(" FROM wdmsnc,ems WHERE wdmsnc.emsobjectid = ems.objectid  ");
	  	sb.append(" and wdmsnc.aendmeobjectid in ( select objectid from me  where juzhanobjectid  = '").append(aendZDid).append("' ) ");
	  	sb.append(" and wdmsnc.zendmeobjectid in ( select objectid from me  where juzhanobjectid  = '").append(zendZDid).append("' ) ");
	  	sb.append(" and wdmsnc.direction='").append(direction).append("'");
	  	
	  	//sb.append(" and wdmsnc.objectid in ('UUID:b5422ceb-210a-11e4-9365-005056862639') ");
	  	return dbUtil.query(sb.toString(), new DbWdmSncMapper());
	}
	
	/**
	 * 查询波道路由
	 * 
	 * @param sncid
	 * @param direction
	 * @return
	 */
	public List<WdmSncRoute> queryForRoute( String sncid , String direction){
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append("select wdmsncroute.*,");
		sb2.append("sncobjectid,routeseq,wayseq,aendctpid,zendctpid, ");
		sb2.append("(select model from card , ptp where card.objectid = ptp.cardobjectid and ptp.objectid=aendptpobjectid  ) acardmodel, ");
		sb2.append("(select model from card , ptp where card.objectid = ptp.cardobjectid and ptp.objectid=zendptpobjectid  ) zcardmodel, ");
		sb2.append("(select juzhanobjectid from me   where objectid = aendmeobjectid) ajuzhan, ");
		sb2.append("(select juzhanobjectid from me   where objectid = zendmeobjectid) zjuzhan, ");
		sb2.append("(select t.tsnobjectid from transformsubnetworklink t, me m where  t.meobjectid = m.parentmeobjectid and m.objectid = aendmeobjectid) tsnid , ");
		sb2.append("aendmeobjectid,aendptpobjectid, ");
		sb2.append("zendmeobjectid,zendptpobjectid ");
		sb2.append("FROM wdmsncroute WHERE 1=1 and sncobjectid = '").append( sncid ).append("' ");
		sb2.append("and protectionrole='0' and direction='").append(direction).append("' ");
		sb2.append(" order by  wdmsncroute.routeseq, wdmsncroute.wayseq ");
		
		logger.info("查询数据库路由开始：sncobjectid =" + sncid);
		List<WdmSncRoute> wdmsncroutelist = dbUtil.getJdbcTemplate().query(sb2.toString(), new WdmSncRouteMapper());
  	
		return wdmsncroutelist ; 
  	
	}

	public Collection<DbWdmSncAll> getWdmSncAll(String emsid) {

		
		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append("(select juzhanobjectid from me   where objectid = wdmsnc.aendmeobjectid) ajuzhan, ");
	  	sb.append("(select juzhanobjectid from me   where objectid = wdmsnc.zendmeobjectid) zjuzhan, ");
	  	sb.append(" wdmsnc.objectid sncobjectid, ");
	  	sb.append(" wdmsnc.direction sncdirection, ");
	  	sb.append(" wdmsnc.sncname sncname, ");
	  	sb.append(" wdmsnc.rate sncrate, ");
	  	sb.append("(select model from card , ptp where card.objectid = ptp.cardobjectid and ptp.objectid= wdmsncroute.aendptpobjectid  ) acardmodel, ");
	  	sb.append("(select model from card , ptp where card.objectid = ptp.cardobjectid and ptp.objectid= wdmsncroute.zendptpobjectid  ) zcardmodel, ");
	  	sb.append(" wdmsncroute.* ");
	  	sb.append(" FROM wdmsnc,wdmsncroute WHERE wdmsnc.objectid = wdmsncroute.sncobjectid  ");
	  	sb.append(" and wdmsncroute.protectionrole='0' ");
	  	sb.append(" and wdmsnc.emsobjectid='").append(emsid).append("' ");
	  	sb.append(" order by wdmsncroute.sncobjectid , wdmsncroute.direction , wdmsncroute.routeseq, wdmsncroute.wayseq ");
	  	
	  	logger.info("查询数据库路由开始：" + sb.toString());
	  	return dbUtil.getJdbcTemplate().query(sb.toString(), new DbWdmSncAllExtractor());
		
	}

	public Map<String, List<DbWdmSncAll>> getZdWmdsnc(String emsid) {
		
		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append("(select juzhanobjectid from me   where objectid = wdmsnc.aendmeobjectid) ajuzhan, ");
	  	sb.append("(select juzhanobjectid from me   where objectid = wdmsnc.zendmeobjectid) zjuzhan, ");
	  	sb.append(" wdmsnc.* ");
	  	sb.append(" FROM wdmsnc ");
	  	sb.append(" where wdmsnc.emsobjectid='").append(emsid).append("' ");
	  	sb.append(" and objectid in ('UUID:516979b8-10da-11e5-9c2d-005056862639','UUID:516979b9-10da-11e5-9c2d-005056862639') ");
	  	
	  	logger.info("getZdWmdsnc查询数据库路由开始：" + sb.toString());
	  	return dbUtil.getJdbcTemplate().query(sb.toString(), new DbWdmSncZdExtractor());
		
	}
	

	
}
