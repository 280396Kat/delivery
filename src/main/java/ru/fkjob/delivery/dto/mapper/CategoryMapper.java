package ru.fkjob.delivery.dto.mapper;

import ru.fkjob.delivery.dto.dish.DishDto;
import ru.fkjob.delivery.dto.category.CategoryDto;
import ru.fkjob.delivery.dto.category.CategoryInfoDto;
import ru.fkjob.delivery.entity.CategoryEntity;

import java.util.List;

public interface CategoryMapper {

    CategoryInfoDto toDtoInfo(CategoryEntity entity);

    CategoryDto toDto(CategoryEntity entity);

    List<CategoryDto> toDto(List<CategoryEntity> entity);

    CategoryEntity toEntity(DishDto dto);

    CategoryEntity toEntity(CategoryInfoDto dto);

    List<CategoryInfoDto> toDtoInfo(List<CategoryEntity> entity);
}
