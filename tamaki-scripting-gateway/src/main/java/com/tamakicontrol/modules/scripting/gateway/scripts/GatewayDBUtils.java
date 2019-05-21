package com.tamakicontrol.modules.scripting.gateway.scripts;

import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistenceSession;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

import com.tamakicontrol.modules.scripting.AbstractDBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class GatewayDBUtils extends AbstractDBUtils {

    private final Logger logger = LoggerFactory.getLogger("Tamaki Scripting");
    private GatewayContext context;

    public GatewayDBUtils(GatewayContext _context) {context = _context;}

    @Override
    public List<List<Object>> runInternalQueryImpl(String query) {
        PersistenceSession internalDBSession = context.getPersistenceInterface().getSession();
        List<List<Object>> jData = null;

        try {
            jData = internalDBSession.getDBInterface().runQuery(query);
        }catch (SQLException e){
            logger.error(e.getStackTrace().toString());
        }finally {
            internalDBSession.close();
        }

        return jData;
    }

    @Override
    public List<List<Object>> runPrepInternalQueryImpl(String query, Object[] args){
        PersistenceSession internalDBSession = context.getPersistenceInterface().getSession();
        List<List<Object>> jData = null;

        try{
            jData = internalDBSession.getDBInterface().runPrepQuery(query, args);
        }catch (SQLException e){
            logger.error("Exception thrown while querying internal database", e);
        }finally {
            internalDBSession.close();
        }

        return jData;
    }

    @Override
    protected int runInternalUpdateQueryImpl(String query) {
        PersistenceSession internalDBSession = context.getPersistenceInterface().getSession();
        int retSize = 0;

        try{
            retSize = internalDBSession.getDBInterface().runUpdateQuery(query);
            internalDBSession.commit();
        }catch(SQLException e){
            internalDBSession.rollback();
            logger.error("Exception thrown while querying internal database", e);
        }finally {
            internalDBSession.close();
        }

        return retSize;
    }

    @Override
    protected int runPrepInternalUpdateQueryImpl(String query, Object[] args) {
        PersistenceSession internalDBSession = context.getPersistenceInterface().getSession();
        int retSize = 0;

        try{
            retSize = internalDBSession.getDBInterface().runPrepUpdate(query, args);
            internalDBSession.commit();
        }catch(SQLException e){
            internalDBSession.rollback();
            logger.error("Exception thrown while querying internal database", e);
        }finally {
            internalDBSession.close();
        }

        return retSize;
    }
}
