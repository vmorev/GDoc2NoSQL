package com.vmorev.gdoc2ts;

import com.google.gdata.util.ServiceException;
import com.vmorev.gdoc2ts.connector.GDocConnector;
import com.vmorev.gdoc2ts.connector.TSConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * User: vmorev
 * Date: 11/3/11 6:18 PM
 */
public class GDoc2TS {
    private static Properties prop = null;
    private final static Logger logger = LoggerFactory.getLogger(GDoc2TS.class);

    public static void main(String[] args) throws IOException, ServiceException {
        logger.info("Setting up Google Doc connection");
        GDocConnector gDocConnector = new GDocConnector(
                getProperty("gdoc.username"), getProperty("gdoc.password"));
        logger.info("Setting up TerraStore connection");
        TSConnector tsConnector = new TSConnector(getProperty("ts.url"));

        String rootBucket = getProperty("gdoc.rootBucket");
        DocumentLoader docLoader = new DocumentLoader(gDocConnector, tsConnector);

        logger.info("Starting sync");
        docLoader.syncBucket(rootBucket);
        logger.info("Cleaning odd buckets");
        docLoader.cleanBuckets();
        logger.info("Sync is finished");
    }

    private static String getProperty(String key) throws IOException {
        if (prop == null) {
            FileInputStream fis = new FileInputStream("gdoc2nosql.properties");
            prop = new Properties();
            prop.load(fis);
        }
        return prop.getProperty(key);
    }
}
