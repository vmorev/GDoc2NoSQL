package com.vmorev.gdoc2ts.connector;

import com.google.gdata.data.MediaContent;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.vmorev.gdoc2ts.model.*;
import com.vmorev.gdoc2ts.utils.URLBuilder;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: vmorev
 * Date: 11/13/11 2:17 PM
 */
public class GDocConnector {
    private final Logger logger = LoggerFactory.getLogger(GDocConnector.class);

    private GDocConnection conn;

    public GDocConnector(String user, String password) throws AuthenticationException {
        conn = new GDocConnection(user, password);
    }

    public List<AbstractDocument> getDocumentsByFolder(String resourceId) throws IOException, ServiceException {
        URL url = URLBuilder.buildDocumentListFeedURL(resourceId);
        DocumentListFeed feed = conn.getService().getFeed(url, DocumentListFeed.class);

        List<AbstractDocument> docs = new ArrayList<AbstractDocument>(feed.getEntries().size());
        for (DocumentListEntry entry : feed.getEntries()) {
            if (logger.isDebugEnabled())
                logger.debug("Adding folder entry " + entry.getResourceId() + " with title " + entry.getTitle().getPlainText());

            docs.add(convert(entry));
        }
        return docs;
    }

    public List<AbstractDocument> getDocumentsBySpreadsheet(String resourceId) throws IOException, ServiceException {
        List<AbstractDocument> docs = new ArrayList<AbstractDocument>();
        URL url = URLBuilder.buildSpreadsheetEntryURL(resourceId.substring(resourceId.lastIndexOf(":") + 1));
        SpreadsheetEntry ssEntry = conn.getSsService().getEntry(url, SpreadsheetEntry.class);
        if (logger.isDebugEnabled())
            logger.debug("Adding spreadsheet entry " + resourceId + " with title " + ssEntry.getTitle().getPlainText());

        for (WorksheetEntry weEntry : ssEntry.getWorksheets()) {
            if (logger.isDebugEnabled())
                logger.debug("Adding worksheet entry " + weEntry.getId().substring(weEntry.getId().lastIndexOf('/') + 1) + " with title " + weEntry.getTitle().getPlainText());

            ListFeed lFeed = conn.getSsService().getFeed(weEntry.getListFeedUrl(), ListFeed.class);
            for (ListEntry leEntry : lFeed.getEntries()) {
                if (logger.isDebugEnabled())
                    logger.debug("Adding list entry " + leEntry.getId().substring(leEntry.getId().lastIndexOf('/') + 1) + " with title " + leEntry.getTitle().getPlainText());

                AbstractDocument doc = new SpreadsheetRow();
                String entryResourceId = resourceId + ":" +
                        weEntry.getId().substring(weEntry.getId().lastIndexOf('/') + 1) + ":" +
                        leEntry.getId().substring(leEntry.getId().lastIndexOf('/') + 1);
                doc.setResourceId(entryResourceId);
                doc.setTitle(leEntry.getTitle().getPlainText());
                doc.setUpdateDate(new Date(leEntry.getUpdated().getValue()));

                for (String tag : leEntry.getCustomElements().getTags()) {
                    if (logger.isDebugEnabled())
                        logger.debug("Tag " + tag + " = " + leEntry.getCustomElements().getValue(tag));

                    ((SpreadsheetRow) doc).addTag(tag, leEntry.getCustomElements().getValue(tag));
                }
                docs.add(doc);
            }
        }
        return docs;
    }

    private AbstractDocument convert(DocumentListEntry entry) throws IOException, ServiceException {
        AbstractDocument doc;

        if (AbstractDocument.DOC_TYPE_FOLDER.equals(entry.getType())) {
            doc = new Folder();
        } else if (AbstractDocument.DOC_TYPE_DOCUMENT.equals(entry.getType())) {
            doc = new Document();
            doc.setRawContent(downloadDocument(entry.getResourceId()));
        } else if (AbstractDocument.DOC_TYPE_SPREADSHEETS.equals(entry.getType())) {
            doc = new Spreadsheet();
            // do not need to get raw context here, will receive it later
            // doc.setRawContent(downloadSpreadsheets(entry.getResourceId()));
        } else {
            doc = new UnknownDocument();
        }

        doc.setResourceId(entry.getResourceId());
        doc.setTitle(entry.getTitle().getPlainText());
        doc.setUpdateDate(new Date(entry.getUpdated().getValue()));
        return doc;
    }

    private String downloadSpreadsheets(String resourceId) throws IOException, ServiceException {
        URL url = URLBuilder.buildSpreadsheetDownloadURL(resourceId.substring(resourceId.lastIndexOf(":") + 1));
        return StringEscapeUtils.unescapeHtml(downloadFile(url));
    }

    private String downloadDocument(String resourceId) throws IOException, ServiceException {
        URL url = URLBuilder.buildDocumentDownloadURL(resourceId.substring(resourceId.lastIndexOf(":") + 1));
        return StringEscapeUtils.unescapeHtml(downloadFile(url));
    }

    private String downloadFile(URL exportUrl) throws IOException, ServiceException {
        MediaContent mc = new MediaContent();
        mc.setUri(exportUrl.toString());
        MediaSource ms = conn.getService().getMedia(mc);

        InputStream inStream = null;
        OutputStream outStream = null;
        String result = null;
        try {
            inStream = ms.getInputStream();
            outStream = new ByteArrayOutputStream();

            int c;
            while ((c = inStream.read()) != -1) {
                outStream.write(c);
            }

            result = outStream.toString();
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (outStream != null) {
                outStream.flush();
                outStream.close();
            }
        }
        return result;
    }

}
