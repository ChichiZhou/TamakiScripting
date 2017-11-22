package com.tamakicontrol.modules.scripting.gateway.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.model.values.Quality;
import com.inductiveautomation.ignition.common.script.builtin.DatasetUtilities;
import com.inductiveautomation.ignition.common.sqltags.model.TagPath;
import com.inductiveautomation.ignition.common.sqltags.model.types.TagValue;
import com.inductiveautomation.ignition.common.sqltags.parser.TagPathParser;
import com.inductiveautomation.ignition.gateway.datasource.BasicStreamingDataset;
import com.inductiveautomation.ignition.gateway.sqltags.model.BasicAsyncWriteRequest;
import com.inductiveautomation.ignition.gateway.sqltags.model.BasicWriteRequest;
import com.inductiveautomation.ignition.gateway.sqltags.model.WriteRequest;
import com.tamakicontrol.modules.scripting.AbstractDatasetUtils;
import com.tamakicontrol.modules.scripting.gateway.records.TamakiScriptingSettingsRecord;
import com.tamakicontrol.modules.scripting.gateway.scripts.GatewayDBUtils;
import com.tamakicontrol.modules.utils.ArgumentMap;
import com.tamakicontrol.modules.utils.JsonHistoryQueryParams;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//TODO add/edit/remove tags
//TODO query tag calculations
//TODO alarm.queryStatus, ack
//TODO user.getRoles, getUsers
//TODO sendEmail
//TODO run queries
//TODO add/edit/remove devices
//TODO documentation and comments
//TODO run internal query
public class ScriptingResource extends BaseServlet {

    @Override
    public String getUriBase() {
        return "/main/system/api";
    }

    @Override
    public String getAuthSource() {
        return "default";
    }

    @Override
    public void init() throws ServletException {
        super.init();

        //TODO make api browseable.  Give instructions, parameters, examples.
        addResource("/hello", METHOD_GET, helloWorldResource);
        addResource("/echo", METHOD_POST, echoResource);
        addResource("/tag/queryTagHistory", METHOD_POST, queryTagHistoryResource);
        addResource("/tag/test", METHOD_GET, testResource);
        addResource("/tag/read", METHOD_GET, getReadTagResource);
        addResource("/tag/read", METHOD_POST, postReadTagResource);
        addResource("/tag/readAll", METHOD_POST, readAllTagResource);
        addResource("/tag/write", METHOD_POST, writeTagResource);
        addResource("/tag/writeAll", METHOD_POST, writeAllTagResource);
        //addResource("/db/runInternalQuery", METHOD_POST, runInternalQueryResource);
    }

    private String getSystemName(){
        return getContext().getSystemProperties().getSystemName();
    }

    private ServletResource helloWorldResource = new ServletResource() {
        @Override
        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {
            resp.setContentType("text/plain");
            resp.getWriter().print("Hello World");
        }
    };

    private ServletResource echoResource = new ServletResource() {
        @Override
        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {
            resp.setContentType(req.getContentType());
            IOUtils.copy(req.getReader(), resp.getOutputStream());
        }
    };


    private ServletResource testResource = new ServletResource() {
        @Override
        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {

        }
    };

//    private ServletResource addTagResource = new ServletResource() {
//        @Override
//        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {
//            getContext().getTagManager().getTagProvider().addTags();
//            TagNode tagNode =
//
//        }
//    };


