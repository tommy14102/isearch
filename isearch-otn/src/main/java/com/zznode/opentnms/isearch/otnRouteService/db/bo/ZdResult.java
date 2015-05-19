package com.zznode.opentnms.isearch.otnRouteService.db.bo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class ZdResult implements Serializable{

	private static final long serialVersionUID = -4290935550867512223L;
	
	private String rate ; 
	private String ratedesc ;
	private String sncid ; 
	private String sncname ; 
	private Boolean isused;
	private String cucirtid ;
	private ODU odu ; 
	private String layerdesc ;
	
	private Map<String,LinkedList<ZdResultSingle>> zdmap = new LinkedHashMap<String,LinkedList<ZdResultSingle>>();

	public String getODUinfo(){
		
		if( odu instanceof DSR){
			return odu.getClass().getSimpleName();
		}
		
		if( odu instanceof ODU0){
			return odu.getClass().getSimpleName();
		}
		
		if( odu instanceof ODU1){
			ODU1 odu1 = (ODU1)odu;
			return odu1.getFreeODU(); 
		}
		if( odu instanceof ODU2){
			ODU2 odu2 = (ODU2)odu;
			return odu2.getFreeODU(); 
		}
		if( odu instanceof ODU3){
			ODU3 odu3 = (ODU3)odu;
			return odu3.getFreeODU(); 
		}
		if( odu instanceof ODU4){
			ODU4 odu4 = (ODU4)odu;
			return odu4.getFreeODU(); 
		}
		if( odu instanceof OCH){
			OCH och = (OCH)odu;
			return och.getFreeODU(); 
		}
		return "";
	}
	
	
	
	public Map<String, LinkedList<ZdResultSingle>> getZdmap() {
		return zdmap;
	}

	public void setZdmap(Map<String, LinkedList<ZdResultSingle>> zdmap) {
		this.zdmap = zdmap;
	}
	
	
	public String getLayerdesc() {
		return layerdesc;
	}

	public void setLayerdesc(String layerdesc) {
		this.layerdesc = layerdesc;
	}

	public ODU getOdu() {
		return odu;
	}

	public void setOdu(ODU odu) {
		this.odu = odu;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getRatedesc() {
		return ratedesc;
	}

	public void setRatedesc(String ratedesc) {
		this.ratedesc = ratedesc;
	}

	public String getSncid() {
		return sncid;
	}

	public void setSncid(String sncid) {
		this.sncid = sncid;
	}

	public String getSncname() {
		return sncname;
	}

	public void setSncname(String sncname) {
		this.sncname = sncname;
	}

	public Boolean getIsused() {
		return isused;
	}

	public void setIsused(Boolean isused) {
		this.isused = isused;
	}

	public String getCucirtid() {
		return cucirtid;
	}

	public void setCucirtid(String cucirtid) {
		this.cucirtid = cucirtid;
	}
	

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(" ZdResult [rate:" + rate + ", ratedesc:" + ratedesc + "]").append("\r\n");
		sb.append(" ZdResult [sncid:" + sncid + ", sncname:" + sncname + "]").append("\r\n");
		sb.append(" ZdResult [oduType:" + odu.getClass().getSimpleName() + "]").append("\r\n");
		if( odu instanceof DSR){
			DSR dsr = (DSR)odu;
			sb.append(" ZdResult [DSR:"+ dsr.getClass().getSimpleName()  + ",DSRrate:").append(dsr.getRate()+ ",DSRsncid:").append(dsr.getSncobjectid()).append("]").append("\r\n");
		}
		
		if( odu instanceof ODU0){
			ODU0 odu0 = (ODU0)odu;
			DSR dsr = odu0.getDsr();
			sb.append(" ZdResult [DSR:"+ dsr.getClass().getSimpleName()  + ",DSRrate:").append(dsr.getRate()+ ",DSRsncid:").append(dsr.getSncobjectid()).append("]").append("\r\n");
		}
		
		if( odu instanceof ODU1){
			ODU1 odu1 = (ODU1)odu;
			for (int i = 0; i < odu1.getDsrlist().size(); i++) {
				DSR dsr = odu1.getDsrlist().get(i);
				sb.append(" ZdResult [DSR:"+ dsr.getClass().getSimpleName()  + ",DSRrate:").append(dsr.getRate()+ ",DSRsncid:").append(dsr.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < odu1.getOdu0list().size(); i++) {
				ODU0 odu0 = odu1.getOdu0list().get(i);
				sb.append(" ZdResult ["+ odu0.getClass().getSimpleName()  + ",ODU0sncid:").append(odu0.getSncobjectid()).append("]").append("\r\n");
			}
		}
		
		if( odu instanceof ODU2){
			ODU2 odu2 = (ODU2)odu;
			for (int i = 0; i < odu2.getDsrlist().size(); i++) {
				DSR dsr = odu2.getDsrlist().get(i);
				sb.append(" ZdResult [DSR:"+ dsr.getClass().getSimpleName()  + ",DSRrate:").append(dsr.getRate()+ ",DSRsncid:").append(dsr.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < odu2.getOdu0list().size(); i++) {
				ODU0 odu0 = odu2.getOdu0list().get(i);
				sb.append(" ZdResult ["+ odu0.getClass().getSimpleName()  + ",ODU0sncid:").append(odu0.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < odu2.getOdu1list().size(); i++) {
				ODU1 odu1 = odu2.getOdu1list().get(i);
				sb.append(" ZdResult ["+ odu1.getClass().getSimpleName()  + ",ODU1sncid:").append(odu1.getSncobjectid()).append("]").append("\r\n");
			}
		}
		
		if( odu instanceof ODU3){
			ODU3 odu3 = (ODU3)odu;
			for (int i = 0; i < odu3.getDsrlist().size(); i++) {
				DSR dsr = odu3.getDsrlist().get(i);
				sb.append(" ZdResult [DSR:"+ dsr.getClass().getSimpleName()  + ",DSRrate:").append(dsr.getRate()+ ",DSRsncid:").append(dsr.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < odu3.getOdu0list().size(); i++) {
				ODU0 odu0 = odu3.getOdu0list().get(i);
				sb.append(" ZdResult ["+ odu0.getClass().getSimpleName()  + ",ODU0sncid:").append(odu0.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < odu3.getOdu1list().size(); i++) {
				ODU1 odu1 = odu3.getOdu1list().get(i);
				sb.append(" ZdResult ["+ odu1.getClass().getSimpleName()  + ",ODU1sncid:").append(odu1.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < odu3.getOdu2list().size(); i++) {
				ODU2 odu2 = odu3.getOdu2list().get(i);
				sb.append(" ZdResult ["+ odu2.getClass().getSimpleName() + ",ODU2sncid:").append(odu2.getSncobjectid()).append("]").append("\r\n");
			}
		}
		
		if( odu instanceof ODU4){
			ODU4 odu4 = (ODU4)odu;
			for (int i = 0; i < odu4.getDsrlist().size(); i++) {
				DSR dsr = odu4.getDsrlist().get(i);
				sb.append(" ZdResult [DSR:"+ dsr.getClass().getSimpleName()  + ",DSRrate:").append(dsr.getRate()+ ",DSRsncid:").append(dsr.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < odu4.getOdu0list().size(); i++) {
				ODU0 odu0 = odu4.getOdu0list().get(i);
				sb.append(" ZdResult ["+ odu0.getClass().getSimpleName()  + ",ODU0sncid:").append(odu0.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < odu4.getOdu1list().size(); i++) {
				ODU1 odu1 = odu4.getOdu1list().get(i);
				sb.append(" ZdResult ["+ odu1.getClass().getSimpleName()  + ",ODU1sncid:").append(odu1.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < odu4.getOdu2list().size(); i++) {
				ODU2 odu2 = odu4.getOdu2list().get(i);
				sb.append(" ZdResult ["+ odu2.getClass().getSimpleName()  + ",ODU2sncid:").append(odu2.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < odu4.getOdu3list().size(); i++) {
				ODU3 odu3 = odu4.getOdu3list().get(i);
				sb.append(" ZdResult ["+ odu3.getClass().getSimpleName()  + ",ODU3sncid:").append(odu3.getSncobjectid()).append("]").append("\r\n");
			}
		}
		
		if( odu instanceof OCH){
			OCH och = (OCH)odu;
			for (int i = 0; i < och.getDsrlist().size(); i++) {
				DSR dsr = och.getDsrlist().get(i);
				sb.append(" ZdResult [DSR:"+ dsr.getClass().getSimpleName()  + ",DSRrate:").append(dsr.getRate()+ ",DSRsncid:").append(dsr.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < och.getOdu0list().size(); i++) {
				ODU0 odu0 = och.getOdu0list().get(i);
				sb.append(" ZdResult ["+ odu0.getClass().getSimpleName()  + ",ODU0sncid:").append(odu0.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < och.getOdu1list().size(); i++) {
				ODU1 odu1 = och.getOdu1list().get(i);
				sb.append(" ZdResult ["+ odu1.getClass().getSimpleName()  + ",ODU1sncid:").append(odu1.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < och.getOdu2list().size(); i++) {
				ODU2 odu2 = och.getOdu2list().get(i);
				sb.append(" ZdResult ["+ odu2.getClass().getSimpleName()  + ",ODU2sncid:").append(odu2.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < och.getOdu3list().size(); i++) {
				ODU3 odu3 = och.getOdu3list().get(i);
				sb.append(" ZdResult ["+ odu3.getClass().getSimpleName()  + ",ODU3sncid:").append(odu3.getSncobjectid()).append("]").append("\r\n");
			}
			for (int i = 0; i < och.getOdu4list().size(); i++) {
				ODU4 odu4 = och.getOdu4list().get(i);
				sb.append(" ZdResult ["+ odu4.getClass().getSimpleName()  + ",ODU4sncid:").append(odu4.getSncobjectid()).append("]").append("\r\n");
			}
		}
		
		
		return sb.toString() ;
		
	}

	 
	
	
}
