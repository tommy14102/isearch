/**
 * CardUsedPtpInput.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zznode.opentnms.isearch.otnRouteService.api;

public class CardUsedPtpInput  implements java.io.Serializable {
    private java.lang.String cardobjectid;

    public CardUsedPtpInput() {
    }

    public CardUsedPtpInput(
           java.lang.String cardobjectid) {
           this.cardobjectid = cardobjectid;
    }


    /**
     * Gets the cardobjectid value for this CardUsedPtpInput.
     * 
     * @return cardobjectid
     */
    public java.lang.String getCardobjectid() {
        return cardobjectid;
    }


    /**
     * Sets the cardobjectid value for this CardUsedPtpInput.
     * 
     * @param cardobjectid
     */
    public void setCardobjectid(java.lang.String cardobjectid) {
        this.cardobjectid = cardobjectid;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CardUsedPtpInput)) return false;
        CardUsedPtpInput other = (CardUsedPtpInput) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cardobjectid==null && other.getCardobjectid()==null) || 
             (this.cardobjectid!=null &&
              this.cardobjectid.equals(other.getCardobjectid())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCardobjectid() != null) {
            _hashCode += getCardobjectid().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CardUsedPtpInput.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://api.otnRouteService.isearch.opentnms.zznode.com/", "cardUsedPtpInput"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardobjectid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardobjectid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
