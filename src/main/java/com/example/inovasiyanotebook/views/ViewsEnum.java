package com.example.inovasiyanotebook.views;

import lombok.Getter;

@Getter
public enum ViewsEnum {
    LOGIN("login"),
    CATEGORY("category"),
    PRODUCT("product"),
    REGISTRATION("registration"),
    CLIENT("client"),
    ORDER("order"),
    PRODUCT_MAPPING("productmapping"),
    CHANGE_TASK("change-task"),
    PAGE404("404");

    private String view;

    ViewsEnum(String view) {
        this.view = view;
    }

    public String getViewWithParameter(String parameter) {
        return view + "/" + parameter;
    }
}