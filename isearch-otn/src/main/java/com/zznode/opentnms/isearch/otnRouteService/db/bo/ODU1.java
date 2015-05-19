package com.zznode.opentnms.isearch.otnRouteService.db.bo;

import java.util.ArrayList;
import java.util.List;

public class ODU1 extends ODU{

	private static final long serialVersionUID = 91213596044459451L;

	private List<ODU0> odu0list = new ArrayList<ODU0>() ;
	
	private List<DSR> dsrlist = new ArrayList<DSR>() ;
	
	private ODU0_1 odu0_1 = new ODU0_1();
	private ODU0_2 odu0_2 = new ODU0_2();
	
	public String getFreeODU(){
		
		for (int i = 0; i < odu0list.size(); i++) {
			ODU0 odu0 = odu0list.get(i);
			if(odu0 instanceof ODU0_1){
				odu0_1 = null ;
			}
			if(odu0 instanceof ODU0_2){
				odu0_2 = null ;
			}
		}
		
		for (int i = 0; i < dsrlist.size(); i++) {
			DSR dsr = dsrlist.get(i);
			int rate = dsr.getRate().intValue();
			if( rate==87 || rate==75 || rate==89 ){
				if( dsr instanceof DSR_1){
					odu0_1 = null ;
				}
				if( dsr instanceof DSR_2){
					odu0_2 = null ;
				}
			}
			if( rate==76 ){
				odu0_1 = null ;
				odu0_2 = null ;
			}
		}
		
		if( odu0_1==null && odu0_2 ==null){
			return "";
		} 
		
		
		String rtnStr =  "/odu0=" + odu0_1==null?"":",1" +  odu0_2 ==null?"":",2";
		
		return rtnStr.substring(1);
		
		
	}
	
	
	public List<ODU0> getOdu0list() {
		return odu0list;
	}

	public void setOdu0list(List<ODU0> odu0list) {
		this.odu0list = odu0list;
	}

	public List<DSR> getDsrlist() {
		return dsrlist;
	}

	public void setDsrlist(List<DSR> dsrlist) {
		this.dsrlist = dsrlist;
	}

	
}
