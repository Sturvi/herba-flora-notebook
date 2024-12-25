package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Компонент для отображения макета карточки категории с открытыми заказами.
 * Component to represent a category card layout containing opened orders.
 */
@Slf4j
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CategoriesOpenedOrdersCardLayout {

    private CategoryOpeningPositionDTO categoryOpeningPositionDTO;
    private VerticalLayout layout;
    private VerticalLayout contentLayout;
    private final ObjectProvider<ProductOrderCardLayout> layoutProvider;

    /**
     * Инициализация основного макета и макета содержимого.
     * Initializes the main layout and content layout.
     */
    @PostConstruct
    private void init() {
        log.debug("Initializing CategoriesOpenedOrdersCardLayout.");
        layout = createVerticalLayout("category-order-card");
        contentLayout = createVerticalLayout(null);
        contentLayout.setVisible(false); // Initially hide content
    }

    /**
     * Создает VerticalLayout с опциональным CSS-классом.
     * Creates a VerticalLayout with optional CSS class.
     *
     * @param className CSS-класс для добавления (может быть null).
     *                  CSS class name to add (nullable).
     * @return Настроенный экземпляр VerticalLayout.
     *         A configured VerticalLayout instance.
     */
    private VerticalLayout createVerticalLayout(String className) {
        log.debug("Creating VerticalLayout with class: {}", className);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidthFull();
        if (className != null) {
            verticalLayout.addClassName(className);
        }
        return verticalLayout;
    }

    /**
     * Возвращает основной макет. Выбрасывает исключение, если DTO не установлен.
     * Retrieves the main layout. Throws an exception if DTO is not set.
     *
     * @return Основной VerticalLayout.
     *         The main VerticalLayout.
     */
    public VerticalLayout getLayout() {
        validateDTO();
        return layout;
    }

    /**
     * Устанавливает CategoryOpeningPositionDTO и создает макет.
     * Sets the CategoryOpeningPositionDTO and constructs the layout.
     *
     * @param categoryOpeningPositionDTO DTO для установки.
     *                                   The DTO to set.
     */
    public void setCategoryOpeningPositionDTO(CategoryOpeningPositionDTO categoryOpeningPositionDTO) {
        log.info("Setting CategoryOpeningPositionDTO: {}", categoryOpeningPositionDTO);
        this.categoryOpeningPositionDTO = categoryOpeningPositionDTO;
        constructLayout();
    }

    /**
     * Проверяет, установлен ли DTO. Выбрасывает исключение, если не установлен.
     * Validates if the DTO is set, throwing an exception otherwise.
     */
    private void validateDTO() {
        if (categoryOpeningPositionDTO == null) {
            log.error("CategoryOpeningPositionDTO is not set.");
            throw new IllegalStateException("ProductOpeningPostionDTO is not set");
        }
    }

    /**
     * Создает основной макет с кнопкой переключения и заголовком.
     * Constructs the main layout with toggle button and header.
     */
    private void constructLayout() {
        log.debug("Constructing the layout.");
        Button toggleButton = createToggleButton();
        HorizontalLayout headerLayout = createHeaderLayout(toggleButton);

        layout.add(headerLayout, contentLayout);

        populateContentLayout();
    }

    /**
     * Создает кнопку переключения для отображения/скрытия содержимого.
     * Creates a toggle button to show/hide content.
     *
     * @return Экземпляр кнопки переключения.
     *         The toggle button instance.
     */
    private Button createToggleButton() {
        log.debug("Creating toggle button.");
        Button toggleButton = new Button(VaadinIcon.CARET_DOWN.create());
        toggleButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        toggleButton.addClickListener(event -> toggleContentVisibility(toggleButton));
        return toggleButton;
    }

    /**
     * Переключает видимость содержимого и обновляет иконку кнопки переключения.
     * Toggles the visibility of the content layout and updates the toggle button icon.
     *
     * @param toggleButton Кнопка переключения для обновления.
     *                     The toggle button to update.
     */
    private void toggleContentVisibility(Button toggleButton) {
        boolean isVisible = contentLayout.isVisible();
        contentLayout.setVisible(!isVisible);
        toggleButton.setIcon(isVisible ? VaadinIcon.CARET_DOWN.create() : VaadinIcon.CARET_UP.create());
        log.debug("Content visibility toggled to: {}", !isVisible);
    }

    /**
     * Создает заголовок с меткой категории, меткой даты и кнопкой переключения.
     * Creates the header layout with category label, date label, and toggle button.
     *
     * @param toggleButton Кнопка переключения для включения в заголовок.
     *                     The toggle button to include in the header.
     * @return Настроенный экземпляр HorizontalLayout.
     *         The configured HorizontalLayout instance.
     */
    private HorizontalLayout createHeaderLayout(Button toggleButton) {
        log.debug("Creating header layout.");
        Span categoryLabel = createCategoryLabel();
        Span dateLabel = createDateLabel();

        HorizontalLayout headerLayout = new HorizontalLayout(toggleButton, categoryLabel, dateLabel);
        headerLayout.setSpacing(false);
        headerLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        headerLayout.getStyle().set("margin-bottom", "0.5em"); // Bottom margin
        return headerLayout;
    }

    /**
     * Создает метку для названия категории.
     * Creates a label for the category name.
     *
     * @return Настроенный экземпляр Span.
     *         The configured Span instance.
     */
    private Span createCategoryLabel() {
        log.debug("Creating category label.");
        Span categoryLabel = new Span(categoryOpeningPositionDTO.getCategory().getName());
        categoryLabel.addClassName("category-label");
        return categoryLabel;
    }

    /**
     * Создает метку для даты получения заказа.
     * Creates a label for the order received date.
     *
     * @return Настроенный экземпляр Span.
     *         The configured Span instance.
     */
    private Span createDateLabel() {
        log.debug("Creating date label.");
        Span dateLabel = new Span("Ən köhnə sifariş tarixi: " + categoryOpeningPositionDTO.getOrderReceivedDate());
        dateLabel.addClassName("date-label");
        return dateLabel;
    }

    /**
     * Заполняет макет содержимого карточками заказов.
     * Populates the content layout with product order card layouts.
     */
    private void populateContentLayout() {
        log.debug("Populating content layout.");
        categoryOpeningPositionDTO.getPositionList().forEach(dto -> {
            var card = layoutProvider.getObject();
            card.setProductOpeningPositionDTO(dto);
            contentLayout.add(card.getLayout());
        });
    }
}
