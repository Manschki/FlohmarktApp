package com.example.mseifriedsberger16.flohmarktapp;

import java.io.Serializable;

/**
 * Created by mseifriedsberger16 on 19.03.2019.
 */

public class Article implements Serializable {
    int id, price, phone;
    String name, username, email, password;
    double lat, lng;

    public Article(int id, int price, int phone, String name, String username, String email, String password, double lat, double lng) {
        this.id = id;
        this.price = price;
        this.phone = phone;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.lat = lat;
        this.lng = lng;
    }

    public Article() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", price=" + price +
                ", phone=" + phone +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
