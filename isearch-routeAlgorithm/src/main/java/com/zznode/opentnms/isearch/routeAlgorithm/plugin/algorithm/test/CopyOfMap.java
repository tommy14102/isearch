package com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;



public class CopyOfMap 
{
	
	Set<Integer> openSet =new HashSet<Integer>();   // 未处理过的节点
    Set<Integer> closedSet =new HashSet<Integer>();  //已经处理过的节点
    HashMap<Integer, LinkedHashSet<Integer>> path=new HashMap<Integer,LinkedHashSet<Integer>>();//封装路径信息  
    // 用来存放起始点到其它点当前的距离  
    HashMap<Integer, Double> distanceMap = new HashMap<Integer, Double>();  
    double unreachable = Integer.MAX_VALUE ; 
    
    
  double[][] A;
  public CopyOfMap(int n)
  {
    A = new double[n][n];
       for(int i = 0;i < A.length;i++)
       {
             for(int j = 0;j < A.length;j++)
             {
          	   if(i == j) A[i][j] = 0;
          	   else A[i][j] = -1;
             }
       }
    
  }
  ArrayList<City> cities = new ArrayList<City>();
  
  private double[] D;
  private void relax(int u,int v)
  {
        if(D[v]>D[u]+A[v][u])  
        	D[v]=D[u]+A[v][u];
  }
  
  private double[][] DD = null;
  public void floydWarshall()
  {
     DD = new double[A.length][A.length];
     int[][] p = new int[A.length][A.length];
       int i,j,k;
       for(i = 0;i < A.length;i++)
       {
             for(j = 0;j < A.length;j++)
             {
          	   if(A[i][j]>0)
                   DD[i][j] = A[i][j];
          	   else if(i == j)  DD[i][j] = 0;
          	   else DD[i][j] = 99999999;
          	   
          	   p[i][j] = j;

             }
       }
       
       int ii = 0;
       //for(ii = 0;ii < A.length;ii++)
       for(k = 0;k < A.length;k++)
    	   for(int jl = 0;jl < A.length;jl++)
       {
        if(DD[ii][jl] > DD[ii][k] + DD[k][jl])
        {
            DD[ii][jl] = DD[ii][k] + DD[k][jl]; 
            p[ii][jl] = p[ii][k];//更新下一跳的点
        }
       }
       System.out.println(DD);
  }
  
  public void printFloydWarshallForOneCity(City city)
  {
    System.out.println("floydWarshall:");
    if(DD == null)
    {
      floydWarshall();
    }
    for(int i=0;i<A.length;i++)
    {
      System.out.printf("from %s to %s shortest path is:%f\n",city.name,cities.get(i).name,DD[city.id][i]);
    }
    
  }
  
  public void dijkstra(City city)
  {
    dijkstra(city.id);
    System.out.println("dijkstra:");
    for(int i=0;i<A.length;i++)
    {
      System.out.printf("from %s to %s shortest path is:%f\n", city.name,cities.get(i).name,distanceMap.get(i));
    }
  }
  
  public void dijkstra(int  startindex)
  {
	  
	  	
	    
	    closedSet.add(startindex);
	    for (int i = 0; i < A.length; i++) {  
        	
        	// 用start相邻的点初始化distanceMap  
        	if( A[startindex][i] == -1 ){
        		distanceMap.put(i, unreachable);  
        	}
        	else{
        		distanceMap.put(i, A[startindex][i]);  
        		LinkedHashSet<Integer> pathset = new LinkedHashSet<Integer>();
        		pathset.add(startindex);
        		pathset.add(i);
        		path.put(i, pathset);
        	}
        	
        	//所有的节点都缴入到未处理节点中
        	if( i != startindex ){
        		openSet.add(i);
        	}
        } 
	    
	    while ( closedSet.size() != A.length) {  
            int currentMinIndex = currentMinIndex();  
            if (currentMinIndex == -1 ){
            	break ;
            }
            closedSet.add(currentMinIndex);  
            openSet.remove(currentMinIndex);
            
            // 用此结点更新其它未处理结点的距离  
            for (Integer openNodeindex : openSet) {
				if( A[currentMinIndex][openNodeindex]!= -1  ){
					
					if( distanceMap.get(openNodeindex) == unreachable ){
						distanceMap.put(openNodeindex,  A[currentMinIndex][openNodeindex]  + distanceMap.get(currentMinIndex)); 
						LinkedHashSet<Integer> pathset = new LinkedHashSet<Integer>();
						pathset.addAll( path.get(currentMinIndex)) ;
		        		pathset.add(openNodeindex);
						path.put(openNodeindex, pathset);
					}
					
					if( A[currentMinIndex][openNodeindex]  + distanceMap.get(currentMinIndex) < distanceMap.get(openNodeindex)){
						distanceMap.put(openNodeindex,  A[currentMinIndex][openNodeindex]  + distanceMap.get(currentMinIndex));  
						LinkedHashSet<Integer> pathset = new LinkedHashSet<Integer>();
						pathset.addAll( path.get(currentMinIndex)) ;
		        		pathset.add(openNodeindex);
						path.put(openNodeindex, pathset);
					}
				}
			}
        }
  }
  
