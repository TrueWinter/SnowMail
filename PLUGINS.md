# Plugins

The SnowMail API module is published to Maven Central.

```xml
<dependencies>
    <dependency>
        <groupId>dev.truewinter.snowmail</groupId>
        <artifactId>api</artifactId>
        <!-- Replace VERSION with the version of SnowMail you have installed -->
        <version>VERSION</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## Plugin Information File

You will need to create a resource file called plugin.yml. The contents of the file should be as follows:

```yml
# A unique name for your plugin. Please only use alphanumeric characters and dashes.
name: ExamplePlugin

# The plugin's main class
main_class: com.example.exampleplugin.ExamplePlugin
```

## Developing Your Plugin

Now you can start developing your plugin. The plugin's main class must extend `Plugin`.

```java
public class ExamplePlugin extends Plugin {
  // Important: It is not safe to interact with SnowMail until the onLoad() method is called
  @Override
  public void onLoad() {
    getLogger().info("Loaded plugin");
    registerListeners(this, new EventListener());
    getLogger().info("Registered event listeners");
  }

  @Override
  public void onUnload() {
    getLogger().info("Unloaded plugin");
  }
}
```

Registering event listeners requires an instance of a class that implements `Listener`.

```java
public class EventListener implements Listener {
  /*
    The method can be called anything you'd like, but it must be annotated with
    @EventHandler and there must be one parameter (and no more) with the Event
    you want to subscribe to.
  */
  @EventHandler
  public void onFormSubmit(FormSubmissionEvent e) {
    /*
      Some events are cancellable. Cancelled events prevent SnowMail from
      handling the event (e.g. cancelling a FormSubmissionEvent event will
      stop SnowMail from sending an email for the submission). You should
      check if the event was cancelled before handling it unless you have
      a reason to handle cancelled events.
    */
    if (e.isCancelled()) return;

    // Handle the form submission  
  }
}
```

Note that all listeners are blocking, so please ensure your plugin does what it needs as quickly as possible.

### Using Other Installed Plugins

SnowMail allows plugins to interact with other plugins installed on the same instance. To do so, your main class must implement `ExternalPlugin`. Only after the `onAllPluginsLoaded()` method has been called, can you use the `getPluginByName()` method to get an instance of another plugin.