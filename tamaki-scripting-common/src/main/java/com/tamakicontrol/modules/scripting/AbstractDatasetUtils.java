package com.tamakicontrol.modules.scripting;

import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.expressions.Expression;
import com.inductiveautomation.ignition.common.expressions.ExpressionException;
import com.inductiveautomation.ignition.common.expressions.functions.AbstractFunction;
import com.inductiveautomation.ignition.common.model.values.BasicQualifiedValue;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.tamakicontrol.modules.utils.TamakiDataset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AbstractDatasetUtils {

    private static final Logger logger = LoggerFactory.getLogger("Tamaki Dataset Functions");

    public static String toJSON(Dataset data) {

        List<String> columns = data.getColumnNames();

        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < data.getRowCount(); i++) {
                JSONObject jsonObject = new JSONObject();

                for (String column : columns) {
                    jsonObject.put(column, data.getValueAt(i, column));
                }

                jsonArray.put(jsonObject);
            }

            return jsonArray.toString();

        }catch (JSONException e){
            logger.error("Error while exporting json data", e);
        }

        return "{[]}";
    }


    public static TamakiDataset toTamakiDataset(Dataset data) {
        return new TamakiDataset(data);
    }


    public static class ToTamakiDatasetFunction extends AbstractFunction{

        @Override
        public String getArgDocString() {
            return null;
        }

        @Override
        public Class<?> getType() {
            return null;
        }

        @Override
        protected boolean validateNumArgs(int num) {
            return num == 1;
        }

        @Override
        protected String getFunctionDisplayName() {
            return "toTamakiDataset";
        }

        @Override
        public QualifiedValue execute(Expression[] expressions) throws ExpressionException {
            TamakiDataset data = TamakiDataset.from((Dataset)expressions[0].execute().getValue());
            return new BasicQualifiedValue(data);
        }

    }

}
