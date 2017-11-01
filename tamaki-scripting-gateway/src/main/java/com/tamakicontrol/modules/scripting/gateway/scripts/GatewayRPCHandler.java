package com.tamakicontrol.modules.scripting.gateway.scripts;

import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.tamakicontrol.modules.scripting.TamakRPCProxy;
import org.python.core.PyObject;

import java.util.List;

/**
 * Created by cwarren on 2/14/17.
 */
public class GatewayRPCHandler {

    GatewayContext context;
    GatewayDBUtils dbUtils;
    GatewaySystemUtils systemUtils;

    public GatewayRPCHandler(GatewayContext context){
        this.context = context;
        dbUtils = new GatewayDBUtils(context);
        systemUtils = new GatewaySystemUtils(context);
    }


//    public Object runAtGateway(PyObject object) {
//        return systemUtils.runAtGateway(object);
//    }


    public List<List<Object>> runInternalQuery(String query) {
        return dbUtils.runInternalQuery(query);
    }


    public List<List<Object>> runPrepInternalQuery(String query, Object[] args) {
        return runPrepInternalQuery(query, args);
    }


    public int runInternalUpdateQuery(String query) {
        return dbUtils.runInternalUpdateQuery(query);
    }


    public int runPrepInternalUpdateQuery(String query, Object[] args) {
        return dbUtils.runPrepInternalUpdateQuery(query, args);
    }
}
