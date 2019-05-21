package com.tamakicontrol.modules.scripting.gateway.scripts;

import com.inductiveautomation.ignition.common.model.CommonContext;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.tamakicontrol.modules.scripting.AbstractSystemUtils;
import org.python.core.PyObject;


public class GatewaySystemUtils extends AbstractSystemUtils {

    private GatewayContext context;

    public GatewaySystemUtils(GatewayContext context){
        this.context = context;
    }

    @Override
    protected Object runAtGatewayImpl(PyObject object) {
        return object.__call__();
    }


    @Override
    public CommonContext getContextImpl() throws Exception {
        return context;
    }
}
