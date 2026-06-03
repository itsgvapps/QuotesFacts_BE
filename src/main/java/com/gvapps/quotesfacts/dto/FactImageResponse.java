package com.gvapps.quotesfacts.dto;

import java.util.Map;

public class FactImageResponse {

    private Long id;
    private String p;
    private String a;
    private String au;
    private Map<String, String> images;

    public FactImageResponse() {
    }

    public FactImageResponse(Long id, String p, String a, String au, Map<String, String> images) {
        this.id = id;
        this.p = p;
        this.a = a;
        this.au = au;
        this.images = images;
    }

    public Long getId() {
        return id;
    }

    public String getP() {
        return p;
    }

    public String getA() {
        return a;
    }

    public String getAu() {
        return au;
    }

    public Map<String, String> getImages() {
        return images;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setP(String p) {
        this.p = p;
    }

    public void setA(String a) {
        this.a = a;
    }

    public void setAu(String au) {
        this.au = au;
    }

    public void setImages(Map<String, String> images) {
        this.images = images;
    }
}
