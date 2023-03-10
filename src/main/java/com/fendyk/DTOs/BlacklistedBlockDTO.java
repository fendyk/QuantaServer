package com.fendyk.DTOs;

import org.bukkit.block.Block;

public class BlacklistedBlockDTO {
    int x;
    int y;
    int z;

    public BlacklistedBlockDTO() {}
    public BlacklistedBlockDTO(Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
    }

   public BlacklistedBlockDTO(int x, int y, int z) {
       this.x = x;
       this.y = y;
       this.z = z;
   }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
