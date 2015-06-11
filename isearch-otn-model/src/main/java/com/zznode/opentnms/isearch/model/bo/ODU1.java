package com.zznode.opentnms.isearch.model.bo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODU1 extends ODU{

	private static final long serialVersionUID = 91213596044459451L;

	private static final Logger logger = LoggerFactory.getLogger(ODU1.class);   
	private List<ODU0> odu0list = new ArrayList<ODU0>() ;
	
	private List<DSR> dsrlist = new ArrayList<DSR>() ;
	
	private ODU0 odu0_1 = new ODU0();
	private ODU0 odu0_2 = new ODU0();
	
	public String getFreeODU(Integer rate_i){
		
		int ratelevel = ConstBusiness.rateMap.get(rate_i).intValue();
		if( ratelevel == 0 ){
			//排除odu0的占用资源
			for (int i = 0; i < odu0list.size(); i++) {
				ODU0 odu0 = odu0list.get(i);
				if( odu0.getIndex().intValue()== 1){
					odu0_1 = null ;
				}
				else if( odu0.getIndex().intValue()== 2){
					odu0_2 = null ;
				}
				else{
					throw new RuntimeException("计算空闲odu资源异常");
				}
			}
			//排除客户层的占用资源
			for (int i = 0; i < dsrlist.size(); i++) {
				DSR dsr = dsrlist.get(i);
				int rates = dsr.getRate().intValue();
				if( rates==87 || rates==75 || rates==89 ){
					if( dsr.getIndex().intValue()==1 ){
						odu0_1 = null ;
					}
					if( dsr.getIndex().intValue()==2 ){
						odu0_2 = null ;
					}
				}
				else if( rates==76 ){
					odu0_1 = null ;
					odu0_2 = null ;
				}
				else{
					//直接占满
					logger.error("ODU1, filling max by rate:" + this.getSncobjectid() + ", innnersnc:" + dsr.getSncobjectid());
					//throw new RuntimeException("计算空闲odu资源异常 ");
					odu0_1 = null ;
					odu0_2 = null ;
				}
			}
			if( odu0_1==null && odu0_2 ==null){
				return "";
			} 
			
			String freeodu0Str = (odu0_1==null?"":",1" ) +  (odu0_2 ==null?"":",2");
			String rtnStr = "odu1="+index+ "/odu0=" + freeodu0Str.substring(1);
			
			return rtnStr;
			
		}
		else if( ratelevel == 1 ){
			//如果有占用，那么就不足一个odu1，返回空
			if( odu0list.size() == 0 ||dsrlist.size() == 0 ){
				return "/odu1="+index ;
			}
		}
		
		return "";
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
