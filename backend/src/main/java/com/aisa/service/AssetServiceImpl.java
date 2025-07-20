package com.aisa.service;

import com.aisa.entity.Asset;
import com.aisa.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.time.LocalDate;

@Service
public class AssetServiceImpl implements AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Override
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @Override
    public Asset getAssetById(Long id) {
        return assetRepository.findById(id).orElse(null);
    }

    @Override
    public List<Asset> getAssetsByCategory(String category) {
        return assetRepository.findByCategory(category);
    }

    @Override
    public List<Asset> getAssetsByStatus(Integer status) {
        return assetRepository.findByStatus(status);
    }

    @Override
    public List<Asset> searchAssetsByName(String name) {
        return assetRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public Asset createAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    @Override
    public Asset updateAsset(Long id, Asset asset) {
        Asset existingAsset = assetRepository.findById(id).orElse(null);
        if (existingAsset != null) {
            existingAsset.setCategory(asset.getCategory());
            existingAsset.setName(asset.getName());
            existingAsset.setDescription(asset.getDescription());
            existingAsset.setTotalAcquisition(asset.getTotalAcquisition());
            existingAsset.setTotalValuation(asset.getTotalValuation());
            existingAsset.setHoldingPeriod(asset.getHoldingPeriod());
            existingAsset.setStatus(asset.getStatus());
            return assetRepository.save(existingAsset);
        }
        return null;
    }

    @Override
    public boolean deleteAsset(Long id) {
        if (assetRepository.existsById(id)) {
            assetRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> getCategoryStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        List<Object[]> categoryCounts = assetRepository.countByCategory();

        for (Object[] result : categoryCounts) {
            String category = (String) result[0];
            Long count = (Long) result[1];
            statistics.put(category, count);
        }

        return statistics;
    }

    @Override
    public Map<String, Object> getTotalAcquisitionByCategory(String category) {
        Map<String, Object> result = new HashMap<>();
        Double totalAcquisition = assetRepository.sumTotalAcquisitionByCategory(category);

        result.put("category", category);
        result.put("totalAcquisition", totalAcquisition != null ? totalAcquisition : 0.0);

        return result;
    }

    @Override
    public List<Map<String, Object>> getAssetList(String search) {
        List<Asset> assets;
        if (search != null && !search.trim().isEmpty()) {
            assets = assetRepository.findByNameContainingIgnoreCase(search.trim());
        } else {
            assets = assetRepository.findAll();
        }

        List<Map<String, Object>> assetList = new ArrayList<>();

        for (Asset asset : assets) {
            Map<String, Object> assetData = new HashMap<>();

            // 基本情報
            assetData.put("id", asset.getId());
            assetData.put("category", asset.getCategory());
            assetData.put("name", asset.getName());
            assetData.put("description", asset.getDescription());
            assetData.put("status", asset.getStatus());
            assetData.put("createdAt", asset.getCreatedAt());
            assetData.put("updatedAt", asset.getUpdatedAt());

            // 取得金額合計
            String totalAcquisition = asset.getTotalAcquisition();
            assetData.put("totalAcquisition", totalAcquisition);

            // 評価額合計（現在は取得金額と同じ）
            assetData.put("totalValuation", totalAcquisition);

            // 評価損益計算
            double acquisition = parseDouble(totalAcquisition);
            double valuation = acquisition; // 現在は同じ値
            double profit = valuation - acquisition;
            double profitRate = acquisition > 0 ? (profit / acquisition) * 100 : 0;

            assetData.put("profit", profit);
            assetData.put("profitRate", profitRate);

            // 保有期間と買付日の計算
            Map<String, Object> periodInfo = calculateHoldingPeriod(asset.getId());
            assetData.put("holdingPeriod", periodInfo.get("holdingPeriod"));
            assetData.put("purchaseDate", periodInfo.get("purchaseDate"));

            // ステータス判定（2ヶ月以内に取引があったかどうか）
            boolean isActive = isAssetActive(asset.getId());
            assetData.put("isActive", isActive);

            // 更新日（最後の取引日）
            String lastTradeDate = getLastTradeDate(asset.getId());
            assetData.put("lastTradeDate", lastTradeDate);

            assetList.add(assetData);
        }

        return assetList;
    }

    /**
     * 文字列をdoubleに変換
     */
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException | NullPointerException e) {
            return 0.0;
        }
    }

    /**
     * 保有期間と買付日を計算
     */
    private Map<String, Object> calculateHoldingPeriod(Long assetId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 最初の取引日を取得
            String firstTradeDate = getFirstTradeDate(assetId);
            if (firstTradeDate != null) {
                // 現在日付との差分を計算
                LocalDate firstDate = LocalDate.parse(firstTradeDate);
                LocalDate currentDate = LocalDate.now();

                long months = java.time.temporal.ChronoUnit.MONTHS.between(firstDate, currentDate);
                long years = months / 12;
                long remainingMonths = months % 12;

                String holdingPeriod;
                if (years > 0) {
                    holdingPeriod = years + "年" + (remainingMonths > 0 ? remainingMonths + "ヶ月" : "");
                } else {
                    holdingPeriod = months + "ヶ月";
                }

                String purchaseDate = firstDate.getYear() + "/" +
                        String.format("%02d", firstDate.getMonthValue()) + "～";

                result.put("holdingPeriod", holdingPeriod);
                result.put("purchaseDate", purchaseDate);
            } else {
                result.put("holdingPeriod", "不明");
                result.put("purchaseDate", "不明");
            }
        } catch (Exception e) {
            result.put("holdingPeriod", "不明");
            result.put("purchaseDate", "不明");
        }

        return result;
    }

    /**
     * 最初の取引日を取得
     */
    private String getFirstTradeDate(Long assetId) {
        // 実際の実装ではTradeRepositoryを使用
        // ここでは仮の実装
        return "2024-01-01";
    }

    /**
     * 最後の取引日を取得
     */
    private String getLastTradeDate(Long assetId) {
        // 実際の実装ではTradeRepositoryを使用
        // ここでは仮の実装
        return "2024-12-01";
    }

    /**
     * 資産がアクティブかどうか判定（2ヶ月以内に取引があったか）
     */
    private boolean isAssetActive(Long assetId) {
        // 実際の実装ではTradeRepositoryを使用
        // ここでは仮の実装
        return true;
    }
}