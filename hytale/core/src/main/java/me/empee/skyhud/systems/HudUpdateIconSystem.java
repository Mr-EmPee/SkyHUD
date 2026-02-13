package me.empee.skyhud.systems;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.empee.skyhud.models.SkyIcon;
import me.empee.skyhud.services.SkyIconService;
import me.empee.skyhud.ui.SkyHUD;
import me.empee.utils.multiplehud.MultipleHudUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class HudUpdateIconSystem extends TickingSystem<EntityStore> {

  private final Map<UUID, SkyIcon> timeIconsByWorld = new HashMap<>();

  private final SkyIconService skyIconService;

  @Override
  public void tick(float dt, int index, Store<EntityStore> store) {
    UUID worldId = store.getExternalData().getWorld().getWorldConfig().getUuid();
    SkyIcon oldTime = timeIconsByWorld.get(worldId);

    WorldTimeResource worldTime = store.getResource(WorldTimeResource.getResourceType());
    SkyIcon currentTime = skyIconService.getByPercentage(worldTime.getDayProgress());

    if (currentTime == oldTime) {
      return;
    }

    timeIconsByWorld.put(worldId, currentTime);

    World world = store.getExternalData().getWorld();
    Collection<PlayerRef> playerNets = world.getPlayerRefs();

    for (PlayerRef playerNet : playerNets) {
      Ref<EntityStore> entityRef = playerNet.getReference();
      Player player = store.getComponent(entityRef, Player.getComponentType());

      SkyHUD hud = MultipleHudUtils.getCustomHud(player, SkyHUD.class, SkyHUD.ID);
      if (hud == null) {
        return;
      }

      hud.setIcon(currentTime);

      hud.update();
    }
  }

}
