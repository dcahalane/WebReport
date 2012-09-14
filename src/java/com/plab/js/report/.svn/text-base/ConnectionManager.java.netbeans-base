package com.plab.js.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

public class ConnectionManager {

    static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    static ConnectionManager INSTANCE;
    static Map<String, BoneCP> DATA_SOURCES = new HashMap<String, BoneCP>();
    Map<String, Connection> connections = new HashMap<String, Connection>();
    Properties properties;

    public ConnectionManager() {
    }

    public static ConnectionManager getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new ConnectionManager();
        }
        return INSTANCE;
    }

    public void initializeProperties(Properties property) {
        this.properties = property;
    }

    public Connection getConnection(String connectionName) throws IOException, SQLException {
        return getConnection(connectionName, ORACLE_DRIVER);
    }

    public Connection getConnection(String connectionName, String driver) throws IOException, SQLException {
        String url = properties.getProperty(connectionName);
        String[] specs = url.split(":");
        if(specs != null && specs.length > 1){
            driver = properties.getProperty(specs[1]);
        }
        
        if (driver == null || driver.length() < 1 ) {
            driver = ORACLE_DRIVER;
        }
        BoneCP dataSource = DATA_SOURCES.get(connectionName);

        Connection retVal = null;
        if (dataSource == null) {

            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //ObjectPool connectionPool = new GenericObjectPool(null);
            //ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(getProperties().getProperty(connectionName),null);
            //PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);
            //dataSource = new PoolingDataSource(connectionPool);
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(url);
            dataSource = new BoneCP(config);
            //ds.setUrl(getProperties().getProperty(connectionName));
            DATA_SOURCES.put(connectionName, dataSource);
        }
        retVal = dataSource.getConnection();

        if (retVal == null || retVal.isClosed()) {
            DATA_SOURCES.put(connectionName, null);
        }

        return retVal;
    }
}
