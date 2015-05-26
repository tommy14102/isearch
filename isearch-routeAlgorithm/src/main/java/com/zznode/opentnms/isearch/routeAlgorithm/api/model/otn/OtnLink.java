package com.zznode.opentnms.isearch.routeAlgorithm.api.model.otn;

import java.io.Serializable;
import java.util.Map;

import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Direction;
import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Policy;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;

public class OtnLink extends Link implements Serializable{

	private static final long serialVersionUID = 3732620052975132971L;
	
	private OtnNode aendnode ; 
	private OtnNode zendnode ;
	
	
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
	


	public Map getAttrMap() {
		return attrMap;
	}
	public void setAttrMap(Map attrMap) {
		this.attrMap = attrMap;
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


	public OtnNode getAendnode() {
		return aendnode;
	}


	public void setAendnode(OtnNode aendnode) {
		this.aendnode = aendnode;
	}


	public OtnNode getZendnode() {
		return zendnode;
	}


	public void setZendnode(OtnNode zendnode) {
		this.zendnode = zendnode;
	}


	



	
	
	
	

}
