/**
 * RouteCalculationOutput.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zznode.opentnms.isearch.otnRouteService.api;

public class RouteCalculationOutput  implements java.io.Serializable {
    private java.lang.String protectionType;

    private java.lang.Integer rate;

    private com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationResult[] routeCalculationResultlist;

    private java.lang.String routePlanobjectid;

    private java.lang.String routePlanseq;

    private java.lang.String routeseq;

    public RouteCalculationOutput() {
    }

    public RouteCalculationOutput(
           java.lang.String protectionType,
           java.lang.Integer rate,
           com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationResult[] routeCalculationResultlist,
           java.lang.String routePlanobjectid,
           java.lang.String routePlanseq,
           java.lang.String routeseq) {
           this.protectionType = protectionType;
           this.rate = rate;
           this.routeCalculationResultlist = routeCalculationResultlist;
           this.routePlanobjectid = routePlanobjectid;
           this.routePlanseq = routePlanseq;
           this.routeseq = routeseq;
    }


    /**
     * Gets the protectionType value for this RouteCalculationOutput.
     * 
     * @return protectionType
     */
    public java.lang.String getProtectionType() {
        return protectionType;
    }


    /**
     * Sets the protectionType value for this RouteCalculationOutput.
     * 
     * @param protectionType
     */
    public void setProtectionType(java.lang.String protectionType) {
        this.protectionType = protectionType;
    }


    /**
     * Gets the rate value for this RouteCalculationOutput.
     * 
     * @return rate
     */
    public java.lang.Integer getRate() {
        return rate;
    }


    /**
     * Sets the rate value for this RouteCalculationOutput.
     * 
     * @param rate
     */
    public void setRate(java.lang.Integer rate) {
        this.rate = rate;
    }


    /**
     * Gets the routeCalculationResultlist value for this RouteCalculationOutput.
     * 
     * @return routeCalculationResultlist
     */
    public com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationResult[] getRouteCalculationResultlist() {
        return routeCalculationResultlist;
    }


    /**
     * Sets the routeCalculationResultlist value for this RouteCalculationOutput.
     * 
     * @param routeCalculationResultlist
     */
    public void setRouteCalculationResultlist(com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationResult[] routeCalculationResultlist) {
        this.routeCalculationResultlist = routeCalculationResultlist;
    }

    public com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationResult getRouteCalculationResultlist(int i) {
        return this.routeCalculationResultlist[i];
    }

    public void setRouteCalculationResultlist(int i, com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationResult _value) {
        this.routeCalculationResultlist[i] = _value;
    }


    /**
     * Gets the routePlanobjectid value for this RouteCalculationOutput.
     * 
     * @return routePlanobjectid
     */
    public java.lang.String getRoutePlanobjectid() {
        return routePlanobjectid;
    }


    /**
     * Sets the routePlanobjectid value for this RouteCalculationOutput.
     * 
     * @param routePlanobjectid
     */
    public void setRoutePlanobjectid(java.lang.String routePlanobjectid) {
        this.routePlanobjectid = routePlanobjectid;
    }


    /**
     * Gets the routePlanseq value for this RouteCalculationOutput.
     * 
     * @return routePlanseq
     */
    public java.lang.String getRoutePlanseq() {
        return routePlanseq;
    }


    /**
     * Sets the routePlanseq value for this RouteCalculationOutput.
     * 
     * @param routePlanseq
     */
    public void setRoutePlanseq(java.lang.String routePlanseq) {
        this.routePlanseq = routePlanseq;
    }


    /**
     * Gets the routeseq value for this RouteCalculationOutput.
     * 
     * @return routeseq
     */
    public java.lang.String getRouteseq() {
        return routeseq;
    }


    /**
     * Sets the routeseq value for this RouteCalculationOutput.
     * 
     * @param routeseq
     */
    public void setRouteseq(java.lang.String routeseq) {
        this.routeseq = routeseq;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RouteCalculationOutput)) return false;
        RouteCalculationOutput other = (RouteCalculationOutput) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.protectionType==null && other.getProtectionType()==null) || 
             (this.protectionType!=null &&
              this.protectionType.equals(other.getProtectionType()))) &&
            ((this.rate==null && other.getRate()==null) || 
             (this.rate!=null &&
              this.rate.equals(other.getRate()))) &&
            ((this.routeCalculationResultlist==null && other.getRouteCalculationResultlist()==null) || 
             (this.routeCalculationResultlist!=null &&
              java.util.Arrays.equals(this.routeCalculationResultlist, other.getRouteCalculationResultlist()))) &&
            ((this.routePlanobjectid==null && other.getRoutePlanobjectid()==null) || 
             (this.routePlanobjectid!=null &&
              this.routePlanobjectid.equals(other.getRoutePlanobjectid()))) &&
            ((this.routePlanseq==null && other.getRoutePlanseq()==null) || 
             (this.routePlanseq!=null &&
              this.routePlanseq.equals(other.getRoutePlanseq()))) &&
            ((this.routeseq==null && other.getRouteseq()==null) || 
             (this.routeseq!=null &&
              this.routeseq.equals(other.getRouteseq())));
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
        if (getProtectionType() != null) {
            _hashCode += getProtectionType().hashCode();
        }
        if (getRate() != null) {
            _hashCode += getRate().hashCode();
        }
        if (getRouteCalculationResultlist() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRouteCalculationResultlist());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRouteCalculationResultlist(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRoutePlanobjectid() != null) {
            _hashCode += getRoutePlanobjectid().hashCode();
        }
        if (getRoutePlanseq() != null) {
            _hashCode += getRoutePlanseq().hashCode();
        }
        if (getRouteseq() != null) {
            _hashCode += getRouteseq().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RouteCalculationOutput.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://api.otnRouteService.isearch.opentnms.zznode.com/", "routeCalculationOutput"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("protectionType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "protectionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("routeCalculationResultlist");
        elemField.setXmlName(new javax.xml.namespace.QName("", "routeCalculationResultlist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://api.otnRouteService.isearch.opentnms.zznode.com/", "routeCalculationResult"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("routePlanobjectid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "routePlanobjectid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("routePlanseq");
        elemField.setXmlName(new javax.xml.namespace.QName("", "routePlanseq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("routeseq");
        elemField.setXmlName(new javax.xml.namespace.QName("", "routeseq"));
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
