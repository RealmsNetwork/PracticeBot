package com.sheldera.practicebot.x;

import com.sheldera.practicebot.PracticeBotPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class z implements CommandExecutor, TabCompleter {
   private static final String v0 = "practicebot.admin";
   private static final String v1 = "practicebot.admin.license";
   private static final String w0 = "practicebot.admin.template.spawn";
   private static final String w1 = "practicebot.admin.despawn";
   private static final String x0 = "practicebot.admin.template.create";
   private static final String x1 = "practicebot.admin.template.edit";
   private static final String y0 = "practicebot.admin.template.cancel";
   private static final String y1 = "practicebot.admin.template.delete";
   private final PracticeBotPlugin z0;

   public z(PracticeBotPlugin var1) {
      this.z0 = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 0) {
         return !this.z0.isLicenseActive() ? this.a(var1, new String[]{"license"}) : this.g(var1);
      } else {
         String var5 = var4[0].toLowerCase(Locale.ROOT);
         if (var5.equals("license")) {
            return this.a(var1, var4);
         } else if (!this.z0.isLicenseActive()) {
            var1.sendMessage(this.z0.licenseLockMessage());
            return true;
         } else {
            return switch (var5) {
               case "help" -> this.g(var1);
               case "inventory" -> this.a(var1, this::n);
               case "egui" -> this.a(var1, this::o);
               case "reload" -> this.h(var1);
               case "template" -> this.b(var1, var4);
               case "despawn" -> this.e(var1, var4);
               default -> {
                  var1.sendMessage(h.ac("invalid-usage.practicebot-admin"));
                  yield true;
               }
            };
         }
      }
   }

   private boolean a(CommandSender var1, String[] var2) {
      if (!this.b(var1, "practicebot.admin.license")) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else if (var2.length == 1) {
         String var5 = this.z0.isLicenseActive() ? "&aVALID" : "&cLOCKED";
         String var4 = this.z0.getLicenseManager() != null ? this.z0.getLicenseManager().l1() : "not_initialized";
         var1.sendMessage(h.a("license-status", "{status}", var5, "{last}", var4));
         var1.sendMessage(h.ac("license-usage"));
         return true;
      } else {
         String var3 = var2[1].toLowerCase(Locale.ROOT);
         if (var3.equals("refresh")) {
            this.z0.getLicenseManager().a(var1);
            return true;
         } else if (var3.equals("set")) {
            if (var2.length != 3) {
               var1.sendMessage(h.ac("invalid-usage.license-set"));
               return true;
            } else {
               this.z0.getLicenseManager().a(var1, var2[2]);
               return true;
            }
         } else {
            var1.sendMessage(h.ac("invalid-usage.license"));
            return true;
         }
      }
   }

   private boolean g(CommandSender var1) {
      var1.sendMessage(h.ad("help.admin-header"));

      for (String var3 : h.ae("help.admin-commands")) {
         var1.sendMessage(var3);
      }

      var1.sendMessage(h.ad("help.footer"));
      return true;
   }

   private boolean n(Player var1) {
      if (!this.j(var1)) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else {
         this.z0.getGuiManager().bg(var1);
         return true;
      }
   }

   private boolean o(Player var1) {
      if (!this.j(var1)) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else {
         this.z0.getGuiEditorManager().bb(var1);
         return true;
      }
   }

   private boolean h(CommandSender var1) {
      if (!this.j(var1)) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else {
         this.z0.getConfigManager().a();
         h.C();
         this.z0.getGuiConfigManager().eH();
         this.z0.getBotTemplateManager().a();
         var1.sendMessage(h.ac("config-reloaded"));
         return true;
      }
   }

   private boolean b(CommandSender var1, String[] var2) {
      if (var2.length < 2) {
         var1.sendMessage(h.ac("invalid-usage.template"));
         return true;
      } else {
         String var3 = var2[1].toLowerCase(Locale.ROOT);

         return switch (var3) {
            case "create" -> this.a(var1, var2x -> this.a(var2x, var2));
            case "edit" -> this.a(var1, var2x -> this.b(var2x, var2));
            case "cancel" -> this.a(var1, var2x -> this.c(var2x, var2));
            case "delete" -> this.c(var1, var2);
            case "spawn" -> this.d(var1, var2);
            default -> {
               var1.sendMessage(h.ac("invalid-usage.template"));
               yield true;
            }
         };
      }
   }

   private boolean a(Player var1, String[] var2) {
      if (!this.b(var1, "practicebot.admin.template.create")) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else if (var2.length != 3) {
         var1.sendMessage(h.ac("invalid-usage.template-create"));
         return true;
      } else {
         return this.z0.getTemplateEditorManager().g(var1, var2[2]);
      }
   }

   private boolean b(Player var1, String[] var2) {
      if (!this.b(var1, "practicebot.admin.template.edit")) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else if (var2.length != 3) {
         var1.sendMessage(h.ac("invalid-usage.template-edit"));
         return true;
      } else {
         return this.z0.getTemplateEditorManager().h(var1, var2[2]);
      }
   }

   private boolean c(Player var1, String[] var2) {
      if (!this.b(var1, "practicebot.admin.template.cancel")) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else if (var2.length != 2) {
         var1.sendMessage(h.ac("invalid-usage.template-cancel"));
         return true;
      } else {
         return this.z0.getTemplateEditorManager().a(var1, true);
      }
   }

   private boolean c(CommandSender var1, String[] var2) {
      if (!this.b(var1, "practicebot.admin.template.delete")) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else if (var2.length != 3) {
         var1.sendMessage(h.ac("invalid-usage.template-delete"));
         return true;
      } else {
         return this.z0.getTemplateEditorManager().c(var1, var2[2]);
      }
   }

   private boolean d(CommandSender var1, String[] var2) {
      if (!this.b(var1, "practicebot.admin.template.spawn")) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else if (!this.z0.isCitizensReady()) {
         var1.sendMessage(h.ac("citizens-not-loaded"));
         return true;
      } else if (var2.length != 3 && var2.length != 4 && var2.length != 9 && var2.length != 10) {
         var1.sendMessage(h.ac("invalid-usage.template-spawn"));
         return true;
      } else {
         E var3 = this.z0.getBotTemplateManager().i1(var2[2]);
         if (var3 == null) {
            var1.sendMessage(h.a("errors.template-not-found", "{template}", var2[2]));
            return true;
         } else {
            M var4 = var3.gb();
            if (var2.length != 3 && var2.length != 4) {
               boolean var15 = var2.length == 10;
               if ((var4 == M.NONE || var4 == M.BOT) && var15) {
                  var1.sendMessage(h.a("errors.template-target-not-allowed", "{template}", var3.fe()));
                  return true;
               } else {
                  Player var16 = null;
                  byte var17 = 3;
                  if (var15) {
                     var16 = this.as(var2[3]);
                     if (var16 == null) {
                        var1.sendMessage(h.a("errors.player-not-found", "{player}", var2[3]));
                        return true;
                     }

                     var17 = 4;
                  } else if (var4 == M.REQUIRED) {
                     if (!(var1 instanceof Player var18)) {
                        var1.sendMessage(h.a("errors.template-target-required", "{template}", var3.fe()));
                        return true;
                     }

                     var16 = var18;
                  }

                  World var19 = this.ar(var2[var17]);
                  if (var19 == null) {
                     var1.sendMessage(h.a("errors.world-not-found", "{world}", var2[var17]));
                     return true;
                  } else {
                     Double var9 = this.at(var2[var17 + 1]);
                     Double var10 = this.at(var2[var17 + 2]);
                     Double var11 = this.at(var2[var17 + 3]);
                     Float var12 = this.au(var2[var17 + 4]);
                     Float var13 = this.au(var2[var17 + 5]);
                     if (var9 != null && var10 != null && var11 != null && var12 != null && var13 != null) {
                        Location var14 = new Location(var19, var9, var10, var11, var12, var13);
                        if (!Double.isFinite(var14.getX())
                           || !Double.isFinite(var14.getY())
                           || !Double.isFinite(var14.getZ())
                           || !Float.isFinite(var14.getPitch())
                           || !Float.isFinite(var14.getYaw())) {
                           var1.sendMessage(h.ac("errors.invalid-coordinates"));
                           return true;
                        } else if (this.z0.getBotManager().a(var3, var14, var16) == null) {
                           var1.sendMessage(h.a("errors.template-spawn-failed", "{template}", var3.fe()));
                           return true;
                        } else {
                           var1.sendMessage(
                              h.a(
                                 var16 != null ? "template-spawned-bound" : "template-spawned",
                                 "{template}",
                                 var3.fe(),
                                 "{world}",
                                 var19.getName(),
                                 "{target}",
                                 var16 != null ? var16.getName() : ""
                              )
                           );
                           return true;
                        }
                     } else {
                        var1.sendMessage(h.ac("errors.invalid-coordinates"));
                        return true;
                     }
                  }
               }
            } else if (var1 instanceof Player var5) {
               Player var6 = null;
               boolean var7 = var2.length == 4;
               if (var7) {
                  if (var4 == M.NONE || var4 == M.BOT) {
                     var1.sendMessage(h.a("errors.template-target-not-allowed", "{template}", var3.fe()));
                     return true;
                  }

                  var6 = this.as(var2[3]);
                  if (var6 == null) {
                     var1.sendMessage(h.a("errors.player-not-found", "{player}", var2[3]));
                     return true;
                  }
               } else if (var4 == M.REQUIRED) {
                  var6 = var5;
               }

               if (var4 == M.REQUIRED && var6 == null) {
                  var1.sendMessage(h.a("errors.template-target-required", "{template}", var3.fe()));
                  return true;
               } else {
                  Location var8 = var5.getLocation();
                  if (this.z0.getBotManager().a(var3, var8, var6) == null) {
                     var1.sendMessage(h.a("errors.template-spawn-failed", "{template}", var3.fe()));
                     return true;
                  } else {
                     var1.sendMessage(
                        h.a(
                           var6 != null ? "template-spawned-bound" : "template-spawned",
                           "{template}",
                           var3.fe(),
                           "{world}",
                           var8.getWorld().getName(),
                           "{target}",
                           var6 != null ? var6.getName() : ""
                        )
                     );
                     return true;
                  }
               }
            } else {
               var1.sendMessage(h.ac("invalid-usage.template-spawn"));
               return true;
            }
         }
      }
   }

   private boolean e(CommandSender var1, String[] var2) {
      if (!this.b(var1, "practicebot.admin.despawn")) {
         var1.sendMessage(h.ac("no-permission"));
         return true;
      } else if (!this.z0.isCitizensReady()) {
         var1.sendMessage(h.ac("citizens-not-loaded"));
         return true;
      } else if (var2.length < 2) {
         var1.sendMessage(h.ac("invalid-usage.despawn"));
         return true;
      } else {
         World var3 = this.ar(var2[1]);
         if (var3 == null) {
            var1.sendMessage(h.a("errors.world-not-found", "{world}", var2[1]));
            return true;
         } else if (var2.length >= 3) {
            String var5 = String.join(" ", Arrays.copyOfRange(var2, 2, var2.length)).trim();
            if (var5.isEmpty()) {
               var1.sendMessage(h.ac("invalid-usage.despawn"));
               return true;
            } else if (!this.z0.getBotManager().a(var3, var5, n.MANUAL)) {
               var1.sendMessage(h.a("errors.bot-not-found-in-world", "{bot}", var5, "{world}", var3.getName()));
               return true;
            } else {
               var1.sendMessage(h.a("world-despawned-one", "{world}", var3.getName(), "{bot}", var5));
               return true;
            }
         } else {
            t var4 = this.z0.getBotManager().a(var3, n.MANUAL);
            var1.sendMessage(h.a("world-despawned", "{world}", var3.getName(), "{count}", String.valueOf(var4.X()), "{respawns}", String.valueOf(var4.Y())));
            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 0) {
         return Collections.emptyList();
      } else {
         String var5 = var4[0].toLowerCase(Locale.ROOT);
         if (var4.length == 1) {
            ArrayList var9 = new ArrayList();
            if (this.b(var1, "practicebot.admin.license")) {
               this.a(var9, "license", var4[0]);
               if (!this.z0.isLicenseActive()) {
                  return var9;
               }
            }

            if (this.j(var1)) {
               this.a(var9, "inventory", var4[0]);
               this.a(var9, "egui", var4[0]);
               this.a(var9, "reload", var4[0]);
            }

            if (this.i(var1)) {
               this.a(var9, "template", var4[0]);
            }

            if (this.b(var1, "practicebot.admin.despawn")) {
               this.a(var9, "despawn", var4[0]);
            }

            this.a(var9, "help", var4[0]);
            return var9;
         } else if (var5.equals("license") && this.b(var1, "practicebot.admin.license")) {
            if (var4.length == 2) {
               ArrayList var8 = new ArrayList();
               this.a(var8, "refresh", var4[1]);
               this.a(var8, "set", var4[1]);
               return var8;
            } else {
               return Collections.emptyList();
            }
         } else if (!this.z0.isLicenseActive()) {
            return Collections.emptyList();
         } else if (var5.equals("template")) {
            return this.f(var1, var4);
         } else {
            if (var5.equals("despawn") && this.b(var1, "practicebot.admin.despawn")) {
               if (var4.length == 2) {
                  return this.ap(var4[1]);
               }

               if (var4.length >= 3) {
                  World var6 = this.ar(var4[1]);
                  if (var6 == null) {
                     return Collections.emptyList();
                  }

                  String var7 = var4[var4.length - 1];
                  return this.a(var6, var7);
               }
            }

            return Collections.emptyList();
         }
      }
   }

   private List<String> f(CommandSender var1, String[] var2) {
      if (var2.length == 2) {
         ArrayList var4 = new ArrayList();
         if (this.b(var1, "practicebot.admin.template.create")) {
            this.a(var4, "create", var2[1]);
         }

         if (this.b(var1, "practicebot.admin.template.edit")) {
            this.a(var4, "edit", var2[1]);
         }

         if (this.b(var1, "practicebot.admin.template.cancel")) {
            this.a(var4, "cancel", var2[1]);
         }

         if (this.b(var1, "practicebot.admin.template.delete")) {
            this.a(var4, "delete", var2[1]);
         }

         if (this.b(var1, "practicebot.admin.template.spawn")) {
            this.a(var4, "spawn", var2[1]);
         }

         return var4;
      } else {
         String var3 = var2[1].toLowerCase(Locale.ROOT);
         if ((var3.equals("edit") || var3.equals("delete") || var3.equals("spawn")) && var2.length == 3) {
            return this.ao(var2[2]);
         } else {
            return var3.equals("spawn") && this.b(var1, "practicebot.admin.template.spawn") ? this.a(var2) : Collections.emptyList();
         }
      }
   }

   private List<String> a(String[] var1) {
      if (var1.length < 3) {
         return Collections.emptyList();
      } else {
         E var2 = this.z0.getBotTemplateManager().i1(var1[2]);
         if (var2 == null) {
            return Collections.emptyList();
         } else {
            boolean var3 = this.a(var2, var1);
            if (var1.length == 4) {
               if (var2.gb() == M.REQUIRED) {
                  return this.aq(var1[3]);
               } else if (var2.gb() != M.NONE && var2.gb() != M.BOT) {
                  ArrayList var5 = new ArrayList();
                  var5.addAll(this.aq(var1[3]));
                  var5.addAll(this.ap(var1[3]));
                  return var5;
               } else {
                  return this.ap(var1[3]);
               }
            } else {
               int var4 = var3 ? 4 : 3;
               return var1.length == var4 + 1 ? this.ap(var1[var4]) : Collections.emptyList();
            }
         }
      }
   }

   private boolean a(E var1, String[] var2) {
      if (var1.gb() == M.REQUIRED) {
         return true;
      } else if (var1.gb() == M.NONE || var1.gb() == M.BOT) {
         return false;
      } else if (var2.length <= 4) {
         return false;
      } else {
         World var3 = Bukkit.getWorld(var2[3]);
         return var3 == null;
      }
   }

   private boolean i(CommandSender var1) {
      return this.b(var1, "practicebot.admin.template.create")
         || this.b(var1, "practicebot.admin.template.edit")
         || this.b(var1, "practicebot.admin.template.cancel")
         || this.b(var1, "practicebot.admin.template.delete")
         || this.b(var1, "practicebot.admin.template.spawn");
   }

   private List<String> ao(String var1) {
      ArrayList var2 = new ArrayList();

      for (String var4 : this.z0.getBotTemplateManager().gG()) {
         this.a(var2, var4, var1);
      }

      return var2;
   }

   private List<String> ap(String var1) {
      ArrayList var2 = new ArrayList();

      for (World var4 : Bukkit.getWorlds()) {
         this.a(var2, var4.getName(), var1);
      }

      return var2;
   }

   private List<String> aq(String var1) {
      ArrayList var2 = new ArrayList();

      for (Player var4 : Bukkit.getOnlinePlayers()) {
         this.a(var2, var4.getName(), var1);
      }

      return var2;
   }

   private List<String> a(World var1, String var2) {
      ArrayList var3 = new ArrayList();

      for (String var5 : this.z0.getBotManager().a(var1)) {
         this.a(var3, var5, var2);
      }

      return var3;
   }

   private World ar(String var1) {
      World var2 = Bukkit.getWorld(var1);
      if (var2 != null) {
         return var2;
      } else {
         for (World var4 : Bukkit.getWorlds()) {
            if (var4.getName().equalsIgnoreCase(var1)) {
               return var4;
            }
         }

         return null;
      }
   }

   private Player as(String var1) {
      Player var2 = Bukkit.getPlayerExact(var1);
      if (var2 != null) {
         return var2;
      } else {
         for (Player var4 : Bukkit.getOnlinePlayers()) {
            if (var4.getName().equalsIgnoreCase(var1)) {
               return var4;
            }
         }

         return null;
      }
   }

   private Double at(String var1) {
      try {
         return Double.parseDouble(var1);
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   private Float au(String var1) {
      try {
         return Float.parseFloat(var1);
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   private boolean a(CommandSender var1, aa var2) {
      if (var1 instanceof Player var3) {
         return var2.handle(var3);
      } else {
         var1.sendMessage(h.ad("player-only"));
         return true;
      }
   }

   private boolean j(CommandSender var1) {
      return !(var1 instanceof Player) || var1.hasPermission("practicebot.admin") || var1.hasPermission(this.z0.getConfigManager().bl());
   }

   private boolean b(CommandSender var1, String var2) {
      return !(var1 instanceof Player) || var1.hasPermission(var2) || this.j(var1);
   }

   private void a(List<String> var1, String var2, String var3) {
      if (var2.toLowerCase(Locale.ROOT).startsWith(var3.toLowerCase(Locale.ROOT))) {
         var1.add(var2);
      }
   }
}
