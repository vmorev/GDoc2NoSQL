package com.vmorev.gdoc2ts.model;

import java.util.HashMap;
import java.util.Map;

/**
 * User: vmorev
 * Date: 11/13/11 4:03 PM
 */
public class SpreadsheetRow extends AbstractDocument {
    private Map<String, String> tags;

    public SpreadsheetRow() {
        super(AbstractDocument.DOC_TYPE_SPREADSHEET_ROW);
        tags = new HashMap<String, String>();
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public void addTag(String key, String value) {
        this.tags.put(key, value);
    }

    public String getTag(String key) {
        return this.tags.get(key);
    }
}
