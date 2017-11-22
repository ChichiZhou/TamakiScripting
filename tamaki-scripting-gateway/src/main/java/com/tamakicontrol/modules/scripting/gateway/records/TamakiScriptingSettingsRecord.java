package com.tamakicontrol.modules.scripting.gateway.records;

import com.inductiveautomation.ignition.gateway.localdb.persistence.*;
import com.tamakicontrol.modules.utils.TamakiDataset;
import simpleorm.dataset.SFieldFlags;

public class TamakiScriptingSettingsRecord extends PersistentRecord {

    public static final RecordMeta<TamakiScriptingSettingsRecord> META = new RecordMeta<TamakiScriptingSettingsRecord>(
            TamakiScriptingSettingsRecord.class, "TamakiScriptingSettingsRecord").setNounKey("TamakiScriptingSettingsRecord.Noun").setNounPluralKey(
            "TamakiScriptingSettingsRecord.Noun.Plural");


    public static final IdentityField Id = new IdentityField(META);

    public static final LongField AuthProfileId = new LongField(META, "AuthProfileId", SFieldFlags.SMANDATORY);

    static final Category General = new Category("TamakiScriptingSettingsRecord.Category.General", 1000)
            .include(AuthProfileId);

    // resource roles
    public static final StringField TagReadRole = new StringField(META, "TagReadRole", SFieldFlags.SMANDATORY);
    public static final StringField TagReadAllRole = new StringField(META, "TagReadAllRole", SFieldFlags.SMANDATORY);
    public static final StringField TagWriteRole = new StringField(META, "TagWriteRole", SFieldFlags.SMANDATORY);
    public static final StringField TagWriteAllRole = new StringField(META, "TagWriteAllRole", SFieldFlags.SMANDATORY);
    public static final StringField TagSearchRole = new StringField(META, "TagSearchRole", SFieldFlags.SMANDATORY);
    public static final StringField QueryTagHistoryRole = new StringField(META, "QueryTagHistoryRole", SFieldFlags.SMANDATORY);

    static final Category ResourceRoles = new Category("TamakiScriptingSettingsRecord.Category.ResourceRoles", 1001)
            .include(TagReadRole, TagReadAllRole, TagWriteRole, TagWriteAllRole, TagSearchRole, QueryTagHistoryRole);

    public long getId(){
        return getLong(Id);
    }

    public void setId(Long id){
        setLong(Id, id);
    }

    public long getAuthProfileId(){
        return getLong(AuthProfileId);
    }

    public void setAuthProfileId(Long authProfileId){
        setLong(AuthProfileId, authProfileId);
    }

    public String getTagReadRole(){
        return getString(TagReadRole);
    }

    public void setTagReadRole(String tagReadRole){
        setString(TagReadRole, tagReadRole);
    }

    public String getTagReadAllRole(){
        return getString(TagReadAllRole);
    }

    public void setTagReadAllRole(String tagReadAllRole){
        setString(TagReadAllRole, tagReadAllRole);
    }

    public String getTagWriteRole(){
        return getString(TagWriteRole);
    }

    public void setTagWriteRole(String tagReadRole){
        setString(TagWriteRole, tagReadRole);
    }

    public String getTagWriteAllRole(){
        return getString(TagWriteAllRole);
    }

    public void setTagWriteAllRole(String tagReadRole){
        setString(TagWriteAllRole, tagReadRole);
    }

    public String getTagSearchRole(){
        return getString(TagSearchRole);
    }

    public void setTagSearchRole(String tagSearchRole){
        setString(TagSearchRole, tagSearchRole);
    }

    public String getQueryTagHistoryRole(){
        return getString(QueryTagHistoryRole);
    }

    public void setQueryTagHistoryRole(String queryTagHistoryRole){
        setString(QueryTagHistoryRole, queryTagHistoryRole);
    }


    public TamakiScriptingSettingsRecord(){
        super();
    }

    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }
}
