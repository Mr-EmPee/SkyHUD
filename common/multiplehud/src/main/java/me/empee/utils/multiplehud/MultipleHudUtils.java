package me.empee.utils.multiplehud;

import com.buuz135.mhud.MultipleCustomUIHud;
import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;

@UtilityClass
public class MultipleHudUtils {

  private static boolean hasMultipleHUD;

  static {
    try {
      Class.forName("com.buuz135.mhud.MultipleHUD");
      hasMultipleHUD = true;
    } catch (ClassNotFoundException var2) {
      hasMultipleHUD = false;
    }
  }

  @Nullable
  public static <K extends CustomUIHud> K getCustomHud(Player player, Class<K> hudClazz, String hudId) {
    CustomUIHud currentHud = player.getHudManager().getCustomHud();
    if (currentHud == null) {
      return null;
    }

    if (hudClazz.isInstance(currentHud)) {
      return hudClazz.cast(currentHud);
    }

    if (!hasMultipleHUD) {
      return null;
    }

    if (!(currentHud instanceof MultipleCustomUIHud multipleHud)) {
      return null;
    }

    return (K) multipleHud.get(hudId);
  }

  public static void setCustomHud(Player player, PlayerRef playerRef, String hudID, CustomUIHud hud) {
    if (hasMultipleHUD) {
      MultipleHUD.getInstance().setCustomHud(player, playerRef, hudID, hud);
    } else {
      player.getHudManager().setCustomHud(playerRef, hud);
    }
  }

  public static void resetCustomHud(Player player, PlayerRef playerRef, String hudID) {
    if (hasMultipleHUD) {
      MultipleHUD.getInstance().hideCustomHud(player, hudID);
    } else {
      player.getHudManager().resetHud(playerRef);
    }
  }

}
