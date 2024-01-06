package ru.fkjob.delivery.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.fkjob.delivery.rest.dto.category.CategoryDto;
import ru.fkjob.delivery.rest.dto.category.CategoryInfoDto;
import ru.fkjob.delivery.rest.dto.dish.DishDto;
import ru.fkjob.delivery.rest.dto.dish.DishInfoDto;
import ru.fkjob.delivery.rest.service.CategoryService;
import ru.fkjob.delivery.rest.service.DishService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/menu-info")
@Api(tags = "меню с информацией по блюдам")
@RequiredArgsConstructor
public class MenuController {

    private final DishService dishService;
    private final CategoryService categoryService;

    @GetMapping("/dishes")
    @Operation(summary = "Список всех блюд")
    public List<DishDto> getDishes() {
        return dishService.getDishList();
    }

    @GetMapping("category/dish/{id}")
    @Operation(summary = "Получить информацию по блюду")
    public DishInfoDto getDishById(@PathVariable(name = "id") final Long id) {
        return dishService.getDishEntityById(id);
    }

    @PostMapping("/category/dish/create")
    @Operation(summary = "создать новое блюдо")
    public DishInfoDto getDishById(@RequestBody DishInfoDto dishInfoDto, @RequestParam(name = "categoryId") final Long categoryId) {
        return dishService.save(dishInfoDto, categoryId);
    }

    @GetMapping("/category/dish")
    @Operation(summary = "Получить информацию по блюду по названию")
    public List<DishDto> getDishByName(@RequestParam(name = "name") final String name) {
      return dishService.getDishByName(name);
    }

    @DeleteMapping("/category/dish")
    @Operation(summary = "Удалить блюдо")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void getDishByName(@RequestParam(name = "dishId") final Long dishId) {
       dishService.deleteDishEntityById(dishId);
    }

    @PutMapping(value = "/category/{dishId}/dish")
    @Operation(summary = "редактировать блюдо")
    public Long updateDish(@PathVariable(name = "dishId") final Long dishId, @RequestBody final DishInfoDto dto) {
       return dishService.updateDish(dishId, dto);
    }

    @GetMapping("/category")
    @Operation(summary = "Получить все категории")
    public List<CategoryDto> getAllCategory() {
        return categoryService.getAllCategory();
    }

    @GetMapping("/category/{id}")
    @Operation(summary = "Получить информацию по категории")
    public CategoryInfoDto getCategoryById(@PathVariable(name = "id") final Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/categoryes-dish")
    @Operation(summary = "Список всех категорий с блюдами")
    public List<CategoryInfoDto> getCategoryDishes() {
        return categoryService.getAllCategoryDishes();
    }

}
