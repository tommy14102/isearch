/**
 * RouteCalculationServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zznode.opentnms.isearch.otnRouteService.api;

public class RouteCalculationServiceLocator extends org.apache.axis.client.Service implements com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationService {

    public RouteCalculationServiceLocator() {
    }


    public RouteCalculationServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public RouteCalculationServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for RouteCalculationPort
    private java.lang.String RouteCalculationPort_address = "http://localhost:8088/isearch-otnRouteService/services/RouteCalculation";

    public java.lang.String getRouteCalculationPortAddress() {
        return RouteCalculationPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String RouteCalculationPortWSDDServiceName = "RouteCalculationPort";

    public java.lang.String getRouteCalculationPortWSDDServiceName() {
        return RouteCalculationPortWSDDServiceName;
    }

    public void setRouteCalculationPortWSDDServiceName(java.lang.String name) {
        RouteCalculationPortWSDDServiceName = name;
    }

    public com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculation getRouteCalculationPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(RouteCalculationPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getRouteCalculationPort(endpoint);
    }

    public com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculation getRouteCalculationPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationServiceSoapBindingStub _stub = new com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getRouteCalculationPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setRouteCalculationPortEndpointAddress(java.lang.String address) {
        RouteCalculationPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculation.class.isAssignableFrom(serviceEndpointInterface)) {
                com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationServiceSoapBindingStub _stub = new com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationServiceSoapBindingStub(new java.net.URL(RouteCalculationPort_address), this);
                _stub.setPortName(getRouteCalculationPortWSDDServiceName());
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
        if ("RouteCalculationPort".equals(inputPortName)) {
            return getRouteCalculationPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://api.otnRouteService.isearch.opentnms.zznode.com/", "RouteCalculationService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://api.otnRouteService.isearch.opentnms.zznode.com/", "RouteCalculationPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("RouteCalculationPort".equals(portName)) {
            setRouteCalculationPortEndpointAddress(address);
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
