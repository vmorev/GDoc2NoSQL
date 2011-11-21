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
import java.util.*;

/**
 * User: vmorev
 * Date: 11/13/11 3:00 PM
 */
public class DocumentLoader {
    private final Logger logger = LoggerFactory.getLogger(DocumentLoader.class);
    private TSConnector tsConnector;
    private GDocConnector gDocConnector;
    private Set<String> gBuckets;

    public DocumentLoader(GDocConnector gDocConnector, TSConnector tsConnector) {
        this.tsConnector = tsConnector;
        this.gDocConnector = gDocConnector;
        this.gBuckets = new HashSet<String>();
    }

    public void syncBucket(String bucketName) throws IOException, ServiceException {
        List<AbstractDocument> gDocuments;
        if (bucketName.startsWith(AbstractDocument.DOC_TYPE_FOLDER)) {
            gDocuments = gDocConnector.getDocumentsByBucket(bucketName);
        } else if (bucketName.startsWith(AbstractDocument.DOC_TYPE_SPREADSHEETS)) {
            gDocuments = gDocConnector.getDocumentsBySpreadsheet(bucketName);
        } else {
            return;
        }

        Map<String, AbstractDocument> tsDocuments = new HashMap<String, AbstractDocument>(gDocuments.size());
        this.gBuckets.add(bucketName);

        for (AbstractDocument entry : gDocuments) {
            if (logger.isDebugEnabled())
                logger.debug("Google Doc entry found ID='" + entry.getResourceId() + "', title='" + entry.getTitle() + "'");
            if (AbstractDocument.DOC_TYPE_FOLDER.equals(entry.getType())) {
                syncBucket(entry.getResourceId());
            } else if (AbstractDocument.DOC_TYPE_DOCUMENT.equals(entry.getType())) {
                tsDocuments.put(entry.getResourceId(), entry);
            } else if (AbstractDocument.DOC_TYPE_SPREADSHEETS.equals(entry.getType())) {
                syncBucket(entry.getResourceId());
            } else if (AbstractDocument.DOC_TYPE_SPREADSHEET_ROW.equals(entry.getType())) {
                tsDocuments.put(entry.getResourceId(), entry);
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Bucket to sync " + bucketName);
        for (String key : tsDocuments.keySet()) {
            if (logger.isDebugEnabled())
                logger.debug("Key to sync " + key);
        }

        if (tsDocuments.size() > 0)
            persistBucket(bucketName, tsDocuments);
    }

    private void persistBucket(String bucketName, Map<String, AbstractDocument> tsDocuments) {
        tsConnector.getDocuments(bucketName).clear();
        BucketOperation tsDocs = tsConnector.getDocuments(bucketName);
        if (logger.isDebugEnabled())
            logger.debug("Syncing buckets");
        tsDocs.bulk().put(new Values<AbstractDocument>(tsDocuments));
        //todo: do not remove bucket but rather sync using update date
    }

    public void cleanBuckets() {
        Set<String> tsBuckets = tsConnector.getBuckets().list();
        tsBuckets.removeAll(gBuckets);
        for (String bucket : tsBuckets) {
            if (logger.isDebugEnabled())
                logger.debug("Removing bucket " + bucket);
            tsConnector.getDocuments(bucket).clear();
        }
    }

}
