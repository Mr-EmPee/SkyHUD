package me.empee.skyhud.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entity.system.UpdateLocationSystems;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.empee.skyhud.services.SkyIconService;
import me.empee.skyhud.ui.SkyHUD;
import me.empee.utils.multiplehud.MultipleHudUtils;

import java.util.Set;

@Singleton
@RequiredArgsConstructor
public class HudUpdateVisibilitySystem extends EntityTickingSystem<EntityStore> {

  private final SkyIconService skyIconService;

  public void tick(
      float dt, int index, ArchetypeChunk<EntityStore> archetypeChunk,
      Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer
  ) {
    Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);
    Player player = store.getComponent(playerRef, Player.getComponentType());

    boolean shouldShow = skyIconService.shouldShow(playerRef, store);
    SkyHUD hud = MultipleHudUtils.getCustomHud(player, SkyHUD.class, SkyHUD.ID);
    if (hud == null) {
      PlayerRef playerNet = store.getComponent(playerRef, PlayerRef.getComponentType());
      hud = new SkyHUD(playerNet);

      MultipleHudUtils.setCustomHud(player, playerNet, SkyHUD.ID, hud);

      WorldTimeResource worldTime = store.getResource(WorldTimeResource.getResourceType());
      hud.setIcon(skyIconService.getByPercentage(worldTime.getDayProgress()));
    }

    if (hud.isVisible() != shouldShow) {
      hud.setVisible(shouldShow);
      hud.update();
    }
  }

  public Query<EntityStore> getQuery() {
    return Query.and(Player.getComponentType(), TransformComponent.getComponentType());
  }

  public Set<Dependency<EntityStore>> getDependencies() {
    return Set.of(
        new SystemDependency<>(Order.AFTER, UpdateLocationSystems.TickingSystem.class)
    );
  }
}
