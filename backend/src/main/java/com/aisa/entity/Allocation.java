package com.aisa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "allocations")
public class Allocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "domestic_stock", nullable = false, length = 100)
    private String domesticStock;

    @Column(name = "foreign_stock", nullable = false, length = 100)
    private String foreignStock;

    @Column(name = "japan_bond", length = 100)
    private String japanBond;

    @Column(name = "foreign_bond", length = 100)
    private String foreignBond;

    @Column(name = "cash", length = 100)
    private String cash;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    // コンストラクタ
    public Allocation() {
    }

    public Allocation(Long userId, String domesticStock, String foreignStock, String japanBond, String foreignBond,
            String cash) {
        this.userId = userId;
        this.domesticStock = domesticStock;
        this.foreignStock = foreignStock;
        this.japanBond = japanBond;
        this.foreignBond = foreignBond;
        this.cash = cash;
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDomesticStock() {
        return domesticStock;
    }

    public void setDomesticStock(String domesticStock) {
        this.domesticStock = domesticStock;
    }

    public String getForeignStock() {
        return foreignStock;
    }

    public void setForeignStock(String foreignStock) {
        this.foreignStock = foreignStock;
    }

    public String getJapanBond() {
        return japanBond;
    }

    public void setJapanBond(String japanBond) {
        this.japanBond = japanBond;
    }

    public String getForeignBond() {
        return foreignBond;
    }

    public void setForeignBond(String foreignBond) {
        this.foreignBond = foreignBond;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}