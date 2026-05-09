package main.java.com.gearrentpro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MembershipConfig {
    private String levelId;
    private String levelName;  // REGULAR, SILVER, GOLD
    private BigDecimal discountPercentage;
    private String description;
    private LocalDateTime createdAt;

    // Constructors
    public MembershipConfig() {}

    public MembershipConfig(String levelId, String levelName, 
                            BigDecimal discountPercentage, String description) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.discountPercentage = discountPercentage;
        this.description = description;
    }

    // Getters and Setters
    public String getLevelId() { return levelId; }
    public void setLevelId(String levelId) { this.levelId = levelId; }

    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { 
        this.discountPercentage = discountPercentage; 
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return levelName + " (" + discountPercentage + "% discount)";
    }
}