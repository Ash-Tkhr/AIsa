package com.aisa.controller;

import com.aisa.entity.Trade;
import com.aisa.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin(origins = "*")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    /**
     * 全取引を取得
     */
    @GetMapping
    public ResponseEntity<List<Trade>> getAllTrades() {
        List<Trade> trades = tradeService.getAllTrades();
        return ResponseEntity.ok(trades);
    }

    /**
     * IDで取引を取得
     */
    @GetMapping("/{id}")
    public ResponseEntity<Trade> getTradeById(@PathVariable Long id) {
        Trade trade = tradeService.getTradeById(id);
        if (trade != null) {
            return ResponseEntity.ok(trade);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 銘柄ID別に取引を取得
     */
    @GetMapping("/security/{securityId}")
    public ResponseEntity<List<Trade>> getTradesBySecurityId(@PathVariable Long securityId) {
        List<Trade> trades = tradeService.getTradesBySecurityId(securityId);
        return ResponseEntity.ok(trades);
    }

    /**
     * 売買ステータス別に取引を取得
     */
    @GetMapping("/status/{tradeStatus}")
    public ResponseEntity<List<Trade>> getTradesByStatus(@PathVariable String tradeStatus) {
        List<Trade> trades = tradeService.getTradesByStatus(tradeStatus);
        return ResponseEntity.ok(trades);
    }

    /**
     * 取引を作成
     */
    @PostMapping
    public ResponseEntity<Trade> createTrade(@RequestBody Trade trade) {
        Trade createdTrade = tradeService.createTrade(trade);
        return ResponseEntity.ok(createdTrade);
    }

    /**
     * 取引を更新
     */
    @PutMapping("/{id}")
    public ResponseEntity<Trade> updateTrade(@PathVariable Long id, @RequestBody Trade trade) {
        Trade updatedTrade = tradeService.updateTrade(id, trade);
        if (updatedTrade != null) {
            return ResponseEntity.ok(updatedTrade);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 取引を削除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        boolean deleted = tradeService.deleteTrade(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 指定銘柄の売買統計を取得
     */
    @GetMapping("/statistics/{securityId}")
    public ResponseEntity<Map<String, Object>> getTradeStatistics(@PathVariable Long securityId) {
        Map<String, Object> statistics = tradeService.getTradeStatistics(securityId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 指定銘柄の最新基準額を取得
     */
    @GetMapping("/latest-nav/{securityId}")
    public ResponseEntity<Map<String, Object>> getLatestNav(@PathVariable Long securityId) {
        Map<String, Object> result = tradeService.getLatestNav(securityId);
        return ResponseEntity.ok(result);
    }

    /**
     * 指定銘柄の直前の取引金額を取得
     */
    @GetMapping("/previous-amount/{securityId}")
    public ResponseEntity<Map<String, Object>> getPreviousTradeAmount(@PathVariable Long securityId) {
        Map<String, Object> result = tradeService.getPreviousTradeAmount(securityId);
        return ResponseEntity.ok(result);
    }
}