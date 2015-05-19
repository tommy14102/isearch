package com.zznode.opentnms.isearch.otnRouteService;

public class OT {

	public Integer s = 7;
	public void say(){
		System.out.println(1);
	}
	
	public static void main(String[] args) {
		OT ot = new OTC();
		ot.say();
		System.out.println(ot.s);
	}
}

class OTC extends OT{
	public Integer s = 8;
	public void say(){
		System.out.println(2);
	}
}