package me.empee.skyhud;

import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.event.IBaseEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.avaje.inject.BeanScope;
import me.empee.utils.models.markers.IListener;

import java.lang.reflect.Type;
import java.util.List;

public class SkyHudPlugin extends JavaPlugin {

  public static final HytaleLogger log = HytaleLogger.forEnclosingClass();

  private BeanScope beanScope;

  public SkyHudPlugin(JavaPluginInit init) {
    super(init);
  }

  protected void setup() {
    beanScope = BeanScope.builder()
                         .classLoader(getClass().getClassLoader())
                         .build();

    List<AbstractCommand> commands = beanScope.list(AbstractCommand.class);

    for (AbstractCommand command : commands) {
      log.atInfo().log("Registering %s", command.getClass().getName());
      getCommandRegistry().registerCommand(command);
    }

    List<IListener<IBaseEvent<?>>> eventListeners = beanScope.list((Type) IListener.class);

    for (IListener<IBaseEvent<?>> eventListener : eventListeners) {
      log.atInfo().log("Registering %s", eventListener.getClass().getName());
      getEventRegistry().registerGlobal(eventListener.getEvent(), eventListener::handle);
    }
  }

  protected void start() {
    List<ISystem<EntityStore>> systems = beanScope.list((Type) ISystem.class);

    for (ISystem<EntityStore> system : systems) {
      log.atInfo().log("Registering %s", system.getClass().getName());
      getEntityStoreRegistry().registerSystem(system);
    }
  }

  @Override
  protected void shutdown() {
    beanScope.close();
  }

}