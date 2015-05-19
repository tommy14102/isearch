package com.zznode.opentnms.isearch.otnRouteService.db.bo;

import java.util.ArrayList;
import java.util.List;

public class OCH extends ODU{

	private List<DSR> dsrlist = new ArrayList<DSR>() ;
	
	private List<ODU0> odu0list = new ArrayList<ODU0>();
	
	private List<ODU1> odu1list = new ArrayList<ODU1>();
	
	private List<ODU2> odu2list = new ArrayList<ODU2>();
	
	private List<ODU3> odu3list = new ArrayList<ODU3>();
	
	private List<ODU4> odu4list = new ArrayList<ODU4>();
	
	//och只能接一个odu
	public String getFreeODU(){
		
		if( dsrlist.size()==0 && dsrlist.size()==0 && dsrlist.size()==0 && dsrlist.size()==0 && dsrlist.size()==0 && dsrlist.size()==0 ){
			return "/och";
		}
			
		return "";
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

	public List<ODU4> getOdu4list() {
		return odu4list;
	}

	public void setOdu4list(List<ODU4> odu4list) {
		this.odu4list = odu4list;
	}

	public List<DSR> getDsrlist() {
		return dsrlist;
	}

	public void setDsrlist(List<DSR> dsrlist) {
		this.dsrlist = dsrlist;
	}

	 
	
	
}
