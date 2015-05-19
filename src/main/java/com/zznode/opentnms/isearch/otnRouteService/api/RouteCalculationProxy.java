package com.zznode.opentnms.isearch.otnRouteService.api;

public class RouteCalculationProxy implements com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculation {
  private String _endpoint = null;
  private com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculation routeCalculation = null;
  
  public RouteCalculationProxy() {
    _initRouteCalculationProxy();
  }
  
  public RouteCalculationProxy(String endpoint) {
    _endpoint = endpoint;
    _initRouteCalculationProxy();
  }
  
  private void _initRouteCalculationProxy() {
    try {
      routeCalculation = (new com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationServiceLocator()).getRouteCalculationPort();
      if (routeCalculation != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)routeCalculation)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)routeCalculation)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (routeCalculation != null)
      ((javax.xml.rpc.Stub)routeCalculation)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculation getRouteCalculation() {
    if (routeCalculation == null)
      _initRouteCalculationProxy();
    return routeCalculation;
  }
  
  public com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationOutput calculate(com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationInput inputs) throws java.rmi.RemoteException, com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationFault{
    if (routeCalculation == null)
      _initRouteCalculationProxy();
    return routeCalculation.calculate(inputs);
  }
  
  
}