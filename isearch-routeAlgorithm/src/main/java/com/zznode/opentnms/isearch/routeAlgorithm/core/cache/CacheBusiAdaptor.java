package com.zznode.opentnms.isearch.routeAlgorithm.core.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("CacheBusiAdaptor")
public class CacheBusiAdaptor {
	
    @Autowired
    @Qualifier("SPtnMemcachedClient")
	private SPtnMemcachedClient cacheObj ;
    
    
    
	private static Map<String,Long> topoVersionMap = new HashMap<String,Long>();
	
	
	/**
	public void addTopoMapToCache(Map<String, E2ETopology> topoMap) {
	
	    int expiretime = 60 * 60 * 24 -1 ; 
	    long tag = System.currentTimeMillis();
	    
	    //首次加载，缓存topoVersionMap，并且添加到缓存中。
	    for (Map.Entry<String, E2ETopology> topology : topoMap.entrySet()) {
	        
	        String ncdid = topology.getKey();
	        topoVersionMap.put( ncdid + SptnMemcachedKeys.VersionStr  , tag);
	        cacheObj.set(ncdid + SptnMemcachedKeys.VersionStr, expiretime, tag);
            
        }
	        
	}
	
	private E2ETopology getSingleTopology( String ncdid ) throws Exception{
		
		return e2eResourceService.getSingleTopology(ncdid) ; 
	}

	public Map<String, E2ETopology> freshTopoMap(List<TopologyPo> alltopology, Map<String, E2ETopology> topoMap) throws Exception {
		
	    int expiretime = 60 * 60 * 24 -1 ; 
        long tag = System.currentTimeMillis();
        
		Map<String, E2ETopology> freshTopoMap = new HashMap<String, E2ETopology>();
		
		for (TopologyPo topology : alltopology) {
		    
		    String ncdid = topology.getNcd_id();
			
			String versionKey = ncdid  + SptnMemcachedKeys.VersionStr;
			
			//查询缓存判断是否有变化
			Long version = topoVersionMap.get( versionKey );
			Long cacheVersion =  (Long)cacheObj.get( versionKey );
			
			//如果缓存过期，重新加入
            if( cacheVersion == null ){
                topoVersionMap.put( versionKey , tag ) ; 
                cacheObj.set( versionKey , expiretime, tag);
                E2ETopology e2etopology =  getSingleTopology (ncdid);
                freshTopoMap.put( ncdid , e2etopology );
                cacheObj.set( SptnMemcachedKeys.NCD_TOPOLOGY + ncdid  , expiretime, e2etopology );
            }
			
			//如果本地没有，那么需要加入
            else if( version ==null ){
				topoVersionMap.put( versionKey , cacheVersion) ; 
				Object e2etopology  = cacheObj.get(SptnMemcachedKeys.NCD_TOPOLOGY + ncdid);
                if( e2etopology == null){
                    E2ETopology topo =  getSingleTopology (ncdid);
                    freshTopoMap.put( ncdid , topo ); 
                    cacheObj.set( SptnMemcachedKeys.NCD_TOPOLOGY + ncdid  , expiretime, topo);
                }
                else{
                    freshTopoMap.put( ncdid , (E2ETopology)e2etopology ); 
                }
			}
			
			//如果不同说明有变化，重新加载
			else if(!version.equals( cacheVersion )){
				topoVersionMap.put( versionKey , cacheVersion) ; 
				Object e2etopology  = cacheObj.get(SptnMemcachedKeys.NCD_TOPOLOGY + ncdid);
				if( e2etopology == null){
				    E2ETopology topo =  getSingleTopology (ncdid);
	                freshTopoMap.put( ncdid , topo ); 
	                cacheObj.set( SptnMemcachedKeys.NCD_TOPOLOGY + ncdid  , expiretime, topo);
				}
				else{
				    freshTopoMap.put( ncdid , (E2ETopology)e2etopology ); 
				}
			}
			//如果相同，那么无需重新查询
			else{
				freshTopoMap.put( ncdid , topoMap.get(ncdid) ); 
			}
		}
		
		return freshTopoMap ; 

	}

    public Map<String, E2ETopology> freshSingleTopoMap(TopologyPo po, Map<String, E2ETopology> topoMap) throws Exception {

        int expiretime = 60 * 60 * 24 -1 ; 
        long tag = System.currentTimeMillis();
        String ncdid = po.getNcd_id();
        String versionKey = ncdid  + SptnMemcachedKeys.VersionStr;
        topoVersionMap.put( versionKey , tag ) ; 
        cacheObj.set( versionKey , expiretime, tag);
        E2ETopology e2etopology =  getSingleTopology (ncdid);
        topoMap.put( ncdid , e2etopology );
        cacheObj.set( SptnMemcachedKeys.NCD_TOPOLOGY + ncdid  , expiretime, e2etopology );
        return topoMap;
    }

    public Map<String, E2ETopology> freshAllTopoMap(List<TopologyPo> alltopology) throws Exception {

        int expiretime = 60 * 60 * 24 -1 ; 
        long tag = System.currentTimeMillis();
        
        Map<String, E2ETopology> freshTopoMap = new HashMap<String, E2ETopology>();
        
        for (TopologyPo topology : alltopology) {
            
            String ncdid = topology.getNcd_id();
            
            String versionKey = ncdid  + SptnMemcachedKeys.VersionStr;
            topoVersionMap.put( versionKey , tag ) ; 
            cacheObj.set( versionKey , expiretime, tag);
            E2ETopology e2etopology =  getSingleTopology (ncdid);
            freshTopoMap.put( ncdid , e2etopology );
            cacheObj.set( SptnMemcachedKeys.NCD_TOPOLOGY + ncdid  , expiretime, e2etopology );
        }
        
        return freshTopoMap;
            
    }
    */

   

}
