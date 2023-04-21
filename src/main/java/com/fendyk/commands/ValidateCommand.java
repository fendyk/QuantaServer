package com.fendyk.commands;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.Main;
import com.fendyk.utilities.RankConfiguration;
import net.luckperms.api.model.user.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ValidateCommand {
    private Main main = Main.getInstance();
    private Builder builder;

    private ValidateCommand(Builder builder) {
        this.builder = builder;
    }

    public boolean passed() {
        return builder.isPassed();
    }

    public Builder getBuilder() {
        return builder;
    }

    public static class Builder {
        private Main main = Main.getInstance();
        private Player player;
        private User user;
        private MinecraftUserDTO minecraftUserDTO;
        private LandDTO landDTO;
        private String primaryGroup;
        private RankConfiguration rankConfiguration;

        private boolean passed = true;

        public boolean isPassed() {
            return passed;
        }

        public Builder(Player player) {
            this.player = player;
            if (player == null) {
                throw new IllegalStateException("Player is required");
            }
        }

        private void validateUser() {
            if (user != null) {
                return;
            }
            this.user = main.getLuckPermsApi().getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                passed = false;
                player.sendMessage("We could not determine your user permissions account");
                throw new IllegalStateException("User permissions account could not be determined");
            }
        }

        private void validateMinecraftUserDTO() {
            if (minecraftUserDTO != null) {
                return;
            }
            this.minecraftUserDTO = main.getApi().getMinecraftUserAPI().redis.get(player.getUniqueId());
            if (minecraftUserDTO == null) {
                passed = false;
                player.sendMessage("We could not determine your minecraft account data");
                throw new IllegalStateException("Minecraft account data could not be determined");
            }
        }

        private void validateLandDTO() {
            if (landDTO != null) {
                return;
            }
            this.landDTO = main.getApi().getLandAPI().get(minecraftUserDTO.userId);
            if (landDTO == null) {
                passed = false;
                player.sendMessage("We could not determine your land");
                throw new IllegalStateException("Land data could not be determined");
            }
        }

        private void validatePrimaryGroup() {
            if (primaryGroup != null) {
                return;
            }
            this.primaryGroup = user.getPrimaryGroup();
        }

        private void validateRankConfiguration() {
            if (rankConfiguration != null) {
                return;
            }
            this.rankConfiguration = main.getRanksConfig().getRankConfiguration(primaryGroup);
            if (rankConfiguration == null) {
                passed = false;
                player.sendMessage("We could not determine your rank's configuration");
                throw new IllegalStateException("Rank's configuration could not be found");
            }
        }

        public Builder checkMinecraftUserDTO() {
            validateMinecraftUserDTO();
            return this;
        }

        public Builder checkLastLocation() {
            validateMinecraftUserDTO();
            if(minecraftUserDTO.lastLocation == null) {
                passed = false;
                player.sendMessage("We could not find your last location");
                throw new IllegalStateException("Last location could not be found");
            }
            return this;
        }


        public Builder checkPrimaryGroup() {
            validateUser();
            validatePrimaryGroup();
            return this;
        }

        public Builder checkRankConfiguration() {
            validateUser();
            validatePrimaryGroup();
            validateRankConfiguration();
            return this;
        }

        public Builder checkLandDTO() {
            validateMinecraftUserDTO();
            validateLandDTO();
            return this;
        }

        // Another function that requires the rank configuration
        public Builder anotherFunctionNeedsRankConfiguration() {
            validateRankConfiguration();
            // Your function logic here
            return this;
        }

        public Builder isPlayerOwnerOfLand(Player player, LandDTO landDTO) {
            boolean isOwner = UUID.fromString(landDTO.getOwnerId()).equals(player.getUniqueId());
            if(!isOwner) {

                passed = false;
            }
            return this;
        }


        public ValidateCommand build() {
            return new ValidateCommand(this);
        }

        // Getter methods
        public Player getPlayer() {
            return player;
        }

        public User getUser() {
            return user;
        }

        public String getPrimaryGroup() {
            return primaryGroup;
        }

        public MinecraftUserDTO getMinecraftUserDTO() {
            return minecraftUserDTO;
        }

        public RankConfiguration getRankConfiguration() {
            return rankConfiguration;
        }
    }
}