package distinctsocketeddescriptions.config;

import distinctsocketeddescriptions.effect.DDDEffect;
import net.minecraft.util.text.TextFormatting;
import socketed.common.config.DefaultJsonConfig;
import socketed.common.socket.gem.GemType;
import socketed.common.socket.gem.effect.slot.SocketedSlotTypes;
import socketed.common.socket.gem.filter.ItemFilter;
import socketed.common.socket.gem.util.RandomValueRange;

import java.util.Collections;

public abstract class DefaultJsonAddons {
    public static void initializeBuiltinEntries() {
        DefaultJsonConfig.registerDefaultGemType("fire_damage", new GemType("dsd.tooltip.default.fire_damage", 3, TextFormatting.RED,
                Collections.singletonList(new DDDEffect(SocketedSlotTypes.HAND, "FIRE", new RandomValueRange(3, false))),
                Collections.singletonList(new ItemFilter("defiledlands:scarlite", 0, false))));
    }
}