package com.vmorev.gdoc2ts;

import com.google.gdata.util.ServiceException;
import com.vmorev.gdoc2ts.connector.GDocConnector;
import com.vmorev.gdoc2ts.connector.TSConnector;
import com.vmorev.gdoc2ts.model.AbstractDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrastore.client.BucketOperation;
import terrastore.client.Values;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: vmorev
 * Date: 11/13/11 3:00 PM
 */
public class DocumentLoader {
    private final Logger logger = LoggerFactory.getLogger(DocumentLoader.class);
    private TSConnector tsConnector;
    private GDocConnector gDocConnector;

    public DocumentLoader(GDocConnector gDocConnector, TSConnector tsConnector) {
        this.tsConnector = tsConnector;
        this.gDocConnector = gDocConnector;
    }

    public void syncFolder(String resourceId) throws IOException, ServiceException {
        List<AbstractDocument> gDocs;
        if (resourceId.startsWith(AbstractDocument.DOC_TYPE_FOLDER)) {
            gDocs = gDocConnector.getDocumentsByFolder(resourceId);
        } else if (resourceId.startsWith(AbstractDocument.DOC_TYPE_SPREADSHEETS)) {
            gDocs = gDocConnector.getDocumentsBySpreadsheet(resourceId);
        } else {
            return;
        }

        BucketOperation tsDocs = tsConnector.getDocuments(resourceId);
        Map<String, AbstractDocument> toLoad = new HashMap<String, AbstractDocument>(gDocs.size());

        for (AbstractDocument entry : gDocs) {
            if (logger.isDebugEnabled())
                logger.debug("GDoc entry found with ID: " + entry.getResourceId() + " and title: " + entry.getTitle());
            if (AbstractDocument.DOC_TYPE_FOLDER.equals(entry.getType())) {
                syncFolder(entry.getResourceId());
            } else if (AbstractDocument.DOC_TYPE_DOCUMENT.equals(entry.getType())) {
                toLoad.put(entry.getResourceId(), entry);
            } else if (AbstractDocument.DOC_TYPE_SPREADSHEETS.equals(entry.getType())) {
                syncFolder(entry.getResourceId());
            } else if (AbstractDocument.DOC_TYPE_SPREADSHEET_ROW.equals(entry.getType())) {
                toLoad.put(entry.getResourceId(), entry);
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Syncing folder " + resourceId);
        for (String key : toLoad.keySet()) {
            if (logger.isDebugEnabled())
                logger.debug("Syncing key " + key);
        }
        if (toLoad.size() > 0)
            tsDocs.bulk().put(new Values<AbstractDocument>(toLoad));

        //todo remove obsolete records in db
        //todo sync by date (store bucket max update date and apply all newest)
    }

}
