package ch.fhnw.swc.mrs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import ch.fhnw.swc.mrs.Application;
import ch.fhnw.swc.mrs.util.StatusCodes;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.*;

@Tag("integration")
class ITUserController {
	
	@BeforeAll
	static void setPort() throws Exception {
		Application.main(null);
	}

	@DisplayName("Get a user by its id.")
	@Test
	void testGetUserById() {
		given().
			contentType("application/json").
		when().
			get("http://localhost:4567/users/{id}", "20000000-0000-0000-0000-000000000004").
		then().
			statusCode(200).
			body("name", equalTo("Müller"));
	}
	
	@DisplayName("Delete user")
	@Test
	void testDeleteUser() {
		String json = get("http://localhost:4567/users").asString();
		int elementsBefore = new JsonPath(json).getInt("size()");
		
		given().
		when().
			delete("http://localhost:4567/users/20000000-0000-0000-0000-000000000005").
		then().
			statusCode(StatusCodes.NO_CONTENT);

		json = get("http://localhost:4567/users").asString();
		int elementsAfter = new JsonPath(json).getInt("size()");
		assertEquals(elementsBefore, elementsAfter+1);
	}
		
	@DisplayName("Create user")
	@Test
	void testCreateUser() {
		String data = "?name=Denzler&birthDate=1968-07-20&firstname=Christoph";
		String json = get("http://localhost:4567/users").asString();
		int elementsBefore = new JsonPath(json).getInt("size()");
		
		given().
		when().
			post("http://localhost:4567/users" + data).
		then().
			statusCode(StatusCodes.CREATED).
			body("name", equalTo("Denzler"));

		json = get("http://localhost:4567/users").asString();
		int elementsAfter = new JsonPath(json).getInt("size()");
		assertEquals(elementsBefore, elementsAfter-1);
	}
	
	@DisplayName("Update user")
	@Test
	void testUpdateUser() {
		String body = "{\r\n" + 
				"        \"id\": \"20000000-0000-0000-0000-000000000006\",\r\n" + 
				"        \"name\": \"Meier\",\r\n" + 
				"        \"firstname\": \"Katrin\",\r\n" + 
				"        \"birthDate\": \"2017-06-27\"\r\n" + 
				"    }";
		String json = get("http://localhost:4567/users").asString();
		int elementsBefore = new JsonPath(json).getInt("size()");
		
		given().
			body(body).
		when().
			put("http://localhost:4567/users/20000000-0000-0000-0000-000000000006").
		then().
			statusCode(StatusCodes.OK).
			body("name", equalTo("Meier"));

		json = get("http://localhost:4567/users").asString();
		int elementsAfter = new JsonPath(json).getInt("size()");
		assertEquals(elementsBefore, elementsAfter);
	}
		
	@AfterAll
	static void stopSpark() throws Exception {
		Application.stop();
		Thread.sleep(10000);
	}

}
