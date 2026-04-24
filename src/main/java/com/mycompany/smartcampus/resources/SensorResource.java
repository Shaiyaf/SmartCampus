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
import com.mycompany.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.mycompany.smartcampus.models.ErrorResponse;
import com.mycompany.smartcampus.models.Room;
import com.mycompany.smartcampus.models.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", "Sensor id is required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", "roomId is required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        Room room = DataStore.getRoomById(sensor.getRoomId());

        if (room == null) {
            throw new LinkedResourceNotFoundException("Referenced room does not exist.");
        }

        if (DataStore.sensors.containsKey(sensor.getId())) {
            ErrorResponse error = new ErrorResponse(409, "Conflict", "Sensor with this id already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        DataStore.sensors.put(sensor.getId(), sensor);
        room.getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(DataStore.sensors.values());

        if (type == null || type.trim().isEmpty()) {
            return allSensors;
        }

        return allSensors.stream()
                .filter(sensor -> sensor.getType() != null && sensor.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource() {
        return new SensorReadingResource();
    }
}