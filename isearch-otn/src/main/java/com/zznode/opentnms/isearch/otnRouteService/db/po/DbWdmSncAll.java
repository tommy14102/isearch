package com.zznode.opentnms.isearch.otnRouteService.db.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.zznode.opentnms.isearch.model.bo.ODU;

public class DbWdmSncAll implements Serializable
{
	private static final long serialVersionUID = -5073621148770784068L;
		public String objectId          ;  //Varchar(128),  /*波分通道对象ID*/
        public String emsObjectId       ;  //Varchar(128),  
        public Integer direction        ;  //Integereger    ,   /*方向（0  N/A 1  双向、 2  发、 3  收） */ 
        public String sncName           ;  //Varchar(64),   /*通道名称*/
        public String sncId             ;  //Varchar(64),   /*snc在ems上的标识*/
        public Integer rate             ;  //Integereger,       /*层速率*/
        public String aEndMeObjectId    ;  //Varchar(128),  /*A 端网元对象ID*/
        public String aEndPtpObjectId   ;  //VARCHAR(128),  /*A 端端口对象ID*/
        public String aEndCtpId         ;  //VARCHAR(255),  /*A端 CtpId*/
        public String zEndMeObjectId    ;  //VARCHAR(128),  /*Z 端网元对象ID*/   
        public String zEndPtpObjectId   ;  //VARCHAR(128),  /*Z 端端口对象ID*/  
        public String zEndCtpId         ;  //VARCHAR(255),  /*Z 端 CtpId*/     
        
        public Integer activeState      ;  /*激活状态*/
        public Integer serviceState     ;  /*占用状态（CLIENT层路径*/
        public String rateDesc          ;  /*层速率描述*/
        public String sncType           ;  /*SNC类型，0是ST_SIMPLE、1是ST_ADD_DROP_A......*/
        
        public List<String> passedPtplist = new ArrayList<String>();
        public Boolean isHealthy = true ;  
        public String layerdesc = "" ; //c:client , 0:odu0 , 1:odu1 , 2:odu2 , 3:odu3 , 4: odu4 , o:och 
        public String headptpStr = "" ;  
        public String headctpSer = "";
        public ODU odu ; 
        public List<WdmSncRoute> wdmsncroutelist = new ArrayList<WdmSncRoute>();
        public List<WdmSncRoute> wdmsncrouteReverselist = new ArrayList<WdmSncRoute>();
        public String ratedesc = "" ; 
        public Boolean isCaculatedBid = false ;
        public Boolean isCaculatedReverse = false ;
        public Boolean isReverse = false ;
        
        private String aendjz;
        private String zendjz;
        private Boolean reverseAdded  = false ;
        
