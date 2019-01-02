package ch.fhnw.swc.mrs.data;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.dbunit.Assertion;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DefaultPrepAndExpectedTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.PrepAndExpectedTestCase;
import org.dbunit.PrepAndExpectedTestCaseSteps;
import org.dbunit.VerifyTableDefinition;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchColumnException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import ch.fhnw.swc.mrs.model.PriceCategory;
import ch.fhnw.swc.mrs.model.User;
import ch.fhnw.swc.mrs.util.LocalDateConverter;
import static ch.fhnw.swc.mrs.data.IsBeforeDate.isBefore;

@Tag("integration")
public class ITUserDao {

	/** Class under test: UserDAO. */
	private UserDAO dao;
    private Connection connection;
	private static EmbeddedPostgres pg;
	private static boolean firstTest = true;
	
	private PrepAndExpectedTestCase tc;
	private static VerifyTableDefinition vtd;
	@SuppressWarnings("rawtypes")
	private static Map<Class, Converter> converters = new HashMap<>();

    private static final String COUNT_SQL = "SELECT COUNT(*) FROM clients";

    // the connection string to the database 
	private Sql2o sql2o;
	
	@BeforeAll
	public static void startPostgresql() throws IOException {
		pg = EmbeddedPostgres.start();

        PriceCategory.init();
        vtd = new VerifyTableDefinition("clients", new String[] {"id"});
        converters.put(UUID.class, new UUIDConverter());
        converters.put(LocalDate.class, new LocalDateConverter());
	}
	
	/**
	 * Initialize a DBUnit DatabaseTester object to use in tests.
	 * 
	 * @throws Exception
	 *             whenever something goes wrong.
	 */
	@BeforeEach
	public void setUp() throws Exception {      
        DataSource ds = pg.getPostgresDatabase();
        connection = ds.getConnection();

        // count no. of rows before deletion
        if (!firstTest) {
        	dropDB(connection);
        }
        createDB(connection);

        sql2o = new Sql2o(ds, new PostgresQuirks(converters));
		dao = new UserDAO(sql2o);
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		dropDB(connection);
	}
	
	@Test
    public void testDeleteNonexistingWithoutDbUnit() throws Exception {
        insertData(connection);

		Statement s = connection.createStatement();
        ResultSet r = s.executeQuery(COUNT_SQL);
        r.next();
        int rows = r.getInt(1);
        assertEquals(3, rows);

        // Delete non-existing record
        User user = new User("Denzler", "Christoph", LocalDate.now());
        UUID uid = UUID.randomUUID();
        user.setId(uid);
        dao.delete(uid);
        
        r = s.executeQuery(COUNT_SQL);
        r.next();
        rows = r.getInt(1);
        assertEquals(3, rows);
    }
    
	@Test
    public void testDeleteWithoutDbUnit() throws Exception {
        insertData(connection);

        Statement s = connection.createStatement();
        ResultSet r = s.executeQuery(COUNT_SQL);
        r.next();
        int rows = r.getInt(1);
        assertEquals(3, rows);

        // delete existing record
        UUID did = UUID.fromString("20000000-0000-0000-0000-000000000001");
        User user = new User("Duck", "Donald", LocalDate.of(2013, 1, 13));
        user.setId(did);
        dao.delete(did);
        
        r = s.executeQuery(COUNT_SQL);
        r.next();
        rows = r.getInt(1);
        assertEquals(2, rows);
    }
 	
	private Object runtest(String prepDataFile, String expectedDataFile, PrepAndExpectedTestCaseSteps steps) 
	throws Exception {
		IDatabaseTester tester = new DataSourceDatabaseTester(pg.getPostgresDatabase());
		DataFileLoader loader = new FlatXmlDataFileLoader();
		tc = new UserDaoPrepAndExpectedTestCase(loader, tester);

        final String[] prepDataFiles = {"/ch/fhnw/swc/mrs/data/" + prepDataFile}; // define prep file as set
        final String[] expectedDataFiles = {"/ch/fhnw/swc/mrs/data/" + expectedDataFile}; // define expected file as set
        final VerifyTableDefinition[] tables = {vtd}; // define tables to verify as set
        return tc.runTest(tables, prepDataFiles, expectedDataFiles, steps); // run the test
	}
	
