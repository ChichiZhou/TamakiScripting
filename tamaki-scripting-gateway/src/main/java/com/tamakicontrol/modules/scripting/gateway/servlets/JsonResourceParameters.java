package com.tamakicontrol.modules.scripting.gateway.servlets;

import com.google.gson.Gson;

public class JsonResourceParameters {
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
