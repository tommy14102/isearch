package com.zznode.opentnms.isearch.routeAlgorithm.plugin.algorithm.test;

public class City 
{
  String name; 
  int id;
  static int idCounter = 0;
  public City(String name) 
  {
    this.name=name;
    id = idCounter++;
  }
}
