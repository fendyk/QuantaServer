package com.fendyk.utilities;

public class RankConfiguration {
    String name;
    int chunkClaimLimit;
    double discountPercentage;

    public RankConfiguration(
            String name,
            int chunkClaimLimit,
            double discountPercentage
    ) {
        this.name = name;
        this.chunkClaimLimit = chunkClaimLimit;
        this.discountPercentage = discountPercentage;
    }

    public String getName() {
        return name;
    }

    public int getChunkClaimLimit() {
        return chunkClaimLimit;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
}
