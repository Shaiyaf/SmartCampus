/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus.mappers;

/**
 *
 * @author Shaiyaf
 */

import com.mycompany.smartcampus.models.ErrorResponse;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {

        // Handle JAX-RS exceptions like 404, 400, etc. properly
        if (ex instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) ex;

            ErrorResponse error = new ErrorResponse(
                    webEx.getResponse().getStatus(),
                    webEx.getResponse().getStatusInfo().getReasonPhrase(),
                    ex.getMessage() != null ? ex.getMessage() : "Request failed."
            );

            return Response.status(webEx.getResponse().getStatus())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(error)
                    .build();
        }

        // Handle unexpected server errors
        ErrorResponse error = new ErrorResponse(
                500,
                "Internal Server Error",
                "An unexpected error occurred. Please contact the administrator."
        );

        ex.printStackTrace();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}