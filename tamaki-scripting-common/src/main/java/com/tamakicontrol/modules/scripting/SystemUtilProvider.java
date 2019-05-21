package com.tamakicontrol.modules.scripting;

import com.inductiveautomation.ignition.common.model.CommonContext;
import org.python.core.PyObject;

public interface SystemUtilProvider {

    String getUUID() throws Exception;

    String getStackTrace() throws Exception;

    CommonContext getContext() throws Exception;

    void addToTaskQueue(PyObject object) throws Exception;

    void clearTaskQueue() throws Exception;

    int getTaskQueueLength() throws Exception;

    Object runAtGateway(PyObject object) throws Exception;

}