package ru.belkov.SiteSearchEngine.services;

import org.springframework.stereotype.Service;
import ru.belkov.SiteSearchEngine.model.entity.Field;
import ru.belkov.SiteSearchEngine.repository.FieldRepository;

import java.util.List;

@Service
public class FieldServiceImpl implements FieldService {
    private FieldRepository fieldRepository;

    public FieldServiceImpl(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    @Override
    public List<Field> getAll() {
        return fieldRepository.findAll();
    }

    @Override
    public Field save(Field field) {
        return fieldRepository.save(field);
    }
}
