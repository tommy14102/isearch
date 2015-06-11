package com.zznode.opentnms.isearch.routeAlgorithm.api.model;

import java.io.Serializable;
import java.util.Map;

import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Direction;
import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;

public class Link implements Serializable{

	protected static final long serialVersionUID = 3732620052975132971L;
	
	protected String id ;
	protected Long jump ;
	protected Long latency ;
	protected Long avilableWidth ;
	protected Direction direction ;
	protected Node aendnode;
	protected Node zendnode;
	private int linkindex ; 
	
	// 边的权重
	public Long getWeight(Policy policy){
		
		if( policy.equals( Policy.LESS_JUMP) ){
			if( jump ==null ){
				return 1L ; 
				//throw new RuntimeException("缺少路由信息 ");
			}
			return jump;
		}
	
		if( policy.equals( Policy.MINIMUN_LATENCY) ){
			if( latency ==null ){
				throw new RuntimeException("缺少时延信息");
			}
			return latency;
		}
	
		if( policy.equals( Policy.WIDTH_BALANCE) ){
			if( avilableWidth ==null ){
				throw new RuntimeException("缺少剩余带宽信息 ");
			}
			return avilableWidth;
		}
	
		return  null; 
	}
	
	public int getLinkindex() {
		return linkindex;
	}
	public void setLinkindex(int linkindex) {
		this.linkindex = linkindex;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getLatency() {
		return latency;
	}
	public void setLatency(Long latency) {
		this.latency = latency;
	}
	public Long getAvilableWidth() {
		return avilableWidth;
	}
	public void setAvilableWidth(Long avilableWidth) {
		this.avilableWidth = avilableWidth;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}


	public Long getJump() {
		return jump;
	}


	public void setJump(Long jump) {
		this.jump = jump;
	}


	public Node getAendnode() {
		return aendnode;
	}


	public void setAendnode(Node aendnode) {
		this.aendnode = aendnode;
	}


	public Node getZendnode() {
		return zendnode;
	}


	public void setZendnode(Node zendnode) {
		this.zendnode = zendnode;
	} 

	
	
	

}
