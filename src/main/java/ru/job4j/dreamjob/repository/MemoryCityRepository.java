package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;

import java.util.*;

@Repository
public class MemoryCityRepository implements CityRepository {

    private final Map<Integer, City> cities = new HashMap<>() {
        {
            put(1, new City(1, "Moscow"));
            put(2, new City(2, "St. Petersburg"));
            put(3, new City(3, "Ekaterinburg"));
        }
    };

    @Override
    public Collection<City> findAll() {
        return new ArrayList<>(cities.values());
    }

}
