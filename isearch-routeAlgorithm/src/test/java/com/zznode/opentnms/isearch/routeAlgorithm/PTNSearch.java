package com.zznode.opentnms.isearch.routeAlgorithm;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.zznode.opentnms.isearch.routeAlgorithm.api.ISearch;
import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Direction;
import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.BusinessAvator;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorParam;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.CaculatorResult;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Node;
import com.zznode.opentnms.isearch.routeAlgorithm.core.cache.SPtnMemcachedClient;
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.caculator_dijkstra.Dijkstra;
import com.zznode.opentnms.isearch.routeAlgorithm.plugin.data.importor_base.BaseImporter;

public class PTNSearch {
  
	
	@Test
	public void testIssue(){
	    ISearch isearch = new ISearch();
	    
	    BusinessAvator businessAvator = new BusinessAvator();
	    businessAvator.setKey("PTN");
	    isearch.regist(businessAvator);
	    
	    List<Link> linklist  = new ArrayList<Link>();
	    Link linka = new Link();
	    linka.setAendnode(new Node("A"));
	    linka.setZendnode(new Node("A"));
	    linka.setDirection(Direction.DOUBLE);
	    linka.setJump(1L);
	    linklist.add(linka);
	    
	    Link linkab = new Link();
	    linkab.setAendnode(new Node("A"));
	    linkab.setZendnode(new Node("B"));
	    linkab.setDirection(Direction.DOUBLE);
	    linkab.setJump(1L);
	    linklist.add(linkab);
	    
	    Link linkb = new Link();
	    linkb.setAendnode(new Node("B"));
	    linkb.setZendnode(new Node("B"));
	    linkb.setDirection(Direction.DOUBLE);
	    linkb.setJump(1L);
	    linklist.add(linkb);
	    
	    Link linkbc = new Link();
	    linkbc.setAendnode(new Node("B"));
	    linkbc.setZendnode(new Node("C"));
	    linkbc.setDirection(Direction.DOUBLE);
	    linkbc.setJump(1L);
	    linklist.add(linkbc);
	    
	    Link linkac = new Link();
	    linkac.setAendnode(new Node("A"));
	    linkac.setZendnode(new Node("C"));
	    linkac.setDirection(Direction.DOUBLE);
	    linkac.setJump(1L);
	    linklist.add(linkac);
	    
	    Link linkcd = new Link();
	    linkcd.setAendnode(new Node("C"));
	    linkcd.setZendnode(new Node("D"));
	    linkcd.setDirection(Direction.DOUBLE);
	    linkcd.setJump(1L);
	    linklist.add(linkcd);
	    
	    SPtnMemcachedClient testobj = (SPtnMemcachedClient)App.factory.getBean("SPtnMemcachedClient");
	    BaseImporter baseImporter = new BaseImporter(linklist);
	    
	    isearch.refreshdata(businessAvator.getKey(), baseImporter);
	    
	    
	    CaculatorParam param = new CaculatorParam();
	    param.setAend("A");
	    param.setZend("C");
	    param.setCount(2);
	    param.setPolicy(Policy.LESS_JUMP);
	    
	    
	    CaculatorResult result = isearch.search(businessAvator.getKey(), param, new Dijkstra());
	    System.out.println(result);
	}
	
}
