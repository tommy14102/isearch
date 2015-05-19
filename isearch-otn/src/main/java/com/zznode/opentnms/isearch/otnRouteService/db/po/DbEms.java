package com.zznode.opentnms.isearch.otnRouteService.db.po;
import java.sql.Date;
public class DbEms 
{  
         private String  objectId                ="";  //Varchar(128)                    not null      /*对象标识*/
         private String  idOnEms                 ="";  //VARCHAR(255)                         /*EMS中标识 ，如 EMS=SD-T200-1-P*/
         private String  emsName                 ="";  //Varchar(255)                    not null      /*EMS 名称          
         private String  emsType                 ="";  //Varchar(255)                         /*类型（EMS /SNMS）*/
         private String  emsVersion              ="";  //Varchar(255)                         /*版本号*/
         private String  managementDomain        ="";  //Varchar(255)                         /*管理域（或所属地区）名称*/
         private String  location                ="";  //Varchar(255)                         /*物理位置（EMS服务器存放位置）*/
         private String  vendor                  ="";  //Varchar(255)                         /*设备制造商*/
         private String  hardware                ="";  //Varchar(255)                         /*EMS所使用的硬件平台信息*/
         private String  software                ="";  //Varchar(255)                         /*EMS所使用的软件平台信息*/
         private String  creator                 ="";  //Varchar(255)                         /*创建者标记*/
         private Date createTime             	 ;  /*创建日期*/
         private String  userLabel               ="";  //Varchar(255)                         /*友好名称*/
         private String  ipAddress               ="";  //Varchar(255)                         /*IP地址*/
         private String  northBoundInterfaceName ="";  //Varchar(255)                         /*北向接口名称*/
         private String  northBoundVersion       ="";  //Varchar(255)                         /*北向接口版本*/
         private Integer northBoundNamingPort    ;  //Integer                                 /*北向接口命名服务端口号*/
         private String  serviceState            ="";  //Integer                               /*使用状态
                                                     //                                          可用
                                                     //                                          不可用*/
         private Integer maxMeCount              ;  //Integer                                 /*支持最大管理网元数目*/
         private String  communicationAddress    ="";  //Varchar(255)                         /*通信地址*/
         private String  telephone               ="";  //Varchar(255)                         /*联系电话*/
         private String  additionalInfo          ="";  //Varchar(255)                         /*备注*/
         private String  collectorHostIp         ="";  //Varchar(255)                         /*采集进程所在主机IP*/
         private String  fileServerIp            ="";  //Varchar(255)                         /*原始文件存放主机IP*/
         private String  fileServerFtpUserName   ="";  //Varchar(255)                         /*原始文件存放主机的FTP用户名*/
         private String  fileServerFtpPassword   ="";  //Varchar(255)                         /*原始文件存放主机的FTP密码*/
         private String  mqHostIp                ="";  //Varchar(255)                         /*上报通知的mq主机IP*/
         private Integer mqPort               ;  //Integer                              /*上报通知的mq端口*/
         private String  mqTopic                 ="";  //Varchar(255)                         /*上报通知的mq  TOPIC名称*/
         private Integer connectionState      ;  //Integer                              /*连接状态
                                                     //                                       0 未连接
                                                     //                                        1 连接
                                                     //                                        */
         private String  emsId                   ="";
         private String  serviceLevel            ="";  //Varchar(64)                     /*系统级别 枚举值：省际干线、省内干线、本地骨干、本地汇聚、本地接入*/,  
         private Integer isHasProtection     ;   //Integer     default 0           /* 有无保护 枚举值：有  1 、无  0 */
         private Long    connectionTimestamp     ;
         private String  emsModel                = ""; //华为T2000、U2000 中兴E300、T31 烽火OTNM2000 阿朗ASB
		
         
         
