package com.sheldera.practicebot.x;

enum T {
   MAIN("template_editor_main", 54, "§8§lTemplate Editor"),
   BEHAVIOR("template_editor_behavior", 45, "§8§lTemplate Behavior"),
   COMBAT("template_editor_combat", 45, "§8§lTemplate Combat"),
   INVENTORY("template_editor_inventory", 54, "§8§lTemplate Inventory"),
   CPVP("template_editor_cpvp", 45, "§8§lTemplate CPvP");

   final String guiId;
   final int defaultSize;
   final String defaultTitle;

   private T(String var3, int var4, String var5) {
      this.guiId = var3;
      this.defaultSize = var4;
      this.defaultTitle = var5;
   }
}
