package com.zznode.opentnms.isearch.otnRouteService.db.bo;

import java.util.ArrayList;
import java.util.List;

public class ODU2 extends ODU{

	private List<DSR> dsrlist = new ArrayList<DSR>() ;
	
	private List<ODU0> odu0list = new ArrayList<ODU0>() ;
	
	private List<ODU1> odu1list = new ArrayList<ODU1>() ;
	
	private ODU1_1 odu1_1 = new ODU1_1();
	private ODU1_2 odu1_2 = new ODU1_2();
	private ODU1_3 odu1_3 = new ODU1_3();
	private ODU1_4 odu1_4 = new ODU1_4();
	

	public String getFreeODU(){
		
		for (int i = 0; i < dsrlist.size(); i++) {
			DSR dsr = dsrlist.get(i);
			int rate = dsr.getRate().intValue();
			if( rate ==77 || rate ==113 || rate ==8008 || rate ==8009){
				odu1_1 = null ;
				odu1_2 = null ;
				odu1_3 = null ;
				odu1_4 = null ;
			}
		}
		
		for (int i = 0; i < odu1list.size(); i++) {
			ODU1 odu1 = odu1list.get(i);
			if(odu1 instanceof ODU1_1){
				odu1_1 = null;
			}
			if(odu1 instanceof ODU1_2){
				odu1_2 = null;
			}
			if(odu1 instanceof ODU1_3){
				odu1_3 = null;
			}
			if(odu1 instanceof ODU1_4){
				odu1_4 = null;
			}
		}
		
		if( odu1_1==null && odu1_2 ==null && odu1_3 ==null && odu1_4 ==null){
			return "";
		} 
		
		String rtnStr =  "/odu1=" + odu1_1==null?"":",1" +  odu1_2 ==null?"":",2" +  odu1_3 ==null?"":",3" +  odu1_4 ==null?"":",4" ;
		return rtnStr.substring(1);
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
