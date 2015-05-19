package com.zznode.opentnms.isearch.otnRouteService.db;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class DBUtil {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public  <T> List<T> query(String sql, RowMapper<T> rowMapper){
		
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	 // 设置数据源
	 public void setDataSource(DataSource dataSource) {
		 this.dataSource = dataSource ;
		 this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	 
	 
}
