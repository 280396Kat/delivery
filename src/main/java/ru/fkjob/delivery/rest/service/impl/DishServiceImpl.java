package ru.fkjob.delivery.rest.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.fkjob.delivery.rest.dto.dish.DishDto;
import ru.fkjob.delivery.rest.dto.dish.DishInfoDto;
import ru.fkjob.delivery.store.entity.DishEntity;
import ru.fkjob.delivery.store.repository.DishRepository;
import ru.fkjob.delivery.rest.dto.mapper.DishMapper;
import ru.fkjob.delivery.rest.service.DishService;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    @Override
    public DishInfoDto getDishEntityById(long id) {
        return dishRepository.findById(id)
                .map(entity -> dishMapper.toDtoInfo(entity))
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
    }

    @Override
    public DishDto getDishByName(String name) {
        return dishRepository.findDishEntitiesByName(name)
                .map(entity -> dishMapper.toDto(entity))
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
    }

    @Override
    public Page<DishDto> getDishByPage(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<DishEntity> all = dishRepository.findAll(pageable);
        return all.map(dishMapper::toDto);
    }

    @Override
    public DishInfoDto save(DishInfoDto dishInfoDto) {
        DishEntity entity = dishMapper.toEntity(dishInfoDto);
        DishEntity save = dishRepository.save(entity);
        return dishMapper.toDtoInfo(save);
    }

    @Override
    public void deleteDishEntityById(long id) {
        DishEntity dish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
        dishRepository.delete(dish);
    }

    @Override
    public Long updateDish(long dishId, DishInfoDto dishInfoDto) {
        DishEntity dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
        dish.setName(dishInfoDto.getName());
        dish.setPrice(dishInfoDto.getPrice());
        dish.setDescription(dishInfoDto.getDescription());
        return dishRepository.save(dish).getId();
    }
}
