package dev.truewinter.snowmail.api.plugin;

import dev.truewinter.PluginManager.Listener;
import dev.truewinter.PluginManager.Logger;
import dev.truewinter.PluginManager.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * For <code>dev.truewinter.PluginManager</code> docs,
 * <a href="https://javadoc.jitpack.io/dev/truewinter/PluginManager/latest/javadoc/">click here</a>.
 */
@SuppressWarnings("unused")
public abstract class SnowMailPlugin extends Plugin<SnowMailAPI> {
    @Override
    protected void registerListeners(Plugin<SnowMailAPI> plugin, Listener listener) throws Exception {
        ensureNoApiInteractionInConstructor();
        SnowMailPluginManager.getInstance().getPluginManager().registerListener(plugin, listener);
    }

    @Override
    protected Logger getLogger() {
        ensureNoApiInteractionInConstructor();
        return SnowMailPluginManager.getInstance().getLogger(getName());
    }

    @Override
    protected Plugin<SnowMailAPI> getPluginByName(@NotNull String s) throws ClassCastException, IllegalStateException {
        ensureNoApiInteractionInConstructor();
        return SnowMailPluginManager.getInstance().getPluginManager().getPluginByName(this, s);
    }

    @Override
    protected SnowMailAPI getApi() {
        ensureNoApiInteractionInConstructor();
        return SnowMailPluginManager.getInstance().getApi();
    }
}
