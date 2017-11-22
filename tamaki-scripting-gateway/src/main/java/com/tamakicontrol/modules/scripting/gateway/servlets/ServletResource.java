package com.tamakicontrol.modules.scripting.gateway.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface ServletResource {

    void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException,
            IllegalArgumentException;

}
