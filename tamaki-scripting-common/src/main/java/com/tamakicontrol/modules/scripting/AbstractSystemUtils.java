package com.tamakicontrol.modules.scripting;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.expressions.Expression;
import com.inductiveautomation.ignition.common.expressions.ExpressionException;
import com.inductiveautomation.ignition.common.expressions.functions.AbstractFunction;
import com.inductiveautomation.ignition.common.model.BaseContext;
import com.inductiveautomation.ignition.common.model.values.BasicQualifiedValue;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.script.hints.ScriptFunction;
import org.python.core.PyObject;

import java.util.UUID;

public abstract class AbstractSystemUtils implements SystemUtilProvider {

    static {
        BundleUtil.get().addBundle(
                AbstractSystemUtils.class.getSimpleName(),
                AbstractSystemUtils.class.getClassLoader(),
                AbstractSystemUtils.class.getName().replace('.', '/')
        );
    }

    /**
     * Returns a universally unique identifier from java.util.UUID
     *
     * @return String - Universally unique identifier
     */
    @ScriptFunction(docBundlePrefix = "SystemUtils")
    public String getUUID() throws Exception{
        return UUID.randomUUID().toString();
    }

    /**
     * Ignition expression function to return a universally unique identifier from java.util.UUID
     */
    public static class GetUUIDFunction extends AbstractFunction{

        @Override
        protected String getFunctionDisplayName() {
            return "getUUID";
        }

        @Override
        public String getArgDocString() {
            return "";
        }

        @Override
        public Class<?> getType() {
            return Object.class;
        }

        @Override
        public QualifiedValue execute(Expression[] expressions) throws ExpressionException {
            return new BasicQualifiedValue(UUID.randomUUID().toString());
        }

    }

    /**
     * Returns the stack trace for the currently running thread
     *
     * @return String - Java stack trace for the currently running thread
     */
    @ScriptFunction(docBundlePrefix = "SystemUtils")
    public String getStackTrace() throws Exception{
        return Thread.currentThread().getStackTrace()[0].toString();
    }

    /**
     * Ignition function to return a stack trace string for the currently running thread
     */
    public static class GetStackTraceFunction extends AbstractFunction{

        @Override
        protected String getFunctionDisplayName() {
            return "getUUID";
        }

        @Override
        public String getArgDocString() {
            return "";
        }

        @Override
        public Class<?> getType() {
            return Object.class;
        }

        @Override
        public QualifiedValue execute(Expression[] expressions) throws ExpressionException {
            return new BasicQualifiedValue(Thread.currentThread().getStackTrace()[0].toString());
        }

    }

    @Override
    public BaseContext getContext() throws Exception {
        return getContextImpl();
    }

    protected abstract BaseContext getContextImpl() throws Exception;

    @Override
    public void addToTaskQueue(PyObject object) throws Exception {
        addToTaskQueueImpl(object);
    }

    protected void addToTaskQueueImpl(PyObject object) throws Exception {
        TamakiTaskQueue.getInstance().addTaskToQueue(object);
    }

    @Override
    public void clearTaskQueue() throws Exception {
        TamakiTaskQueue.getInstance();
    }

    @Override
    public int getTaskQueueLength() throws Exception {
        return 0;
    }

    @Override
    public Object runAtGateway(PyObject object) throws Exception {
        return runAtGatewayImpl(object);
    }

    //TODO implement this
    protected Object runAtGatewayImpl(PyObject object) throws Exception {
        throw new Exception("Function is not yet implemented");
    }
}

