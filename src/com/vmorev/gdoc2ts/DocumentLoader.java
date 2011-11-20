package com.vmorev.gdoc2ts;

import com.google.gdata.util.ServiceException;
import com.vmorev.gdoc2ts.connector.GDocConnector;
import com.vmorev.gdoc2ts.connector.TSConnector;
import com.vmorev.gdoc2ts.model.AbstractDocument;
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
    private TSConnector tsConnector;
    private GDocConnector gDocConnector;

    public DocumentLoader(GDocConnector gDocConnector, TSConnector tsConnector) {
        this.tsConnector = tsConnector;
        this.gDocConnector = gDocConnector;
    }

    public void syncFolder(String folderId) throws IOException, ServiceException {
        List<AbstractDocument> gDocs = gDocConnector.getDocumentsByFolder(folderId);
        BucketOperation tsDocs = tsConnector.getDocuments(folderId);

        Map<String, AbstractDocument> toLoad = new HashMap<String, AbstractDocument>(gDocs.size());

        for (AbstractDocument entry : gDocs) {
            if (AbstractDocument.DOC_TYPE_FOLDER.equals(entry.getType())) {
                syncFolder(entry.getResourceId());
            } else if (AbstractDocument.DOC_TYPE_DOCUMENT.equals(entry.getType())) {
                toLoad.put(entry.getResourceId(), entry);
            }
        }

        if (toLoad.size() > 0)
            tsDocs.bulk().put(new Values<AbstractDocument>(toLoad));

        //todo remove obsolete records in db
        //todo sync by date (store bucket max update date and apply all newest)
    }

}