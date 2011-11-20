package com.vmorev.gdoc2ts.pilot;

import com.google.gdata.util.ServiceException;
import com.vmorev.gdoc2ts.DocumentLoader;
import com.vmorev.gdoc2ts.connector.GDocConnector;
import com.vmorev.gdoc2ts.connector.TSConnector;
import com.vmorev.gdoc2ts.model.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * User: vmorev
 * Date: 11/3/11 6:18 PM
 */
public class PilotGDoc {
    private static Properties prop = null;

    public static void main(String[] args) throws IOException, ServiceException {
        GDocConnector gDocConnector = new GDocConnector(
                getProperty("gdoc.username"), getProperty("gdoc.password"));
        TSConnector tsConnector = new TSConnector(getProperty("ts.url"));

        String rootFolderId = getProperty("gdoc.rootFolder");

        DocumentLoader docLoader = new DocumentLoader(gDocConnector, tsConnector);
        docLoader.syncFolder(rootFolderId);

        Map<String, Document> docs = tsConnector.getDocuments(rootFolderId).values().get(Document.class);
        for (Document doc : docs.values()) {
            System.out.println("Reading " + doc.getTitle());
        }
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
