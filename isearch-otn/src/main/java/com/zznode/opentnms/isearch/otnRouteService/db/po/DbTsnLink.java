package com.zznode.opentnms.isearch.otnRouteService.db.po;
public class DbTsnLink 
{
        private String  m_ObjectId         ="";  //Varchar(128)                         /*子网标识*/
        private String  m_TsnObjectId      ="";  //VARCHAR(255)                         /*所属EMS标识*/
        private String  m_MeObjectId       ="";  //VARCHAR(255)                         /*父子网标识*/
        
		public String getM_ObjectId() {
			return m_ObjectId;
		}
		public void setM_ObjectId(String m_ObjectId) {
			this.m_ObjectId = m_ObjectId;
		}
		public String getM_TsnObjectId() {
			return m_TsnObjectId;
		}
		public void setM_TsnObjectId(String m_TsnObjectId) {
			this.m_TsnObjectId = m_TsnObjectId;
		}
		public String getM_MeObjectId() {
			return m_MeObjectId;
		}
		public void setM_MeObjectId(String m_MeObjectId) {
			this.m_MeObjectId = m_MeObjectId;
		}
       
               
}

