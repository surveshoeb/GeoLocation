package com.example.geolocation.Model;

/**
 * Created by Rizwan on 18-02-2018.
 */

public class Item {
     private String name;
     private int length;

     public Item(String name, int length)
     {
         this.name = name;
         this.length = length;
     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
