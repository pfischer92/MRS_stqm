package ch.fhnw.swc.mrs.controller;

import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.model.User;
import ch.fhnw.swc.mrs.util.JsonUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import static ch.fhnw.swc.mrs.util.JsonUtil.dataToJson;
import static spark.Spark.*;

public final class RentalController {

    private static MRSServices backend;

    private static Route fetchAllRentals = (Request request, Response response) -> {
        Collection<Rental> rentals = backend.getAllRentals();
        return dataToJson(rentals);
    };
    /**
     * Initialize RentalController by registering back-end and routes.
     * @param services the back-end component.
     */
    public static void init(MRSServices services) {
        if (services == null) {
            throw new IllegalArgumentException("Backend component missing");
        }
        backend = services;
        JsonUtil.registerSerializer(new RentalSerializer());

        get("/rentals",     RentalController.fetchAllRentals);
        //get("/users/:id", UserController.fetchOneUser);
        //delete("/users/:id", UserController.deleteUser);
        //post("/users", UserController.createUser);
        //put("/users/:id", UserController.updateUser);
    }

    private static class RentalSerializer extends StdSerializer<Rental> {

        RentalSerializer() {
            super(Rental.class);
        }

        @Override
        public void serialize(Rental r, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            jgen.writeStringField("id", r.getId().toString());
            jgen.writeStringField("movie", r.getMovie().getTitle());
            jgen.writeStringField("user", r.getUser().getName() + r.getUser().getFirstName());
            jgen.writeStringField("rentalDate", r.getRentalDate().format(DateTimeFormatter.ISO_DATE));
            jgen.writeEndObject();
        }
    }
}
