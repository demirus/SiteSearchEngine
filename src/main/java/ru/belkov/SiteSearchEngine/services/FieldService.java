package ru.belkov.SiteSearchEngine.services;

import ru.belkov.SiteSearchEngine.model.entity.Field;

import java.util.List;

public interface FieldService {
    List<Field> getAll();

    Field save(Field field);
}
