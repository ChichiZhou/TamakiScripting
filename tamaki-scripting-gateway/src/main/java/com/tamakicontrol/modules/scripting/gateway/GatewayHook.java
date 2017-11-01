package com.tamakicontrol.modules.scripting.gateway;

import com.inductiveautomation.ignition.common.expressions.ExpressionFunctionManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.tamakicontrol.modules.scripting.AbstractSystemUtils;
import com.tamakicontrol.modules.scripting.TamakiTaskQueue;
import com.tamakicontrol.modules.scripting.gateway.scripts.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayHook extends AbstractGatewayModuleHook {

    private final Logger logger = LoggerFactory.getLogger("Tamaki Scripts");
    private GatewayContext context;

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;

        try {
            TamakiTaskQueue.initialize(10);
        }catch (Exception e){
            logger.error("Exception thrown while setting up task queue", e);
        }
    }

    @Override
    public void startup(LicenseState licenseState) {
        logger.info("Loading Tamaki Scripts Module");
    }

    @Override
    public void shutdown() {
        logger.info("Stopping Tamaki Scripts Module");
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        super.initializeScriptManager(manager);
        manager.addScriptModule("system.util", new GatewaySystemUtils(context), new PropertiesFileDocProvider());
        manager.addScriptModule("system.user", new GatewaySecurityUtils(), new PropertiesFileDocProvider());
        manager.addScriptModule("system.db", new GatewayDBUtils(context), new PropertiesFileDocProvider());
        manager.addScriptModule("system.pdf", new GatewayPDFUtils(), new PropertiesFileDocProvider());
    }

    @Override
    public void configureFunctionFactory(ExpressionFunctionManager factory) {
        super.configureFunctionFactory(factory);
        factory.addFunction("getUUID","Strings", new AbstractSystemUtils.GetUUIDFunction());
        factory.addFunction("getStackTrace","Strings", new AbstractSystemUtils.GetStackTraceFunction());
    }

    @Override
    public Object getRPCHandler(ClientReqSession session, Long projectId) {
        return new GatewayRPCHandler(this.context);
    }

    @Override
    public boolean isFreeModule() {
        return true;
    }
}
