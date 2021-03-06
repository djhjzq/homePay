package com.demchenko.homepay.service.impl;

import com.demchenko.homepay.dto.request.EstateRegistryForm;
import com.demchenko.homepay.entity.City;
import com.demchenko.homepay.entity.Estate;
import com.demchenko.homepay.entity.EstateType;
import com.demchenko.homepay.entity.Street;
import com.demchenko.homepay.exception.EstateNotFoundException;
import com.demchenko.homepay.repository.EstateRepository;
import com.demchenko.homepay.service.EstateService;
import com.demchenko.homepay.service.StreetService;
import com.demchenko.homepay.service.UserService;
import com.demchenko.homepay.service.CityService;
import com.demchenko.homepay.specification.EstateSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class EstateServiceImpl implements EstateService {

    private final UserService userService;

    private final EstateRepository estateRepository;

    private final StreetService streetService;

    private final CityService cityService;

    @Autowired
    public EstateServiceImpl(UserService userService, EstateRepository estateRepository, StreetService streetService, CityService cityService) {
        this.userService = userService;
        this.estateRepository = estateRepository;
        this.streetService = streetService;
        this.cityService = cityService;
    }

    @Override
    public Estate createEstate(Long userId,
                             Long cityId,
                             Long streetId, Integer houseNumber,
                             Integer flatNumber) {
        Estate estate = new Estate();
        estate.setStreet(streetService.findStreetById(streetId));
        estate.setCity(cityService.findCityById(cityId));
        estate.setHouseNumber(houseNumber);
        estate.setFlatNumber(flatNumber);

        if(flatNumber != 0) {
            estate.setEstateType(EstateType.FLAT);
        } else estate.setEstateType(EstateType.HOUSE);
        estate.getUserSets().add(userService.findUserById(userId));
        userService.findUserById(userId).getEstateSet().add(estate);
        log.info("Save estate to repository with userId: {}, cityId: {}, " +
                "streetId: {}, houseNumber: {}, flatNumber: {}", userId, cityId,
                streetId, houseNumber, flatNumber);
        return estateRepository.save(estate);

    }

    @Override
    public Estate registryEstate(EstateRegistryForm estateRegistryForm) {
       City city =  cityService.findCityByName(estateRegistryForm.cityName());
        Street street = streetService.findStreetByCityAndName(city.getId(), estateRegistryForm.streetName());
        return createEstate(estateRegistryForm.userId(), city.getId(),
                street.getId(), estateRegistryForm.houseNumber(),
                estateRegistryForm.flatNumber());
    }

    @Override
    public Estate findEstateById(Long id) {
        log.info("Try to find estate by id: {}", id);
        return estateRepository.findById(id).
                orElseThrow(()-> new EstateNotFoundException
                        ("Estate by " + id + " was not found."));
    }

    @Override
    public Set<Estate> findAllEstatesByUserId(Long userId) {
        log.info("Try to find estates by userId: {}", userId);
        return userService.findUserById(userId).getEstateSet();
    }

    @Override
    public void deleteEstate(Long userId, Long estateId) {
        Estate estate = findEstateById(estateId);
        estate.getUserSets().remove(userService.findUserById(userId));
        userService.findUserById(userId).getEstateSet()
                .remove(estate);
        cityService.findCityById(estate.getCity().getId()).getEstateSet()
                .remove(estate);
        streetService.findStreetById(estate.getStreet().getId()).getEstateSet()
                .remove(estate);
        log.info("Try to delete estate by id: {}", estateId);
        estateRepository.deleteById(estateId);
    }

    @Override
    public List<Estate> search(Long cityId, Long streetId, Integer houseNumber, Long estateType) {
        EstateSpecification estateSpecification = new EstateSpecification(cityId, streetId, houseNumber, estateType);
        log.info("Try to find estates by cityId: {}, streetId: {}, houseNumber: {}," +
                "estateType: {}", cityId, streetId, houseNumber, estateType);
        return estateRepository.findAll(estateSpecification);
    }

    @Override
    public Page<Estate> findAllEstates(Pageable pageable) {
        log.info("Get all objects by page: {}", pageable.getPageNumber());
        return estateRepository.findAll(pageable);
    }
}
