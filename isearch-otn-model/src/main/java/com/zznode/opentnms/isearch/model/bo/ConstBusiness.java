package com.zznode.opentnms.isearch.model.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstBusiness {
	
	//客户层速率对应表
	public static Map<Integer,Integer> rateMap = new HashMap<Integer,Integer>();
	
	//客户层速率对应描述
	public static Map<Integer,String> rateDescMap = new HashMap<Integer,String>();
	
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
		rateMap.put(76, 1); //2.5G
		rateMap.put(77, 2); //10G
		rateMap.put(113, 2);
		rateMap.put(8008, 2);
		rateMap.put(8009, 2);
		rateMap.put(78, 3);//40G
		rateMap.put(115, 3);
		rateMap.put(8043, 4);//100G
		
		rateDescMap.put(50, "GE");
		rateDescMap.put(65, "GE");
		rateDescMap.put(73, "155M");
		rateDescMap.put(74, "622M");
		rateDescMap.put(75, "1.25G");
		rateDescMap.put(87, "GE");
		rateDescMap.put(97, "FE");
		rateDescMap.put(89, "1.25G");
		rateDescMap.put(8005, "");
		rateDescMap.put(8021, "");
		rateDescMap.put(8022, "");
		rateDescMap.put(76, "2.5G"); //2.5G
		rateDescMap.put(77, "10G"); //10G
		rateDescMap.put(113, "10GE");
		rateDescMap.put(8008,"10GE");
		rateDescMap.put(8009,"10GE");
		rateDescMap.put(78, "40G");//40G
		rateDescMap.put(115, "40GE");
		rateDescMap.put(8043, "100G");//100G

		 
		
		odu0rateList.add(8031); // LR_OCH_Data_Unit_0
		odu1rateList.add(104); // LR_OCH_Data_Unit_1
		odu1rateList.add(107); // LR_OCH_Data_Unit_1
		odu2rateList.add(105); // LR_OCH_Data_Unit_2
		odu2rateList.add(108); // LR_OCH_Data_Unit_2
		odu3rateList.add(106); // LR_OCH_Data_Unit_3
		odu3rateList.add(109); // LR_OCH_Data_Unit_3
		odu4rateList.add(8041); 
		ochrateList.add(40); 
		
		/**
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
	    */
	    
	    passedProtectedCardSet.add("DCP");
	    
	}
	
	public static ODU getOduByCtp( String ctp ){
		
		Pattern p = null ; 
		Matcher m = null ;
		p = Pattern.compile(".*/dsr=(\\d*)", Pattern.CASE_INSENSITIVE);
		m = p.matcher(ctp);
		if( m.matches()  ){
			DSR dsr = new DSR();
			dsr.setIndex(Integer.valueOf(m.group(1)));
			return dsr ; 
		}
		p = Pattern.compile(".*/odu0=(\\d*)", Pattern.CASE_INSENSITIVE);
		m = p.matcher(ctp);
		if( m.matches()  ){
			ODU0 odu0 = new ODU0();
			odu0.setIndex(Integer.valueOf(m.group(1)));
			return odu0 ; 
		}
		p = Pattern.compile(".*/odu1=(\\d*)", Pattern.CASE_INSENSITIVE);
		m = p.matcher(ctp);
		if( m.matches()   ){
			ODU1 odu1 = new ODU1();
			//特殊处理/odu1=1015这种情况
			int index = Integer.valueOf(m.group(1)).intValue();
			if( index > 1000 ){
				index = index - 1000;
			}
			odu1.setIndex( index );
			return odu1 ; 
		}
		p = Pattern.compile(".*/o[dt]u2=(\\d*)", Pattern.CASE_INSENSITIVE);
		m = p.matcher(ctp);
		if( m.matches()   ){
			ODU2 odu2 = new ODU2();
			odu2.setIndex(Integer.valueOf(m.group(1)));
			return odu2 ; 
		}
		p = Pattern.compile(".*/o[dt]u3=(\\d*)", Pattern.CASE_INSENSITIVE);
		m = p.matcher(ctp);
		if( m.matches()  ){
			ODU3 odu3 = new ODU3();
			odu3.setIndex(Integer.valueOf(m.group(1)));
			return odu3 ; 
		}
		
		return null ;
	}

	public static void main(String[] args) {
		Pattern p = Pattern.compile(".*/o[dt]u2=(\\d*)", Pattern.CASE_INSENSITIVE);
		String ww = "/odu2=2";
		Matcher m = p.matcher(ww);
		if( m.matches()  ){
			System.out.println( m.group(1) );
		}
	}

}
