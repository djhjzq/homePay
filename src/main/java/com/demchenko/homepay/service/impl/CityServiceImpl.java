package com.demchenko.homepay.service.impl;

import com.demchenko.homepay.entity.City;
import com.demchenko.homepay.repository.CityRepository;
import com.demchenko.homepay.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public void createCity(String cityName) {
        City city = new City();
        city.setName(cityName);
        cityRepository.save(city);
    }

    @Override
    public City findCityById(Long cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new RuntimeException("City with this id does not found"));
    }

    @Override
    public City findCityByName(String cityName) {
        return cityRepository.findCityByName(cityName)
                .orElseThrow(() -> new RuntimeException("City does not found"));
    }
}