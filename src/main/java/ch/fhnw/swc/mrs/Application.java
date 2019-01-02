package ch.fhnw.swc.mrs;

import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.controller.MovieController;
import ch.fhnw.swc.mrs.controller.UserController;
import ch.fhnw.swc.mrs.data.SimpleMRSServices;
import ch.fhnw.swc.mrs.model.ChildrenPriceCategory;
import ch.fhnw.swc.mrs.model.NewReleasePriceCategory;
import ch.fhnw.swc.mrs.model.RegularPriceCategory;
import ch.fhnw.swc.mrs.util.Filters;
import static spark.Spark.port;
import static spark.Spark.staticFiles;
import static spark.Spark.exception;

public final class Application {
	
	/**
	 * The Application main entry point.
	 * @param args are ignored.
	 * @throws Exception whenever something goes wrong.
	 */
	public static void main(String[] args) throws Exception {
		loadPriceCategories();
		// Instantiate dependencies
		MRSServices backend = new SimpleMRSServices();
		backend.init();
		
		//Configure Spark
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        port(4567);
        staticFiles.location("/public");
        staticFiles.expireTime(600L);
        

        // Set up before-filters (called before each get/post)
        Filters.beforeGetPost();

        // Set up routes
        MovieController.init(backend);
        UserController.init(backend);
//        get("*",                     ViewUtil.notFound);

        //Set up after-filters (called after each get/post)
        Filters.afterGetPost();

    }
	
    private static void loadPriceCategories() throws Exception {
    	RegularPriceCategory.getInstance();
    	ChildrenPriceCategory.getInstance();
    	NewReleasePriceCategory.getInstance();
    }
    
    public static void stop() {
    	spark.Spark.stop();
    }
	
	private Application() { }
}

