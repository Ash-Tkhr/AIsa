package com.aisa.service;

import com.aisa.entity.Trade;
import com.aisa.entity.Asset;
import com.aisa.repository.TradeRepository;
import com.aisa.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Override
    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    @Override
    public Trade getTradeById(Long id) {
        return tradeRepository.findById(id).orElse(null);
    }

    @Override
    public List<Trade> getTradesBySecurityId(Long securityId) {
        return tradeRepository.findBySecurityIdOrderByCreatedAtDesc(securityId);
    }

    @Override
    public List<Trade> getTradesByStatus(String tradeStatus) {
        return tradeRepository.findByTradeStatus(tradeStatus);
    }

    @Override
    @Transactional
    public Trade createTrade(Trade trade) {
        Trade savedTrade = tradeRepository.save(trade);

        // 資産テーブルの更新
        updateAssetFromTrade(savedTrade);

        return savedTrade;
    }

    @Override
    @Transactional
    public Trade updateTrade(Long id, Trade trade) {
        Trade existingTrade = tradeRepository.findById(id).orElse(null);
        if (existingTrade != null) {
            // 既存の取引の影響を元に戻す
            reverseAssetUpdate(existingTrade);

            // 新しい取引データで更新
            existingTrade.setSecurityId(trade.getSecurityId());
            existingTrade.setNav(trade.getNav());
            existingTrade.setTradeAmount(trade.getTradeAmount());
            existingTrade.setUnits(trade.getUnits());
            existingTrade.setNavDate(trade.getNavDate());
            existingTrade.setTradeStatus(trade.getTradeStatus());

            Trade updatedTrade = tradeRepository.save(existingTrade);

            // 資産テーブルの更新
            updateAssetFromTrade(updatedTrade);

            return updatedTrade;
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteTrade(Long id) {
        Trade trade = tradeRepository.findById(id).orElse(null);
        if (trade != null) {
            // 資産テーブルから影響を除去
            reverseAssetUpdate(trade);

            tradeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> getTradeStatistics(Long securityId) {
        Map<String, Object> statistics = new HashMap<>();

        // 買い取引の合計金額
        Double buyAmount = tradeRepository.sumTradeAmountBySecurityIdAndStatus(securityId, "1");
        statistics.put("totalBuyAmount", buyAmount != null ? buyAmount : 0.0);

        // 売り取引の合計金額
        Double sellAmount = tradeRepository.sumTradeAmountBySecurityIdAndStatus(securityId, "0");
        statistics.put("totalSellAmount", sellAmount != null ? sellAmount : 0.0);

        // 買い取引の合計口数
        Double buyUnits = tradeRepository.sumUnitsBySecurityIdAndStatus(securityId, "1");
        statistics.put("totalBuyUnits", buyUnits != null ? buyUnits : 0.0);

        // 売り取引の合計口数
        Double sellUnits = tradeRepository.sumUnitsBySecurityIdAndStatus(securityId, "0");
        statistics.put("totalSellUnits", sellUnits != null ? sellUnits : 0.0);

        // 純保有口数
        double netUnits = (buyUnits != null ? buyUnits : 0.0) - (sellUnits != null ? sellUnits : 0.0);
        statistics.put("netUnits", netUnits);

        return statistics;
    }

    @Override
    public Map<String, Object> getLatestNav(Long securityId) {
        Map<String, Object> result = new HashMap<>();

        // 最新の取引から基準額を取得
        List<Trade> trades = tradeRepository.findBySecurityIdOrderByCreatedAtDesc(securityId);
        if (!trades.isEmpty()) {
            result.put("latestNav", trades.get(0).getNav());
            result.put("navDate", trades.get(0).getNavDate());
        } else {
            result.put("latestNav", "0");
            result.put("navDate", "");
        }

        return result;
    }

    @Override
    public Map<String, Object> getPreviousTradeAmount(Long securityId) {
        Map<String, Object> result = new HashMap<>();

        // 最新の取引から取引金額を取得
        List<Trade> trades = tradeRepository.findBySecurityIdOrderByCreatedAtDesc(securityId);
        if (!trades.isEmpty()) {
            result.put("previousAmount", trades.get(0).getTradeAmount());
        } else {
            result.put("previousAmount", "0");
        }

        return result;
    }

    /**
     * 取引に基づいて資産テーブルを更新
     */
    private void updateAssetFromTrade(Trade trade) {
        Optional<Asset> assetOpt = assetRepository.findById(trade.getSecurityId());
        if (assetOpt.isPresent()) {
            Asset asset = assetOpt.get();

            // 取引ステータスに基づいて資産を更新
            if ("1".equals(trade.getTradeStatus())) {
                // 買い取引の場合、積立中または保有のみに設定
                if (asset.getStatus() == null || asset.getStatus() == 2) {
                    asset.setStatus(0); // 積立中
                }
            } else if ("0".equals(trade.getTradeStatus())) {
                // 売り取引の場合、売却済みに設定
                asset.setStatus(2); // 売却済み
            }

            // 最新の基準額を資産の評価額として設定
            asset.setTotalValuation(java.time.LocalDateTime.now());

            assetRepository.save(asset);
        }
    }

    /**
     * 取引の影響を資産テーブルから除去
     */
    private void reverseAssetUpdate(Trade trade) {
        Optional<Asset> assetOpt = assetRepository.findById(trade.getSecurityId());
        if (assetOpt.isPresent()) {
            Asset asset = assetOpt.get();

            // 取引削除時は、他の取引の状況を確認してステータスを調整
            List<Trade> remainingTrades = tradeRepository.findBySecurityIdOrderByCreatedAtDesc(trade.getSecurityId());
            remainingTrades.removeIf(t -> t.getId().equals(trade.getId()));

            if (remainingTrades.isEmpty()) {
                // 取引がなくなった場合
                asset.setStatus(2); // 売却済み
            } else {
                // 残りの取引を確認
                boolean hasBuyTrades = remainingTrades.stream().anyMatch(t -> "1".equals(t.getTradeStatus()));
                if (hasBuyTrades) {
                    asset.setStatus(0); // 積立中
                } else {
                    asset.setStatus(2); // 売却済み
                }
            }

            assetRepository.save(asset);
        }
    }
}