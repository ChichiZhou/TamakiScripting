package com.tamakicontrol.modules.scripting;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.script.hints.ScriptArg;
import com.inductiveautomation.ignition.common.script.hints.ScriptFunction;
import com.inductiveautomation.ignition.common.sqltags.model.event.TagChangeListener;
import org.python.core.PyObject;

import java.util.List;

public abstract class AbstractTagUtils implements TagUtilProvider {

    static {
        BundleUtil.get().addBundle(
                AbstractSystemUtils.class.getSimpleName(),
                AbstractSystemUtils.class.getClassLoader(),
                AbstractSystemUtils.class.getName().replace('.', '/')
        );
    }

    @Override
    @ScriptFunction(docBundlePrefix = "TagUtils")
    public Object getParameterValue(
            @ScriptArg("tagPath") String tagPath,
            @ScriptArg("paramName") String paramName
    ) {
        return getParameterValueImpl(tagPath, paramName);
    }

    protected abstract Object getParameterValueImpl(String tagPath, String paramName);

    @Override
    @ScriptFunction(docBundlePrefix = "TagUtils")
    public TagChangeListener subscribe(@ScriptArg("tagPath") String tagPath, @ScriptArg("onChange") PyObject onChange) throws Exception {
        return subscribeImpl(tagPath, onChange);
    }

    protected abstract TagChangeListener subscribeImpl(String tagPath, PyObject onChange) throws Exception;

    @Override
    @ScriptFunction(docBundlePrefix = "TagUtils")
    public List<TagChangeListener> subscribeAll(@ScriptArg("tagPaths") List<String> tagPaths,
                                                @ScriptArg("changeHandlers") List<PyObject> changeHandlers)
            throws Exception {
        return subscribeAllImpl(tagPaths, changeHandlers);
    }

    protected abstract List<TagChangeListener> subscribeAllImpl(List<String> tagPaths, List<PyObject> changeHandlers)
            throws Exception;

    @Override
    @ScriptFunction(docBundlePrefix = "TagUtils")
    public void unsubscribe(@ScriptArg("tagPath") String tagPath, @ScriptArg("onChange") TagChangeListener onChange)
            throws Exception {
        unsubscribeImpl(tagPath, onChange);
    }

    protected abstract void unsubscribeImpl(String tagPath, TagChangeListener onChange) throws Exception;

    @Override
    @ScriptFunction(docBundlePrefix = "TagUtils")
    public void unsubscribeAll(@ScriptArg("tagPaths") List<String> tagPaths,
                               @ScriptArg("changeHandlers") List<TagChangeListener> changeHandlers) throws Exception {
        unsubscribeAllImpl(tagPaths, changeHandlers);
    }

    protected abstract void unsubscribeAllImpl(List<String> tagPaths, List<TagChangeListener> changeHandlers) throws Exception;

}
