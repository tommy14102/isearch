package com.zznode.opentnms.isearch.otnRouteService.db.bo;

import java.util.ArrayList;
import java.util.List;

public class ODU4 extends ODU{

	private List<DSR> dsrlist = new ArrayList<DSR>() ;
	
	private List<ODU0> odu0list = new ArrayList<ODU0>() ;
	
	private List<ODU1> odu1list = new ArrayList<ODU1>() ;
	
	private List<ODU2> odu2list = new ArrayList<ODU2>() ;
	
	private List<ODU3> odu3list = new ArrayList<ODU3>() ;
	
	
	public String getFreeODU(){
		
		int odu2count = 0 ; 
		for (int i = 0; i < dsrlist.size(); i++) {
			DSR dsr = dsrlist.get(i);
			int rate = dsr.getRate().intValue();
			if( rate ==8043 ){
				odu2count = 10 ;
			}
		}
		
		for (int i = 0; i < odu2list.size(); i++) {
			odu2count ++ ;
		}
		for (int i = 0; i < odu3list.size(); i++) {
			odu2count +=4 ;
		}
		
		if( odu2count==10){
			return "";
		} 
		
		String rtnStr =  "/odu2=" ;
		for (int i = 1; i < odu2count; i++) {
			rtnStr +=  ","+i ;
		}
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
