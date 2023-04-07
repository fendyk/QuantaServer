package com.fendyk.utilities;

public class RankConfiguration {
    String name;
    int renewableChunkSlots;
    int chunkSlots;
    int memberSlots;
    double discountPercentage;

    public RankConfiguration(
            String name,
            int renewableChunkSlots,
            int chunkSlots,
            int memberSlots,
            double discountPercentage
    ) {
        this.name = name;
        this.renewableChunkSlots = renewableChunkSlots;
        this.chunkSlots = chunkSlots;
        this.memberSlots = memberSlots;
        this.discountPercentage = discountPercentage;
    }

    public String getName() {
        return name;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public int getChunkSlots() {
        return chunkSlots;
    }

    public int getMemberSlots() {
        return memberSlots;
    }

    public int getRenewableChunkSlots() {
        return renewableChunkSlots;
    }
}
