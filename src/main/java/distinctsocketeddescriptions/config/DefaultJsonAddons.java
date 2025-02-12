package distinctsocketeddescriptions.config;

import distinctsocketeddescriptions.activator.DDDAttackingActivator;
import distinctsocketeddescriptions.effect.DDDDamageEffect;
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
                Collections.singletonList(new DDDDamageEffect(SocketedSlotTypes.HAND, new DDDAttackingActivator(false, true, true, false, true), "ddd_fire", new RandomValueRange(3, false))),
                Collections.singletonList(new ItemFilter("defiledlands:scarlite", 0, false))));
    }
}