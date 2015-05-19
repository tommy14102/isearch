package com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.test;



public class Graph {

  public static void main(String[] args)
  {
    // TODO Auto-generated method stub
    Map M = new Map(12);
    City a = new City("a");
    City b = new City("b");
    City c = new City("c");
    City d = new City("d");
    City e = new City("e");
    City f = new City("f");
    City g = new City("g");
    City h = new City("h");
    City i = new City("i");
    City j = new City("j");
    City k = new City("k");
    City l = new City("l");
    
    M.createEdge(a,b,3);
    M.createEdge(a,c,5);
    M.createEdge(a,d,4);
    
    M.createEdge(b,f,6);
    
    M.createEdge(c,d,2);
    M.createEdge(c,g,4);
    
    M.createEdge(d,e,1);
    M.createEdge(d,h,5);
    
    M.createEdge(e,f,2);
    M.createEdge(e,i,4);
    
    M.createEdge(f,j,5);
    
    M.createEdge(g,h,3);
    M.createEdge(g,k,6);
    
    M.createEdge(h,i,6);
    M.createEdge(h,k,7);
    
    M.createEdge(i,j,3);
    M.createEdge(i,l,5);
    
    M.createEdge(j,l,9);
    
    M.createEdge(k,l,8);
    
    System.out.println("the graph is:\n");
    System.out.println(M);
    
  
    System.out.println();
    System.out.println("findPathByDFS:a to l");
    M.findPathByDFS(a,l);
    
    System.out.println();
    System.out.println("findPathByBFS:a to l");
    M.findPathByBFS(a,l);
    
    System.out.println();
    System.out.println("bellmanFord from a:");
    M.bellmanFord(a);
    
    System.out.println();
    System.out.println("dijkstra from a:");
    M.dijkstra(a);
    
    System.out.println();
    System.out.println("bellmanFord,print example from a:");
    M.floydWarshall();
    M.printFloydWarshallForOneCity(a);
  }

}