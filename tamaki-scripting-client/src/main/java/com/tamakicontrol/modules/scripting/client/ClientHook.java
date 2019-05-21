package com.tamakicontrol.modules.scripting.client;

import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.client.script.ClientTagUtilities;
import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.expressions.ExpressionFunctionManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.vision.api.client.AbstractClientModuleHook;
import com.tamakicontrol.modules.scripting.AbstractSystemUtils;

import com.tamakicontrol.modules.scripting.TamakiTaskQueue;
import com.tamakicontrol.modules.scripting.client.scripts.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHook extends AbstractClientModuleHook {

    private final Logger logger = LoggerFactory.getLogger("Tamaki Scripting");

    private ClientContext context;

    @Override
    public void startup(ClientContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);
        logger.info("Initializing Tamaki Scripting Module");

        this.context = context;

        ClientTagUtils.initialize(context);
        TamakiTaskQueue.initialize(10);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        super.initializeScriptManager(manager);
        manager.addScriptModule("system.util", new ClientSystemUtils(context), new PropertiesFileDocProvider());
        manager.addScriptModule("system.security", new ClientSecurityUtils(context), new PropertiesFileDocProvider());
        manager.addScriptModule("system.tag", ClientTagUtils.getInstance(), new PropertiesFileDocProvider());
        manager.addScriptModule("system.db", new ClientDBUtils(), new PropertiesFileDocProvider());
        manager.addScriptModule("system.gui", new ClientGUIUtils(), new PropertiesFileDocProvider());
        manager.addScriptModule("system.pdf", new ClientPDFUtils(), new PropertiesFileDocProvider());

        Dataset test = new BasicDataset();
        test.getColumnNames();
    }

    @Override
    public void configureFunctionFactory(ExpressionFunctionManager factory) {
        super.configureFunctionFactory(factory);
        factory.addFunction("getUUID","Strings", new AbstractSystemUtils.GetUUIDFunction());
        factory.addFunction("getStackTrace","Strings", new AbstractSystemUtils.GetStackTraceFunction());
        factory.addFunction("getParamValue","Advanced", new ClientTagUtils.GetParameterValueFunction());
    }

}
