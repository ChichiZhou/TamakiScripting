package com.tamakicontrol.modules.scripting.client.scripts;

import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.client.script.ClientTagUtilities;
import com.inductiveautomation.ignition.common.config.ExtendedPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.expressions.Expression;
import com.inductiveautomation.ignition.common.expressions.ExpressionException;
import com.inductiveautomation.ignition.common.expressions.functions.AbstractFunction;
import com.inductiveautomation.ignition.common.model.values.BasicQualifiedValue;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.model.values.QualityCode;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataQuality;
import com.tamakicontrol.modules.scripting.AbstractTagUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ClientTagUtils extends AbstractTagUtils {

    // Get inductive client tag utilities
    private static ClientTagUtilities clientTagUtilities;

    private static final Logger logger = LoggerFactory.getLogger(ClientTagUtils.class);

    private ClientTagUtils(ClientContext context){
        super(context);
    }

    private static ClientTagUtils instance;

    public static synchronized void initialize(ClientContext context){
        if(instance == null){
            instance = new ClientTagUtils(context);
        }
    }

    public static ClientTagUtils getInstance(){
        return instance;
    }

    public static class GetParameterValueFunction extends AbstractFunction {

        @Override
        protected String getFunctionDisplayName() {
            return "getParamValue";
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
            try {
                String tagPath = (String)expressions[0].execute().getValue();
                String paramName = (String)expressions[1].execute().getValue();

                Object value = ClientTagUtils.getInstance().getParameterValue(tagPath, paramName);
                return new BasicQualifiedValue(value);
            }catch(Exception e){
                return new BasicQualifiedValue(-1, QualityCode.Bad);
            }
        }

        @Override
        protected boolean validateNumArgs(int num) {
            return num == 2;
        }

    }

}
