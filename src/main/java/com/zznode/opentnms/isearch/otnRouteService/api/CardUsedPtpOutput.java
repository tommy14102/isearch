/**
 * CardUsedPtpOutput.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zznode.opentnms.isearch.otnRouteService.api;

public class CardUsedPtpOutput  implements java.io.Serializable {
    private com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo[] usedPtplist;

    private com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo[] unusedPtplist;

    public CardUsedPtpOutput() {
    }

    public CardUsedPtpOutput(
           com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo[] usedPtplist,
           com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo[] unusedPtplist) {
           this.usedPtplist = usedPtplist;
           this.unusedPtplist = unusedPtplist;
    }


    /**
     * Gets the usedPtplist value for this CardUsedPtpOutput.
     * 
     * @return usedPtplist
     */
    public com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo[] getUsedPtplist() {
        return usedPtplist;
    }


    /**
     * Sets the usedPtplist value for this CardUsedPtpOutput.
     * 
     * @param usedPtplist
     */
    public void setUsedPtplist(com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo[] usedPtplist) {
        this.usedPtplist = usedPtplist;
    }

    public com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo getUsedPtplist(int i) {
        return this.usedPtplist[i];
    }

    public void setUsedPtplist(int i, com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo _value) {
        this.usedPtplist[i] = _value;
    }


    /**
     * Gets the unusedPtplist value for this CardUsedPtpOutput.
     * 
     * @return unusedPtplist
     */
    public com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo[] getUnusedPtplist() {
        return unusedPtplist;
    }


    /**
     * Sets the unusedPtplist value for this CardUsedPtpOutput.
     * 
     * @param unusedPtplist
     */
    public void setUnusedPtplist(com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo[] unusedPtplist) {
        this.unusedPtplist = unusedPtplist;
    }

    public com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo getUnusedPtplist(int i) {
        return this.unusedPtplist[i];
    }

    public void setUnusedPtplist(int i, com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInfo _value) {
        this.unusedPtplist[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CardUsedPtpOutput)) return false;
        CardUsedPtpOutput other = (CardUsedPtpOutput) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.usedPtplist==null && other.getUsedPtplist()==null) || 
             (this.usedPtplist!=null &&
              java.util.Arrays.equals(this.usedPtplist, other.getUsedPtplist()))) &&
            ((this.unusedPtplist==null && other.getUnusedPtplist()==null) || 
             (this.unusedPtplist!=null &&
              java.util.Arrays.equals(this.unusedPtplist, other.getUnusedPtplist())));
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
        if (getUsedPtplist() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUsedPtplist());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUsedPtplist(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getUnusedPtplist() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUnusedPtplist());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUnusedPtplist(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CardUsedPtpOutput.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://api.otnRouteService.isearch.opentnms.zznode.com/", "cardUsedPtpOutput"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usedPtplist");
        elemField.setXmlName(new javax.xml.namespace.QName("", "usedPtplist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://api.otnRouteService.isearch.opentnms.zznode.com/", "cardUsedPtpInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unusedPtplist");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unusedPtplist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://api.otnRouteService.isearch.opentnms.zznode.com/", "cardUsedPtpInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
