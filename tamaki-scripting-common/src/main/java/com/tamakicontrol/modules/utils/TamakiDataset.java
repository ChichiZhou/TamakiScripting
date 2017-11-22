package com.tamakicontrol.modules.utils;

import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;
import org.python.core.PyDictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TamakiDataset extends BasicDataset {

    public TamakiDataset(List<String> columnNames, List<Class<?>> columnTypes, Object[][] data){
        if(columnNames.size() == data.length)
            this.setAllDirectly(columnNames, columnTypes, data);
        else
            this.setAllDirectly(columnNames, columnTypes, transpose(data, data.length, columnNames.size()));
    }

    public TamakiDataset(Dataset data){
        super(data);
    }

    public static TamakiDataset from(Dataset data){
        return new TamakiDataset(data);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new TamakiDataset(this.getColumnNames(), this.getColumnTypes(), this.getData());
    }

    public Class<?> getTypeForColumn(String columnName){
        List<Class<?>> types = this.getColumnTypes();
        return this.getColumnTypes().get(this.getColumnNames().indexOf(columnName));
    }

    public List<Object> getColumn(String columnName){
        List<Object> column = new ArrayList<Object>();
        for(int i = 0; i < this.getRowCount(); i++){
            column.add(this.getValueAt(i, columnName));
        }

        return column;
    }

    public Set<Object> distinct(Dataset data, String columnName){
        return new HashSet<Object>(getColumn(columnName));
    }

    public Object[][] transpose(Object[][] data, int rows, int cols){
        Object[][] _data = new Object[cols][rows];

        for(int i = 0; i < cols; i++){
            for(int j = 0; j < rows; j++){
                _data[i][j] = data[j][i];
            }
        }

        return _data;
    }

    public Object[] getRow(int rowNum){
        Object[] row = new Object[this.getColumnCount()];

        for(int j = 0; j < this.getColumnCount(); j++)
            row[j] = this.getValueAt(rowNum, j);

        return row;
    }

    // TODO Inductive fucked up and did columnar major datasets
    public TamakiDataset filterRows(List<Integer> rows) throws Exception {
        Object[][] _data = new Object[rows.size()][this.getColumnCount()];
        TamakiDataset __data = (TamakiDataset)this.clone();

        for(int i = 0; i < rows.size(); i++){
            _data[i] = getRow(i);
        }

        return new TamakiDataset(this.getColumnNames(), this.getColumnTypes(), _data);
    }

    public TamakiDataset whereEquals(String column, Object value) throws Exception {
        List<Integer> filterRows = new ArrayList<Integer>();
        for(int i=0; i < this.getRowCount(); i++)
            if(this.getValueAt(i, column).equals(value))
                filterRows.add(i);

        return this.filterRows(filterRows);
    }

    public TamakiDataset whereNotEqual(String column, Object value) throws Exception {
        List<Integer> filterRows = new ArrayList<Integer>();
        for(int i=0; i < this.getRowCount(); i++){
            if(!this.getValueAt(i, column).equals(value)){
                filterRows.add(i);
            }
        }

        return this.filterRows(filterRows);
    }

    public TamakiDataset whereGreaterThan(String column, Object value) throws Exception {
        Double _value = Double.valueOf(value.toString());
        List<Integer> filterRows = new ArrayList<Integer>();
        for(int i=0; i < this.getRowCount(); i++){
            if(_value.compareTo((Double)this.getValueAt(i, column)) > 0){
                filterRows.add(i);
            }
        }

        return this.filterRows(filterRows);
    }

    public TamakiDataset whereGreaterThanEqual(String column, Object value) throws Exception {
        Double _value = Double.valueOf(value.toString());
        List<Integer> filterRows = new ArrayList<Integer>();
        for(int i=0; i < this.getRowCount(); i++){
            if(_value.compareTo((Double)this.getValueAt(i, column)) >= 0){
                filterRows.add(i);
            }
        }

        return this.filterRows(filterRows);
    }

    public TamakiDataset whereLessThan(String column, Object value) throws Exception {
        Double _value = Double.valueOf(value.toString());
        List<Integer> filterRows = new ArrayList<Integer>();
        for(int i=0; i < this.getRowCount(); i++){
            if(_value.compareTo((Double)this.getValueAt(i, column)) < 0){
                filterRows.add(i);
            }
        }

        return this.filterRows(filterRows);
    }

    public TamakiDataset whereLessThanEqual(String column, Object value) throws Exception {
        Double _value = Double.valueOf(value.toString());
        List<Integer> filterRows = new ArrayList<Integer>();
        for(int i=0; i < this.getRowCount(); i++){
            if(_value.compareTo((Double)this.getValueAt(i, column)) <= 0){
                filterRows.add(i);
            }
        }

        return this.filterRows(filterRows);
    }

    public TamakiDataset whereRegexp(String column, String regex) throws Exception {
        List<Integer> filterRows = new ArrayList<Integer>();
        for(int i=0; i < this.getRowCount(); i++){
            if(String.valueOf(this.getValueAt(i, column)).matches(regex)){
                filterRows.add(i);
            }
        }

        return this.filterRows(filterRows);
    }

    public TamakiDataset innerJoin(Dataset data, String joinColumn) throws Exception {
        return null;
    }

    public TamakiDataset leftJoin(Dataset data, PyDictionary conditions) {
        return null;
    }

    public TamakiDataset rightJoin(Dataset data, PyDictionary conditions) {
        return null;
    }

    public TamakiDataset outerJoin(Dataset data, PyDictionary conditions){
        return null;
    }

    private Object[][] copyInto(Object[][] data1, Object[][] data2){

        if(data1.length < 1)
            return data2;

        for(int i=0; i < data1.length; i++){
            for(int j=0; j < data1[0].length; j++){
                data2[i][j] = data1[i][j];
            }
        }

        return data2;
    }

    public TamakiDataset append(List<Object> data) throws Exception{
        if(this.getColumnCount() != data.size())
            throw new Exception("Provided dataset columns do not match this dataset's columns.");

        Object[][] _data = new Object[this.getRowCount() + 1][this.getColumnCount()];
        this.copyInto(this.data, _data);

        for(int j=0; j < data.size(); j++) {
            _data[_data.length - 1][j] = data.get(j);
        }

        return new TamakiDataset(this.getColumnNames(), this.getColumnTypes(), _data);
    }

    public TamakiDataset union(Dataset data) throws Exception{

        if(!this.getColumnNames().equals(data.getColumnNames()))
            throw new Exception("Provided dataset columns do not match this dataset's columns.");

        int rows = data.getRowCount() + this.getRowCount();

        Object[][] _data = new Object[rows][this.getColumnCount()];

        for(int i=0; i < rows; i++){
            for(int j=0; j < this.getColumnCount(); j++){
                _data[i][j] = this.getValueAt(i, j);
            }
        }

        for(int i=0; i < rows; i++){
            for(int j=0; j < this.getColumnCount(); j++){
                _data[i + this.getRowCount() - 1][j] = data.getValueAt(i, j);
            }
        }

        return new TamakiDataset(this.getColumnNames(), this.getColumnTypes(), _data);
    }


    public TamakiDataset groupBy(String columnName){
        return null;
    }

    public int countAll(){
        return this.getRowCount();
    }

    public int count(String columnName){
        int count = 0;
        for(int i=0; i < this.getRowCount(); i++){
            count += (this.getValueAt(i, columnName) != null ? 1 : 0);
        }

        return count;
    }

}
