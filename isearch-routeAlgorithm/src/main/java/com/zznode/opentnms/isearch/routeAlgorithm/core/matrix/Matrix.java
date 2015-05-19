package com.zznode.opentnms.isearch.routeAlgorithm.core.matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Direction;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Node;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Section;

public class Matrix implements Serializable{

	private static final long serialVersionUID = 8882346066911346996L;
	
	public int size;
	public Section[][] matrix;
	public int nowsize ; 
	
	
	//节点与矩阵元素的对应关系
	private BidiMap pointMap = new DualHashBidiMap();
	private HashMap<String,Node> nodeValMap = new HashMap<String,Node>();
	
	public Matrix(int size){
		this.size = size ; 
		matrix = new Section[size][size];
	}
	
	public Matrix(int size , Section[][] matrix){
		this.size = size ; 
		this.matrix = matrix;
	}

	/**
	 * 添加邻接矩阵的边
	 * 参数为起点和终端的ncdid，是否单向
	 * 
	 * @param apoint
	 * @param zpoint
	 */
	public void addArc(Node aendNode , Node zendNode , Direction direction , Link topo){
		
		addArcSingle(aendNode, zendNode, topo);
		if( direction.equals( Direction.DOUBLE )){
			addArcSingle(zendNode , aendNode, topo);
		}
		
	}
	
	public void addArcSingle(Node aendNode , Node zendNode , Link topo){
		
		String aendNodeid = aendNode.getId();
		String zendNodeid = zendNode.getId();
		
		nodeValMap.put(aendNodeid, aendNode);
		nodeValMap.put(zendNodeid, zendNode);
		
		Integer aindex = (Integer)pointMap.get(aendNodeid);
		if( aindex == null ){
			pointMap.put(aendNodeid, nowsize++);
		}
		
		Integer zindex = (Integer)pointMap.get(zendNodeid);
		if( zindex == null ){
			pointMap.put(zendNodeid, nowsize++);
		}
				
		aindex = (Integer)pointMap.get(aendNodeid);
		zindex = (Integer)pointMap.get(zendNodeid);
		
		if( matrix[aindex][zindex] == null ){
			Section section = new Section();
			section.setId(topo.getId());
			section.setAendNode(aendNodeid);
			section.setZendNode(zendNodeid);
			
			List<Link> linklist = new ArrayList<Link>();
			linklist.add(topo);
			section.setLinklist(linklist);
			matrix[aindex][zindex] = section;	
		}
		else{
			Section section = matrix[aindex][zindex] ;
			section.getLinklist().add(topo);
		}
	}
	
	
	
	/**
	 * 展示邻接矩阵
	 */
	public String showSearchInfo() { 
		
	    StringBuilder sb = new StringBuilder("\r\n");
	    
		for(int i = 0; i < size; i++){
		    sb.append( i + ":" + pointMap.getKey(i) + "\r\n");
		}
		sb.append("----------\r\n");
		
		sb.append("   ");
        for(int i = 0; i < size; i++){
            sb.append(String.format("%2d", i));  
        }
        sb.append("\r\n");   
          
        int lable = 0;  
        for(Section[] a : matrix) {  
            sb.append(lable++ + " [");  
            for(Section i : a){
                if( i !=null){
                    sb.append(String.format("%2s", i.getAttrMap().get("showinfo")==null ? " " :i.getAttrMap().get("showinfo")  ));  
                }else{
                    sb.append(String.format("%2s", " "));  
                }
            }  
            sb.append(" ]"+ "\r\n");  
        }  
        
        return sb.toString() ; 
    }  
	
	
	/**
	 * 展示邻接矩阵
	 */
	public String show() { 
		
	    StringBuilder sb = new StringBuilder("\r\n");
	    
		for(int i = 0; i < size; i++){
		    sb.append( i + ":" + pointMap.getKey(i) + "\r\n");
		}
		sb.append("----------\r\n");
		
		sb.append("   ");
        for(int i = 0; i < size; i++){
            sb.append(String.format("%2d", i));  
        }
        sb.append("\r\n");   
          
        int lable = 0;  
        for(Object[] a : matrix) {  
            sb.append(lable++ + " [");  
            for(Object i : a){
                if( i !=null){
                    sb.append(String.format("%2s", "*"));  
                }else{
                    sb.append(String.format("%2s", " "));  
                }
            }  
            sb.append(" ]"+ "\r\n");  
        }  
        
        return sb.toString() ; 
    }  
	
	
	
	public Section[][] getMatrix(){
		return matrix;
	}

	public void setMatrix(Section[][] matrix) {
		this.matrix = matrix;
	}

    public BidiMap getPointMap() {
        return pointMap;
    }

    public void setPointMap(BidiMap pointMap) {
        this.pointMap = pointMap;
    }

    

}
