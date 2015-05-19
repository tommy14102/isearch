package com.zznode.opentnms.isearch.routeAlgorithm.plugin.data.importor_dbbase;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class DBUtil {

	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
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
