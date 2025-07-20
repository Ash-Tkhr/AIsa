package com.aisa.repository;

import com.aisa.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    /**
     * ユーザーID別に比率設定を取得
     */
    List<Allocation> findByUserId(Long userId);

    /**
     * ユーザーIDで最新の比率設定を取得
     */
    Optional<Allocation> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}