package ch.fhnw.swc.mrs.util;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.User;

public final class JsonUtil {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Register a serializer with the mapper.
	 * @param serializer the custom serializer.
	 */
	public static void registerSerializer(StdSerializer<?> serializer) {
		SimpleModule module = new SimpleModule();
		module.addSerializer(serializer);
		mapper.registerModule(module);
	}
	
	/**
	 * Convert DTO to JSON.
	 * @param data the dto.
	 * @return a JSON String.
	 */
    public static String dataToJson(Object data) {
    	try {
			return mapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("IOEXception while mapping object (" + data + ") to JSON", e);
		}
    }
    
    /**
     * Convert JSON to Movie object.
     * @param json the json object to parse.
     * @return a Movie object created with the data from the json object.
     * @throws IOException thrown upon parse problems.
     */
    public static Movie jsonToMovie(String json) throws IOException {
    	JsonNode node = mapper.readTree(json);
    	String title = node.get("title").asText();
    	String releaseDate = node.get("releaseDate").asText();
    	String priceCategory = node.get("priceCategory").asText();
    	int ageRating = node.get("ageRating").asInt();
		Movie m = new Movie(title, releaseDate, priceCategory, ageRating);
		m.setId(UUID.fromString(node.get("id").asText()));
		return m;
    }
    
    /**
     * Convert JSON to Movie object.
     * @param json the json object to parse.
     * @return a Movie object created with the data from the json object.
     * @throws IOException thrown upon parse problems.
     */
    public static User jsonToUser(String json) throws IOException {
    	JsonNode node = mapper.readTree(json);
    	String name = node.get("name").asText();
    	String firstname = node.get("firstname").asText();
    	String birthdate = node.get("birthDate").asText();
		User u = new User(name, firstname, birthdate);
		u.setId(UUID.fromString(node.get("id").asText()));
		return u;
    }
    
    // prevent instantiation
    private JsonUtil() { }
}
