package com.aisa.repository;

import com.aisa.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    /**
     * カテゴリ別に資産を取得
     */
    List<Asset> findByCategory(String category);

    /**
     * ステータス別に資産を取得
     */
    List<Asset> findByStatus(Integer status);

    /**
     * カテゴリとステータスで資産を取得
     */
    List<Asset> findByCategoryAndStatus(String category, Integer status);

    /**
     * 名前で資産を検索
     */
    List<Asset> findByNameContainingIgnoreCase(String name);

    /**
     * カテゴリ別の資産数を取得
     */
    @Query("SELECT a.category, COUNT(a) FROM Asset a GROUP BY a.category")
    List<Object[]> countByCategory();

    /**
     * 指定したカテゴリの資産合計取得額を取得
     */
    @Query("SELECT SUM(CAST(a.totalAcquisition AS DECIMAL)) FROM Asset a WHERE a.category = :category")
    Double sumTotalAcquisitionByCategory(@Param("category") String category);
}