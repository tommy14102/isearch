package com.zznode.opentnms.isearch.otnRouteService.db.po;

import java.sql.ResultSet;

public class DbWdmSncRoute 
{
    public String m_ObjectId = "";        //VARCHAR(128),              /*波分通道路由对象ID*/
    public String m_SncObjectId = "";     //VARCHAR(128),              /*波分通道对象ID*/
    public String m_EmsObjectId = "";     //VARCHAR(128),
    public Integer m_RouteSeq = 1;            //INTEGER                    /*记录通道路由号（可能存在多个通道路由）*/,
    public Integer m_WaySeq = 1;              //INTEGER                    /*记录一个通道路由中的路由序号*/,
    public String m_AEndMeObjectId = "";  //VARCHAR(128)               /*A 端网元对象ID*/,
    public String m_AEndPtpObjectId = ""; //VARCHAR(128)               /*A 端端口对象ID*/,
    public String m_AEndCtpId = "";       //VARCHAR(255)               /*A端 CtpId*/,
    public String m_ZEndMeObjectId = "";  //VARCHAR(128)               /*Z 端网元对象ID*/,   
    public String m_ZEndPtpObjectId = ""; //VARCHAR(128)               /*Z 端端口对象ID*/,  
    public String m_ZEndCtpId = "";       //VARCHAR(255)               /*Z 端 CtpId*/,      
    public Integer m_LinkType = 0;            //INTEGER                    /*连接类型 类型  0 拓扑连接  1 交叉连接*/,
    public Integer m_Direction = 0;           //INTEGER                    /*方向，0 正向，1 反向*/,
    public Integer m_ProtectionRole = 0;      //INTEGER                    /*保护角色，0 工作，1 保护*/,
    
	public String getM_ObjectId() {
		return m_ObjectId;
	}
	public void setM_ObjectId(String m_ObjectId) {
		this.m_ObjectId = m_ObjectId;
	}
	public String getM_SncObjectId() {
		return m_SncObjectId;
	}
	public void setM_SncObjectId(String m_SncObjectId) {
		this.m_SncObjectId = m_SncObjectId;
	}
	public String getM_EmsObjectId() {
		return m_EmsObjectId;
	}
	public void setM_EmsObjectId(String m_EmsObjectId) {
		this.m_EmsObjectId = m_EmsObjectId;
	}
	public Integer getM_RouteSeq() {
		return m_RouteSeq;
	}
	public void setM_RouteSeq(Integer m_RouteSeq) {
		this.m_RouteSeq = m_RouteSeq;
	}
	public Integer getM_WaySeq() {
		return m_WaySeq;
	}
	public void setM_WaySeq(Integer m_WaySeq) {
		this.m_WaySeq = m_WaySeq;
	}
	public String getM_AEndMeObjectId() {
		return m_AEndMeObjectId;
	}
	public void setM_AEndMeObjectId(String m_AEndMeObjectId) {
		this.m_AEndMeObjectId = m_AEndMeObjectId;
	}
	public String getM_AEndPtpObjectId() {
		return m_AEndPtpObjectId;
	}
	public void setM_AEndPtpObjectId(String m_AEndPtpObjectId) {
		this.m_AEndPtpObjectId = m_AEndPtpObjectId;
	}
	public String getM_AEndCtpId() {
		return m_AEndCtpId;
	}
	public void setM_AEndCtpId(String m_AEndCtpId) {
		this.m_AEndCtpId = m_AEndCtpId;
	}
	public String getM_ZEndMeObjectId() {
		return m_ZEndMeObjectId;
	}
	public void setM_ZEndMeObjectId(String m_ZEndMeObjectId) {
		this.m_ZEndMeObjectId = m_ZEndMeObjectId;
	}
	public String getM_ZEndPtpObjectId() {
		return m_ZEndPtpObjectId;
	}
	public void setM_ZEndPtpObjectId(String m_ZEndPtpObjectId) {
		this.m_ZEndPtpObjectId = m_ZEndPtpObjectId;
	}
	public String getM_ZEndCtpId() {
		return m_ZEndCtpId;
	}
	public void setM_ZEndCtpId(String m_ZEndCtpId) {
		this.m_ZEndCtpId = m_ZEndCtpId;
	}
	public Integer getM_LinkType() {
		return m_LinkType;
	}
	public void setM_LinkType(Integer m_LinkType) {
		this.m_LinkType = m_LinkType;
	}
	public Integer getM_Direction() {
		return m_Direction;
	}
	public void setM_Direction(Integer m_Direction) {
		this.m_Direction = m_Direction;
	}
	public Integer getM_ProtectionRole() {
		return m_ProtectionRole;
	}
	public void setM_ProtectionRole(Integer m_ProtectionRole) {
		this.m_ProtectionRole = m_ProtectionRole;
	}

    
    
}
