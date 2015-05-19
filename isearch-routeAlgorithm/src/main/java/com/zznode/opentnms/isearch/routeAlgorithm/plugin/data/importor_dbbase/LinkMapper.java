package com.zznode.opentnms.isearch.routeAlgorithm.plugin.data.importor_dbbase;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zznode.opentnms.isearch.routeAlgorithm.api.enumrate.Direction;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Node;

public class LinkMapper implements RowMapper<Link> {

	private DBConfig dBConfi ;
	
	public LinkMapper(DBConfig dBConfig){
		dBConfi = dBConfig ; 
	}
	
	public Link mapRow(ResultSet resultset, int arg1) throws SQLException {
		Link link = new Link();
		link.setId(resultset.getString("objectid"));
		link.setAendnode( new Node(resultset.getString(dBConfi.getAendnode())));
		link.setZendnode( new Node(resultset.getString(dBConfi.getZendnode())));
		link.setDirection(Direction.definde(resultset.getString(dBConfi.getDirection())));
		
		return link  ; 
	}


}
