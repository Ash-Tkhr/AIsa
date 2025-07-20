package com.aisa.repository;

import com.aisa.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    /**
     * 銘柄ID別に売買記録を取得
     */
    List<Trade> findBySecurityIdOrderByCreatedAtDesc(Long securityId);

    /**
     * 売買ステータス別に売買記録を取得
     */
    List<Trade> findByTradeStatus(String tradeStatus);

    /**
     * 銘柄IDと売買ステータスで売買記録を取得
     */
    List<Trade> findBySecurityIdAndTradeStatus(Long securityId, String tradeStatus);

    /**
     * 指定した銘柄の売買合計金額を取得
     */
    @Query("SELECT SUM(CAST(t.tradeAmount AS DECIMAL)) FROM Trade t WHERE t.securityId = :securityId AND t.tradeStatus = :tradeStatus")
    Double sumTradeAmountBySecurityIdAndStatus(@Param("securityId") Long securityId,
            @Param("tradeStatus") String tradeStatus);

    /**
     * 指定した銘柄の合計口数を取得
     */
    @Query("SELECT SUM(CAST(t.units AS DECIMAL)) FROM Trade t WHERE t.securityId = :securityId AND t.tradeStatus = :tradeStatus")
    Double sumUnitsBySecurityIdAndStatus(@Param("securityId") Long securityId,
            @Param("tradeStatus") String tradeStatus);
}