/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus;

/**
 *
 * @author Shaiyaf
 */

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import com.mycompany.smartcampus.resources.DiscoveryResource;
import com.mycompany.smartcampus.resources.RoomResource;
import com.mycompany.smartcampus.resources.SensorResource;
import com.mycompany.smartcampus.mappers.RoomNotEmptyExceptionMapper;
import com.mycompany.smartcampus.mappers.LinkedResourceNotFoundExceptionMapper;
import com.mycompany.smartcampus.mappers.SensorUnavailableExceptionMapper;
import com.mycompany.smartcampus.mappers.GlobalExceptionMapper;

@ApplicationPath("/api/v1")
public class AppConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);

        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);

        return classes;
    }
}