package com.aisa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "total_acquisition", nullable = false, length = 100)
    private String totalAcquisition;

    @Column(name = "total_valuation")
    private LocalDateTime totalValuation;

    @Column(name = "holding_period")
    private LocalDateTime holdingPeriod;

    @Column(name = "status")
    private Integer status = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // コンストラクタ
    public Asset() {
    }

    public Asset(String category, String name, String description, String totalAcquisition) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.totalAcquisition = totalAcquisition;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTotalAcquisition() {
        return totalAcquisition;
    }

    public void setTotalAcquisition(String totalAcquisition) {
        this.totalAcquisition = totalAcquisition;
    }

    public LocalDateTime getTotalValuation() {
        return totalValuation;
    }

    public void setTotalValuation(LocalDateTime totalValuation) {
        this.totalValuation = totalValuation;
    }

    public LocalDateTime getHoldingPeriod() {
        return holdingPeriod;
    }

    public void setHoldingPeriod(LocalDateTime holdingPeriod) {
        this.holdingPeriod = holdingPeriod;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}