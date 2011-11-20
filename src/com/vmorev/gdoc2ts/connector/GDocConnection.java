package com.vmorev.gdoc2ts.connector;

import com.google.gdata.client.GoogleAuthTokenFactory;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.util.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: vmorev
 * Date: 11/20/11 5:36 PM
 */
public class GDocConnection {
    private final Logger logger = LoggerFactory.getLogger(GDocConnector.class);

    private static final String APP_NAME = "GDoc2Terrastore";
    private static final String SERVICE_DOCS = "docs";
    private static final String SERVICE_SPREADSHEET = "spreadsheet";
    private DocsService service;
    private SpreadsheetService ssService;
    private String serviceName;
    private GoogleAuthTokenFactory.UserToken docsToken;
    private GoogleAuthTokenFactory.UserToken spreadsheetToken;

    public GDocConnection(String user, String password) throws AuthenticationException {
        service = new DocsService(APP_NAME);
        service.setUserCredentials(user, password);
        ssService = new SpreadsheetService(APP_NAME);
        ssService.setUserCredentials(user, password);
        serviceName = SERVICE_DOCS;
        docsToken = (GoogleAuthTokenFactory.UserToken) service.getAuthTokenFactory().getAuthToken();
        spreadsheetToken = (GoogleAuthTokenFactory.UserToken) ssService.getAuthTokenFactory().getAuthToken();
    }

    public DocsService getService() {
        if (SERVICE_SPREADSHEET.equals(serviceName)) {
            logger.info("Switching UserToken from " + SERVICE_SPREADSHEET + " to " + SERVICE_DOCS);
            service.setUserToken(docsToken.getValue());
            serviceName = SERVICE_DOCS;
        }
        return service;
    }

    public SpreadsheetService getSsService() {
        if (SERVICE_DOCS.equals(serviceName)) {
            logger.info("Switching UserToken from " + SERVICE_DOCS + " to " + SERVICE_SPREADSHEET);
            //setting spreadsheet token into docs service is a hack
            //this is required even if we are using spreadsheet service and not docs one
            service.setUserToken(spreadsheetToken.getValue());
            serviceName = SERVICE_SPREADSHEET;
        }
        return ssService;
    }
}
