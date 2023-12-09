package com.example.inovasiyanotebook.views;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public interface DesignTools {

    default HorizontalLayout addEmptySpace() {
        HorizontalLayout space = new HorizontalLayout();
        space.setWidthFull();
        space.setHeight("20px");

        return space;
    }
}
