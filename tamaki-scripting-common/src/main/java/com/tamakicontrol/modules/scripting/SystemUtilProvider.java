package com.tamakicontrol.modules.scripting;

import com.inductiveautomation.ignition.common.model.BaseContext;
import org.python.core.PyObject;

public interface SystemUtilProvider {

    String getUUID() throws Exception;

    String getStackTrace() throws Exception;

    BaseContext getContext() throws Exception;

    void addToTaskQueue(PyObject object) throws Exception;

    void clearTaskQueue() throws Exception;

    int getTaskQueueLength() throws Exception;

    Object runAtGateway(PyObject object) throws Exception;

}