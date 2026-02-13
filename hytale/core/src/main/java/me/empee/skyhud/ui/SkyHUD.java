package me.empee.skyhud.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.empee.skyhud.models.SkyIcon;

import java.util.Map;

public class SkyHUD extends CustomUIHud {

  public static String ID = "TimeHud";

  private static final Map<SkyIcon, String> iconPaths = Map.of(
      SkyIcon.DAWN, "dawn.png",
      SkyIcon.MORNING, "morning.png",
      SkyIcon.DAY, "day.png",
      SkyIcon.AFTERNOON, "afternoon.png",
      SkyIcon.EVENING, "evening.png",
      SkyIcon.NIGHT, "night.png"
  );

  @Getter
  private boolean visible;

  private UICommandBuilder uiCommandBuilder = new UICommandBuilder();

  public SkyHUD(PlayerRef playerRef) {
    super(playerRef);
  }

  protected void build(UICommandBuilder uiCommandBuilder) {
    uiCommandBuilder.append("SkyHUD.ui");
  }

  public void setVisible(boolean visible) {
    if (visible == this.visible) return;

    uiCommandBuilder.set("#TimeHudImage.Visible", visible);
    this.visible = visible;
  }

  public void setIcon(SkyIcon icon) {
    uiCommandBuilder.set("#TimeHudImage.AssetPath", "UI/Custom/Images/" + iconPaths.get(icon));
  }

  public void update() {
    update(false, uiCommandBuilder);
    uiCommandBuilder = new UICommandBuilder();
  }

}
