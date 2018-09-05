package com.convert.manager;

public class Page {

    //page 内容
    private String page;

    public Page(StringBuilder stringBuilder){
        page = stringBuilder.toString();
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPage() {
        return page;
    }
}
