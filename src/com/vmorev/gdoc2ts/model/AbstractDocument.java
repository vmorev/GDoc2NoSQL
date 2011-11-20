package com.vmorev.gdoc2ts.model;

import java.util.Date;

/**
 * User: vmorev
 * Date: 11/13/11 4:05 PM
 */
public abstract class AbstractDocument {

    public static final String DOC_TYPE_FOLDER = "folder";
    public static final String DOC_TYPE_DOCUMENT = "document";
    public static final String DOC_TYPE_SPREADSHEETS = "spreadsheet";
    public static final String DOC_TYPE_SPREADSHEET_ROW = "spreadsheetRow";
    public static final String DOC_TYPE_UNKNOWN = "unknown";

    private String type;
    private String resourceId;
    private String title;
    private Date updateDate;
    private String rawContent;

    public AbstractDocument(String docType) {
        this.type = docType;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
