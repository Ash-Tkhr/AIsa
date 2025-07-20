package com.aisa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "security_id", nullable = false)
    private Long securityId;

    @Column(name = "nav", nullable = false, length = 100)
    private String nav;

    @Column(name = "trade_amount", nullable = false, length = 20)
    private String tradeAmount;

    @Column(name = "units", nullable = false, length = 20)
    private String units;

    @Column(name = "nav_date", nullable = false, length = 20)
    private String navDate;

    @Column(name = "trade_status", length = 500)
    private String tradeStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_id", insertable = false, updatable = false)
    private Asset asset;

    // コンストラクタ
    public Trade() {
    }

    public Trade(Long securityId, String nav, String tradeAmount, String units, String navDate, String tradeStatus) {
        this.securityId = securityId;
        this.nav = nav;
        this.tradeAmount = tradeAmount;
        this.units = units;
        this.navDate = navDate;
        this.tradeStatus = tradeStatus;
        this.createdAt = LocalDateTime.now();
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    public String getNav() {
        return nav;
    }

    public void setNav(String nav) {
        this.nav = nav;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getNavDate() {
        return navDate;
    }

    public void setNavDate(String navDate) {
        this.navDate = navDate;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}