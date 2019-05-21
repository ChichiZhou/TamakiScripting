package com.tamakicontrol.modules.scripting.client.scripts;

import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory;
import com.inductiveautomation.ignition.client.model.ClientContext;

import com.inductiveautomation.ignition.common.model.CommonContext;
import com.tamakicontrol.modules.scripting.AbstractSystemUtils;
import com.tamakicontrol.modules.scripting.SystemUtilProvider;

public class ClientSystemUtils extends AbstractSystemUtils {

    private final SystemUtilProvider rpc;

    ClientContext context;

    public ClientSystemUtils(ClientContext context){
        this.context = context;

        rpc = ModuleRPCFactory.create(
                "com.tamakicontrol.modules.scripting.tamaki-scripting",
                SystemUtilProvider.class
        );
    }

    @Override
    public CommonContext getContextImpl() throws Exception {
        return this.context;
    }
}
