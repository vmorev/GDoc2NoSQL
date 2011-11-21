package com.vmorev.gdoc2ts.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: vmorev
 * Date: 11/20/11 12:28 PM
 */
public class URLBuilder {
    private static final Logger logger = LoggerFactory.getLogger(URLBuilder.class);

    private static final String DOCS_HOST = "https://docs.google.com";
    private static final String SPREADSHEET_HOST = "https://spreadsheet.google.com";

    private static final String URL_FEED = "/feeds";
    private static final String URL_DEFAULT_SUF = "/private/full/";
    private static final String URL_FOLDER = URL_FEED + "/default" + URL_DEFAULT_SUF;
    private static final String URL_FOLDER_SUF = "/contents";
    private static final String URL_DOCUMENTS_DOWNLOAD = "/document/d/";
    private static final String URL_DOCUMENTS_DOWNLOAD_SUF = "/export";
    private static final String URL_SPREADSHEET_DOWNLOAD = "/spreadsheet/fm";
    private static final String URL_SPREADSHEET = URL_FEED + "/spreadsheets" + URL_DEFAULT_SUF;

    public static URL getDocumentListFeedURL(String resourceId) throws MalformedURLException {
        return buildUrl(DOCS_HOST + URL_FOLDER + resourceId + URL_FOLDER_SUF);
    }

    public static URL getDocumentDownloadURL(String resourceId) throws MalformedURLException {
        String[] parameters = {"format=html"};
        return buildUrl(DOCS_HOST + URL_DOCUMENTS_DOWNLOAD + resourceId + URL_DOCUMENTS_DOWNLOAD_SUF, parameters);
    }

    public static URL getSpreadsheetDownloadURL(String resourceId) throws MalformedURLException {
        String[] parameters = {"key=" + resourceId, "fmcmd=5"};
        return buildUrl(DOCS_HOST + URL_SPREADSHEET_DOWNLOAD, parameters);
    }

    public static URL getSpreadsheetEntryURL(String resourceId) throws MalformedURLException {
        return buildUrl(SPREADSHEET_HOST + URL_SPREADSHEET + resourceId, null);
    }

    private static URL buildUrl(String path) throws MalformedURLException {
        return buildUrl(path, null);
    }

    private static URL buildUrl(String path, String[] parameters) throws MalformedURLException {
        StringBuffer url = new StringBuffer();
        url.append(path);

        if (parameters != null && parameters.length > 0) {
            url.append("?");
            for (int i = 0; i < parameters.length; i++) {
                url.append(parameters[i]);
                if (i != (parameters.length - 1)) {
                    url.append("&");
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Compiled URL is " + url.toString());
        return new URL(url.toString());
    }

}