	@Test
	public void testDeleteNonexisting() throws Exception {
        runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {
            // Delete non-existing record
            User user = new User("Denzler", "Christoph", LocalDate.now());
            UUID uid = UUID.randomUUID();
            user.setId(uid);
            dao.delete(uid);

            return null;        	
        });
	}

	/**
	 * Delete an existing user.
	 * @throws Exception when anything goes wrong.
	 */
	@Test
    public void testDelete() throws Exception {
        runtest("UserDaoTestData.xml", "UserDaoDeleteResult.xml", () -> {
        	// delete existing record
            UUID did = UUID.fromString("20000000-0000-0000-0000-000000000001");
            User user = new User("Duck", "Donald", LocalDate.of(2013, 1, 13));
            user.setId(did);
            dao.delete(did);

            return null;        	
        });
    }
	
	@Test
    public void testGetById() throws Exception {
		UUID mid = UUID.fromString("20000000-0000-0000-0000-000000000002");
		Object o = runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {
        	// Get by id
            return dao.getById(mid);
        });
		if (o instanceof User) {
			User user = (User)o;
	        assertEquals("Micky", user.getFirstName());
	        assertEquals("Mouse", user.getName());
	        assertEquals(mid, user.getId());			
		}
    }	
	
	@SuppressWarnings("unchecked")
	@Test
    public void testGetByName() throws Exception {
		Object o = runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {
        	// Get by name
            return dao.getByName("Duck");
        });
		if (o instanceof List<?>) {
			List<User> userlist = (List<User>)o;
			assertEquals(2, userlist.size());

			ITable actualTable = convertToTable(userlist);

			InputStream stream = this.getClass().getResourceAsStream("UserDaoGetByNameResult.xml");
			IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(stream);
			ITable expectedTable = expectedDataSet.getTable("CLIENTS");

			Assertion.assertEquals(expectedTable, actualTable);
		}
    }
	
	/**
	 * See if we get all users.
	 * @throws Exception when anything goes wrong.
	 */
	@Test
	public void testGetAll() throws Exception {
        @SuppressWarnings("unchecked")
		List<User> userlist = (List<User>) runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {        	
                    return dao.getAll();        	
                });

        ITable actualTable = convertToTable(userlist);

        InputStream stream = this.getClass().getResourceAsStream("UserDaoTestData.xml");
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(stream);
        ITable expectedTable = expectedDataSet.getTable("CLIENTS");

        Assertion.assertEquals(expectedTable, actualTable);
	}
	
	@Test
    public void testSave() throws Exception {
        runtest("UserDaoTestData.xml", "UserDaoInsertResult.xml", () -> {
        	// insert new user
            UUID gid = UUID.fromString("20000000-A000-0000-0000-000000000001");
            User goofy = new User("Goofy", "Goofus", LocalDate.of(1936, 10, 12));
            goofy.setId(gid);
            dao.saveOrUpdate(goofy);

            return null;        	
        });
    }
    
	@Test
    public void testUpdate() throws Exception {
        runtest("UserDaoTestData.xml", "UserDaoUpdateResult.xml", () -> {
        	// update existing user
	        UUID did = UUID.fromString("20000000-0000-0000-0000-000000000001");
	        User daisy = new User("Duck", "Daisy", LocalDate.of(2013, 01, 13));
	        daisy.setId(did);
	        daisy.setFirstName("Daisy");
	        dao.saveOrUpdate(daisy);
	        return null;
        });
    }
    
	
    private void createDB(Connection c) throws SQLException {
        Statement s = c.createStatement();
        s.execute("CREATE TABLE public.clients\n" + 
        		"(\n" + 
        		"    \"id\" uuid NOT NULL,\n" + 
        		"    \"name\" text COLLATE pg_catalog.\"default\" NOT NULL,\n" + 
        		"    \"firstname\" text COLLATE pg_catalog.\"default\" NOT NULL,\n" + 
        		"    \"birthdate\" date NOT NULL,\n" + 
        		"    CONSTRAINT clients_pkey PRIMARY KEY (\"id\")\n" + 
        		")");
	}
	
	private void dropDB(Connection c) throws SQLException {
		Statement s = c.createStatement();
		s.execute("DROP TABLE public.clients");
	}
	
	private void insertData(Connection c) throws SQLException {
		Statement s = c.createStatement();
		s.execute("INSERT INTO public.clients (id, firstname, name, birthdate)"
				+ "VALUES('20000000-0000-0000-0000-000000000001', 'Donald', 'Duck', '2013-01-13')");
		s.execute("INSERT INTO public.clients (id, firstname, name, birthdate)"
				+ "VALUES('20000000-0000-0000-0000-000000000002', 'Micky', 'Mouse', '1935-11-03')");
		s.execute("INSERT INTO public.clients (id, firstname, name, birthdate)"
				+ "VALUES('20000000-0000-0000-0000-000000000009', 'Donald', 'Duck', '1945-09-09')");
	}
	
	@SuppressWarnings("deprecation")
	private ITable convertToTable(List<User> userlist) throws Exception {
		ITableMetaData meta = new TableMetaData();
		DefaultTable t = new DefaultTable(meta);
		int row = 0;
		for (User u : userlist) {
			t.addRow();
			LocalDate d = u.getBirthdate();
			t.setValue(row, "id", u.getId());
			t.setValue(row, "name", u.getName());
			t.setValue(row, "firstname", u.getFirstName());
			t.setValue(row, "birthdate", new Date(d.getYear()-1900, d.getMonthValue()-1, d.getDayOfMonth()));
			row++;
		}
		return t;
	}

	private static final class TableMetaData implements ITableMetaData {

		private List<Column> cols = new ArrayList<>();

		TableMetaData() {
			cols.add(new Column("id", DataType.UNKNOWN));
			cols.add(new Column("name", DataType.VARCHAR));
			cols.add(new Column("firstname", DataType.VARCHAR));
			cols.add(new Column("birthdate", DataType.DATE));
		}

		@Override
		public int getColumnIndex(String colname) throws DataSetException {
			int index = 0;
			for (Column c : cols) {
				if (c.getColumnName().equals(colname.toLowerCase())) {
					return index;
				}
				index++;
			}
			throw new NoSuchColumnException(getTableName(), colname);
		}

		@Override
		public Column[] getColumns() throws DataSetException {
			return cols.toArray(new Column[4]);
		}

		@Override
		public Column[] getPrimaryKeys() throws DataSetException {
			Column[] cols = new Column[1];
			cols[0] = new Column("id", DataType.UNKNOWN);
			return cols;
		}

		@Override
		public String getTableName() {
			return "clients";
		}
	}
	
	private static final class UserDaoPrepAndExpectedTestCase extends DefaultPrepAndExpectedTestCase {

	    public UserDaoPrepAndExpectedTestCase(
	            final DataFileLoader dataFileLoader,
	            final IDatabaseTester databaseTester)
	    {
	        super(dataFileLoader, databaseTester);
	    }

	    @Override
	    protected void setUpDatabaseConfig(final DatabaseConfig config)
	    {
			config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
	    }
	}

	@SuppressWarnings("unchecked")
	@DisplayName("test that all rad users from DB are not null")
	@Test
	public void testVerifyAllUsersAreNotNull() throws Exception {
		List<User> userList = (List<User>) runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {        	
                    return dao.getAll();        	
                });

        assertThat(userList, not(hasItems(nullValue())));

	}

	@SuppressWarnings("unchecked")
	@DisplayName("test that all read users have valid birthdate")
	@Test
	public void testAllUsersHaveValidBirthdate() throws Exception {
		List<User> userList = (List<User>) runtest("UserDaoTestData.xml", "UserDaoTestData.xml", () -> {        	
                    return dao.getAll();        	
                });

        for(User u : userList) {
        	assertThat(u.getBirthdate(), allOf(notNullValue(), isBefore(LocalDate.now())));
        }

	}
	
}
