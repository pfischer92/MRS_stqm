package ch.fhnw.swc.mrs.controller;

import ch.fhnw.swc.mrs.Application;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
public class ITRentalController {

    @BeforeAll
    static void setPort() throws Exception {
        Application.main(null);
    }

    @DisplayName("Get all rentals")
    @Test
    void testGetAllRentals() {
        given().
                when().
                get("http://localhost:4567/rentals/").
                then().
                statusCode(200);

        String json = get("http://localhost:4567/rentals").asString();
        int elements = new JsonPath(json).getInt("size()");
        assertEquals(7, elements);
    }
}
