package com.zznode.opentnms.isearch.otnRouteService.db.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.zznode.opentnms.isearch.otnRouteService.db.bo.ODU;

public class DbWdmSnc implements Serializable
{
	private static final long serialVersionUID = -5073621148770784068L;
		public String objectId          ="";  //Varchar(128),  /*波分通道对象ID*/
        public String emsObjectId       ="";  //Varchar(128),  
        public Integer direction         =0 ;  //Integereger    ,   /*方向（0  N/A 1  双向、 2  发、 3  收） */ 
        public String sncName           ="";  //Varchar(64),   /*通道名称*/
        public String sncId             ="";  //Varchar(64),   /*snc在ems上的标识*/
        public Integer rate              =0 ;  //Integereger,       /*层速率*/
        public String aEndMeObjectId    ="";  //Varchar(128),  /*A 端网元对象ID*/
        public String aEndPtpObjectId   ="";  //VARCHAR(128),  /*A 端端口对象ID*/
        public String aEndCtpId         ="";  //VARCHAR(255),  /*A端 CtpId*/
        public String zEndMeObjectId    ="";  //VARCHAR(128),  /*Z 端网元对象ID*/   
        public String zEndPtpObjectId   ="";  //VARCHAR(128),  /*Z 端端口对象ID*/  
        public String zEndCtpId         ="";  //VARCHAR(255),  /*Z 端 CtpId*/     
        public String aEndFrequency     ="";  //Varchar(32),   /*A端合波后占用的频率*/
        public String zEndFrequency     ="";  //Varchar(32),   /*Z端合波后占用的频率*/
        public String aEndClientRate    ="";  //Varchar(10),   /*A端业务侧速率*/
        public String zEndClientRate    ="";  //Varchar(10),   /*Z端业务侧速率*/
        public String aEndClientType    ="";  //Varchar(10),   /*A端业务侧类型*/
        public String zEndClientType    ="";  //Varchar(10),   /*Z端业务侧类型*/
        
        public Integer activeState       =0;  /*激活状态*/
        public Integer serviceState      =0;  /*占用状态（CLIENT层路径*/
        public String rateDesc          ="";  /*层速率描述*/
        public String sncType           ="";  /*SNC类型，0是ST_SIMPLE、1是ST_ADD_DROP_A......*/
        public String a2EndMeObjectId   ="";  /*A2端网元OBJECTID*/
        public String a2EndPtpObjectId  ="";  /*A2端端口OBJECTID*/
        public String a2EndCtpId        ="";  /*A2端CTP*/
        public String z2EndMeObjectId   ="";  /*Z2端网元OBJECTID*/
        public String z2EndPtpObjectId  ="";  /*Z2端端口OBJECTID*/
        public String z2EndCtpId        ="";  /*Z2端CTP*/
        public String waveLength        ="";  /*波长，即两个波峰之间的距离，单位nm（OCH层路径）*/
        public String lambda            ="";  /*光波序号（OCH层路径）*/
        public String reverseObjectId   ="";  /*反向波分路径对象ID*/
        public String routeInfo         ="";  /*路由描述*/
        public String reverseRouteInfo  ="";  /*反向路由描述*/
        public String aEndLayer         ="";  /*A1端端口层速率列表，以半角左斜线分隔*/
        public String zEndLayer         ="";  /*Z1端端口层速率列表，以半角左斜线分隔*/
        public String a2EndLayer        ="";  /*A2端端口层速率列表，以半角左斜线分隔*/
        public String z2EndLayer        ="";  /*Z2端端口层速率列表，以半角左斜线分隔*/
        
