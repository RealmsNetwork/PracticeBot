package com.sheldera.practicebot.api.cpvp;

public final class CpvpSettingsUpdateRequest$Builder {
   Boolean anchoringMode;
   Boolean breakBlocks;
   Double lowHpCrystalLethalReserve;

   CpvpSettingsUpdateRequest$Builder() {
   }

   public CpvpSettingsUpdateRequest$Builder anchoringMode(boolean var1) {
      this.anchoringMode = var1;
      return this;
   }

   public CpvpSettingsUpdateRequest$Builder breakBlocks(boolean var1) {
      this.breakBlocks = var1;
      return this;
   }

   public CpvpSettingsUpdateRequest$Builder lowHpCrystalLethalReserve(double var1) {
      this.lowHpCrystalLethalReserve = var1;
      return this;
   }

   public CpvpSettingsUpdateRequest build() {
      return new CpvpSettingsUpdateRequest(this);
   }
}
