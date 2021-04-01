package com.example.virtualwallet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connections {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("my_did")
    @Expose
    private String my_did;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("their_did")
    @Expose
    private String their_did;
    @SerializedName("count")
    @Expose
    private String count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMy_did() {
        return my_did;
    }

    public void setMy_did(String my_did) {
        this.my_did = my_did;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTheir_did() {
        return their_did;
    }

    public void setTheir_did(String their_did) {
        this.their_did = their_did;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
