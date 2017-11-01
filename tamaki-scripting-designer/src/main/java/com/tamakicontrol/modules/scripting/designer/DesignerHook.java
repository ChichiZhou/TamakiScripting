package com.tamakicontrol.modules.scripting.designer;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.expressions.ExpressionFunctionManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

import com.tamakicontrol.modules.scripting.*;
import com.tamakicontrol.modules.scripting.client.scripts.*;

import com.tamakicontrol.modules.scripting.AbstractDBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesignerHook extends AbstractDesignerModuleHook {

    private final Logger logger = LoggerFactory.getLogger("Tamaki Scripting");

    private DesignerContext context;

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);

        this.context = context;

        BundleUtil.get().addBundle("DBUtils", AbstractDBUtils.class, "DBUtils");
        BundleUtil.get().addBundle("SecurityUtils", AbstractSecurityUtils.class, "SecurityUtils");
        BundleUtil.get().addBundle("SystemUtils", AbstractSystemUtils.class, "SystemUtils");
        BundleUtil.get().addBundle("TagUtils", AbstractTagUtils.class, "TagUtils");
        BundleUtil.get().addBundle("GUIUtils", AbstractGUIUtils.class, "GUIUtils");
        BundleUtil.get().addBundle("PDFUtils", AbstractPDFUtils.class, "PDFUtils");

        TamakiTaskQueue.initialize(10);
        ClientTagUtils.initialize(context);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        super.initializeScriptManager(manager);
        manager.addScriptModule("system.util", new ClientSystemUtils(context), new PropertiesFileDocProvider());
        manager.addScriptModule("system.tag", new ClientTagUtils(), new PropertiesFileDocProvider());
        manager.addScriptModule("system.db", new ClientDBUtils(), new PropertiesFileDocProvider());
        manager.addScriptModule("system.gui", new ClientGUIUtils(), new PropertiesFileDocProvider());
        manager.addScriptModule("system.pdf", new ClientPDFUtils(), new PropertiesFileDocProvider());

    }

    @Override
    public void configureFunctionFactory(ExpressionFunctionManager factory) {
        super.configureFunctionFactory(factory);
        factory.addFunction("getUUID","Strings", new AbstractSystemUtils.GetUUIDFunction());
        factory.addFunction("getStackTrace","Strings", new AbstractSystemUtils.GetStackTraceFunction());
        factory.addFunction("getParamValue","Advanced", new ClientTagUtils.GetParameterValueFunction());
    }

}
