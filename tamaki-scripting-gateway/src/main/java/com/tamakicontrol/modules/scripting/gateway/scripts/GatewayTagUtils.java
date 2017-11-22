package com.tamakicontrol.modules.scripting.gateway.scripts;

import com.inductiveautomation.ignition.common.config.ExtendedPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.sqltags.model.TagPath;
import com.inductiveautomation.ignition.common.sqltags.model.TagProp;
import com.inductiveautomation.ignition.common.sqltags.model.event.TagChangeEvent;
import com.inductiveautomation.ignition.common.sqltags.model.event.TagChangeListener;
import com.inductiveautomation.ignition.common.sqltags.parser.TagPathParser;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.tamakicontrol.modules.scripting.AbstractTagUtils;

import org.python.core.PyObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GatewayTagUtils extends AbstractTagUtils {

    private GatewayContext context;
    private final Logger logger = LoggerFactory.getLogger("Tamaki Scripting");

    public GatewayTagUtils(GatewayContext gatewayContext){
        this.context = gatewayContext;
    }

    @Override
    protected Object getParameterValueImpl(String tagPath, String paramName) {
        List<TagPath> tagPaths = new ArrayList<>();
        tagPath = String.format("%s.ExtendedProperties", tagPath );
        try {
            tagPaths.add(TagPathParser.parse(tagPath));
            List<QualifiedValue> values = context.getTagManager().read(tagPaths);

            if(values.size() > 1) {
                ExtendedPropertySet parameters = (ExtendedPropertySet)values.get(0).getValue();
                for (PropertyValue<?> param : parameters) {
                    if (param.getProperty().getName().equals(paramName))
                        return param.getValue();
                }
            }

        }catch (IOException e){
            logger.error(String.format("Error while parsing tagpath: %s", tagPath ), e);
        }catch (ClassCastException e){
            logger.error(String.format("Error while casting properties for tagpath: %s", tagPath ), e);
        }

        return null;
    }

    @Override
    protected TagChangeListener subscribeImpl(String tagPath, PyObject onChange) throws Exception {

        TagChangeListener listener =  new TagChangeListener() {
            @Override
            public void tagChanged(TagChangeEvent tagChangeEvent) {
                onChange.__call__((PyObject) tagChangeEvent.getTag().getValue());
            }

            @Override
            public TagProp getTagProperty() {
                return null;
            }
        };

        context.getTagManager().subscribe(TagPathParser.parse(tagPath), listener);

        return listener;
    }

    @Override
    protected ArrayList<TagChangeListener> subscribeAllImpl(List<String> tagPaths, List<PyObject> changeHandlers) throws Exception {
        ArrayList<TagPath> paths = new ArrayList<TagPath>();
        ArrayList<TagChangeListener> listeners = new ArrayList<TagChangeListener>();

        if(tagPaths.size() != changeHandlers.size())
            throw new Exception("List Sizes are not Equal");

        for(int i=0; i < tagPaths.size(); i++){
            paths.add(TagPathParser.parse(tagPaths.get(0)));

            final PyObject changeListener = changeHandlers.get(i);
            listeners.add(new TagChangeListener() {
                @Override
                public void tagChanged(TagChangeEvent tagChangeEvent) {
                    changeListener.__call__((PyObject)tagChangeEvent.getTag().getValue());
                }

                @Override
                public TagProp getTagProperty() {
                    return null;
                }
            });
        }

        context.getTagManager().subscribe(paths, listeners);

        return listeners;
    }

    @Override
    protected void unsubscribeImpl(String tagPath, TagChangeListener onChange) throws Exception {
        context.getTagManager().unsubscribe(TagPathParser.parse(tagPath), onChange);
    }

    @Override
    protected void unsubscribeAllImpl(List<String> tagPaths, List<TagChangeListener> changeHandlers) throws Exception {
        ArrayList<TagPath> paths = new ArrayList<>();

        for(int i=0; i < tagPaths.size(); i++)
            paths.add(TagPathParser.parse(tagPaths.get(i)));

        context.getTagManager().unsubscribe(paths, changeHandlers);
    }
}
