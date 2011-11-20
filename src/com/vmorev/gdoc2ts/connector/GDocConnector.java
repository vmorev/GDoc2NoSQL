package com.vmorev.gdoc2ts.connector;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.vmorev.gdoc2ts.model.AbstractDocument;
import com.vmorev.gdoc2ts.model.Document;
import com.vmorev.gdoc2ts.model.Folder;
import com.vmorev.gdoc2ts.model.UnknownDocument;
import com.vmorev.gdoc2ts.utils.URLBuilder;
import org.apache.commons.lang.StringEscapeUtils;

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
    private DocsService service;

    private static final String APP_NAME = "GDoc2Terrastore";

    public GDocConnector(String user, String password) throws AuthenticationException {
        service = new DocsService(APP_NAME);
        service.setUserCredentials(user, password);
    }

    public List<AbstractDocument> getDocumentsByFolder(String resourceId) throws IOException, ServiceException {
        URL url = URLBuilder.buildFolderURL(resourceId);
        DocumentListFeed feed = service.getFeed(url, DocumentListFeed.class);

        List<AbstractDocument> docs = new ArrayList<AbstractDocument>(feed.getEntries().size());
        for (DocumentListEntry entry : feed.getEntries()) {
            AbstractDocument doc = convert(entry, true);
            docs.add(doc);
        }
        return docs;
    }

    private AbstractDocument convert(DocumentListEntry entry, boolean loadContent) throws IOException, ServiceException {
        AbstractDocument doc;

        if (AbstractDocument.DOC_TYPE_FOLDER.equals(entry.getType())) {
            doc = new Folder();
        } else if (AbstractDocument.DOC_TYPE_DOCUMENT.equals(entry.getType())) {
            doc = new Document();
            if (loadContent) {
                String encoded = downloadDocument(entry.getResourceId());
                ((Document) doc).setXml(StringEscapeUtils.unescapeHtml(encoded));
            }
            /*
        } else if (AbstractDocument.DOC_TYPE_SPREADSHEETS.equals(entry.getType())) {
            //todo exclude from folder
            //todo add new folder for excel
            doc = new ();
            if (loadContent) {
                String encoded = downloadSpreadsheets(entry.getResourceId());
                (() doc).setXml(StringEscapeUtils.unescapeHtml(encoded));
            }
            */
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
        return downloadFile(url);
    }

    private String downloadDocument(String resourceId) throws IOException, ServiceException {
        URL url = URLBuilder.buildDocumentDownloadURL(resourceId.substring(resourceId.lastIndexOf(":") + 1));
        return downloadFile(url);
    }

    private String downloadFile(URL exportUrl) throws IOException, ServiceException {
        MediaContent mc = new MediaContent();
        mc.setUri(exportUrl.toString());
        MediaSource ms = service.getMedia(mc);

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
