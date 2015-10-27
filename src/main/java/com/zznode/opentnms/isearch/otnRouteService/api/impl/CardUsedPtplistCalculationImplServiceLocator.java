/**
 * CardUsedPtplistCalculationImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zznode.opentnms.isearch.otnRouteService.api.impl;

public class CardUsedPtplistCalculationImplServiceLocator extends org.apache.axis.client.Service implements com.zznode.opentnms.isearch.otnRouteService.api.impl.CardUsedPtplistCalculationImplService {

    public CardUsedPtplistCalculationImplServiceLocator() {
    }


    public CardUsedPtplistCalculationImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CardUsedPtplistCalculationImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CardUsedPtplistCalculationImplPort
    private java.lang.String CardUsedPtplistCalculationImplPort_address = "http://10.4.1.50:8088/isearch-otnRouteService/services/CardUsedPtpCalculation";

    public java.lang.String getCardUsedPtplistCalculationImplPortAddress() {
        return CardUsedPtplistCalculationImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CardUsedPtplistCalculationImplPortWSDDServiceName = "CardUsedPtplistCalculationImplPort";

    public java.lang.String getCardUsedPtplistCalculationImplPortWSDDServiceName() {
        return CardUsedPtplistCalculationImplPortWSDDServiceName;
    }

    public void setCardUsedPtplistCalculationImplPortWSDDServiceName(java.lang.String name) {
        CardUsedPtplistCalculationImplPortWSDDServiceName = name;
    }

    public com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtplistCalculation getCardUsedPtplistCalculationImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CardUsedPtplistCalculationImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCardUsedPtplistCalculationImplPort(endpoint);
    }

    public com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtplistCalculation getCardUsedPtplistCalculationImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.zznode.opentnms.isearch.otnRouteService.api.impl.CardUsedPtplistCalculationImplServiceSoapBindingStub _stub = new com.zznode.opentnms.isearch.otnRouteService.api.impl.CardUsedPtplistCalculationImplServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getCardUsedPtplistCalculationImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCardUsedPtplistCalculationImplPortEndpointAddress(java.lang.String address) {
        CardUsedPtplistCalculationImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtplistCalculation.class.isAssignableFrom(serviceEndpointInterface)) {
                com.zznode.opentnms.isearch.otnRouteService.api.impl.CardUsedPtplistCalculationImplServiceSoapBindingStub _stub = new com.zznode.opentnms.isearch.otnRouteService.api.impl.CardUsedPtplistCalculationImplServiceSoapBindingStub(new java.net.URL(CardUsedPtplistCalculationImplPort_address), this);
                _stub.setPortName(getCardUsedPtplistCalculationImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CardUsedPtplistCalculationImplPort".equals(inputPortName)) {
            return getCardUsedPtplistCalculationImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://impl.api.otnRouteService.isearch.opentnms.zznode.com/", "CardUsedPtplistCalculationImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://impl.api.otnRouteService.isearch.opentnms.zznode.com/", "CardUsedPtplistCalculationImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CardUsedPtplistCalculationImplPort".equals(portName)) {
            setCardUsedPtplistCalculationImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
