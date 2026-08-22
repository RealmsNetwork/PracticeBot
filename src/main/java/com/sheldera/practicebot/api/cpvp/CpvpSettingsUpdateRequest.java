package com.sheldera.practicebot.api.cpvp;

import java.util.Optional;

public final class CpvpSettingsUpdateRequest {
   private final Boolean anchoringMode;
   private final Boolean breakBlocks;
   private final Double lowHpCrystalLethalReserve;

   CpvpSettingsUpdateRequest(CpvpSettingsUpdateRequest$Builder var1) {
      this.anchoringMode = var1.anchoringMode;
      this.breakBlocks = var1.breakBlocks;
      this.lowHpCrystalLethalReserve = var1.lowHpCrystalLethalReserve;
   }

   public static CpvpSettingsUpdateRequest$Builder builder() {
      return new CpvpSettingsUpdateRequest$Builder();
   }

   public Optional<Boolean> anchoringModeOptional() {
      return Optional.ofNullable(this.anchoringMode);
   }

   public Optional<Boolean> breakBlocksOptional() {
      return Optional.ofNullable(this.breakBlocks);
   }

   public Optional<Double> lowHpCrystalLethalReserveOptional() {
      return Optional.ofNullable(this.lowHpCrystalLethalReserve);
   }

   public boolean hasChanges() {
      return this.anchoringMode != null || this.breakBlocks != null || this.lowHpCrystalLethalReserve != null;
   }
}
