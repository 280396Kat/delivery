package ru.fkjob.delivery.rest.dto.dish;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Блюдо")
public class DishDto {

    @ApiModelProperty(name = "Идентификатор блюда")
    private Long id;

    @ApiModelProperty(name = "Название блюда")
    private String name;
}
