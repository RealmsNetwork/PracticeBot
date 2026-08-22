package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;

final class S {
   boolean lookAtTarget;
   boolean followTarget;
   boolean randomWalk;
   boolean holdShield;
   boolean useShield;
   boolean resistance;
   boolean frozen;
   boolean shieldInMainHand;

   private S() {
   }

   static S d(E var0) {
      S var1 = new S();
      G var2 = var0.gg();
      var1.lookAtTarget = var2.go();
      var1.followTarget = var2.gp();
      var1.randomWalk = var2.isRandomWalk();
      var1.holdShield = var2.isHoldShield();
      var1.useShield = var2.isUseShield();
      var1.resistance = var2.isResistance();
      var1.frozen = var2.isFrozen();
      var1.shieldInMainHand = var2.isShieldInMainHand();
      return var1;
   }

   void l(BotTrait var1) {
      this.lookAtTarget = var1.isLookAtOwner();
      this.followTarget = var1.isFollowOwner();
      this.randomWalk = var1.isRandomWalk();
      this.holdShield = var1.isHoldShield();
      this.useShield = var1.isUseShield();
      this.resistance = var1.isResistance();
      this.frozen = var1.isFrozen();
      this.shieldInMainHand = var1.isShieldInMainHand();
   }
}
