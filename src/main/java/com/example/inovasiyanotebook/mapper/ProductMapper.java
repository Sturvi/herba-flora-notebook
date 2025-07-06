package com.example.inovasiyanotebook.mapper;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.ProductExtraInfo;
import com.example.inovasiyanotebook.views.aiinformation.components.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "ts", source = "ts")
    @Mapping(target = "barcode", source = "barcode")
    @Mapping(target = "weight", source = "weight")
    @Mapping(target = "shelfLife", source = "shelfLife")
    @Mapping(target = "extraInfo", source = "extraInfo", qualifiedByName = "mapExtraInfo")
    ProductDTO toProductDTO(Product product);

    @Named("mapExtraInfo")
    default Map<String, String> mapExtraInfo(List<ProductExtraInfo> extraInfoList) {
        return extraInfoList.stream()
                .collect(Collectors.toMap(ProductExtraInfo::getKey, ProductExtraInfo::getValue));
    }
}