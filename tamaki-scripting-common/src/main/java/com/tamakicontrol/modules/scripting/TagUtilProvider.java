package com.tamakicontrol.modules.scripting;

import com.inductiveautomation.ignition.common.sqltags.model.event.TagChangeListener;
import org.python.core.PyObject;

import java.util.List;

public interface TagUtilProvider {

    Object getParameterValue(String tagPath, String paramName);

    TagChangeListener subscribe(String tagPath, PyObject onChange) throws Exception;

    List<TagChangeListener> subscribeAll(List<String> tagPaths, List<PyObject> changeHandlers) throws Exception;

    void unsubscribe(String tagPath, TagChangeListener onChange) throws Exception;

    void unsubscribeAll(List<String> tagPaths, List<TagChangeListener> changeHandlers) throws Exception;

}
