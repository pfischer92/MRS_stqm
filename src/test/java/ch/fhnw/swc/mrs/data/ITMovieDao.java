package ch.fhnw.swc.mrs.data;

import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.PriceCategory;
import ch.fhnw.swc.mrs.util.LocalDateConverter;
import ch.fhnw.swc.mrs.util.PriceCategoryConverter;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.dbunit.*;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag("integration")
public class ITMovieDao {

    /** Class under test: MovieDAO. */
    private MovieDAO dao;
    private Connection connection;
    private static EmbeddedPostgres pg;
    private static boolean firstTest = true;

    private PrepAndExpectedTestCase tc;
    private static VerifyTableDefinition vtd;
    @SuppressWarnings("rawtypes")
    private static Map<Class, Converter> converters = new HashMap<>();

    // the connection string to the database
    private Sql2o sql2o;

    @BeforeAll
    public static void startPostgresql() throws IOException {
        pg = EmbeddedPostgres.start();

        PriceCategory.init();
        vtd = new VerifyTableDefinition("movies", new String[] {"id"});
        converters.put(UUID.class, new UUIDConverter());
        converters.put(LocalDate.class, new LocalDateConverter());
        converters.put(PriceCategory.class, new PriceCategoryConverter());
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
        dao = new MovieDAO(sql2o);
    }

    @AfterEach
    public void tearDown() throws Exception {
        dropDB(connection);
    }

    private void createDB(Connection c) throws SQLException {
        Statement s = c.createStatement();
        s.execute("CREATE TABLE public.movies\n" +
                "(\n" +
                "    \"id\" uuid NOT NULL,\n" +
                "    \"title\" text COLLATE pg_catalog.\"default\" NOT NULL,\n" +
                "    \"rented\" boolean NOT NULL,\n" +
                "    \"releasedate\" date NOT NULL,\n" +
                "    \"pricecategory\" text COLLATE pg_catalog.\"default\" NOT NULL,\n" +
                "    \"agerating\" integer NOT NULL,\n" +
                "    CONSTRAINT movies_pkey PRIMARY KEY (\"id\")\n" +
                ")");
    }

    private void dropDB(Connection c) throws SQLException {
        Statement s = c.createStatement();
        s.execute("DROP TABLE public.movies");
    }

    private Object runtest(String prepDataFile, String expectedDataFile, PrepAndExpectedTestCaseSteps steps)
            throws Exception {
        IDatabaseTester tester = new DataSourceDatabaseTester(pg.getPostgresDatabase());
        DataFileLoader loader = new FlatXmlDataFileLoader();
        tc = new ITMovieDao.MovieDaoPrepAndExpectedTestCase(loader, tester);

        final String[] prepDataFiles = {"/ch/fhnw/swc/mrs/data/" + prepDataFile}; // define prep file as set
        final String[] expectedDataFiles = {"/ch/fhnw/swc/mrs/data/" + expectedDataFile}; // define expected file as set
        final VerifyTableDefinition[] tables = {vtd}; // define tables to verify as set
        return tc.runTest(tables, prepDataFiles, expectedDataFiles, steps); // run the test
    }

    @Test
    public void testDeleteNonexisting() throws Exception {
        runtest("MovieDaoTestData.xml", "MovieDaoTestData.xml", () -> {
            // Delete non-existing record
            Movie movie = new Movie("Test","2013-07-12","Children",18);
            UUID uid = UUID.randomUUID();
            movie.setId(uid);
            dao.delete(uid);

            return null;
        });
    }

    /**
     * Delete an existing movie.
     * @throws Exception when anything goes wrong.
     */
    @Test
    public void testDelete() throws Exception {
        runtest("MovieDaoTestData.xml", "MovieDaoDeleteResult.xml", () -> {
            // delete existing record
            UUID did = UUID.fromString("20000000-0000-0000-0000-000000000001");
            Movie movie = new Movie("Saw","2017-01-01","Regular",16);
            movie.setId(did);
            dao.delete(did);

            return null;
        });
    }

	@Test
    public void testGetById() throws Exception {
		UUID mid = UUID.fromString("20000000-0000-0000-0000-000000000002");
		Object o = runtest("MovieDaoTestData.xml", "MovieDaoTestData.xml", () -> {
        	// Get by id
            return dao.getById(mid);
        });
		if (o instanceof Movie) {
			LocalDate ld = LocalDate.parse("2013-01-01", DateTimeFormatter.ISO_DATE);
			Movie movie = (Movie)o;
	        assertEquals("Lion", movie.getTitle());
	        assertEquals(false, movie.isRented());
	        assertEquals(ld, movie.getReleaseDate());
	        assertEquals(14, movie.getAgeRating());
	        assertEquals("Children", movie.getPriceCategory().toString());
		}
    }	


    private static final class MovieDaoPrepAndExpectedTestCase extends DefaultPrepAndExpectedTestCase {

        public MovieDaoPrepAndExpectedTestCase(
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

}
