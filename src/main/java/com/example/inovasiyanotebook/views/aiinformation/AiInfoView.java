package com.example.inovasiyanotebook.views.aiinformation;

import com.example.inovasiyanotebook.service.viewservices.product.extrainformationdialog.ProductExtraInfoDialogService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.aiinformation.components.DownloadButton;
import com.example.inovasiyanotebook.views.aiinformation.service.AiInfoViewService;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Sunni intelekt melumatlari") //todo грамматика
@Route(value = "ai-information", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AiInfoView extends VerticalLayout {

    private final DesignTools designTools;
    private final AiInfoViewService aiInfoViewService;
    private final DownloadButton downloadButton;


    public AiInfoView(DesignTools designTools, AiInfoViewService aiInfoViewService, DownloadButton downloadButton) {
        this.designTools = designTools;
        this.aiInfoViewService = aiInfoViewService;
        this.downloadButton = downloadButton;
        setSizeFull();
    }


    @PostConstruct
    public void init() {
        var headerHorizontalLayout = new HorizontalLayout(
                designTools.getAllCommonViewHeader("AI ucun melumatlar", null),
                downloadButton.getComponent());
        headerHorizontalLayout.setAlignItems(Alignment.END);

        add(headerHorizontalLayout, aiInfoViewService.getVerticalLayout());
    }
}
