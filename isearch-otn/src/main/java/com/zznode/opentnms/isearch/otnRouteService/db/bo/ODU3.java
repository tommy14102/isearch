package com.zznode.opentnms.isearch.otnRouteService.db.bo;

import java.util.ArrayList;
import java.util.List;

public class ODU3 extends ODU{

	private List<DSR> dsrlist = new ArrayList<DSR>() ;
	
	private List<ODU0> odu0list = new ArrayList<ODU0>() ;
	
	private List<ODU1> odu1list = new ArrayList<ODU1>() ;
	
	private List<ODU2> odu2list = new ArrayList<ODU2>() ;

	
	private ODU2_1 odu2_1 = new ODU2_1();
	private ODU2_2 odu2_2 = new ODU2_2();
	private ODU2_3 odu2_3 = new ODU2_3();
	private ODU2_4 odu2_4 = new ODU2_4();
	
	public String getFreeODU(){
		
		for (int i = 0; i < dsrlist.size(); i++) {
			DSR dsr = dsrlist.get(i);
			int rate = dsr.getRate().intValue();
			if( rate ==78 || rate ==115 ){
				odu2_1 = null ;
				odu2_2 = null ;
				odu2_3 = null ;
				odu2_4 = null ;
			}
		}
		
		for (int i = 0; i < odu2list.size(); i++) {
			ODU2 odu2 = odu2list.get(i);
			if( odu2 instanceof ODU2_1){
				odu2_1 = null;
			}
			if( odu2 instanceof ODU2_2){
				odu2_2 = null;
			}
			if( odu2 instanceof ODU2_3){
				odu2_3 = null;
			}
			if( odu2 instanceof ODU2_4){
				odu2_4 = null;
			}
		}
		
		if( odu2_1==null && odu2_2 ==null && odu2_3 ==null && odu2_4 ==null){
			return "";
		} 
		
		String rtnStr =  "/odu2=" + odu2_1==null?"":",1" +  odu2_2 ==null?"":",2" +  odu2_3 ==null?"":",3" +  odu2_4 ==null?"":",4" ;
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

	
	
	
}
