package distinctsocketeddescriptions.config;

import distinctsocketeddescriptions.effect.DDDDamageEffect;
import distinctsocketeddescriptions.effect.DDDResistanceEffect;
import net.minecraft.util.text.TextFormatting;
import socketed.api.socket.gem.GemType;
import socketed.api.socket.gem.effect.slot.SocketedSlotTypes;
import socketed.api.socket.gem.util.RandomValueRange;
import socketed.common.config.DefaultJsonConfig;
import socketed.common.socket.gem.filter.ItemFilter;

import java.util.Collections;

public abstract class DefaultJsonAddons {
    public static void initializeBuiltinEntries() {
        DefaultJsonConfig.registerDefaultGemType("fire_damage", new GemType("dsd.tooltip.default.fire_damage", 3, TextFormatting.RED,
                Collections.singletonList(new DDDDamageEffect(SocketedSlotTypes.HAND, "ddd_fire", new RandomValueRange(3, false), true)),
                Collections.singletonList(new ItemFilter("defiledlands:scarlite", 0, false))));
        DefaultJsonConfig.registerDefaultGemType("fire_resistance", new GemType("dsd.tooltip.default.fire_resistance", 3, TextFormatting.RED,
                Collections.singletonList(new DDDResistanceEffect(SocketedSlotTypes.BODY, "ddd_fire", new RandomValueRange(0.5F, false), true)),
                Collections.singletonList(new ItemFilter("minecraft:magma", 0, false))));
    }
}