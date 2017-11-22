package com.tamakicontrol.modules.scripting.gateway.pages;

import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.components.RecordEditForm;
import com.inductiveautomation.ignition.gateway.web.models.LenientResourceModel;
import com.inductiveautomation.ignition.gateway.web.pages.IConfigPage;
import com.tamakicontrol.modules.scripting.gateway.records.TamakiScriptingSettingsRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Application;

public class TamakiScriptingSettingsPage extends RecordEditForm {

    public TamakiScriptingSettingsPage(final IConfigPage configPage){
        super(configPage, null, new LenientResourceModel("TamakiScripting.nav.settings.panelTitle"),
                ((GatewayContext) Application.get()).getPersistenceInterface().find(TamakiScriptingSettingsRecord.META, 0L));
    }

    @Override
    public Pair<String, String> getMenuLocation() {
        return Pair.of("tamakiscripting", "settings");
    }

}
