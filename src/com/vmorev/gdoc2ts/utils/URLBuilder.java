package com.vmorev.gdoc2ts.utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: vmorev
 * Date: 11/20/11 12:28 PM
 */
public class URLBuilder {

    private static final String DEFAULT_HOST = "https://docs.google.com";

    private static final String URL_DEFAULT = "/default";
    private static final String URL_DOCLIST_FEED = "/private/full";
    private static final String URL_FOLDERS = "/contents";
    private static final String URL_FEED = "/feeds";
    private static final String URL_DOCUMENTS = "/document/d/";
    private static final String URL_DOCUMENTS_EXPORT_SUF = "/export";
    private static final String URL_SPREADSHEETS = "/spreadsheet/fm";

    public static URL buildFolderURL(String resourceId) throws MalformedURLException {
        return buildUrl(URL_FEED + URL_DEFAULT + URL_DOCLIST_FEED + "/" + resourceId + URL_FOLDERS);
    }

    public static URL buildDocumentDownloadURL(String resourceId) throws MalformedURLException {
        String[] parameters = {"format=html"};
        return buildUrl(URL_DOCUMENTS + resourceId + URL_DOCUMENTS_EXPORT_SUF, parameters);
    }

    public static URL buildSpreadsheetDownloadURL(String resourceId) throws MalformedURLException {
        String[] parameters = {"key=" + resourceId, "fmcmd=5"};
        return buildUrl(URL_SPREADSHEETS, parameters);
    }

    private static URL buildUrl(String path) throws MalformedURLException {
        return buildUrl(path, null);
    }

    private static URL buildUrl(String path, String[] parameters) throws MalformedURLException {
        StringBuffer url = new StringBuffer();
        url.append(DEFAULT_HOST);

        if (path != null) {
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
        }
        return new URL(url.toString());
    }

}
