package com.zznode.opentnms.isearch.otnRouteService.api.webservice;

public class HelloServiceImplProxy implements com.zznode.opentnms.isearch.otnRouteService.api.webservice.HelloServiceImpl {
  private String _endpoint = null;
  private com.zznode.opentnms.isearch.otnRouteService.api.webservice.HelloServiceImpl helloServiceImpl = null;
  
  public HelloServiceImplProxy() {
    _initHelloServiceImplProxy();
  }
  
  public HelloServiceImplProxy(String endpoint) {
    _endpoint = endpoint;
    _initHelloServiceImplProxy();
  }
  
  private void _initHelloServiceImplProxy() {
    try {
      helloServiceImpl = (new com.zznode.opentnms.isearch.otnRouteService.api.webservice.HelloServiceImplServiceLocator()).getHelloServiceImpl();
      if (helloServiceImpl != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)helloServiceImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)helloServiceImpl)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (helloServiceImpl != null)
      ((javax.xml.rpc.Stub)helloServiceImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.zznode.opentnms.isearch.otnRouteService.api.webservice.HelloServiceImpl getHelloServiceImpl() {
    if (helloServiceImpl == null)
      _initHelloServiceImplProxy();
    return helloServiceImpl;
  }
  
  public java.lang.String say(java.lang.String name) throws java.rmi.RemoteException{
    if (helloServiceImpl == null)
      _initHelloServiceImplProxy();
    return helloServiceImpl.say(name);
  }
  
  
}