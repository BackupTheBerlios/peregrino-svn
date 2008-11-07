package org.peregrino.persistence.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.peregrino.persistence.JdbcReferenceDAO;
import org.peregrino.persistence.Reference;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class JdbcReferenceDAOImpl implements JdbcReferenceDAO {

	private SimpleJdbcTemplate jdbcTemplate;

	private static final String REFERENCE_INSERT = "insert into refs (id, category, header, description) values (DEFAULT, ?, ?, ?)";

	public SimpleJdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	} 

	public void dropTable() {
		this.jdbcTemplate.update("drop table refs");
	}
	
	public void createTable() {
		this.jdbcTemplate
				.update("create table refs(id SMALLINT GENERATED ALWAYS AS IDENTITY, category integer, header varchar(100), description varchar(255))");
	}

	public void insertReference(final Reference pRef) {
		this.jdbcTemplate.update(REFERENCE_INSERT, pRef.getCategory(), pRef
				.getHeader(), pRef.getDescription());
	}

	public void deleteReference(final Reference pRef) {
		this.jdbcTemplate.update("delete from refs where id=" + pRef.getId());
	}

	public void modifyReference(final Reference pRef) {
		this.jdbcTemplate.update("update refs set description='"
				+ pRef.getDescription() + "', category=" + pRef.getCategory()
				+ ", header='" + pRef.getHeader() + "' where id="
				+ pRef.getId());
	}

	public List<Reference> getAllReferences() {
		List<Reference> matches = this.jdbcTemplate.query("select * from refs",
				new ParameterizedRowMapper<Reference>() {

					@Override
					public Reference mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Reference lRef = new ReferenceImpl();
						lRef.setId(rs.getInt(1));
						lRef.setCategory(rs.getInt(2));
						lRef.setHeader(rs.getString(3));
						lRef.setDescription(rs.getString(4));

						return lRef;
					}

				});

		return matches;
	}

}
