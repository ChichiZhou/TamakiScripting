package com.tamakicontrol.modules.scripting;

public interface TagUtilProvider {

    /**
     *
     * getParamValue
     *
     * @author Cody Warren
     * @since 2015
     *
     * @param tagPath
     * @param paramName
     *
     * */
    Object getParameterValue(String tagPath, String paramName);
}
