package com.zznode.opentnms.isearch.otnRouteService.consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zznode.opentnms.isearch.otnRouteService.db.bo.DSR_1;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.DSR_2;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU0_1;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU0_2;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU1_1;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU1_2;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU1_3;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU1_4;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU2_1;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU2_2;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU2_3;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU2_4;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU3_1;
import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU3_2;

public class ConstBusiness {
	
	//客户层速率对应表
	public static Map<Integer,Integer> rateMap = new HashMap<Integer,Integer>();
	
	public static Map<String,Class<? extends ODU>> odumap = new HashMap<String,Class<? extends ODU>>();
	
	public static Set<String> passedProtectedCardSet = new HashSet<String>();
	
	public static List<Integer> odu0rateList = new ArrayList<Integer>();
	public static List<Integer> odu1rateList = new ArrayList<Integer>();
	public static List<Integer> odu2rateList = new ArrayList<Integer>();
	public static List<Integer> odu3rateList = new ArrayList<Integer>();
	public static List<Integer> odu4rateList = new ArrayList<Integer>();
	public static List<Integer> ochrateList = new ArrayList<Integer>();
	
	
	
	static{
		
		rateMap.put(50, 0);
		rateMap.put(65, 0);
		rateMap.put(73, 0);
		rateMap.put(74, 0);
		rateMap.put(75, 0);
		rateMap.put(87, 0);
		rateMap.put(97, 0);
		rateMap.put(89, 0);
		rateMap.put(8005, 0);
		rateMap.put(8021, 0);
		rateMap.put(8022, 0);
		rateMap.put(76, 1);
		rateMap.put(77, 2);
		rateMap.put(113, 2);
		rateMap.put(8008, 2);
		rateMap.put(8009, 2);
		rateMap.put(78, 3);
		rateMap.put(115, 3);
		rateMap.put(8043, 4);
		 
		
		odu0rateList.add(8031); // LR_OCH_Data_Unit_0
		odu1rateList.add(104); // LR_OCH_Data_Unit_1
		odu2rateList.add(105); // LR_OCH_Data_Unit_2
		odu3rateList.add(106); // LR_OCH_Data_Unit_3
		odu4rateList.add(8041); 
		ochrateList.add(40); 
		
		odumap.put("/dsr=1", DSR_1.class);
	    odumap.put("/dsr=2", DSR_2.class);
	    odumap.put("/odu0=1", ODU0_1.class);
	    odumap.put("/odu0=2", ODU0_2.class);
	    odumap.put("/odu1=1", ODU1_1.class);
	    odumap.put("/odu1=2", ODU1_2.class);
	    odumap.put("/odu1=3", ODU1_3.class);
	    odumap.put("/odu1=4", ODU1_4.class);
	    odumap.put("/odu2=1", ODU2_1.class);
	    odumap.put("/och=1/odu3=1/odu2=1", ODU2_1.class);
	    odumap.put("/odu2=2", ODU2_2.class);
	    odumap.put("/och=1/odu3=1/odu2=2", ODU2_2.class);
	    odumap.put("/odu2=3", ODU2_3.class);
	    odumap.put("/och=1/odu3=1/odu2=3", ODU2_3.class);
	    odumap.put("/odu2=4", ODU2_4.class);
	    odumap.put("/och=1/odu3=1/odu2=4", ODU2_4.class);
	    odumap.put("/odu3=1", ODU3_1.class);
	    odumap.put("/och=1/otu3=1", ODU3_1.class);
	    odumap.put("/odu3=2", ODU3_2.class);
	    odumap.put("/och=1/otu3=2", ODU3_2.class);
	    
	    
	    passedProtectedCardSet.add("DCP");
	    
	}

}
