package com.aisa.service;

import com.aisa.entity.Asset;
import java.util.List;
import java.util.Map;

public interface AssetService {

    /**
     * 全資産を取得
     */
    List<Asset> getAllAssets();

    /**
     * IDで資産を取得
     */
    Asset getAssetById(Long id);

    /**
     * カテゴリ別に資産を取得
     */
    List<Asset> getAssetsByCategory(String category);

    /**
     * ステータス別に資産を取得
     */
    List<Asset> getAssetsByStatus(Integer status);

    /**
     * 名前で資産を検索
     */
    List<Asset> searchAssetsByName(String name);

    /**
     * 資産を作成
     */
    Asset createAsset(Asset asset);

    /**
     * 資産を更新
     */
    Asset updateAsset(Long id, Asset asset);

    /**
     * 資産を削除
     */
    boolean deleteAsset(Long id);

    /**
     * カテゴリ別の資産統計を取得
     */
    Map<String, Object> getCategoryStatistics();

    /**
     * 指定カテゴリの合計取得額を取得
     */
    Map<String, Object> getTotalAcquisitionByCategory(String category);

    /**
     * 資産一覧（詳細情報付き）を取得
     */
    List<Map<String, Object>> getAssetList(String search);
}