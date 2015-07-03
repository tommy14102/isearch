package com.zznode.opentnms.isearch.model.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODU4 extends ODU{

	private static final long serialVersionUID = 4935613434708981594L;
	private static final Logger logger = LoggerFactory.getLogger(ODU4.class);   

	private List<DSR> dsrlist = new ArrayList<DSR>() ;
	
	private List<ODU0> odu0list = new ArrayList<ODU0>() ;
	
	private List<ODU1> odu1list = new ArrayList<ODU1>() ;
	
	private List<ODU2> odu2list = new ArrayList<ODU2>() ;
	
	private List<ODU3> odu3list = new ArrayList<ODU3>() ;
	
	
	public String getFreeODU(Integer rate_i){
		
		Integer ratelevel = ConstBusiness.rateMap.get(rate_i);
		Integer odulevel = ConstBusiness.odukMap.get(rate_i);
		
		if( (ratelevel!=null && ratelevel.intValue() == 0)||(odulevel!=null && odulevel.intValue() == 1) ){
			
			int[] odu0Array = new int[2*32+2*8 + 1];
			Arrays.fill(odu0Array, 1);  //一个odu4共有2*32+2*8个odu0
			
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
			//排除odu2的占用资源
			for (int i = 0; i < odu2list.size(); i++) {
				ODU2 odu2 = odu2list.get(i);
				odu0Array[ 1 + 8* (odu2.getIndex()-1) ] = 0 ;
				odu0Array[ 1 + 8* (odu2.getIndex()-1)+1] = 0 ;
				odu0Array[ 1 + 8* (odu2.getIndex()-1)+2] = 0 ;
				odu0Array[ 1 + 8* (odu2.getIndex()-1)+3] = 0 ;
				odu0Array[ 1 + 8* (odu2.getIndex()-1)+4] = 0 ;
				odu0Array[ 1 + 8* (odu2.getIndex()-1)+5] = 0 ;
				odu0Array[ 1 + 8* (odu2.getIndex()-1)+6] = 0 ;
				odu0Array[ 1 + 8* (odu2.getIndex()-1)+7] = 0 ;
			}
			//排除odu3的占用资源
			for (int i = 0; i < odu3list.size(); i++) {
				ODU3 odu3 = odu3list.get(i);
				int startindex = 1+ 32* (odu3.getIndex()-1);
				int endindex = startindex + 31 ; 
				for ( ; startindex <= endindex; startindex++) {
					odu0Array[startindex] = 0 ;
				}
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
				}
				else if( rate ==77 || rate ==113 || rate ==8008 || rate ==8009){
					odu0Array[ 1 + 8* (dsr.getIndex()-1) ] = 0 ;
					odu0Array[ 1 + 8* (dsr.getIndex()-1)+1] = 0 ;
					odu0Array[ 1 + 8* (dsr.getIndex()-1)+2] = 0 ;
					odu0Array[ 1 + 8* (dsr.getIndex()-1)+3] = 0 ;
					odu0Array[ 1 + 8* (dsr.getIndex()-1)+4] = 0 ;
					odu0Array[ 1 + 8* (dsr.getIndex()-1)+5] = 0 ;
					odu0Array[ 1 + 8* (dsr.getIndex()-1)+6] = 0 ;
					odu0Array[ 1 + 8* (dsr.getIndex()-1)+7] = 0 ;
				}
				else if( rate ==78 || rate ==115 ){
					int startindex = 1+ 32* (dsr.getIndex()-1);
					int endindex = startindex + 31 ; 
					for ( ; startindex <= endindex; startindex++) {
						odu0Array[startindex] = 0 ;
					}
				}
				else if( rate ==8043 ){
					Arrays.fill(odu0Array, 0); 
				}
				else{
					//直接占满
					logger.error("ODU4, filling max by rate:" + this.getSncobjectid() + ", innnersnc:" + dsr.getSncobjectid());
					//throw new RuntimeException("计算空闲odu资源异常 ");
					Arrays.fill(odu0Array, 0); 
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
			
			String rtnStr = "odu4="+index+ "/odu0=" + freeodu0Str.substring(1);
			
			return rtnStr;
			
		}
		else if( (ratelevel!=null && ratelevel.intValue() == 2)||(odulevel!=null && odulevel.intValue() == 2) ){
			
			int[] odu0Array = new int[2*16+2*4 + 1];
			Arrays.fill(odu0Array, 1);  //一个odu3共有2*16+2*4个odu1
			
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
			//排除odu2的占用资源
			for (int i = 0; i < odu2list.size(); i++) {
				ODU2 odu2 = odu2list.get(i);
				int odu2index = odu2.getIndex();
				odu0Array[ 1 + 4* (odu2index-1) ] = 0 ;
				odu0Array[ 1 + 4* (odu2index-1)+1] = 0 ;
				odu0Array[ 1 + 4* (odu2index-1)+2] = 0 ;
				odu0Array[ 1 + 4* (odu2index-1)+3] = 0 ;
			}
			//排除odu3的占用资源，odu3=odu1*16
			for (int i = 0; i < odu3list.size(); i++) {
				ODU3 odu3 = odu3list.get(i);
				int startindex = 1+ 16* (odu3.getIndex()-1);
				int endindex = startindex + 15 ; 
				for ( ; startindex <= endindex; startindex++) {
					odu0Array[startindex] = 0 ;
				}
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
					odu0Array[ 1 + 4* (dsrindex-1) ] = 0 ;
					odu0Array[ 1 + 4* (dsrindex-1)+1] = 0 ;
					odu0Array[ 1 + 4* (dsrindex-1)+2] = 0 ;
					odu0Array[ 1 + 4* (dsrindex-1)+3] = 0 ;
				}
				else if( rate ==78 || rate ==115 ){
					int startindex = 1+ 16* (dsrindex-1);
					int endindex = startindex + 15 ; 
					for ( ; startindex <= endindex; startindex++) {
						odu0Array[startindex] = 0 ;
					} 
				}
				else if( rate ==8043 ){
					Arrays.fill(odu0Array, 0); 
				}
				else{
					//直接占满
					logger.error("ODU4, filling max by rate:" + this.getSncobjectid() + ", innnersnc:" + dsr.getSncobjectid());
					//throw new RuntimeException("计算空闲odu资源异常 ");
					Arrays.fill(odu0Array, 0); 
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
			
			String rtnStr = "odu3="+index+ "/odu1=" + freeodu0Str.substring(1);
			
			return rtnStr;
			
		}
		else if( (ratelevel!=null && ratelevel.intValue() == 3)||(odulevel!=null && odulevel.intValue() == 3) ){
			
			int[] odu0Array = new int[11];
			Arrays.fill(odu0Array, 1);  //一个odu4共有10个odu2
			
			//排除odu0的占用资源
			for (int i = 0; i < odu0list.size(); i++) {
				ODU0 odu0 = odu0list.get(i);
				int odu0index = odu0.getIndex();
				odu0Array[(odu0index+7)/8] = 0 ;
			}
			//排除odu1的占用资源
			for (int i = 0; i < odu1list.size(); i++) {
				ODU1 odu1 = odu1list.get(i);
				int odu1index = odu1.getIndex();
				odu0Array[ (odu1index+3)/4 ] = 0 ;
			}
			//排除odu2的占用资源
			for (int i = 0; i < odu2list.size(); i++) {
				ODU2 odu2 = odu2list.get(i);
				int odu2index = odu2.getIndex();
				odu0Array[ odu2index  ] = 0 ;
			}
			//排除odu3的占用资源
			for (int i = 0; i < odu3list.size(); i++) {
				ODU3 odu3 = odu3list.get(i);
				int odu3index = odu3.getIndex();
				odu0Array[ 1 + 4* (odu3index-1) ] = 0 ;
				odu0Array[ 1 + 4* (odu3index-1)+1] = 0 ;
				odu0Array[ 1 + 4* (odu3index-1)+2] = 0 ;
				odu0Array[ 1 + 4* (odu3index-1)+3] = 0 ;
			}
			//排除dsr占用的资源
			for (int i = 0; i < dsrlist.size(); i++) {
				DSR dsr = dsrlist.get(i);
				int rate = dsr.getRate().intValue();
				int dsrindex = dsr.getIndex();
				if( rate==87 || rate==75 || rate==89 ){
					odu0Array[(dsrindex+7)/8] = 0 ;
				}
				else if( rate==76 ){
					odu0Array[(dsrindex+3)/4] = 0 ;
				}
				else if( rate ==77 || rate ==113 || rate ==8008 || rate ==8009){
					odu0Array[ dsrindex  ] = 0 ;
				}
				else if( rate ==78 || rate ==115 ){
					odu0Array[ 1 + 4* (dsrindex-1) ] = 0 ;
					odu0Array[ 1 + 4* (dsrindex-1)+1] = 0 ;
					odu0Array[ 1 + 4* (dsrindex-1)+2] = 0 ;
					odu0Array[ 1 + 4* (dsrindex-1)+3] = 0 ;
				}
				else if( rate ==8043 ){
					Arrays.fill(odu0Array, 0); 
				}
				else{
					//直接占满
					logger.error("ODU4, filling max by rate:" + this.getSncobjectid() + ", innnersnc:" + dsr.getSncobjectid());
					//throw new RuntimeException("计算空闲odu资源异常 ");
					Arrays.fill(odu0Array, 0); 
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
			
			String rtnStr = "odu4="+index+ "/odu2=" + freeodu0Str.substring(1);
			
			return rtnStr;
			
		}
		else if( (ratelevel!=null && ratelevel.intValue() == 4)||(odulevel!=null && odulevel.intValue() == 4) ){
			
			int[] odu0Array = new int[4];
			Arrays.fill(odu0Array, 1);  //一个odu4共有2.5个odu3
			
			//排除odu0的占用资源
			for (int i = 0; i < odu0list.size(); i++) {
				ODU0 odu0 = odu0list.get(i);
				int odu0index = odu0.getIndex();
				odu0Array[(odu0index+31)/32] = 0 ;
			}
			//排除odu1的占用资源
			for (int i = 0; i < odu1list.size(); i++) {
				ODU1 odu1 = odu1list.get(i);
				int odu1index = odu1.getIndex();
				odu0Array[ (odu1index+15)/16 ] = 0 ;
			}
			//排除odu2的占用资源
			for (int i = 0; i < odu2list.size(); i++) {
				ODU2 odu2 = odu2list.get(i);
				int odu2index = odu2.getIndex();
				odu0Array[ (odu2index+3)/4  ] = 0 ;
			}
			//排除odu3的占用资源
			for (int i = 0; i < odu3list.size(); i++) {
				ODU3 odu3 = odu3list.get(i);
				int odu3index = odu3.getIndex();
				odu0Array[ odu3index ] = 0 ;
			}
			//排除dsr占用的资源
			for (int i = 0; i < dsrlist.size(); i++) {
				DSR dsr = dsrlist.get(i);
				int rate = dsr.getRate().intValue();
				int dsrindex = dsr.getIndex();
				if( rate==87 || rate==75 || rate==89 ){
					odu0Array[(dsrindex+31)/32] = 0 ;
				}
				else if( rate==76 ){
					odu0Array[ (dsrindex+15)/16 ] = 0 ;
				}
				else if( rate ==77 || rate ==113 || rate ==8008 || rate ==8009){
					odu0Array[ (dsrindex+3)/4  ] = 0 ;
				}
				else if( rate ==78 || rate ==115 ){
					odu0Array[ dsrindex ] = 0 ;
				}
				else if( rate ==8043 ){
					Arrays.fill(odu0Array, 0); 
				}
				else{
					//直接占满
					logger.error("ODU4, filling max by rate:" + this.getSncobjectid() + ", innnersnc:" + dsr.getSncobjectid());
					//throw new RuntimeException("计算空闲odu资源异常 ");
					Arrays.fill(odu0Array, 0); 
				}
			}
			
			String freeodu0Str = ""; 
			for (int i = 1; i < odu0Array.length-1; i++) {
				if( odu0Array[i] == 1){
					freeodu0Str += ","+i ; 
				}
			}
			if( freeodu0Str.equals("") ){
				return "";
			} 
			
			String rtnStr = "odu4="+index+ "/odu3=" + freeodu0Str.substring(1);
			
			return rtnStr;
			
		}
		else if( (ratelevel!=null && ratelevel.intValue() == 5)||(odulevel!=null && odulevel.intValue() == 5) ){
			//如果有占用，那么就不足一个odu1，返回空
			if( odu0list.size() == 0 && odu1list.size() == 0 && odu2list.size() == 0 && odu3list.size() == 0 && dsrlist.size() == 0 ){
				return "/odu4="+index ;
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

	public List<ODU2> getOdu2list() {
		return odu2list;
	}

	public void setOdu2list(List<ODU2> odu2list) {
		this.odu2list = odu2list;
	}

	public List<ODU3> getOdu3list() {
		return odu3list;
	}

	public void setOdu3list(List<ODU3> odu3list) {
		this.odu3list = odu3list;
	}
	
	
	
	
	
}
