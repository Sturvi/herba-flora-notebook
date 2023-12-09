package com.example.inovasiyanotebook.views;

public enum ViewsEnum {
    LOGIN("login"),
    ABOUT("about"),
    HELLO("hello"),
    REGISTRATION("registration"),
    CLIENT ("/client"),
    PAGE404("404");

    private String view;

    ViewsEnum(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public String getViewWithParameter(String parameter) {
        return view + "/" + parameter;
    }
}