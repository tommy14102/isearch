package com.zznode.opentnms.isearch.routeAlgorithm;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.zznode.opentnms.isearch.routeAlgorithm.api.ISearch;
import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Direction;
import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.BusinessAvator;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResult;
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_floyd.FloydWarshall;
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.data.importor_dbbase.DBBaseImporter;
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.data.importor_dbbase.DBConfig;

public class PTNDBSearch {
  
	
	@Test
	public void testIssue(){
	    ISearch isearch = new ISearch();
	    
	    BusinessAvator businessAvator = new BusinessAvator();
	    businessAvator.setKey("PTN");
	    isearch.regist(businessAvator);
	    
	     
	    DBConfig dbConfig = new DBConfig();
	    dbConfig.setAendnode("aendmeobjectid");
	    dbConfig.setZendnode("zendmeobjectid");
	    dbConfig.setAendeage("aendptpobjectid");
	    dbConfig.setZendeage("zendptpobjectid");
	    dbConfig.setDirection("direction");
	    Map<String, Direction> directionMap = new HashMap<String, Direction>();
	    directionMap.put("0", Direction.SINGLE);
	    directionMap.put("1", Direction.DOUBLE);
	    dbConfig.setDirectionMap(directionMap);
	    dbConfig.setSql("select t.* from topolink t, objectemsrelation r where r.emsobjectid = 'SNI-T2000-1-P' and r.objectid = t.objectid and r.objecttype = '6' ");
	    
	    DBBaseImporter dbBaseImporter = new DBBaseImporter(dbConfig);
	    isearch.refreshdata(businessAvator.getKey(), dbBaseImporter);
	    
	    
	    CaculatorParam param = new CaculatorParam();
	    param.setAend("UUID:38376e50-5c2f-11e0-88c8-0018fe2fc2b3");
	    param.setZend("UUID:3aa17a42-899f-11de-9029-0018fe2fc2b3");
	    param.setCount(2);
	    param.setPolicy(Policy.LESS_JUMP);
	    
	    
	    CaculatorResult result = isearch.search(businessAvator.getKey(), param, new FloydWarshall());
	    System.out.println(result);
	}
	
}
