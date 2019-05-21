package com.tamakicontrol.modules.scripting;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.config.ExtendedPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.model.CommonContext;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.script.hints.ScriptArg;
import com.inductiveautomation.ignition.common.script.hints.ScriptFunction;
import com.inductiveautomation.ignition.common.sqltags.model.event.TagChangeListener;
import com.inductiveautomation.ignition.common.tags.model.TagPath;
import com.inductiveautomation.ignition.common.tags.paths.parser.TagPathParser;
import org.python.core.PyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class AbstractTagUtils implements TagUtilProvider {

    static {
        BundleUtil.get().addBundle(
                AbstractSystemUtils.class.getSimpleName(),
                AbstractSystemUtils.class.getClassLoader(),
                AbstractSystemUtils.class.getName().replace('.', '/')
        );
    }

    private static final Logger logger = LoggerFactory.getLogger(AbstractTagUtils.class);

    protected CommonContext context;

    public AbstractTagUtils(CommonContext context){
        this.context = context;
    }

    @Override
    @ScriptFunction(docBundlePrefix = "TagUtils")
    public Object getParameterValue(
            @ScriptArg("tagPath") String tagPath,
            @ScriptArg("paramName") String paramName
    ) {
        return getParameterValueImpl(tagPath, paramName);
    }

    protected Object getParameterValueImpl(String tagPath, String paramName) {
        ExtendedPropertySet parameters = (ExtendedPropertySet)read(tagPath + ".ExtendedProperties").getValue();

        try {
            for (PropertyValue param : parameters) {
                if (param.getProperty().getName().equals(paramName))
                    return param.getValue();
            }
        }catch(Exception e){
            return null;
        }

        return null;
    }

    @Nullable
    protected QualifiedValue read(String tagPath){

        List<TagPath> tagPaths = Arrays.asList(TagPathParser.parseSafe(tagPath + ".ExtendedProperties"));

        try {
            return context.getTagManager().readAsync(tagPaths).get().get(0);
        }catch (ExecutionException | InterruptedException e){
            logger.error("Exception thrown while reading tag", e);
        }

        return null;
    }

}
