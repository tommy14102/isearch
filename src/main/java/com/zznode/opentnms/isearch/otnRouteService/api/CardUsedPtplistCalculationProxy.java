package com.zznode.opentnms.isearch.otnRouteService.api;

public class CardUsedPtplistCalculationProxy implements com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtplistCalculation {
  private String _endpoint = null;
  private com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtplistCalculation cardUsedPtplistCalculation = null;
  
  public CardUsedPtplistCalculationProxy() {
    _initCardUsedPtplistCalculationProxy();
  }
  
  public CardUsedPtplistCalculationProxy(String endpoint) {
    _endpoint = endpoint;
    _initCardUsedPtplistCalculationProxy();
  }
  
  private void _initCardUsedPtplistCalculationProxy() {
    try {
      cardUsedPtplistCalculation = (new com.zznode.opentnms.isearch.otnRouteService.api.impl.CardUsedPtplistCalculationImplServiceLocator()).getCardUsedPtplistCalculationImplPort();
      if (cardUsedPtplistCalculation != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)cardUsedPtplistCalculation)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)cardUsedPtplistCalculation)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (cardUsedPtplistCalculation != null)
      ((javax.xml.rpc.Stub)cardUsedPtplistCalculation)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtplistCalculation getCardUsedPtplistCalculation() {
    if (cardUsedPtplistCalculation == null)
      _initCardUsedPtplistCalculationProxy();
    return cardUsedPtplistCalculation;
  }
  
  public com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpOutput cardUsedPtp(com.zznode.opentnms.isearch.otnRouteService.api.CardUsedPtpInput inputs) throws java.rmi.RemoteException, com.zznode.opentnms.isearch.otnRouteService.api.RouteCalculationFault{
    if (cardUsedPtplistCalculation == null)
      _initCardUsedPtplistCalculationProxy();
    return cardUsedPtplistCalculation.cardUsedPtp(inputs);
  }
  
  
}