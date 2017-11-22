package com.tamakicontrol.modules.utils;

import com.inductiveautomation.ignition.common.QualifiedPath;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoricalTagPathParser{

    /**
     *
     * Creates a fully qualified Ignition tag history path.
     *
     * @param tagPath - tag path to parse
     * @return QualifiedPath - qualified tag path
     *
     */
    public static QualifiedPath parse(String tagPath, String defaultDatasource, String defaultTagProvider,
                                              String defaultSystemName){

        String dataSource="unknown", tagProvider="unknown", sysName="unknown", path="unknown";

        //tagPath.matches("\\[.*\\].*")
        Matcher notDefaults = Pattern.compile("^\\[(.*)\\](.*)$").matcher(tagPath);

        // Matches strings starting with [*]
        if(notDefaults.find()){
            String notDefaultData = notDefaults.group(1); // pull out anything inside []
            path = notDefaults.group(2); // pull off the tagpath

            Matcher sysDatasourceMatch = Pattern.compile("(.*)/\\.(.*)").matcher(notDefaultData);
            Matcher fullyQualifiedMatch = Pattern.compile("(.*)/(.*):(.*)").matcher(notDefaultData);

            if(notDefaultData.equals("~")){
                dataSource = defaultDatasource;
                tagProvider = defaultTagProvider;
                sysName = defaultSystemName;

                // matches just a string inside ex: process, historian etc.
            }else if(notDefaultData.matches("[a-zA-Z]+")){
                dataSource = notDefaultData;
                tagProvider = defaultTagProvider;
                sysName = defaultSystemName;

                // matches a fully qualified tagpath ex: process/ignition-cody-pc:default
            }else if(fullyQualifiedMatch.find()){
                dataSource = fullyQualifiedMatch.group(1);
                sysName = fullyQualifiedMatch.group(2);
                tagProvider = fullyQualifiedMatch.group(3);

                // matches a datasource & tag provider match ex: process/.system, /.system, historian/.system etc.
            }else if(sysDatasourceMatch.find()){
                dataSource = sysDatasourceMatch.group(1).isEmpty() ? defaultDatasource : sysDatasourceMatch.group(1);
                tagProvider =sysDatasourceMatch.group(2);
                sysName = defaultSystemName;
            }

        }else{
            dataSource = defaultDatasource;
            tagProvider = defaultTagProvider;
            sysName = defaultSystemName;
            path = tagPath;
        }

        String strPath = String.format("histprov:%s:/drv:%s:%s:/tag:%s", dataSource, sysName, tagProvider, path);
        return QualifiedPath.parseSafe(strPath);
    }

    public static void test(String[] args){

        String[] tagPaths = {
                "tags/ramp/ramp0",
                "[~]tags/ramp/ramp0",
                "[~]tags/ramp/ramp0",
                "[~]demoproject/cipdemo/basic/c1s_cip1_operpause/value",
                "[process]tags/random/randominteger2",
                "[/.system]gateway/performance/cpu usage",
                "[Test]gateway/database/historian/queriespersecond",
                "[Test/.system]gateway/database/historian/queriespersecond",
                "[/.system]gateway/database/historian/activeconnections",
                "[historian/ignition-cody-pc:system]gateway/database/historian/queriespersecond4",
                "[historian/ignition-cody-pc:default]tags/ramp/ramp0",
                "[process/ignition-cody-pc:default]tags/random/randomboolean1"
        };

        for(int i=0; i < tagPaths.length; i++){
            System.out.println(
                    parse(tagPaths[i], "Historian", "default", "ignition-cody-pc")
            );
        }

    }

}