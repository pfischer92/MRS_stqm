package ch.fhnw.swc.mrs.util;

import spark.Request;
import spark.Response;

import static spark.Spark.after;

import spark.Filter;

public final class Filters {

    /** 
     * If a user manually manipulates paths and forgets to add a trailing slash, redirect the user to the correct path.
     */
    static Filter addTrailingSlashes = (Request request, Response response) -> {
        if (!request.pathInfo().endsWith("/")) {
            response.redirect(request.pathInfo() + "/");
        }
    };

    /**
     * Enable GZIP for all responses.
     */
    static Filter addGzipHeader = (Request request, Response response) -> {
    	response.header("Content-Type",	"application/json");
        response.header("Content-Encoding", "gzip");
    };
    
    /**
     * Set up before-filters (called before each get/post)
     */
    public static void beforeGetPost() {
//    	before("*", Filters.addTrailingSlashes);
    }
    
    /**
     * Set up after-filters (called after each get/post)
     */
    public static void afterGetPost() {
    	after("*", Filters.addGzipHeader);
    }
    
    private Filters() { }

}
