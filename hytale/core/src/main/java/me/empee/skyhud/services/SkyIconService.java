package me.empee.skyhud.services;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.environment.EnvironmentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.environment.EnvironmentColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import jakarta.inject.Singleton;
import me.empee.skyhud.models.SkyIcon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@Singleton
public class SkyIconService {

  private final Map<Integer, Boolean> caveEnvironments = Collections.synchronizedMap(new HashMap<>());
  private final NavigableMap<Float, SkyIcon> timeIconsByPercentage = new TreeMap<>();

  public SkyIconService() {
    setupIconsByPercentage();
  }

  private void setupIconsByPercentage() {
    timeIconsByPercentage.put(0.0f, SkyIcon.NIGHT);
    timeIconsByPercentage.put(0.143f, SkyIcon.DAWN);
    timeIconsByPercentage.put(0.23f, SkyIcon.MORNING);
    timeIconsByPercentage.put(0.40f, SkyIcon.DAY);
    timeIconsByPercentage.put(0.68f, SkyIcon.AFTERNOON);
    timeIconsByPercentage.put(0.82f, SkyIcon.EVENING);
    timeIconsByPercentage.put(0.92f, SkyIcon.NIGHT);
  }

  public SkyIcon getByPercentage(float time) {
    return timeIconsByPercentage.floorEntry(time).getValue();
  }

  public boolean shouldShow(Ref<EntityStore> playerRef, Store<EntityStore> store) {
    TransformComponent location = store.getComponent(playerRef, TransformComponent.getComponentType());
    if (location == null) {
      return false;
    }

    Vector3i position = location.getPosition().toVector3i();

    int x = position.getX();
    int y = position.getY();
    int z = position.getZ();

    Ref<ChunkStore> chunkRef = location.getChunkRef();
    Store<ChunkStore> chunkStore = chunkRef.getStore();

    EnvironmentChunk chunkEnvironment = chunkStore.getComponent(chunkRef, EnvironmentChunk.getComponentType());
    EnvironmentColumn environmentColumn = chunkEnvironment.get(x, z);

    int environmentId = environmentColumn.get(y);

    return caveEnvironments.computeIfAbsent(environmentId, this::isCaveEnvironment);
  }

  private boolean isCaveEnvironment(int environmentId) {
    Environment environment = Environment.getAssetMap().getAsset(environmentId);
    if (environment == null) {
      return false;
    }

    AssetExtraInfo.Data data = Environment.getAssetStore().getCodec().getData(environment);
    if (data == null) {
      return false;
    }

    for (var entry : data.getRawTags().entrySet()) {
      for (String tag : entry.getValue()) {
        if (tag.equals("Caves")) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean isSkyNotVisible(Ref<EntityStore> playerRef, Store<EntityStore> worldStore) {
    TransformComponent location = worldStore.getComponent(playerRef, TransformComponent.getComponentType());
    if (location == null) {
      return false;
    }

    Vector3i position = location.getPosition().toVector3i();

    int x = position.getX();
    int y = position.getY();
    int z = position.getZ();

    Ref<ChunkStore> chunkRef = location.getChunkRef();
    Store<ChunkStore> chunkStore = chunkRef.getStore();

    BlockChunk blockChunkComponent = chunkStore.getComponent(chunkRef, BlockChunk.getComponentType());
    BlockSection chunkSection = blockChunkComponent.getSectionAtBlockY(y);

    if (chunkSection.getGlobalLight().getChangeId() == 0) {
      return false;
    }

    //FIXME SkyLight server-side computation is not reliable, sometimes is 0 (after it has been computed) even if that's not true
    //      I assume cause it couldn't be computed we should check if computation has failed
    int skyLight = chunkSection.getGlobalLight().getSkyLight(x, y, z);

    return skyLight <= 1;
  }

}
