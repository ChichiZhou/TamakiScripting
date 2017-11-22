package com.tamakicontrol.modules.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inductiveautomation.ignition.common.Path;
import com.inductiveautomation.ignition.common.script.builtin.DateUtilities;
import com.inductiveautomation.ignition.common.sqltags.history.Aggregate;
import com.inductiveautomation.ignition.common.sqltags.history.AggregationMode;
import com.inductiveautomation.ignition.common.sqltags.history.ReturnFormat;
import com.inductiveautomation.ignition.common.sqltags.history.TagHistoryQueryParams;
import com.inductiveautomation.ignition.common.util.Flags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonHistoryQueryParams implements TagHistoryQueryParams {

    private static final Logger logger = LoggerFactory.getLogger("Tamaki JSON Query Params");

    private List<String> tagPaths = new ArrayList<String>();
    private String dataFormat = "json";
    private String defaultDatasource = "Historian";
    private String defaultTagProvider = "default";
    private String defaultSystemName = "Ignition157";
    private List<? extends Path> paths = new ArrayList<Path>();
    private List<String> aliases = new ArrayList<String>();
    private Date startDate = new Date();
    private Date endDate = new Date();
    private int returnSize = 0;
    private String aggregationMode = "Average";
    private Aggregate _aggregationMode = AggregationMode.Average;
    private List<String> aggregationModes = new ArrayList<String>();
    private List<Aggregate> columnAggregationModes = new ArrayList<Aggregate>();
    private ReturnFormat returnFormat = ReturnFormat.Wide;
    private Flags queryFlags = new Flags();

    public JsonHistoryQueryParams(){
        this.startDate = new Date();
        startDate = DateUtilities.addSeconds(startDate, -600);
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public List<String> getTagPaths(){
        return this.tagPaths;
    }

    public void setTagPaths(List<String> tagPaths){
        this.tagPaths = tagPaths;
        buildPaths();
    }

    public String getDataFormat(){
        return this.dataFormat;
    }

    public void setDataFormat(String dataFormat){
        this.dataFormat = dataFormat;
    }

    public String getDefaultDatasource(){
        return defaultDatasource;
    }

    public void setDefaultDatasource(String defaultDatasource){
        this.defaultDatasource = defaultDatasource;
    }

    public String getDefaultTagProvider(){
        return defaultTagProvider;
    }

    public void setDefaultTagProvider(String defaultTagProvider){
        this.defaultTagProvider = defaultTagProvider;
    }

    public String getDefaultSystemName(){
        return defaultSystemName;
    }

    public void setDefaultSystemName(String defaultSystemName){
        this.defaultSystemName = defaultSystemName;
    }

    public void build(){
        buildPaths();
        buildAggregationMode();
        buildAggregationModes();
    }

    private void buildPaths(){
        logger.debug("Building tag history paths");

        try {
            List<Path> _paths = new ArrayList<Path>();
            for (String tagPath : tagPaths) {
                _paths.add(HistoricalTagPathParser.parse(tagPath, defaultDatasource, defaultTagProvider, defaultSystemName));
            }

            setPaths(_paths);
        }catch (Exception e){
            logger.error("Error while parsing tag path", e);
        }
    }

    private void buildAggregationModes(){
        List<Aggregate> _aggregationModes = new ArrayList<Aggregate>();
        for(String aggMode : aggregationModes){
            _aggregationModes.add(AggregationMode.valueOfCaseInsensitive(aggMode));
        }

        setColumnAggregationModes(_aggregationModes);
    }

    private void buildAggregationMode(){
        setAggregationMode(AggregationMode.valueOfCaseInsensitive(aggregationMode));
    }

    public void addTagPath(String tagPath){
        tagPaths.add(tagPath);
        buildPaths();
    }

    @Override
    public List<? extends Path> getPaths() {
        return paths;
    }

    public void setPaths(List<? extends Path> paths){
        logger.debug("Setting history paths");
        this.paths = paths;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases){
        this.aliases = aliases;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }

    @Override
    public int getReturnSize() {
        return returnSize;
    }

    public void setReturnSize(int returnSize){
        this.returnSize = returnSize;
    }

    @Override
    public Aggregate getAggregationMode() {
        return _aggregationMode;
    }

    public void setAggregationMode(Aggregate aggregationMode){
        this._aggregationMode = aggregationMode;
    }

    public void setAggregationMode(String aggregationMode){
        this.aggregationMode = aggregationMode;
        this._aggregationMode = AggregationMode.valueOfCaseInsensitive(aggregationMode);
    }

    public List<String> getAggregationModes(){
        return aggregationModes;
    }

    public void setAggregationModes(List<String> aggregationModes){
        this.aggregationModes = aggregationModes;
    }

    @Override
    public List<Aggregate> getColumnAggregationModes() {
        return columnAggregationModes;
    }

    public void setColumnAggregationModes(List<Aggregate> columnAggregationModes){
        this.columnAggregationModes = columnAggregationModes;
    }

    @Override
    public ReturnFormat getReturnFormat() {
        return returnFormat;
    }

    public void setReturnFormat(String returnFormat){
        if(returnFormat.toUpperCase().equals("WIDE"))
            this.returnFormat = ReturnFormat.Wide;
        else if(returnFormat.toUpperCase().equals("TALL"))
            this.returnFormat = ReturnFormat.Tall;
        else{
            logger.warn(String.format("Unrecognized return format %s", returnFormat));
        }
    }

    @Override
    public Flags getQueryFlags() {
        return queryFlags;
    }

    public void setQueryFlags(Flags queryFlags){
        this.queryFlags = queryFlags;
    }


}
