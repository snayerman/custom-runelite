package net.runelite.client.plugins.sandcrabs;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sandcrabs")
public interface SandcrabsConfig extends Config
{
    @ConfigItem(
            position = 1,
            keyName = "enable",
            name = "Start/stop plugin",
            description = "Toggle the plugin on/off"
    )
    default boolean enabled() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "position",
            name = "Set overlay position",
            description = "ha"
    )
    default Position position() { return Position.BOTTOM_RIGHT ;}
}