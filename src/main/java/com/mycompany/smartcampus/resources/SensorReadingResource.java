/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus.resources;

/**
 *
 * @author Shaiyaf
 */


import com.mycompany.smartcampus.DataStore;
import com.mycompany.smartcampus.exceptions.SensorUnavailableException;
import com.mycompany.smartcampus.models.ErrorResponse;
import com.mycompany.smartcampus.models.Sensor;
import com.mycompany.smartcampus.models.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    @GET
    public List<SensorReading> getReadings(@PathParam("sensorId") String sensorId) {
        return DataStore.readings.getOrDefault(sensorId, new ArrayList<>());
    }

    @POST
    public Response postReading(@PathParam("sensorId") String sensorId, SensorReading reading) {
        Sensor sensor = DataStore.getSensorById(sensorId);

        if (sensor == null) {
            ErrorResponse error = new ErrorResponse(404, "Not Found", "Sensor not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        if (reading == null || reading.getValue() == null || reading.getTimestamp() == null || reading.getTimestamp().trim().isEmpty()) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", "Reading timestamp and value are required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is under maintenance and cannot accept readings.");
        }

        DataStore.readings.putIfAbsent(sensorId, Collections.synchronizedList(new ArrayList<>()));
        DataStore.readings.get(sensorId).add(reading);

        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}