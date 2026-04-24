/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus.models;

/**
 *
 * @author Shaiyaf
 */



public class Sensor {
    private String id;
    private String type;
    private String roomId;
    private String status;
    private Double currentValue;

    public Sensor() {
    }

    public Sensor(String id, String type, String roomId, String status) {
        this.id = id;
        this.type = type;
        this.roomId = roomId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getStatus() {
        return status;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }
}