    /*
    *
    * queryTagHistoryResource
    *
    * Query tag history and return .json
    *
    * */
    //TODO Require fully qualified tag paths or find a way to provide defaults?
    private ServletResource queryTagHistoryResource = new ServletResource() {
        @Override
        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {

            if(!validateSecurity(req)){
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            BufferedReader reader = req.getReader();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonHistoryQueryParams queryParams = gson.fromJson(reader, JsonHistoryQueryParams.class);

            queryParams.setDefaultSystemName(getSystemName());
            queryParams.build();

            logger.debug(queryParams.toString());

            try {
                BasicStreamingDataset data = new BasicStreamingDataset();

                logger.info(data.toString());
                getContext().getTagManager().queryHistory(queryParams, data);


                if(queryParams.getDataFormat().equalsIgnoreCase("json")) {
                    resp.setContentType("application/json");
                    resp.getWriter().write(AbstractDatasetUtils.toJSON(data));
                }else if(queryParams.getDataFormat().equalsIgnoreCase("csv")) {
                    resp.setContentType("text/csv");


                    DatasetUtilities.toCSVJavaStreaming(data, true, false, resp.getWriter(), true);

                    //TODO gzip output?
                    /*
                    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(resp.getOutputStream());
                    Writer writer = new OutputStreamWriter(gzipOutputStream);
                    DatasetUtilities.toCSVJavaStreaming(data, true, false, writer, true);
                    */
                }

            }catch (Exception e){
                logger.error("Error while querying tag history", e);
                resp.sendError(400);
            }

        }
    };

    /*
    *
    * readTagResource
    *
    * Returns a qualified value for a provided tagpath
    *
    * */
    private ServletResource postReadTagResource = new ServletResource() {

        class TagResourceParameters extends JsonResourceParameters {
            private String tagPath = "";
            private transient TagPath _tagPath = null;

            private void build() throws IOException{
                //TODO null pointer exception here...
                //_tagPath = TagPathParser.parse("default", getSystemName(), tagPath);
                _tagPath = TagPathParser.parse(tagPath);
            }

            private TagPath getQualifiedTagPath(){
                return _tagPath;
            }
        }


        @Override
        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {

            if(!validateSecurity(req)){
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Gson gson = new GsonBuilder().create();
            TagResourceParameters params = gson.fromJson(req.getReader(), TagResourceParameters.class);

            try {
                logger.debug(params.toString());
                params.build();

                resp.setContentType("application/json");
                TagValue value = getContext().getTagManager().getTag(params.getQualifiedTagPath()).getValue();
                resp.getWriter().write(gson.toJson(value));
            }catch (IOException e){
                logger.warn("IO Exception while parsing tag path in resource", e);
                resp.sendError(400);
            }

        }
    };

    private ServletResource getReadTagResource = new ServletResource() {
        @Override
        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {

            if(!validateSecurity(req)){
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            ArgumentMap args = getRequestParams(req.getQueryString());
            String tagPath = args.getStringArg("tagPath");
            TagPath path = TagPathParser.parse("default", getSystemName(), tagPath);

            TagValue value = getContext().getTagManager().getTag(path).getValue();

            if (args.getStringArg("dataFormat", "json").equalsIgnoreCase("csv")) {
                resp.setContentType("text/csv");
                resp.getWriter().write(
                        String.format(
                                "_value,_quality,_timestamp\n%s,%s,%s",
                                value.getValue().toString(),
                                value.getQuality().toString(),
                                value.getTimestamp().toString())
                );

                //TODO gzip output?
                //GZIPOutputStream gzipStream = new GZIPOutputStream(resp.getOutputStream());

            } else {
                Gson gson = new Gson();
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(value));
            }

        }
    };

    private ServletResource readAllTagResource = new ServletResource() {

        class ReadAllTagResourceParams extends JsonResourceParameters {

            private List<String> tagPaths;
            private transient List<TagPath> _tagPaths;


            public void build() throws IOException{
                List<TagPath>__tagPaths = new ArrayList<>();

                for(String tagPath : tagPaths){
                    __tagPaths.add(TagPathParser.parse(tagPath));
                }

                this._tagPaths = __tagPaths;
            }

            public List<TagPath> getQualifiedTagPaths(){
                return _tagPaths;
            }

        }

        @Override
        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {
            if(!validateSecurity(req)){
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Gson gson = new Gson();
            ReadAllTagResourceParams params = gson.fromJson(req.getReader(), ReadAllTagResourceParams.class);
            params.build();

            List<QualifiedValue> values = getContext().getTagManager().read(params.getQualifiedTagPaths());

            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(values));
        }
    };

    private ServletResource writeTagResource = new ServletResource() {

        class WriteTagResourceParams extends JsonResourceParameters {
            private String tagPath = "";
            private transient TagPath _tagPath = null;

            private Object value;

            public void build() throws IOException{
                //TODO null pointer exception here
                //_tagPath = TagPathParser.parse("default", getSystemName(), tagPath);
                _tagPath = TagPathParser.parse(tagPath);
            }

            public TagPath getQualifiedTagPath(){
                return _tagPath;
            }

            public Object getValue(){
                return value;
            }
        }

        @Override
        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {

            if(!validateSecurity(req)){
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Gson gson = new Gson();
            WriteTagResourceParams params = gson.fromJson(req.getReader(), WriteTagResourceParams.class);
            params.build();

            @SuppressWarnings("unchecked")
            List<WriteRequest<TagPath>> writeRequests = new ArrayList<>();
            writeRequests.add(new BasicAsyncWriteRequest<>(params.getQualifiedTagPath(), params.getValue()));

            List<Quality> results = getContext().getTagManager().write(writeRequests, getRequestUser(req), false);

            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(results));
        }
    };

    private ServletResource writeAllTagResource = new ServletResource() {

        class WriteTagsResourceParams extends JsonResourceParameters {

            private List<String> tagPaths;
            private List<Object> values;
            private transient List<TagPath> _tagPaths;


            public void build() throws IOException{
                List<TagPath>__tagPaths = new ArrayList<>();

                for(String tagPath : tagPaths){
                    //TODO null pointer exception here
                    //__tagPaths.add(TagPathParser.parse("default", getSystemName(), tagPath));
                    __tagPaths.add(TagPathParser.parse(tagPath));
                }

                this._tagPaths = __tagPaths;
            }

            public List<TagPath> getQualifiedTagPaths(){
                return _tagPaths;
            }

            public List<Object> getValues(){
                return values;
            }

        }

        @Override
        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {

            if(!validateSecurity(req)){
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Gson gson = new Gson();
            WriteTagsResourceParams params = gson.fromJson(req.getReader(), WriteTagsResourceParams.class);
            params.build();

            List<WriteRequest<TagPath>> writeRequests = new ArrayList<>();
            for(int i=0; i < params.getQualifiedTagPaths().size(); i++){
                writeRequests.add(new BasicWriteRequest<>(
                        params.getQualifiedTagPaths().get(i), params.getValues().get(i)));
            }

            List<Quality> results = getContext().getTagManager().write(writeRequests, getRequestUser(req), false);

            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(results));
        }
    };


    //TODO implement this?  Or is it a security risk???
    private ServletResource runInternalQueryResource = new ServletResource() {

        class InternalQueryParams{
            public String query;
            public List<String> args;
        }

        @Override
        public void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IllegalArgumentException {

            if(!validateSecurity(req)){
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Gson gson = new Gson();
            InternalQueryParams params = gson.fromJson(req.getReader(), InternalQueryParams.class);

            //TODO check this much better
            String query = params.query;
            query.toLowerCase();
            if(query.contains("update") || query.contains("insert") || query.contains("drop")){
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

            GatewayDBUtils dbUtils = new GatewayDBUtils(getContext());
            List<List<Object>> data = dbUtils.runInternalQuery(params.query);
                    //dbUtils.runPrepInternalQuery(params.query, params.args.toArray());

            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(data));
        }
    };



}

