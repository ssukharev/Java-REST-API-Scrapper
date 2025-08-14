package com.example.scrapper.util;

import com.fasterxml.jackson.databind.JsonNode;

public class Scrapped{
    private String source;
    private JsonNode data;

    public Scrapped() {
    }

    public Scrapped(String source, JsonNode data) {
        this.source = source;
        this.data = data;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}