        public List<String> passedPtplist = new ArrayList<String>();
        public Boolean isHealthy = true ;  
        public String layerdesc = "" ; //c:client , 0:odu0 , 1:odu1 , 2:odu2 , 3:odu3 , 4: odu4 , o:och 
        public String headptpStr = "" ;  
        public String headctpSer = "";
        public ODU odu ; 
        public List<WdmSncRoute> wdmsncroutelist = new ArrayList<WdmSncRoute>();
        public String ratedesc = "" ; 
        public Boolean isCaculated = false ;
        public Boolean isReverse = false ;
        
        
		public Boolean getIsReverse() {
			return isReverse;
		}
		public void setIsReverse(Boolean isReverse) {
			this.isReverse = isReverse;
		}
		public Boolean getIsCaculated() {
			return isCaculated;
		}
		public void setIsCaculated(Boolean isCaculated) {
			this.isCaculated = isCaculated;
		}
		public String getRatedesc() {
			return ratedesc;
		}
		public void setRatedesc(String ratedesc) {
			this.ratedesc = ratedesc;
		}
		public List<WdmSncRoute> getWdmsncroutelist() {
			return wdmsncroutelist;
		}
		public void setWdmsncroutelist(List<WdmSncRoute> wdmsncroutelist) {
			this.wdmsncroutelist = wdmsncroutelist;
		}
		public ODU getOdu() {
			return odu;
		}
		public void setOdu(ODU odu) {
			this.odu = odu;
		}
		public String getHeadctpSer() {
			return headctpSer;
		}
		public void setHeadctpSer(String headctpSer) {
			this.headctpSer = headctpSer;
		}
		public String getHeadptpStr() {
			return headptpStr;
		}
		public void setHeadptpStr(String headptpStr) {
			this.headptpStr = headptpStr;
		}
		public String getLayerdesc() {
			return layerdesc;
		}
		public void setLayerdesc(String layerdesc) {
			this.layerdesc = layerdesc;
		}
		public Boolean getIsHealthy() {
			return isHealthy;
		}
		public void setIsHealthy(Boolean isHealthy) {
			this.isHealthy = isHealthy;
		}
		public List<String> getPassedPtplist() {
			return passedPtplist;
		}
		public void setPassedPtplist(List<String> passedPtplist) {
			this.passedPtplist = passedPtplist;
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
		public String getaEndFrequency() {
			return aEndFrequency;
		}
		public void setaEndFrequency(String aEndFrequency) {
			this.aEndFrequency = aEndFrequency;
		}
		public String getzEndFrequency() {
			return zEndFrequency;
		}
		public void setzEndFrequency(String zEndFrequency) {
			this.zEndFrequency = zEndFrequency;
		}
		public String getaEndClientRate() {
			return aEndClientRate;
		}
		public void setaEndClientRate(String aEndClientRate) {
			this.aEndClientRate = aEndClientRate;
		}
		public String getzEndClientRate() {
			return zEndClientRate;
		}
		public void setzEndClientRate(String zEndClientRate) {
			this.zEndClientRate = zEndClientRate;
		}
		public String getaEndClientType() {
			return aEndClientType;
		}
		public void setaEndClientType(String aEndClientType) {
			this.aEndClientType = aEndClientType;
		}
		public String getzEndClientType() {
			return zEndClientType;
		}
		public void setzEndClientType(String zEndClientType) {
			this.zEndClientType = zEndClientType;
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
		public String getA2EndMeObjectId() {
			return a2EndMeObjectId;
		}
		public void setA2EndMeObjectId(String a2EndMeObjectId) {
			this.a2EndMeObjectId = a2EndMeObjectId;
		}
		public String getA2EndPtpObjectId() {
			return a2EndPtpObjectId;
		}
		public void setA2EndPtpObjectId(String a2EndPtpObjectId) {
			this.a2EndPtpObjectId = a2EndPtpObjectId;
		}
		public String getA2EndCtpId() {
			return a2EndCtpId;
		}
		public void setA2EndCtpId(String a2EndCtpId) {
			this.a2EndCtpId = a2EndCtpId;
		}
		public String getZ2EndMeObjectId() {
			return z2EndMeObjectId;
		}
		public void setZ2EndMeObjectId(String z2EndMeObjectId) {
			this.z2EndMeObjectId = z2EndMeObjectId;
		}
		public String getZ2EndPtpObjectId() {
			return z2EndPtpObjectId;
		}
		public void setZ2EndPtpObjectId(String z2EndPtpObjectId) {
			this.z2EndPtpObjectId = z2EndPtpObjectId;
		}
		public String getZ2EndCtpId() {
			return z2EndCtpId;
		}
		public void setZ2EndCtpId(String z2EndCtpId) {
			this.z2EndCtpId = z2EndCtpId;
		}
		public String getWaveLength() {
			return waveLength;
		}
		public void setWaveLength(String waveLength) {
			this.waveLength = waveLength;
		}
		public String getLambda() {
			return lambda;
		}
		public void setLambda(String lambda) {
			this.lambda = lambda;
		}
		public String getReverseObjectId() {
			return reverseObjectId;
		}
		public void setReverseObjectId(String reverseObjectId) {
			this.reverseObjectId = reverseObjectId;
		}
		public String getRouteInfo() {
			return routeInfo;
		}
		public void setRouteInfo(String routeInfo) {
			this.routeInfo = routeInfo;
		}
		public String getReverseRouteInfo() {
			return reverseRouteInfo;
		}
		public void setReverseRouteInfo(String reverseRouteInfo) {
			this.reverseRouteInfo = reverseRouteInfo;
		}
		public String getaEndLayer() {
			return aEndLayer;
		}
		public void setaEndLayer(String aEndLayer) {
			this.aEndLayer = aEndLayer;
		}
		public String getzEndLayer() {
			return zEndLayer;
		}
		public void setzEndLayer(String zEndLayer) {
			this.zEndLayer = zEndLayer;
		}
		public String getA2EndLayer() {
			return a2EndLayer;
		}
		public void setA2EndLayer(String a2EndLayer) {
			this.a2EndLayer = a2EndLayer;
		}
		public String getZ2EndLayer() {
			return z2EndLayer;
		}
		public void setZ2EndLayer(String z2EndLayer) {
			this.z2EndLayer = z2EndLayer;
		}
        
}        
        
        
