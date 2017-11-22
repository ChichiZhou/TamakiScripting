package com.tamakicontrol.modules.scripting.gateway;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.expressions.ExpressionFunctionManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.gateway.authentication.records.InternalAuthProperties;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.user.UserSourceProfile;
import com.inductiveautomation.ignition.gateway.user.UserSourceProfileRecord;
import com.inductiveautomation.ignition.gateway.web.models.ConfigCategory;
import com.inductiveautomation.ignition.gateway.web.models.DefaultConfigTab;
import com.inductiveautomation.ignition.gateway.web.models.IConfigTab;
import com.tamakicontrol.modules.scripting.AbstractSystemUtils;
import com.tamakicontrol.modules.scripting.TamakiTaskQueue;
import com.tamakicontrol.modules.scripting.gateway.pages.TamakiScriptingSettingsPage;
import com.tamakicontrol.modules.scripting.gateway.records.TamakiScriptingSettingsRecord;
import com.tamakicontrol.modules.scripting.gateway.scripts.*;
import com.tamakicontrol.modules.scripting.gateway.servlets.ScriptingResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GatewayHook extends AbstractGatewayModuleHook {

    private final Logger logger = LoggerFactory.getLogger("Tamaki Scripts");
    private GatewayContext context;

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.context = gatewayContext;

        // Register GatewayHook.properties by registering the GatewayHook.class with BundleUtils
        BundleUtil.get().addBundle("TamakiScripting", getClass(), "TamakiScripting");

        // Verify that the tamaki scripting settings table exists in the database
        verifySchema(gatewayContext);

        // Create a settings record in the internal DB
        maybeCreateScriptingSettings(gatewayContext);

        //

        // Initialize the Task Queue with a default length
        try {
            TamakiTaskQueue.initialize(10);
        }catch (Exception e){
            logger.error("Exception thrown while setting up task queue", e);
        }


        gatewayContext.addServlet("api", ScriptingResource.class);

    }

    @Override
    public void startup(LicenseState licenseState) {
        logger.info("Loading Tamaki Scripts Module");
    }

    @Override
    public void shutdown() {
        logger.info("Stopping Tamaki Scripts Module");
        context.removeServlet("api");
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        super.initializeScriptManager(manager);
        manager.addScriptModule("system.tag", new GatewayTagUtils(context), new PropertiesFileDocProvider());
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


    /*
    *
    * */
    private void verifySchema(GatewayContext context) {
        try {
            context.getSchemaUpdater().updatePersistentRecords(TamakiScriptingSettingsRecord.META);
        } catch (SQLException e) {
            logger.error("Error verifying persistent record schemas for TamakiScripting records.", e);
        }
    }

    public void maybeCreateScriptingSettings(GatewayContext context) {
        logger.trace("Attempting to create TamakiScripting Settings Record");
        try {
            TamakiScriptingSettingsRecord settingsRecord = context.getLocalPersistenceInterface().createNew(TamakiScriptingSettingsRecord.META);
            settingsRecord.setId(0L);
            settingsRecord.setAuthProfileId(0L); // Ignition default auth profile is always 0L

            settingsRecord.setTagReadRole("Administrator");
            settingsRecord.setTagReadAllRole("Administrator");
            settingsRecord.setTagWriteRole("Administrator");
            settingsRecord.setTagWriteAllRole("Administrator");
            settingsRecord.setTagSearchRole("Administrator");
            settingsRecord.setQueryTagHistoryRole("Administrator");

            /*
			 * This doesn't override existing settings, only replaces it with these if we didn't
			 * exist already.
			 */
            context.getSchemaUpdater().ensureRecordExists(settingsRecord);
        } catch (Exception e) {
            logger.error("Failed to establish TamakiScripting Record exists", e);
        }

        logger.trace("TamakiScripting Settings Record Established");
    }


    /**
     * This sets up the config panel
     */
    public static final ConfigCategory CONFIG_CATEGORY = new ConfigCategory("tamakiscripting", "TamakiScripting.nav.header", 700);

    @Override
    public List<ConfigCategory> getConfigCategories() {
        return Collections.singletonList(CONFIG_CATEGORY);
    }

    public static final IConfigTab TAMAKI_SCRIPTING_CONFIG_ENTRY = DefaultConfigTab.builder()
            .category(CONFIG_CATEGORY)
            .name("settings")
            .i18n("TamakiScripting.nav.settings.title")
            .page(TamakiScriptingSettingsPage.class)
            .terms("tamaki scripting settings")
            .build();

    @Override
    public List<? extends IConfigTab> getConfigPanels() {
        return Arrays.asList(
                TAMAKI_SCRIPTING_CONFIG_ENTRY
        );
    }








}
