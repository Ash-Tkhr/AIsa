package com.aisa.controller;

import com.aisa.entity.Asset;
import com.aisa.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class AssetController {

    @Autowired
    private AssetService assetService;

    /**
     * 全資産を取得
     */
    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    /**
     * IDで資産を取得
     */
    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long id) {
        Asset asset = assetService.getAssetById(id);
        if (asset != null) {
            return ResponseEntity.ok(asset);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * カテゴリ別に資産を取得
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Asset>> getAssetsByCategory(@PathVariable String category) {
        List<Asset> assets = assetService.getAssetsByCategory(category);
        return ResponseEntity.ok(assets);
    }

    /**
     * ステータス別に資産を取得
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Asset>> getAssetsByStatus(@PathVariable Integer status) {
        List<Asset> assets = assetService.getAssetsByStatus(status);
        return ResponseEntity.ok(assets);
    }

    /**
     * 名前で資産を検索
     */
    @GetMapping("/search")
    public ResponseEntity<List<Asset>> searchAssetsByName(@RequestParam String name) {
        List<Asset> assets = assetService.searchAssetsByName(name);
        return ResponseEntity.ok(assets);
    }

    /**
     * 資産を作成
     */
    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        Asset createdAsset = assetService.createAsset(asset);
        return ResponseEntity.ok(createdAsset);
    }

    /**
     * 資産を更新
     */
    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        Asset updatedAsset = assetService.updateAsset(id, asset);
        if (updatedAsset != null) {
            return ResponseEntity.ok(updatedAsset);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 資産を削除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        boolean deleted = assetService.deleteAsset(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * カテゴリ別の資産統計を取得
     */
    @GetMapping("/statistics/category")
    public ResponseEntity<Map<String, Object>> getCategoryStatistics() {
        Map<String, Object> statistics = assetService.getCategoryStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 指定カテゴリの合計取得額を取得
     */
    @GetMapping("/statistics/total-acquisition/{category}")
    public ResponseEntity<Map<String, Object>> getTotalAcquisitionByCategory(@PathVariable String category) {
        Map<String, Object> result = assetService.getTotalAcquisitionByCategory(category);
        return ResponseEntity.ok(result);
    }

    /**
     * 資産一覧（詳細情報付き）を取得
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getAssetList(@RequestParam(required = false) String search) {
        List<Map<String, Object>> assetList = assetService.getAssetList(search);
        return ResponseEntity.ok(assetList);
    }
}