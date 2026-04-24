/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus;

/**
 *
 * @author Shaiyaf
 */


import com.mycompany.smartcampus.models.Room;
import com.mycompany.smartcampus.models.Sensor;
import com.mycompany.smartcampus.models.SensorReading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    public static Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public static Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    public static Room getRoomById(String id) {
        return rooms.get(id);
    }

    public static Sensor getSensorById(String id) {
        return sensors.get(id);
    }
}