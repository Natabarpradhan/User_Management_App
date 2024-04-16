package Intellect.com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Intellect.com.entity.CountryEntity;

public interface CountryRepo extends JpaRepository<CountryEntity, Integer> {

}