  private int currentMinIndex() {  
	  
	  Iterator<Entry<Integer, Double>> it = distanceMap.entrySet().iterator();  
      double min = Integer.MAX_VALUE;  
      int minIndex = -1;  
      while (it.hasNext()) {  
          Entry<Integer, Double> entry = it.next();  
          if ( openSet.contains(entry.getKey()) && entry.getValue() < min) {  
               min = entry.getValue();  
               minIndex = entry.getKey();  
          }  
      }  
      return minIndex;  
  }   
  
  public void bellmanFord(City city)
  {
    bellmanFord(city.id);
    System.out.println("bellmanFord:");
    for(int i=0;i<A.length;i++)
    {
      System.out.printf("from %s to %s shortest path is:%f\n", city.name,cities.get(i).name,D[i]);
    }
  }
  

  public void bellmanFord(int srcId)
  {
    D = new double[A.length];
    for(int i=0;i<A.length;i++)
    {
      D[i] = 99999999;//无穷大
    }
    D[srcId] = 0;
    for(int i=0;i<A.length;i++)//外层循环次数
    {
      for(int j=0;j<A.length;j++)
      {
        for(int k=0;k<A.length;k++)
        {
          if(A[j][k]>0)
          {
            relax(j,k);
          }
        }
      }
      
    }
  }
  
  
  Queue<Integer> bfsQueue = new LinkedList<Integer>();
  boolean[] bfsFlag;
  int bsfPre[];
  public void findPathByBFS(City src,City dst)
  {
    System.out.printf("bfs find path between '%s' and '%s'!\n",src.name,dst.name);
    findPathByBFS( src.id, dst.id);
    printBFS(dst.id);
    
  }
  public void findPathByBFS(int srcId,int dstId)
  {
    bsfPre = new int[A.length];
    bfsQueue.clear();
    bfsFlag = new boolean[A.length];
    for(int i=0;i<A.length;i++)
    {
      bfsFlag[i] = false;
      bsfPre[i] = -1;
    }
    

    bfsQueue.offer(srcId);
    bfsFlag[srcId] = true;
    
    while(!bfsQueue.isEmpty())
    {
      int current = bfsQueue.poll();
      for(int index=0;index<A.length;index++)
      {
        if(current == index) continue;
        if(A[current][index]>0) //两者相连
        {
          if(index == dstId)//找到目标了
          {
            bfsFlag[index] = true;
            bsfPre[index] = current;
            return;//直接返回
          }
          if(bfsFlag[index] == false)//如果未访问过
          {
            bfsFlag[index] = true;
            bsfPre[index] = current;
            bfsQueue.offer(index);
          }
        }
        
      }
    }
    
  
    

    
  }
  
  private void printBFS(int dstId)
  {
    int index = dstId;
    
    do
    {
      System.out.printf("<-%s", cities.get(index).name);
      index = bsfPre[index];
    }while(index != -1);
    System.out.println();
  }
  
  ArrayList<Integer> dfsPath = new ArrayList<Integer>();
  boolean[] dfsFlag;
  private void printDFS()
  {
    for(Integer node:dfsPath)
    {
      System.out.printf("->%s", cities.get(node).name);
    }
    System.out.println();
  }
  public void findPathByDFS(City src,City dst)
  {
    System.out.printf("dfs find path between '%s' and '%s'!\n",src.name,dst.name);
    findPathByDFS(src.id, dst.id);
  }
  public void findPathByDFS(int srcId,int dstId)
  {
    dfsPath.clear();
    dfsFlag = new boolean[A.length];
    for(int i=0;i<A.length;i++)
    {
      dfsFlag[i] = false;
    }
    dfsPath.add(srcId);
    dfsFlag[srcId] = true;
    dfs( srcId, dstId);
    printDFS();
  }
  private void dfs(int srcId,int dstId)
  {
    for(int index=0;index<A[srcId].length;index++)
    {
      if(srcId == index) continue;
      if(A[srcId][index]>0)//两者连接
      {
        if(index == dstId)//找到目标了
        {
          dfsFlag[index] = true;
          dfsPath.add(index);
          return;
        }
        if(dfsFlag[index] == false)//如果该节点未访问过
        {
          dfsFlag[index] = true;
          dfsPath.add(index);
          dfs(index,dstId);
          if(dfsFlag[dstId] == false)//目标没找到
            dfsPath.remove(index);
          else return;
        }
        
      }
    }
  }
  
  public void createEdge(City a, City b, double w) 
  {
    A[a.id][b.id]=w;
    A[b.id][a.id]=w;//added by me!
    cities.add(a.id,a);
    cities.add(b.id,b);
  }
  
  public String toString()
  {
    String r = "I am a map of " + A.length + " cities."; 
    r += " My connections are:\n";
    for (int i=0;i<A.length;i++) 
    {
      for (int j=0;j<A[0].length;j++)
        r+=A[i][j]+"\t";
      r+="\n";
    }
    return r;
  }
}