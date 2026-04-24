/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus.models;

/**
 *
 * @author Shaiyaf
 */



import java.util.ArrayList;
import java.util.List;

public class Room {
    private String id;
    private String name;
    private String building;
    private List<String> sensorIds = new ArrayList<>();

    public Room() {
    }

    public Room(String id, String name, String building) {
        this.id = id;
        this.name = name;
        this.building = building;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getBuilding() {
        return building;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }
}