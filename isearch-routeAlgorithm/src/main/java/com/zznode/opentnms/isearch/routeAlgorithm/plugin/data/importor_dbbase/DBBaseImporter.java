package com.zznode.opentnms.isearch.routeAlgorithm.plugin.data.importor_dbbase;

import java.util.List;

import org.apache.log4j.Logger;

import com.zznode.opentnms.isearch.routeAlgorithm.App;
import com.zznode.opentnms.isearch.routeAlgorithm.api.model.Link;
import com.zznode.opentnms.isearch.routeAlgorithm.core.data.DataProcessor;

public class DBBaseImporter extends DataProcessor{

	private DBConfig dBConfi ;
	private static Logger log = Logger.getLogger(DBBaseImporter.class);
	private DBUtil testobj = (DBUtil)App.factory.getBean("DBUtil");
	
	
	public DBBaseImporter(DBConfig dBConfig){
		dBConfi = dBConfig ; 
	}
	 
	
	 
	@Override
	public List<Link> argnizeData() {

		@SuppressWarnings("unchecked")
		List<Link> list = testobj.getJdbcTemplate().query(dBConfi.getSql(), new LinkMapper(dBConfi));
		 return list;
	}
	
	
	
	
	
	/**
	public void save(Person person) {
        jdbcTemplate.update("insert into person(name,age,sex)values(?,?,?)",
                new Object[] { person.getName(), person.getAge(),
                        person.getSex() }, new int[] { java.sql.Types.VARCHAR,
                        java.sql.Types.INTEGER, java.sql.Types.VARCHAR });
    }

    public void update(Person person) {
        jdbcTemplate.update("update person set name=?,age=?,sex=? where id=?",
                new Object[] { person.getName(), person.getAge(),
                        person.getSex(), person.getId() }, new int[] {
                        java.sql.Types.VARCHAR, java.sql.Types.INTEGER,
                        java.sql.Types.VARCHAR, java.sql.Types.INTEGER });

    }

    public Person getPerson(Integer id) {
        Person person = (Person) jdbcTemplate.queryForObject(
                "select * from person where id=?", new Object[] { id },
                new int[] { java.sql.Types.INTEGER }, new PersonRowMapper());
        return person;

    }

    @SuppressWarnings("unchecked")
    public List<Person> getPerson() {
        List<Person> list = jdbcTemplate.query("select * from person", new PersonRowMapper());
        return list;

    }

    public void delete(Integer id) {
        jdbcTemplate.update("delete from person where id = ?", new Object[] { id },
                new int[] { java.sql.Types.INTEGER });

    }
    */
    


}
