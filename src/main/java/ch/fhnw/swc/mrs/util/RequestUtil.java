package ch.fhnw.swc.mrs.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import spark.Request;

public final class RequestUtil {

	/**
	 * Extract the parameter /:id from the request.
	 * @param request the request to extract the id parameter from.
	 * @return the id or -1 if there was none or an illegal one.
	 */
    public static UUID getParamId(Request request) {
    	String param = request.params("id");
   		return UUID.fromString(param);
    }

    /**
     * Extract the parameter ?name from the request.
     * @param request the request to get the attribute from.
     * @return the value of the name parameter.
     */
    public static String getParamName(Request request) {
    	return request.queryParams("name");
    }
    
    /**
     * Extract the parameter ?firstName from the request.
     * @param request the request to get the attribute from.
     * @return the value of the firstName parameter.
     */
    public static String getParamFirstname(Request request) {
    	return request.queryParams("firstname");
    }
    
    /**
     * Extract the parameter ?birthDate from the request.
     * @param request the request to get the attribute from.
     * @return the rented attributes value.
     */
    public static LocalDate getParamBirthdate(Request request) {
    	String birthDate = request.queryParams("birthDate");
    	return LocalDate.parse(birthDate, DateTimeFormatter.ISO_DATE);
    }

    /**
     * Extract the parameter ?rented from the request.
     * @param request the request to get the attribute from.
     * @return the rented attributes value.
     */
    public static String getParamRented(Request request) {
    	return request.queryParams("rented");
    }
    
    /**
     * Extract the parameter ?title from the request.
     * @param request the request to get the attribute from.
     * @return the title.
     */
    public static String getParamTitle(Request request) {
    	return request.queryParams("title");
    }

    /**
     * Extract the parameter ?releaseDate from the request.
     * @param request the request to get the attribute from.
     * @return the release date.
     */
    public static LocalDate getParamReleaseDate(Request request) {
    	String relDate = request.queryParams("releaseDate");
    	return LocalDate.parse(relDate, DateTimeFormatter.ISO_DATE);
    }

    /**
     * Extract the parameter ?title from the request.
     * @param request the request to get the attribute from.
     * @return the rented attributes value.
     */
    public static String getParamPriceCategory(Request request) {
    	return request.queryParams("priceCategory");
    }

    /**
     * Extract the parameter ?title from the request.
     * @param request the request to get the attribute from.
     * @return the rented attributes value.
     */
    public static int getParamAgeRating(Request request) {
    	String param = request.queryParams("ageRating");
    	int rating;
    	try {
    		rating = Integer.parseInt(param);
    	} catch (NumberFormatException nfe) {
    		rating = -1;
    	}
   		return rating;
    }

    /**
     * Retrieve the locale from the requests session.
     * @param request to work on.
     * @return the locale.
     */
    public static String getSessionLocale(Request request) {
        return request.session().attribute("locale");
    }

    /**
     * Determine whether the client accepts html.
     * @param request the request to work on.
     * @return whether html is an acceptable format.
     */
    public static boolean clientAcceptsHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }

    /**
     * Determine whether the client accepts json.
     * @param request the request to work on.
     * @return whether json is an acceptable format.
     */
    public static boolean clientAcceptsJson(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("application/json");
    }

    // prevent instantiation
    private RequestUtil() { }
}
