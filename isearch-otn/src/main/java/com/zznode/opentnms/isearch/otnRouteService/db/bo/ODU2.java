package com.zznode.opentnms.isearch.otnRouteService.db.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zznode.opentnms.isearch.otnRouteService.consts.ConstBusiness;

public class ODU2 extends ODU{

	private static final long serialVersionUID = 6415678903693768402L;

	
	private List<DSR> dsrlist = new ArrayList<DSR>() ;
	
	private List<ODU0> odu0list = new ArrayList<ODU0>() ;
	
	private List<ODU1> odu1list = new ArrayList<ODU1>() ;
	

	public String getFreeODU(Integer rate_i){
		
		int ratelevel = ConstBusiness.rateMap.get(rate_i).intValue();
		if( ratelevel == 0 ){
			
			int[] odu0Array = new int[9];
			Arrays.fill(odu0Array, 1);  //一个odu2共有8个odu0
			
			//排除odu0的占用资源
			for (int i = 0; i < odu0list.size(); i++) {
				ODU0 odu0 = odu0list.get(i);
				odu0Array[odu0.getIndex()] = 0 ;
			}
			//排除odu1的占用资源
			for (int i = 0; i < odu1list.size(); i++) {
				ODU1 odu1 = odu1list.get(i);
				odu0Array[ 1 + 2* (odu1.getIndex()-1) ] = 0 ;
				odu0Array[ 1 + 2* (odu1.getIndex()-1)+1] = 0 ;
			}
			//排除dsr占用的资源
			for (int i = 0; i < dsrlist.size(); i++) {
				DSR dsr = dsrlist.get(i);
				int rate = dsr.getRate().intValue();
				if( rate==87 || rate==75 || rate==89 ){
					odu0Array[dsr.getIndex()] = 0 ;
				}
				else if( rate==76 ){
					odu0Array[ 1 + 2* (dsr.getIndex()-1) ] = 0 ;
					odu0Array[ 1 + 2* (dsr.getIndex()-1)+1] = 0 ;
				}else if( rate ==77 || rate ==113 || rate ==8008 || rate ==8009){
					Arrays.fill(odu0Array, 0); 
				}
				else{
					throw new RuntimeException("计算空闲odu资源异常");
				}
			}
			
			String freeodu0Str = ""; 
			for (int i = 1; i < odu0Array.length; i++) {
				if( odu0Array[i] == 1){
					freeodu0Str += ","+i ; 
				}
			}
			if( freeodu0Str.equals("") ){
				return "";
			} 
			
			String rtnStr = "odu2="+index+ "/odu0=" + freeodu0Str.substring(1);
			
			return rtnStr;
			
		}
		else if( ratelevel == 1 ){
			
			int[] odu0Array = new int[5];
			Arrays.fill(odu0Array, 1);  //一个odu2共有4个odu1
			
			//排除odu0的占用资源
			for (int i = 0; i < odu0list.size(); i++) {
				ODU0 odu0 = odu0list.get(i);
				int odu0index = odu0.getIndex();
				odu0Array[(odu0index+1)/2] = 0 ;
			}
			//排除odu1的占用资源
			for (int i = 0; i < odu1list.size(); i++) {
				ODU1 odu1 = odu1list.get(i);
				int odu1index = odu1.getIndex();
				odu0Array[ odu1index ] = 0 ;
			}
			//排除dsr占用的资源
			for (int i = 0; i < dsrlist.size(); i++) {
				DSR dsr = dsrlist.get(i);
				int rate = dsr.getRate().intValue();
				int dsrindex = dsr.getIndex();
				if( rate==87 || rate==75 || rate==89 ){
					odu0Array[(dsrindex+1)/2] = 0 ;
				}
				else if( rate==76 ){
					odu0Array[ dsrindex ] = 0 ;
				}
				else if( rate ==77 || rate ==113 || rate ==8008 || rate ==8009){
					Arrays.fill(odu0Array, 0); 
				}
				else{
					throw new RuntimeException("计算空闲odu资源异常");
				}
			}
			
			String freeodu0Str = ""; 
			for (int i = 1; i < odu0Array.length; i++) {
				if( odu0Array[i] == 1){
					freeodu0Str += ","+i ; 
				}
			}
			if( freeodu0Str.equals("") ){
				return "";
			} 
			
			String rtnStr = "odu2="+index+ "/odu1=" + freeodu0Str.substring(1);
			
			return rtnStr;
			
		}
		else if( ratelevel == 2 ){
			//如果有占用，那么就不足一个odu1，返回空
			if( odu0list.size() == 0 || odu1list.size() == 0 || dsrlist.size() == 0 ){
				return "/odu1="+index ;
			}
		}
		return "";
	}


	public List<DSR> getDsrlist() {
		return dsrlist;
	}

	public void setDsrlist(List<DSR> dsrlist) {
		this.dsrlist = dsrlist;
	}

	public List<ODU0> getOdu0list() {
		return odu0list;
	}

	public void setOdu0list(List<ODU0> odu0list) {
		this.odu0list = odu0list;
	}

	public List<ODU1> getOdu1list() {
		return odu1list;
	}

	public void setOdu1list(List<ODU1> odu1list) {
		this.odu1list = odu1list;
	}

	
	
	
}
