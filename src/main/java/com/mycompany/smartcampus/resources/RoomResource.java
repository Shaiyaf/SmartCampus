/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.smartcampus.resources;

import com.mycompany.smartcampus.DataStore;
import com.mycompany.smartcampus.exceptions.RoomNotEmptyException;
import com.mycompany.smartcampus.models.ErrorResponse;
import com.mycompany.smartcampus.models.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public List<Room> getRooms() {
        return new ArrayList<>(DataStore.rooms.values());
    }

    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", "Room id is required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (DataStore.rooms.containsKey(room.getId())) {
            ErrorResponse error = new ErrorResponse(409, "Conflict", "Room with this id already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        DataStore.rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRoomById(roomId);
        if (room == null) {
            ErrorResponse error = new ErrorResponse(404, "Not Found", "Room not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRoomById(roomId);

        if (room == null) {
            ErrorResponse error = new ErrorResponse(404, "Not Found", "Room not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room has active sensors assigned.");
        }

        DataStore.rooms.remove(roomId);
        return Response.ok().entity("{\"message\":\"Room deleted successfully\"}").build();
    }
}

