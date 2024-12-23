package ru.backspark.task.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.backspark.task.entity.SocksEntity;

import java.util.Optional;

public interface SockRepository extends JpaRepository<SocksEntity, Long> {

    Optional<SocksEntity> findByColorAndCottonPercentage(String color, int cottonPercentage);
}

