package com.aisa.repository;

import com.aisa.entity.Indicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndicatorRepository extends JpaRepository<Indicator, Long> {

    /**
     * ユーザーID別に比率を取得
     */
    List<Indicator> findByUserId(Long userId);

    /**
     * ユーザーIDで最新の比率を取得
     */
    Optional<Indicator> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}