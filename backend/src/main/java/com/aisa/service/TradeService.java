package com.aisa.service;

import com.aisa.entity.Trade;
import java.util.List;
import java.util.Map;

public interface TradeService {

    /**
     * 全取引を取得
     */
    List<Trade> getAllTrades();

    /**
     * IDで取引を取得
     */
    Trade getTradeById(Long id);

    /**
     * 銘柄ID別に取引を取得
     */
    List<Trade> getTradesBySecurityId(Long securityId);

    /**
     * 売買ステータス別に取引を取得
     */
    List<Trade> getTradesByStatus(String tradeStatus);

    /**
     * 取引を作成
     */
    Trade createTrade(Trade trade);

    /**
     * 取引を更新
     */
    Trade updateTrade(Long id, Trade trade);

    /**
     * 取引を削除
     */
    boolean deleteTrade(Long id);

    /**
     * 指定銘柄の売買統計を取得
     */
    Map<String, Object> getTradeStatistics(Long securityId);

    /**
     * 指定銘柄の最新基準額を取得
     */
    Map<String, Object> getLatestNav(Long securityId);

    /**
     * 指定銘柄の直前の取引金額を取得
     */
    Map<String, Object> getPreviousTradeAmount(Long securityId);
}