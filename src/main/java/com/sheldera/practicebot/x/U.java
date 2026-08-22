package com.sheldera.practicebot.x;

import com.sheldera.practicebot.BotTrait;

final class U {
   boolean lookAtTarget;
   boolean followTarget;
   boolean randomWalk;
   boolean holdShield;
   boolean useShield;
   boolean resistance;
   boolean shieldInMainHand;
   boolean pvpEnabled;
   boolean strafe;
   boolean wTap;
   boolean sTap;
   boolean crits;
   boolean shieldBreaker;
   boolean retreat;
   int reachMode;
   int aggression;
   int critChance;
   int critSpeed;

   private U() {
   }

   static U e(E var0) {
      U var1 = new U();
      G var2 = var0.gg();
      H var3 = var0.gh();
      var1.lookAtTarget = var2.go();
      var1.followTarget = var2.gp();
      var1.randomWalk = var2.isRandomWalk();
      var1.holdShield = var2.isHoldShield();
      var1.useShield = var2.isUseShield();
      var1.resistance = var2.isResistance();
      var1.shieldInMainHand = var2.isShieldInMainHand();
      var1.pvpEnabled = var2.isPvpEnabled();
      var1.strafe = var3.gq();
      var1.wTap = var3.gr();
      var1.sTap = var3.gs();
      var1.crits = var3.gt();
      var1.shieldBreaker = var3.gu();
      var1.retreat = var3.gv();
      var1.reachMode = var3.gw();
      var1.aggression = var3.gx();
      var1.critChance = var3.gy();
      var1.critSpeed = var3.gz();
      return var1;
   }

   void l(BotTrait var1) {
      this.lookAtTarget = var1.isLookAtOwner();
      this.followTarget = var1.isFollowOwner();
      this.randomWalk = var1.isRandomWalk();
      this.holdShield = var1.isHoldShield();
      this.useShield = var1.isUseShield();
      this.resistance = var1.isResistance();
      this.shieldInMainHand = var1.isShieldInMainHand();
      this.pvpEnabled = var1.isPvpEnabled();
      this.strafe = var1.isPvpStrafe();
      this.wTap = var1.isPvpWTap();
      this.sTap = var1.isPvpSTap();
      this.crits = var1.isPvpCrits();
      this.shieldBreaker = var1.isPvpShieldBreaker();
      this.retreat = var1.isPvpRetreat();
      this.reachMode = var1.getPvpReachMode();
      this.aggression = var1.getPvpAggression();
      this.critChance = var1.getPvpCritChance();
      this.critSpeed = var1.getPvpCritSpeed();
   }
}
