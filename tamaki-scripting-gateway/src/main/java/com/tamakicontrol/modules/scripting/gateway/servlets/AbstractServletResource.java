package com.tamakicontrol.modules.scripting.gateway.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractServletResource implements ServletResource {

    private String method = "GET";
    private String path = "/";
    private String reqRole = null;
    private boolean reqLogin = false;

    public AbstractServletResource(String method, String path, boolean reqLogin){
        this.method = method;
        this.path = path;
        this.reqLogin = reqLogin;
    }

    public AbstractServletResource(String method, String path, String reqRole){
        this.method = method;
        this.path = path;
        this.reqLogin = true;
        this.reqRole = reqRole;
    }

    public AbstractServletResource(String method, String path){
        this.method = method;
        this.path = path;
        this.reqRole = reqRole;
    }

    @Override
    public void doRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, IllegalArgumentException {
        doRequestImpl(req, resp);
    }


    protected abstract void doRequestImpl(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, IllegalArgumentException;

}
