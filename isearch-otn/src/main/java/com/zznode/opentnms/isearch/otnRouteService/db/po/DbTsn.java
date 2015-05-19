package com.zznode.opentnms.isearch.otnRouteService.db.po;
public class DbTsn
{
        private String  objectId             ="";  //Varchar(128)                         /*子网标识*/
        private String  emsObjectId          ="";  //VARCHAR(255)                         /*所属EMS标识*/
        private String  parentObjectId       ="";  //VARCHAR(255)                         /*父子网标识*/
        private String  userLabel            ="";  //Varchar(255)                         /*友好名*/
        private String  createUserName       ="";  //Varchar(255)                         /*创建子网的用户名*/
        private Integer xOffSet;  				//Integer							   /*X轴坐标*/
        private Integer yOffSet;                  //Integer							   /*Y轴坐标*/
        
        
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
		public String getParentObjectId() {
			return parentObjectId;
		}
		public void setParentObjectId(String parentObjectId) {
			this.parentObjectId = parentObjectId;
		}
		public String getUserLabel() {
			return userLabel;
		}
		public void setUserLabel(String userLabel) {
			this.userLabel = userLabel;
		}
		public String getCreateUserName() {
			return createUserName;
		}
		public void setCreateUserName(String createUserName) {
			this.createUserName = createUserName;
		}
		public Integer getxOffSet() {
			return xOffSet;
		}
		public void setxOffSet(Integer xOffSet) {
			this.xOffSet = xOffSet;
		}
		public Integer getyOffSet() {
			return yOffSet;
		}
		public void setyOffSet(Integer yOffSet) {
			this.yOffSet = yOffSet;
		}
        
        
        
        
}

