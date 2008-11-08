package org.peregrino.persistence;

import java.io.OutputStream;
import java.util.List;

public interface JdbcReferenceDAO {

	public void createTable();
	
	public void dropTable();
	
	public void insertReference(final Reference pRef);
	
	public List<Reference> getAllReferences();
	
	public void deleteReference(final Reference pRef);
	
	public void modifyReference(final Reference pRef);
	
	public OutputStream exportXml() throws Exception ;
}