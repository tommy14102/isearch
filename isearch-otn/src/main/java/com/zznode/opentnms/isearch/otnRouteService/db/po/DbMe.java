package com.zznode.opentnms.isearch.otnRouteService.db.po;

public class DbMe 
{
	public String  objectId              ="";  //Varchar(128)                    not null      /*对象标识
	public String  meId                  ="";  //Varchar(255)                    not null      /*网元标示符*/
	public String  nativeName            ="";  //Varchar(255)                         /*网元在厂商网管系统中的本地名称*/
	public String  model                 ="";  //Varchar(255)                     
	public String  vendor                ="";  //Varchar(255)                         /*网元设备制造商*/
	public String  managementDomain      ="";  //Varchar(255)                         /*管理域（或所属地区）名称*/
	public String  juzhanobjectid        ="";
	public String  jifangobjectid        ="";
	public String  parentmeobjectid      ="";
	
	
	public String getParentmeobjectid() {
		return parentmeobjectid;
	}
	public void setParentmeobjectid(String parentmeobjectid) {
		this.parentmeobjectid = parentmeobjectid;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getMeId() {
		return meId;
	}
	public void setMeId(String meId) {
		this.meId = meId;
	}
	public String getNativeName() {
		return nativeName;
	}
	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getManagementDomain() {
		return managementDomain;
	}
	public void setManagementDomain(String managementDomain) {
		this.managementDomain = managementDomain;
	}
	public String getJuzhanobjectid() {
		return juzhanobjectid;
	}
	public void setJuzhanobjectid(String juzhanobjectid) {
		this.juzhanobjectid = juzhanobjectid;
	}
	public String getJifangobjectid() {
		return jifangobjectid;
	}
	public void setJifangobjectid(String jifangobjectid) {
		this.jifangobjectid = jifangobjectid;
	}
	
	
}                                           
