package com.zznode.opentnms.isearch.otnRouteService.manage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zznode.opentnms.isearch.model.bo.ConstBusiness;
import com.zznode.opentnms.isearch.otnRouteService.api.model.CardUsedPtpInfo;
import com.zznode.opentnms.isearch.otnRouteService.api.model.CardUsedPtpOutput;
import com.zznode.opentnms.isearch.otnRouteService.db.DBUtil;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbEmsMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbMeMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbMeParentExtractor;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbWdmOchZdExtractor;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbWdmSncAllExtractor;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbWdmSncMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.DbWdmSncZdExtractor;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.UsedPtpMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.WdmSncRouteMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.mapper.ZhiluPtpMapper;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbEms;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbMe;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSnc;
import com.zznode.opentnms.isearch.otnRouteService.db.po.DbWdmSncAll;
import com.zznode.opentnms.isearch.otnRouteService.db.po.UsedPtp;
import com.zznode.opentnms.isearch.otnRouteService.db.po.WdmSncRoute;
import com.zznode.opentnms.isearch.otnRouteService.db.po.ZhiluPtp;
import com.zznode.opentnms.isearch.otnRouteService.util.PropertiesHander;

@Component
public class ResourceManager {

	@Autowired
	public DBUtil dbUtil;
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);   
	
	public Map<String,String> meParentMap = new HashMap<String,String>();
	public HashSet<String> usedPtpSet = new HashSet<String>();
	
    @PostConstruct
    public void postConstruct1(){

    	/**
    	StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append(" objectid , parentmeobjectid  ");
	  	sb.append(" FROM me ");
	  	
	  	meParentMap =  dbUtil.getJdbcTemplate().query(sb.toString(), new DbMeParentExtractor()); 
	  	
	  	
	  	String[] zlcards = PropertiesHander.getPropertylist("zlcard");
	  	
	  	StringBuilder sb2 = new StringBuilder();
	  	sb2.append(" SELECT ");
	  	sb2.append(" ptp.objectid  ");
	  	sb2.append(" FROM wdmsncroute , ptp , card ");
	  	sb2.append(" where wdmsncroute.aendptpobjectid = ptp.objectid ");
	  	sb2.append(" and ptp.cardobjectid = card.objectid ");
	  	sb2.append(" and ").append( getSqlInStatement("card.model", zlcards));
	  	
	  	List<String> ptplist =  dbUtil.getJdbcTemplate().queryForList(sb2.toString(), String.class); 
	  	logger.info("usedPtpSet.addsqi: " +  sb2.toString() );
	  	for (int i = 0; i < ptplist.size(); i++) {
	  		logger.info("usedPtpSet.add: " +  ptplist.get(i) );
	  		usedPtpSet.add( ptplist.get(i));
		}
	  	
	  	StringBuilder sb3 = new StringBuilder();
	  	sb3.append(" SELECT ");
	  	sb3.append(" ptp.objectid  ");
	  	sb3.append(" FROM wdmsncroute , ptp , card ");
	  	sb3.append(" where wdmsncroute.zendptpobjectid = ptp.objectid ");
	  	sb3.append(" and ptp.cardobjectid = card.objectid ");
	  	sb3.append(" and ").append( getSqlInStatement("card.model", zlcards));
	  	
	  	List<String> ptplist2 =  dbUtil.getJdbcTemplate().queryForList(sb3.toString(), String.class); 
	  	logger.info("usedPtpSet.addsq2: " +  sb3.toString() );
	  	for (int i = 0; i < ptplist2.size(); i++) {
	  		logger.info("usedPtpSet2.add: " +  ptplist2.get(i) );
	  		usedPtpSet.add( ptplist2.get(i));
		}
		*/
    }
	
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
	public List<String>  getDCardModel() {
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select model from cardmodel where 1=1 and cardtype='分波盘' ");
		return dbUtil.getJdbcTemplate().queryForList( sqlBuf.toString(),String.class); 
	}
	
	public String[]  getNCardModel() {
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select model from cardmodel where 1=1 and cardtype='线路盘' ");
		//return dbUtil.getJdbcTemplate().queryForList( sqlBuf.toString(),String.class); 
		return PropertiesHander.getPropertylist("xlcard");
	}
	
	public String[] getTCardModel() {
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select model from cardmodel where 1=1 and cardtype='支路盘' ");
		//return dbUtil.getJdbcTemplate().queryForList( sqlBuf.toString(),String.class); 
		return PropertiesHander.getPropertylist("zlcard");
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
	  	//sb.append(" and wdmsnc.objectid in ('UUID:51666c7a-10da-11e5-9c2d-005056862639','UUID:5165a951-10da-11e5-9c2d-005056862639')");
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
	  	sb.append(" FROM wdmsnc,objectemsrelation r ");
	  	sb.append(" where wdmsnc.emsobjectid='").append(emsid).append("' ");
	  	sb.append(" and wdmsnc.objectid = r.objectid  ");
	  	sb.append(" and r.isdelete='0'  ");
	  	sb.append(" and rate not in ('40','107','108','109','8042')  and wdmsnc.objectid = 'UUID:1de234c4-aeac-11e5-8587-0050568478c2' ");
	  	//sb.append(" and direction = '1' ");
	  	//sb.append(" and wdmsnc.objectid in ('UUID:51686850-10da-11e5-9c2d-005056862639','UUID:51681a33-10da-11e5-9c2d-005056862639')");
	  	//sb.append(" and objectid in ('UUID:5169ef20-10da-11e5-9c2d-005056862639','UUID:5169c801-10da-11e5-9c2d-005056862639','UUID:5169a10c-10da-11e5-9c2d-005056862639','UUID:516979c8-10da-11e5-9c2d-005056862639') ");
	  	sb.append(" union all ");
	  	sb.append(" SELECT ");
	  	sb.append("(select juzhanobjectid from me   where objectid = wdmsnc.aendmeobjectid) ajuzhan, ");
	  	sb.append("(select juzhanobjectid from me   where objectid = wdmsnc.zendmeobjectid) zjuzhan, ");
	  	sb.append(" wdmsnc.* ");
	  	sb.append(" FROM wdmsnc,objectemsrelation r ");
	  	sb.append(" where wdmsnc.emsobjectid='").append(emsid).append("' ");
	  	sb.append(" and wdmsnc.objectid = r.objectid  ");
	  	sb.append(" and r.isdelete='0'  ");
	  	sb.append(" and rate in ('107','108','109','8042') and wdmsnc.objectid = 'UUID:1de234c4-aeac-11e5-8587-0050568478c2'");
	  	sb.append(" and not exists( select 1 from wdmsnc c where c.aendptpobjectid = wdmsnc.aendptpobjectid and c.zendptpobjectid = wdmsnc.zendptpobjectid and c.rate in ('104','105','106','8041') ) ");
	  	
	  	
	  	logger.info("getZdWmdsnc查询数据库路由开始：" + sb.toString());
	  	Map<String, List<DbWdmSncAll>> rtnmsp = dbUtil.getJdbcTemplate().query(sb.toString(), new DbWdmSncZdExtractor());
	  	
	  	return rtnmsp;
		
	}
	
	public List<ZhiluPtp> queryZLPtp(String meobjectid , Integer rate, String aendptp , String vendor, String direc )  {
		
		//String[] zlcards = PropertiesHander.getPropertylist("zlcard");
		
		Integer ratelevel = ConstBusiness.rateMap.get(rate);
		if( ratelevel==null ){
			ratelevel = ConstBusiness.odukMap.get(rate);
		}
		if( ratelevel.intValue() == 0 ){
			ratelevel = 1 ;
		}
		String ratelevelkey = "zlportSRate_" + ratelevel ; 
		List<String> avaliableCardType = Arrays.asList( PropertiesHander.getPropertylist(ratelevelkey) );
		
		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append(" ptp.objectid ptpobjectid ,ptp.cardobjectid cardobjectid,card.model cardmodel, ptp.tptype, ptp.ptpid  ");
	  	sb.append(" FROM ptp,card ");
	  	sb.append(" where card.meobjectid='").append(meobjectid).append("' ");
	  	sb.append(" and card.objectid = ptp.cardobjectid ");
	  	sb.append(" and ptp.tptype = 'PTP' ");
	  	sb.append(" and ").append( getSqlInStatement("card.model", (String[])avaliableCardType.toArray(new String[avaliableCardType.size()])));
	  	if( !StringUtils.isEmpty(aendptp) ){
	  		sb.append(" and ptp.objectid = '").append(aendptp).append("' ");
	  	}
	  	//sb.append(" order by card.model ");
	  	 
	  	//sb.append(" and not exists(select 1 from wdmsncroute where aendptpobjectid = ptp.objectid) ");
	  	//sb.append(" and not exists(select 1 from wdmsncroute where zendptpobjectid = ptp.objectid) ");
	  	
	  	
	  	List<ZhiluPtp> zlptplist = dbUtil.getJdbcTemplate().query(sb.toString(), new ZhiluPtpMapper());
	  	logger.info("查询支路盘信息："+ sb.toString()  );
	  	logger.info("查询支路盘信息结果："+ zlptplist.size()  );
	  	
	  	if( zlptplist==null || zlptplist.size()==0 ){
	  		avaliableCardType = Arrays.asList( PropertiesHander.getPropertylist("zlportSRate_any") );
	  		StringBuilder sb2 = new StringBuilder();
	  		sb2.append(" SELECT ");
	  		sb2.append(" ptp.objectid ptpobjectid ,ptp.cardobjectid cardobjectid,card.model cardmodel, ptp.tptype, ptp.ptpid  ");
	  		sb2.append(" FROM ptp,card ");
	  		sb2.append(" where card.meobjectid='").append(meobjectid).append("' ");
	  		sb2.append(" and card.objectid = ptp.cardobjectid ");
	  		sb2.append(" and ptp.tptype = 'PTP' ");
	  		sb2.append(" and ").append( getSqlInStatement("card.model", (String[])avaliableCardType.toArray(new String[avaliableCardType.size()])));
	  		if( !StringUtils.isEmpty(aendptp) ){
	  			sb2.append(" and ptp.objectid = '").append(aendptp).append("' ");
		  	}
	  		
	  		zlptplist = dbUtil.getJdbcTemplate().query(sb2.toString(), new ZhiluPtpMapper());
	  		logger.info("查询支路盘信息any："+ sb2.toString()  );
	  	}
	  	
	  	if( zlptplist!=null && zlptplist.size()> 0){
	  		logger.info("usedPtpSet checksize："+ zlptplist.size()  );
	  		for (Iterator<ZhiluPtp> iter= zlptplist.iterator(); iter.hasNext();) {
				ZhiluPtp zhiluPtp = iter.next();
				logger.info("usedPtpSet check："+ zhiluPtp.getPtpobjectid()  );
				if( usedPtpSet.contains( zhiluPtp.getPtpobjectid())){
					logger.info("usedPtpSet check result true "  );
					iter.remove();
					continue;
				}
				
				if(vendor.equals("中兴")){
					//判断ptpid的direction和type
					Pattern pattern = Pattern.compile("^/direction=(\\w+)/rack=\\d+/shelf=\\d+/slot=\\d+/type=\\w+_(\\w+)/port=\\d+$");
					Matcher matcher = pattern.matcher(zhiluPtp.getPtpid());
					boolean b= matcher.matches();
					if(!b){
						continue;
					}
					matcher.reset();
					matcher.find();
						
					String direction = matcher.group(1);
					String type = matcher.group(2);

					if(direc.equals("0")){
						if(!direction.equalsIgnoreCase("sink")){
							continue;
						}
						if(!type.equalsIgnoreCase("so")){
							continue;
						}
					}
					else{
						if(!direction.equalsIgnoreCase("src")){
							continue;
						}
						if(!type.equalsIgnoreCase("si")){
							continue;
						}
					}
					
				}
			}
	  		logger.info("usedPtpSet checksize2："+ zlptplist.size()  );
	  	}
	  	
	  	return zlptplist;
		
	}
	
	public List<ZhiluPtp> query100GZLPtp(String meobjectid ,  String aendptp , String vendor, String direc ,List<String> avaliableCardType)  {
		
		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append(" ptp.objectid ptpobjectid ,ptp.cardobjectid cardobjectid,card.model cardmodel, ptp.tptype, ptp.ptpid  ");
	  	sb.append(" FROM ptp,card ");
	  	sb.append(" where card.meobjectid='").append(meobjectid).append("' ");
	  	sb.append(" and card.objectid = ptp.cardobjectid ");
	  	sb.append(" and ptp.tptype = 'PTP' ");
	  	sb.append(" and ").append( getSqlInStatement("card.model", (String[])avaliableCardType.toArray(new String[avaliableCardType.size()])));
	  	if( !StringUtils.isEmpty(aendptp) ){
	  		sb.append(" and ptp.objectid = '").append(aendptp).append("' ");
	  	}
	  	//sb.append(" order by card.model ");
	  	 
	  	//sb.append(" and not exists(select 1 from wdmsncroute where aendptpobjectid = ptp.objectid) ");
	  	//sb.append(" and not exists(select 1 from wdmsncroute where zendptpobjectid = ptp.objectid) ");
	  	
	  	
	  	List<ZhiluPtp> zlptplist = dbUtil.getJdbcTemplate().query(sb.toString(), new ZhiluPtpMapper());
	  	logger.info("查询支路盘信息："+ sb.toString()  );
	  	logger.info("查询支路盘信息结果："+ zlptplist.size()  );
	  	
	  	if( zlptplist==null || zlptplist.size()==0 ){
	  		avaliableCardType = Arrays.asList( PropertiesHander.getPropertylist("zlportSRate_any") );
	  		StringBuilder sb2 = new StringBuilder();
	  		sb2.append(" SELECT ");
	  		sb2.append(" ptp.objectid ptpobjectid ,ptp.cardobjectid cardobjectid,card.model cardmodel, ptp.tptype, ptp.ptpid  ");
	  		sb2.append(" FROM ptp,card ");
	  		sb2.append(" where card.meobjectid='").append(meobjectid).append("' ");
	  		sb2.append(" and card.objectid = ptp.cardobjectid ");
	  		sb2.append(" and ptp.tptype = 'PTP' ");
	  		sb2.append(" and ").append( getSqlInStatement("card.model", (String[])avaliableCardType.toArray(new String[avaliableCardType.size()])));
	  		if( !StringUtils.isEmpty(aendptp) ){
	  			sb2.append(" and ptp.objectid = '").append(aendptp).append("' ");
		  	}
	  		
	  		zlptplist = dbUtil.getJdbcTemplate().query(sb2.toString(), new ZhiluPtpMapper());
	  		logger.info("查询支路盘信息any："+ sb2.toString()  );
	  	}
	  	
	  	if( zlptplist!=null && zlptplist.size()> 0){
	  		logger.info("usedPtpSet checksize："+ zlptplist.size()  );
	  		for (Iterator<ZhiluPtp> iter= zlptplist.iterator(); iter.hasNext();) {
				ZhiluPtp zhiluPtp = iter.next();
				logger.info("usedPtpSet check："+ zhiluPtp.getPtpobjectid()  );
				if( usedPtpSet.contains( zhiluPtp.getPtpobjectid())){
					logger.info("usedPtpSet check result true "  );
					iter.remove();
					continue;
				}
				
				if(vendor.equals("中兴")){
					//判断ptpid的direction和type
					Pattern pattern = Pattern.compile("^/direction=(\\w+)/rack=\\d+/shelf=\\d+/slot=\\d+/type=\\w+_(\\w+)/port=\\d+$");
					Matcher matcher = pattern.matcher(zhiluPtp.getPtpid());
					boolean b= matcher.matches();
					if(!b){
						continue;
					}
					matcher.reset();
					matcher.find();
						
					String direction = matcher.group(1);
					String type = matcher.group(2);

					if(direc.equals("0")){
						if(!direction.equalsIgnoreCase("sink")){
							continue;
						}
						if(!type.equalsIgnoreCase("so")){
							continue;
						}
					}
					else{
						if(!direction.equalsIgnoreCase("src")){
							continue;
						}
						if(!type.equalsIgnoreCase("si")){
							continue;
						}
					}
					
				}
			}
	  		logger.info("usedPtpSet checksize2："+ zlptplist.size()  );
	  	}
	  	
	  	return zlptplist;
		
	}

	
	public List<ZhiluPtp> queryZLClinetPtp(String zlptpobjectid, String aendptp ) {
		
		/**
		if( usedPtpSet.contains(zlptpobjectid) ){
			StringBuilder sb = new StringBuilder();
		  	sb.append(" SELECT ");
		  	sb.append(" p.objectid ptpobjectid ,p.cardobjectid cardobjectid,card.model cardmodel  ");
		  	sb.append(" FROM ptp p , ptp,card  ");
		  	sb.append(" where ptp.objectid='").append(zlptpobjectid).append("' ");
		  	sb.append(" and ptp.cardobjectid=card.objectid ");
		  	sb.append(" and p.cardobjectid=card.objectid ");
		  	sb.append(" and p.tptype = 'PTP' ");
		  	sb.append(" and p.objectid = '").append(zlptpobjectid).append("' ");
		  	if( !StringUtils.isEmpty(aendptp) ){
		  		sb.append(" and p.objectid = '").append(aendptp).append("' ");
		  	}
		  	
		  	logger.info("查询zl盘信息："+ zlptpobjectid);
		  	List<ZhiluPtp> zlptplist =  dbUtil.getJdbcTemplate().query(sb.toString(), new ZhiluPtpMapper());
		  	return zlptplist;
		  	
	  	}
	  	*/
		
		
		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append(" p.objectid ptpobjectid ,p.cardobjectid cardobjectid,card.model cardmodel, p.tptype  ");
	  	sb.append(" FROM ptp p , ptp,card  ");
	  	sb.append(" where ptp.objectid='").append(zlptpobjectid).append("' ");
	  	sb.append(" and ptp.cardobjectid=card.objectid ");
	  	sb.append(" and p.cardobjectid=card.objectid ");
	  	sb.append(" and p.tptype = 'PTP' ");
	  	if( !StringUtils.isEmpty(aendptp) ){
	  		sb.append(" and p.objectid = '").append(aendptp).append("' ");
	  	}
	  	else{
	  		sb.append(" and p.objectid = '").append(zlptpobjectid).append("' ");
	  	}
	  	
	  	logger.info("查询支路盘信息2："+ zlptpobjectid);
	  	List<ZhiluPtp> zlptplist =  dbUtil.getJdbcTemplate().query(sb.toString(), new ZhiluPtpMapper());
	  	
	  	if( zlptplist!=null && zlptplist.size()> 0){
	  		for (Iterator<ZhiluPtp> iter= zlptplist.iterator(); iter.hasNext();) {
				ZhiluPtp zhiluPtp = iter.next();
				if( usedPtpSet.contains( zhiluPtp.getPtpobjectid())){
					iter.remove();
					continue;
				}
			}
	  	}
	  	
	  	return zlptplist;
		
	}
	
	public String getEmsVendorByWdmtrailid(String sncid) {

		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append(" ems.vendor ");
	  	sb.append(" FROM wdmtrail,ems ");
	  	sb.append(" where wdmtrail.aendemsobjectid = ems.emsid and wdmtrail.sncid='").append(sncid).append("' ");
	  	
	  	return dbUtil.getJdbcTemplate().queryForObject(sb.toString(), String.class); 
	  	
	}
	
	
	public List<ZhiluPtp> queryZLClinetPtp2(String zlptpobjectid, String aendptp, String vendor, String direc ) {
		
		
		//查询ptp信息
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" p.objectid ptpobjectid ,p.cardobjectid cardobjectid, p.tptype, card.model cardmodel ");
		sb.append(" FROM ptp p , card  ");
		sb.append(" where p.objectid='").append(zlptpobjectid).append("' ");
		sb.append(" and p.cardobjectid=card.objectid ");
		  	
		List<ZhiluPtp> ptps =  dbUtil.getJdbcTemplate().query(sb.toString(), new ZhiluPtpMapper());
		ZhiluPtp ptp = ptps.get(0);
		
		if( ptp.getTptype().equals("FTP")){
			
				//如果是FTP，那么查询支路侧口
				StringBuilder sb2 = new StringBuilder();
				sb2.append(" SELECT ");
				sb2.append(" p.objectid ptpobjectid ,p.cardobjectid cardobjectid, card.model cardmodel , p.tptype, ptpid ");
				sb2.append(" FROM ptp p , card  ");
				sb2.append(" where ");
				sb2.append(" and p.cardobjectid=card.objectid ");
				sb2.append(" and p.tptype = 'PTP' ");
				sb2.append(" and card.objectid = '").append( ptp.getCardobjectid() ).append("' ");
			  	if( !StringUtils.isEmpty(aendptp) ){
			  		sb.append(" and p.objectid = '").append(aendptp).append("' ");
			  	}
			  	
			  	logger.info("查询支路盘信息1："+ zlptpobjectid);
			  	List<ZhiluPtp> zlptplist =  dbUtil.getJdbcTemplate().query(sb.toString(), new ZhiluPtpMapper());
			  	if( zlptplist!=null && zlptplist.size()> 0){
			  		for (Iterator<ZhiluPtp> iter= zlptplist.iterator(); iter.hasNext();) {
						ZhiluPtp zhiluPtp = iter.next();
						if( usedPtpSet.contains( zhiluPtp.getPtpobjectid())){
							iter.remove();
							continue;
						}
						if(vendor.equals("中兴")){
							//判断ptpid的direction和type
							Pattern pattern = Pattern.compile("^/direction=(\\w+)/rack=\\d+/shelf=\\d+/slot=\\d+/type=\\w+_(\\w+)/port=\\d+$");
							Matcher matcher = pattern.matcher(zhiluPtp.getPtpid());
							boolean b= matcher.matches();
							if(!b){
								continue;
							}
							matcher.reset();
							matcher.find();
								
							String direction = matcher.group(1);
							String type = matcher.group(2);

							if(direc.equals("0")){
								if(!direction.equalsIgnoreCase("sink")){
									continue;
								}
								if(!type.equalsIgnoreCase("so")){
									continue;
								}
							}
							else{
								if(!direction.equalsIgnoreCase("src")){
									continue;
								}
								if(!type.equalsIgnoreCase("si")){
									continue;
								}
							}
						}
			  	}
		  	}
		  	return zlptplist;
		}
		else{
			
			List<ZhiluPtp> zlptplist = new ArrayList<ZhiluPtp>();
			if( StringUtils.isEmpty(aendptp) || zlptpobjectid.equals(aendptp) ){
				zlptplist.add(ptp);
			}
			return zlptplist ; 
			
		}
	}

	 public static String getSqlInStatement(String columnName, String[] values) {

	        if (values == null || values.length == 0)
	            return " 1=1 ";
	        ArrayList<String> valueList = new ArrayList<String>();
	        for (int i = 0; i < values.length; i++) {
	            if (values[i] != null && values[i].length() > 0)
	                valueList.add(values[i]);
	        }
	        return getSqlInStatement(columnName, valueList);

	   }
	 
	 public static String getSqlInStatement(String columnName,
	            ArrayList<String> valueList) {
	        StringBuffer sqlBuf = new StringBuffer();
	        if (valueList == null || valueList.size() == 0)
	            return "";
	        sqlBuf.append(" ").append(columnName).append(" in (");
	        int count = 0;
	        for (int i = 0; i < valueList.size(); i++) {
	            String value = valueList.get(i);
	            if (value == null || value.length() == 0)
	                continue;
	            if (count > 0)
	                sqlBuf.append(",");
	            sqlBuf.append("'").append(value).append("'");
	            count++;
	        }
	        sqlBuf.append(") ");
	        return sqlBuf.substring(0);
	    }

	 
	
	@Deprecated
	public String queryParentMeObjectid(String meobjectid) {
		
		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append(" parentmeobjectid  ");
	  	sb.append(" FROM me ");
	  	sb.append(" where  objectid='").append(meobjectid).append("' ");
	  	
	  	return dbUtil.getJdbcTemplate().queryForObject(sb.toString(), String.class); 
	  	
		
	}
	
	public boolean hasCucirtBySncid(String sncid){
		
		String  sql = " select count(objectid) from circuit where objectid in ( select circuitobjectid from circuitroute where relatedrouteobjectid = '"+sncid +"' ) ";
		
		String cucirtid = dbUtil.getJdbcTemplate().queryForObject(sql, String.class);
				
		return !cucirtid.equals("0");
				
	}

	public Map<String,List<DbWdmSncAll>> getAllOch(String emsid) {
		
		Map<String,List<DbWdmSncAll>> rtnlist = new HashMap<String,List<DbWdmSncAll>>();
		
		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append("(select juzhanobjectid from me   where objectid = wdmsnc.aendmeobjectid) juzhan, ");
	  	sb.append(" wdmsnc.* ");
	  	sb.append(" FROM wdmsnc,objectemsrelation r");
	  	sb.append(" where wdmsnc.emsobjectid='").append(emsid).append("' ");
	  	sb.append(" and wdmsnc.objectid = r.objectid  ");
	  	sb.append(" and r.isdelete='0'  ");
	  	sb.append(" and rate = '40' ");
	  	//sb.append(" and direction = '1' ");
	  	//sb.append(" and objectid in ('UUID:5169ef20-10da-11e5-9c2d-005056862639','UUID:5169c801-10da-11e5-9c2d-005056862639','UUID:5169a10c-10da-11e5-9c2d-005056862639','UUID:516979c8-10da-11e5-9c2d-005056862639') ");
	  	/**
	  	sb.append(" and objectid in ( ");
	  	sb.append(" 'UUID:5166baa8-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:5166badf-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:516a1616-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:5166bad4-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:5166e1c9-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:516a161a-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:516a1625-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:516a1626-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:516a1627-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:516a3d19-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:516a640f-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:516a6410-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:516a6411-10da-11e5-9c2d-005056862639',");
	  	sb.append(" 'UUID:516ad974-10da-11e5-9c2d-005056862639' ) ");
	  	*/
	  	logger.info("getZdWmdsnc查询数据库路由开始：" + sb.toString());
	  	Map<String,List<DbWdmSncAll>> ochroutes =  dbUtil.getJdbcTemplate().query(sb.toString(), new DbWdmOchZdExtractor());
	  	for(Map.Entry<String,List<DbWdmSncAll>> mp : ochroutes.entrySet()){
	  		List<DbWdmSncAll> routs =  mp.getValue();
	  		for (Iterator<DbWdmSncAll> iter = routs.iterator(); iter.hasNext();) {
				DbWdmSncAll dbWdmSncAll =  iter.next();
		  		List<WdmSncRoute> wdmsncroutelist = this.queryForRoute(dbWdmSncAll.getObjectId(),"0");
				if( wdmsncroutelist ==null || wdmsncroutelist.size()==0){
					iter.remove() ; 
				    logger.info( "och无路由，舍弃:" + dbWdmSncAll.getObjectId() );
				    continue ;
				}
				dbWdmSncAll.setWdmsncroutelist( wdmsncroutelist );
				dbWdmSncAll.setIsReverse(Boolean.FALSE);
			}
	  	}
	  	
	  	rtnlist.putAll(ochroutes);
	  	
	  	StringBuilder sb2 = new StringBuilder();
	  	sb2.append(" SELECT ");
	  	sb2.append("(select juzhanobjectid from me   where objectid = wdmsnc.zendmeobjectid) juzhan, ");
	  	sb2.append(" wdmsnc.* ");
	  	sb2.append(" FROM wdmsnc ");
	  	sb2.append(" where wdmsnc.emsobjectid='").append(emsid).append("' ");
	  	sb2.append(" and rate = '40' ");
	  	//sb2.append(" and objectid in ('UUID:5169ef20-10da-11e5-9c2d-005056862639','UUID:5169c801-10da-11e5-9c2d-005056862639','UUID:5169a10c-10da-11e5-9c2d-005056862639','UUID:516979c8-10da-11e5-9c2d-005056862639') ");
	  	/**
	  	sb2.append(" and objectid in ( ");
	  	sb2.append(" 'UUID:5166baa8-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:5166badf-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:516a1616-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:5166bad4-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:5166e1c9-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:516a161a-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:516a1625-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:516a1626-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:516a1627-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:516a3d19-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:516a640f-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:516a6410-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:516a6411-10da-11e5-9c2d-005056862639',");
	  	sb2.append(" 'UUID:516ad974-10da-11e5-9c2d-005056862639' ) ");
	  	*/
	  	logger.info("getZdWmdsnc查询数据库路由开始2：" + sb2.toString());
	  	Map<String,List<DbWdmSncAll>> ochroutes2 =  dbUtil.getJdbcTemplate().query(sb2.toString(), new DbWdmOchZdExtractor());
	  	for(Map.Entry<String,List<DbWdmSncAll>> mp : ochroutes2.entrySet()){
	  		String key = mp.getKey();
	  		List<DbWdmSncAll> routs =  mp.getValue();
	  		for (Iterator<DbWdmSncAll> iter = routs.iterator(); iter.hasNext();) {
	  			DbWdmSncAll dbWdmSncAll =  iter.next();
	  			List<WdmSncRoute> wdmsncroutelist = this.queryForRoute(dbWdmSncAll.getObjectId(),"1");
	  			if( wdmsncroutelist ==null || wdmsncroutelist.size()==0){
	  				iter.remove() ; 
	  				logger.info( "och无路由2，舍弃:" + dbWdmSncAll.getObjectId() );
	  				continue ;
	  			}
	  			dbWdmSncAll.setWdmsncroutelist( wdmsncroutelist );
	  			dbWdmSncAll.setIsReverse(Boolean.TRUE);
	  		}
	  		
	  		if(rtnlist.containsKey(key)){
	  			rtnlist.get(key).addAll(routs);
	  		}
	  		else{
	  			rtnlist.put(key, routs);
	  		}
		}
	  		
	  	
	  	return rtnlist ; 
	  	
	}

	public List<WdmSncRoute> queryForRouteReverse(String sncid) {
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
		sb2.append("and protectionrole='0' and direction='0' ");
		sb2.append(" order by  wdmsncroute.routeseq , wdmsncroute.wayseq ");
		
		logger.info("查询数据库路由开始：sncobjectid =" + sncid);
		List<WdmSncRoute> wdmsncroutelist = dbUtil.getJdbcTemplate().query(sb2.toString(), new WdmSncRouteMapper());
		Collections.reverse( wdmsncroutelist );
		for (int i = 0; i < wdmsncroutelist.size(); i++) {
			WdmSncRoute route = wdmsncroutelist.get(i);
			String acardmodel = route.getAcardmodel();
			String zcardmodel = route.getZcardmodel();
			route.setAcardmodel(zcardmodel);
			route.setZcardmodel(acardmodel);
			
			String ameid = route.getM_AEndMeObjectId();
			String zmeid = route.getM_ZEndMeObjectId();
			route.setM_AEndMeObjectId(zmeid);
			route.setM_ZEndMeObjectId(ameid);
			
			String aptpid = route.getM_AEndPtpObjectId();
			String zptpid = route.getM_ZEndPtpObjectId();
			route.setM_AEndPtpObjectId(zptpid);
			route.setM_AEndPtpObjectId(aptpid);
			
			String actpid = route.getM_AEndCtpId();
			String zctpid = route.getM_ZEndCtpId();
			route.setM_AEndCtpId(zctpid);
			route.setM_ZEndCtpId(actpid);
			
		}
  	
		return wdmsncroutelist ; 
	}

	public String queryZhandianBySncid(String sncid) {

		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append("(select juzhanobjectid from me where objectid = aendmeobjectid)||'-'||(select juzhanobjectid from me where objectid = zendmeobjectid) juzhanStr ");
	  	sb.append(" FROM wdmsnc ");
	  	sb.append(" where objectid='").append(sncid).append("' ");
	  	
	  	return dbUtil.getJdbcTemplate().queryForObject(sb.toString(), String.class); 
	  	
	}

	public String getPtpMeInfo(String portid) {

		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append(" meobjectid ");
	  	sb.append(" FROM ptp ");
	  	sb.append(" where objectid='").append(portid).append("' ");
	  	
	  	return dbUtil.getJdbcTemplate().queryForObject(sb.toString(), String.class); 
	  	
	}

	public CardUsedPtpOutput queryCardptpinfo(String cardobjectid) {
		
		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append(" p.objectid ptpobjectid ,p.portname ptpname,  ");
	  	sb.append(" (select max(objectid) from wdmsnc where aendptpobjectid = p.objectid) awdmtrailid,  ");
	  	sb.append(" (select max(objectid) from wdmsnc where zendptpobjectid = p.objectid) zwdmtrailid   ");
	  	sb.append(" FROM ptp p  ");
	  	sb.append(" where p.cardobjectid='").append(cardobjectid).append("' ");
	  	
	  	logger.info("查询ptp信息："+ cardobjectid);
	  	List<UsedPtp> zlptplist =  dbUtil.getJdbcTemplate().query(sb.toString(), new UsedPtpMapper());
	  	
	  	CardUsedPtpOutput output = new CardUsedPtpOutput();
	  	List<CardUsedPtpInfo> usedPtplist = new ArrayList<CardUsedPtpInfo>();
		List<CardUsedPtpInfo> unusedPtplist = new ArrayList<CardUsedPtpInfo>();
		
		for (int i = 0; i < zlptplist.size(); i++) {
			UsedPtp ptp = zlptplist.get(i);
			CardUsedPtpInfo info = new CardUsedPtpInfo();
			info.setObjectid(ptp.getPtpobjectid());
			info.setPortname(ptp.getPtpname());
			
			if( StringUtils.isEmpty( ptp.getAwdmtrailid()) && StringUtils.isEmpty( ptp.getZwdmtrailid())  ){
				unusedPtplist.add(info);
			}
			else{
				usedPtplist.add(info);
			}
		}
	  	
		output.setUsedPtplist(usedPtplist);
		output.setUnusedPtplist(unusedPtplist);
		
	  	return output;
	  	
	}

	public String getCtpByPtp(String ptpobjectid) {
		
		StringBuilder sb = new StringBuilder();
	  	sb.append(" SELECT ");
	  	sb.append(" ctpid  ");
	  	sb.append(" FROM ctp ");
	  	sb.append(" where ptpobjectid ='").append(ptpobjectid).append("' ");
	  	
	  	return dbUtil.getJdbcTemplate().queryForObject(sb.toString(), String.class); 
	}
	
}
