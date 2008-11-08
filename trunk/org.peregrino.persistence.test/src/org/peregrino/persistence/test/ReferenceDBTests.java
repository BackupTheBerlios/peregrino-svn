/**
 * Tests for the creation, update, and delete of reference entries on a database level.
 */
package org.peregrino.persistence.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import junit.framework.TestCase;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.peregrino.persistence.JdbcReferenceDAO;
import org.peregrino.persistence.Reference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Testing on a database level.
 * 
 * @author tderflinger
 * 
 */
public class ReferenceDBTests extends TestCase {

	private static final String DATASET_MODIFIED_XML = "dataset/modified.xml";

	private static final String DATASET_TWO_XML = "dataset/two.xml";

	private static final String REFERENCE_DAO = "referenceDAO";

	private static final String SPRING_CONFIG_XML = "spring-config.xml";

	private static final String REFERENCE_BEAN2 = "referenceBean2";
	private static final String REFERENCE_BEAN1 = "referenceBean1";
	private static final String REFERENCE_BEAN0 = "referenceBean0";

	private static final String DATASET_FULL_XML = "dataset/full.xml";

	private String dbURL = "jdbc:derby://localhost:1527/referenceDB;create=true;user=me;password=mine";

	private static final String TABLENAME = "refs";

	// jdbc Connection
	private Connection conn = null;
	private Statement stmt = null;

	private ApplicationContext ctx = null;

	@Before
	public void setUp() {
		createConnection();

		this.ctx = new ClassPathXmlApplicationContext(SPRING_CONFIG_XML);
	}

	@After
	public void tearDown() {
		shutdown();
	}

	/**
	 * Connection for dbUnit.
	 */
	private void createConnection() {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			// Get a connection
			conn = DriverManager.getConnection(dbURL);
		} catch (Exception except) {
			except.printStackTrace();
		}
	}

	private IDatabaseConnection getConnection() throws Exception {
		return new DatabaseConnection(conn);
	}

	// to be done only once when exporting
	// private void exportXml() throws Exception {
	// // full database export
	// IDataSet fullDataSet = getConnection().createDataSet();
	// FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));
	// }

	private void shutdown() {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				DriverManager.getConnection(dbURL + ";shutdown=true");
				conn.close();
			}
		} catch (SQLException sqlExcept) {

		}
	}

	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream(DATASET_FULL_XML));
	}

	/**
	 * Does some inserts into the database and compares the resulting table with
	 * the table data from the XML file.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInsert() throws Exception {
		final JdbcReferenceDAO refDAO = (JdbcReferenceDAO) ctx
				.getBean(REFERENCE_DAO);

		refDAO.dropTable();
		refDAO.createTable();
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN0));
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN1));
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN2));

		// Fetch database data after executing your code
		ITable actualTable = getConnection().createDataSet()
				.getTable(TABLENAME);

		// Load expected data from an XML dataset
		IDataSet expectedDataSet = new FlatXmlDataSet(
				new File(DATASET_FULL_XML));
		ITable expectedTable = expectedDataSet.getTable(TABLENAME);

		// dbUnit
		Assertion.assertEquals(expectedTable, actualTable);
	}

	/**
	 * Deletes one row and compares the result to the dbunit xml.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDelete() throws Exception {
		final JdbcReferenceDAO refDAO = (JdbcReferenceDAO) ctx
				.getBean(REFERENCE_DAO);

		refDAO.dropTable();
		refDAO.createTable();
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN0));
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN1));
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN2));

		List<Reference> lReferences = refDAO.getAllReferences();

		Reference ref1 = lReferences.get(1);
		// now remove one reference
		refDAO.deleteReference(ref1);

		// Fetch database data after executing your code
		ITable actualTable = getConnection().createDataSet()
				.getTable(TABLENAME);

		// Load expected data from an XML dataset
		IDataSet expectedDataSet = new FlatXmlDataSet(new File(DATASET_TWO_XML));
		ITable expectedTable = expectedDataSet.getTable(TABLENAME);
		// dbUnit
		Assertion.assertEquals(expectedTable, actualTable);
	}

	/**
	 * Updates one row and compares the result with the dbunit xml.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdate() throws Exception {
		final JdbcReferenceDAO refDAO = (JdbcReferenceDAO) ctx
				.getBean(REFERENCE_DAO);

		refDAO.dropTable();
		refDAO.createTable();
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN0));
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN1));
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN2));

		List<Reference> lReferences = refDAO.getAllReferences();

		Reference ref1 = lReferences.get(1);
		ref1.setCategory(7);
		ref1.setHeader("Strassenbau");
		ref1.setDescription("Hausbau im Hundsrück");

		refDAO.modifyReference(ref1);

		lReferences = refDAO.getAllReferences();

		// Fetch database data after executing your code
		ITable actualTable = getConnection().createDataSet()
				.getTable(TABLENAME);

		// Load expected data from an XML dataset
		IDataSet expectedDataSet = new FlatXmlDataSet(new File(
				DATASET_MODIFIED_XML));
		ITable expectedTable = expectedDataSet.getTable(TABLENAME);

		// dbUnit
		Assertion.assertEquals(expectedTable, actualTable);
	}

	// does some stress testing with 10,000 entries.
	@Test
	public void testStress() throws Exception {
		final JdbcReferenceDAO refDAO = (JdbcReferenceDAO) ctx
				.getBean(REFERENCE_DAO);

		refDAO.dropTable();
		refDAO.createTable();

		for (int i = 0; i < 10000; i++) {
			Reference myRef = (Reference) ctx.getBean(REFERENCE_BEAN0);
			myRef.setCategory(i);
			myRef.setHeader("HEADER TEST\nCOOL...");
			myRef.setDescription("Description test...");
			refDAO.insertReference(myRef);
		}

		// Fetch database data after executing your code
		ITable actualTable = getConnection().createDataSet()
				.getTable(TABLENAME);
		assertEquals(10000, actualTable.getRowCount());
		IDataSet fullDataSet = getConnection().createDataSet();
		FlatXmlDataSet.write(fullDataSet, new FileOutputStream(
				"dataset/tenthousand.xml"));
	}

	@Test
	public void testXmlExport() {

	}

	/**
	 * tests that the jdbcReferenceDAO.getAllReferences() works.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAll() throws Exception {
		final JdbcReferenceDAO refDAO = (JdbcReferenceDAO) ctx
				.getBean(REFERENCE_DAO);

		refDAO.dropTable();
		refDAO.createTable();
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN0));
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN1));
		refDAO.insertReference((Reference) ctx.getBean(REFERENCE_BEAN2));

		List<Reference> lReferences = refDAO.getAllReferences();

		Reference ref0 = lReferences.get(0);
		Reference ref2 = lReferences.get(2);

		// compare the first record
		assertEquals(1, ref0.getId());
		assertEquals(2, ref0.getCategory());
		assertEquals("Hochhausbau", ref0.getHeader());
		assertEquals("Hochhausbau in München", ref0.getDescription());

		// compare the third record
		assertEquals(3, ref2.getId());
		assertEquals(4, ref2.getCategory());
		assertEquals("Brückenbau", ref2.getHeader());
		assertEquals("Brückenbau in Berlin", ref2.getDescription());

		for (Reference ref : lReferences) {
			System.out.println(ref.getId() + "\t\t" + ref.getCategory()
					+ "\t\t" + ref.getHeader() + "\t\t" + ref.getDescription());
		}
	}

}
