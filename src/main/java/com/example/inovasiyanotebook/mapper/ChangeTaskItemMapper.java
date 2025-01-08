package com.example.inovasiyanotebook.mapper;

import com.example.inovasiyanotebook.dto.ChangeTaskItemDTO;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChangeTaskItemMapper {
    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(target = "productName", expression = "java(mapProductNames(changeTaskItem))")
    ChangeTaskItemDTO toDTO(ChangeTaskItem changeTaskItem);

    ChangeTaskItemDTO copyDTO(ChangeTaskItemDTO dto);

    @Mapping(target = "task", ignore = true)
    @Mapping(target = "product", ignore = true)
    ChangeTaskItem toEntity(ChangeTaskItemDTO dto);


    default String mapProductNames(ChangeTaskItem changeTaskItem) {
        return changeTaskItem.getProduct().getName();
    }
}
