package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CategoriesOpenedOrdersCardLayout {
    private CategoryOpeningPositionDTO categoryOpeningPositionDTO;
    private VerticalLayout layout;
    private VerticalLayout contentLayout;
    private final ObjectProvider<ProductOrderCardLayout> layoutProvider;

    @PostConstruct
    private void init() {
        layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.addClassName("category-order-card");

        contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();
        contentLayout.setVisible(false); // Скрываем контент по умолчанию
    }

    public VerticalLayout getLayout() {
        if (categoryOpeningPositionDTO == null) {
            throw new RuntimeException("ProductOpeningPostionDTO Dont set");
        }
        return layout;
    }

    public void setCategoryOpeningPositionDTO(CategoryOpeningPositionDTO categoryOpeningPositionDTO) {
        this.categoryOpeningPositionDTO = categoryOpeningPositionDTO;
        constructLayout();
    }

    private void constructLayout() {
        Button toggleButton = new Button(VaadinIcon.CARET_DOWN.create());
        toggleButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        toggleButton.addClickListener(event -> {
            boolean isVisible = contentLayout.isVisible();
            contentLayout.setVisible(!isVisible);
            toggleButton.setIcon(isVisible ? VaadinIcon.CARET_DOWN.create() : VaadinIcon.CARET_UP.create());
        });

        Span categoryLabel = new Span(categoryOpeningPositionDTO.getCategory().getName());
        categoryLabel.getElement().getStyle().set("font-size", "2em"); // Размер текста как H2
        categoryLabel.getElement().getStyle().set("color", "white"); // Белый цвет
        categoryLabel.getElement().getStyle().set("margin-right", "0.5em"); // Отступ после иконки
        categoryLabel.getElement().getStyle().set("margin-left", "0.5em"); // Отступ после иконки

        Span dateLabel = new Span("En kohne sifaris tarixi: " + categoryOpeningPositionDTO.getOrderReceivedDate());
        dateLabel.getElement().getStyle().set("font-size", "1.2em"); // Увеличенный размер даты
        dateLabel.getElement().getStyle().set("color", "white"); // Белый цвет
        dateLabel.getElement().getStyle().set("font-style", "italic"); // Курсив для даты

        HorizontalLayout headerLayout = new HorizontalLayout(toggleButton, categoryLabel,dateLabel);
        headerLayout.setSpacing(false);
        headerLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        headerLayout.getStyle().set("margin-bottom", "0.5em"); // Отступ снизу



        layout.add(headerLayout, contentLayout);

        categoryOpeningPositionDTO.getPositionList().forEach(dto -> {
            var card = layoutProvider.getObject();
            card.setProductOpeningPositionDTO(dto);
            contentLayout.add(card.getLayout());
        });
    }
}
