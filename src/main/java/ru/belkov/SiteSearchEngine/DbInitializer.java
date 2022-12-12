package ru.belkov.SiteSearchEngine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.belkov.SiteSearchEngine.model.entity.Field;
import ru.belkov.SiteSearchEngine.services.FieldService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DbInitializer implements CommandLineRunner {
    private FieldService fieldService;

    public DbInitializer(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @Override
    public void run(String... args) throws Exception {
        initFields();
    }

    private void initFields() {
        List<Field> fields = fieldService.getAll();

        Field title = new Field();
        title.setName("title");
        title.setSelector("title");
        title.setWeight(1d);
        addFieldIfNotExists(title, fields);

        Field body = new Field();
        body.setName("body");
        body.setSelector("body");
        body.setWeight(0.8d);
        addFieldIfNotExists(body, fields);
    }

    private void addFieldIfNotExists(Field field, List<Field> dbFields) {
        if (dbFields.stream().filter(f -> f.getName().equals(field.getName())).collect(Collectors.toList()).size() < 1) {
            fieldService.save(field);
        }
    }
}