		public String getAendjz() {
			return aendjz;
		}
		public void setAendjz(String aendjz) {
			this.aendjz = aendjz;
		}
		public String getZendjz() {
			return zendjz;
		}
		public void setZendjz(String zendjz) {
			this.zendjz = zendjz;
		}
		public String getObjectId() {
			return objectId;
		}
		public void setObjectId(String objectId) {
			this.objectId = objectId;
		}
		public String getEmsObjectId() {
			return emsObjectId;
		}
		public void setEmsObjectId(String emsObjectId) {
			this.emsObjectId = emsObjectId;
		}
		public Integer getDirection() {
			return direction;
		}
		public void setDirection(Integer direction) {
			this.direction = direction;
		}
		public String getSncName() {
			return sncName;
		}
		public void setSncName(String sncName) {
			this.sncName = sncName;
		}
		public String getSncId() {
			return sncId;
		}
		public void setSncId(String sncId) {
			this.sncId = sncId;
		}
		public Integer getRate() {
			return rate;
		}
		public void setRate(Integer rate) {
			this.rate = rate;
		}
		public String getaEndMeObjectId() {
			return aEndMeObjectId;
		}
		public void setaEndMeObjectId(String aEndMeObjectId) {
			this.aEndMeObjectId = aEndMeObjectId;
		}
		public String getaEndPtpObjectId() {
			return aEndPtpObjectId;
		}
		public void setaEndPtpObjectId(String aEndPtpObjectId) {
			this.aEndPtpObjectId = aEndPtpObjectId;
		}
		public String getaEndCtpId() {
			return aEndCtpId;
		}
		public void setaEndCtpId(String aEndCtpId) {
			this.aEndCtpId = aEndCtpId;
		}
		public String getzEndMeObjectId() {
			return zEndMeObjectId;
		}
		public void setzEndMeObjectId(String zEndMeObjectId) {
			this.zEndMeObjectId = zEndMeObjectId;
		}
		public String getzEndPtpObjectId() {
			return zEndPtpObjectId;
		}
		public void setzEndPtpObjectId(String zEndPtpObjectId) {
			this.zEndPtpObjectId = zEndPtpObjectId;
		}
		public String getzEndCtpId() {
			return zEndCtpId;
		}
		public void setzEndCtpId(String zEndCtpId) {
			this.zEndCtpId = zEndCtpId;
		}
		public Integer getActiveState() {
			return activeState;
		}
		public void setActiveState(Integer activeState) {
			this.activeState = activeState;
		}
		public Integer getServiceState() {
			return serviceState;
		}
		public void setServiceState(Integer serviceState) {
			this.serviceState = serviceState;
		}
		public String getRateDesc() {
			return rateDesc;
		}
		public void setRateDesc(String rateDesc) {
			this.rateDesc = rateDesc;
		}
		public String getSncType() {
			return sncType;
		}
		public void setSncType(String sncType) {
			this.sncType = sncType;
		}
		public List<String> getPassedPtplist() {
			return passedPtplist;
		}
		public void setPassedPtplist(List<String> passedPtplist) {
			this.passedPtplist = passedPtplist;
		}
		public Boolean getIsHealthy() {
			return isHealthy;
		}
		public void setIsHealthy(Boolean isHealthy) {
			this.isHealthy = isHealthy;
		}
		public String getLayerdesc() {
			return layerdesc;
		}
		public void setLayerdesc(String layerdesc) {
			this.layerdesc = layerdesc;
		}
		public String getHeadptpStr() {
			return headptpStr;
		}
		public void setHeadptpStr(String headptpStr) {
			this.headptpStr = headptpStr;
		}
		public String getHeadctpSer() {
			return headctpSer;
		}
		public void setHeadctpSer(String headctpSer) {
			this.headctpSer = headctpSer;
		}
		public ODU getOdu() {
			return odu;
		}
		public void setOdu(ODU odu) {
			this.odu = odu;
		}
		public List<WdmSncRoute> getWdmsncroutelist() {
			return wdmsncroutelist;
		}
		public void setWdmsncroutelist(List<WdmSncRoute> wdmsncroutelist) {
			this.wdmsncroutelist = wdmsncroutelist;
		}
		public List<WdmSncRoute> getWdmsncrouteReverselist() {
			return wdmsncrouteReverselist;
		}
		public void setWdmsncrouteReverselist(List<WdmSncRoute> wdmsncrouteReverselist) {
			this.wdmsncrouteReverselist = wdmsncrouteReverselist;
		}
		public String getRatedesc() {
			return ratedesc;
		}
		public void setRatedesc(String ratedesc) {
			this.ratedesc = ratedesc;
		}
		public Boolean getIsCaculatedBid() {
			return isCaculatedBid;
		}
		public void setIsCaculatedBid(Boolean isCaculatedBid) {
			
			if("UUID:5165a955-10da-11e5-9c2d-005056862639".equals(this.objectId)){
				System.out.println(999);
			}
			this.isCaculatedBid = isCaculatedBid;
		}
		public Boolean getIsCaculatedReverse() {
			return isCaculatedReverse;
		}
		public void setIsCaculatedReverse(Boolean isCaculatedReverse) {
			this.isCaculatedReverse = isCaculatedReverse;
		}
		public Boolean getIsReverse() {
			return isReverse;
		}
		public void setIsReverse(Boolean isReverse) {
			this.isReverse = isReverse;
		}
		public Boolean getReverseAdded() {
			return reverseAdded;
		}
		public void setReverseAdded(Boolean reverseAdded) {
			this.reverseAdded = reverseAdded;
		}
        
        
        
        
}        
        
        
