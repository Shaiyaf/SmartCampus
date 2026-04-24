/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus.filters;



/**
 *
 * @author Shaiyaf
 */
import javax.ws.rs.container.*;
import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.ext.Provider;

// This class will log the details of incoming requests and outgoing responses
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger logger = Logger.getLogger(LoggingFilter.class.getName()); // Log messages to console

    // This method will log the request details
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        logger.info("Request Method: " + requestContext.getMethod());
        logger.info("Request URI: " + requestContext.getUriInfo().getRequestUri().toString());
    }

    // This method will log the response details
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        logger.info("Response Status: " + responseContext.getStatus());
    }
}