         public String getObjectId() {
			return objectId;
		}
		public void setObjectId(String objectId) {
			this.objectId = objectId;
		}
		public String getIdOnEms() {
			return idOnEms;
		}
		public void setIdOnEms(String idOnEms) {
			this.idOnEms = idOnEms;
		}
		public String getEmsName() {
			return emsName;
		}
		public void setEmsName(String emsName) {
			this.emsName = emsName;
		}
		public String getEmsType() {
			return emsType;
		}
		public void setEmsType(String emsType) {
			this.emsType = emsType;
		}
		public String getEmsVersion() {
			return emsVersion;
		}
		public void setEmsVersion(String emsVersion) {
			this.emsVersion = emsVersion;
		}
		public String getManagementDomain() {
			return managementDomain;
		}
		public void setManagementDomain(String managementDomain) {
			this.managementDomain = managementDomain;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		public String getVendor() {
			return vendor;
		}
		public void setVendor(String vendor) {
			this.vendor = vendor;
		}
		public String getHardware() {
			return hardware;
		}
		public void setHardware(String hardware) {
			this.hardware = hardware;
		}
		public String getSoftware() {
			return software;
		}
		public void setSoftware(String software) {
			this.software = software;
		}
		public String getCreator() {
			return creator;
		}
		public void setCreator(String creator) {
			this.creator = creator;
		}
		public Date getCreateTime() {
			return createTime;
		}
		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}
		public String getUserLabel() {
			return userLabel;
		}
		public void setUserLabel(String userLabel) {
			this.userLabel = userLabel;
		}
		public String getIpAddress() {
			return ipAddress;
		}
		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}
		public String getNorthBoundInterfaceName() {
			return northBoundInterfaceName;
		}
		public void setNorthBoundInterfaceName(String northBoundInterfaceName) {
			this.northBoundInterfaceName = northBoundInterfaceName;
		}
		public String getNorthBoundVersion() {
			return northBoundVersion;
		}
		public void setNorthBoundVersion(String northBoundVersion) {
			this.northBoundVersion = northBoundVersion;
		}
		public Integer getNorthBoundNamingPort() {
			return northBoundNamingPort;
		}
		public void setNorthBoundNamingPort(Integer northBoundNamingPort) {
			this.northBoundNamingPort = northBoundNamingPort;
		}
		public String getServiceState() {
			return serviceState;
		}
		public void setServiceState(String serviceState) {
			this.serviceState = serviceState;
		}
		public Integer getMaxMeCount() {
			return maxMeCount;
		}
		public void setMaxMeCount(Integer maxMeCount) {
			this.maxMeCount = maxMeCount;
		}
		public String getCommunicationAddress() {
			return communicationAddress;
		}
		public void setCommunicationAddress(String communicationAddress) {
			this.communicationAddress = communicationAddress;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getAdditionalInfo() {
			return additionalInfo;
		}
		public void setAdditionalInfo(String additionalInfo) {
			this.additionalInfo = additionalInfo;
		}
		public String getCollectorHostIp() {
			return collectorHostIp;
		}
		public void setCollectorHostIp(String collectorHostIp) {
			this.collectorHostIp = collectorHostIp;
		}
		public String getFileServerIp() {
			return fileServerIp;
		}
		public void setFileServerIp(String fileServerIp) {
			this.fileServerIp = fileServerIp;
		}
		public String getFileServerFtpUserName() {
			return fileServerFtpUserName;
		}
		public void setFileServerFtpUserName(String fileServerFtpUserName) {
			this.fileServerFtpUserName = fileServerFtpUserName;
		}
		public String getFileServerFtpPassword() {
			return fileServerFtpPassword;
		}
		public void setFileServerFtpPassword(String fileServerFtpPassword) {
			this.fileServerFtpPassword = fileServerFtpPassword;
		}
		public String getMqHostIp() {
			return mqHostIp;
		}
		public void setMqHostIp(String mqHostIp) {
			this.mqHostIp = mqHostIp;
		}
		public Integer getMqPort() {
			return mqPort;
		}
		public void setMqPort(Integer mqPort) {
			this.mqPort = mqPort;
		}
		public String getMqTopic() {
			return mqTopic;
		}
		public void setMqTopic(String mqTopic) {
			this.mqTopic = mqTopic;
		}
		public Integer getConnectionState() {
			return connectionState;
		}
		public void setConnectionState(Integer connectionState) {
			this.connectionState = connectionState;
		}
		public String getEmsId() {
			return emsId;
		}
		public void setEmsId(String emsId) {
			this.emsId = emsId;
		}
		public String getServiceLevel() {
			return serviceLevel;
		}
		public void setServiceLevel(String serviceLevel) {
			this.serviceLevel = serviceLevel;
		}
		public Integer getIsHasProtection() {
			return isHasProtection;
		}
		public void setIsHasProtection(Integer isHasProtection) {
			this.isHasProtection = isHasProtection;
		}
		public Long getConnectionTimestamp() {
			return connectionTimestamp;
		}
		public void setConnectionTimestamp(Long connectionTimestamp) {
			this.connectionTimestamp = connectionTimestamp;
		}
		public String getEmsModel() {
			return emsModel;
		}
		public void setEmsModel(String emsModel) {
			this.emsModel = emsModel;
		}
         
         
         
}
