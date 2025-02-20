package distinctsocketeddescriptions.config;

import distinctsocketeddescriptions.activator.DDDAttackingActivator;
import distinctsocketeddescriptions.activator.DDDAttackedActivator;
import distinctsocketeddescriptions.effect.determinedamage.DDDDamageEffect;
import distinctsocketeddescriptions.effect.DDDResistanceEffect;
import net.minecraft.util.text.TextFormatting;
import socketed.common.config.DefaultJsonConfig;
import socketed.common.socket.gem.GemType;
import socketed.common.socket.gem.effect.activatable.target.SelfTarget;
import socketed.common.socket.gem.effect.slot.SocketedSlotTypes;
import socketed.common.socket.gem.filter.ItemFilter;
import socketed.common.socket.gem.util.RandomValueRange;

import java.util.Collections;

public abstract class DefaultJsonAddons {
    public static void initializeBuiltinEntries() {
        DefaultJsonConfig.registerDefaultGemType("fire_damage", new GemType("dsd.tooltip.default.fire_damage", 3, TextFormatting.RED,
                Collections.singletonList(new DDDDamageEffect(SocketedSlotTypes.HAND, new DDDAttackingActivator(null, true), Collections.singletonList(new SelfTarget(null)), "ddd_fire", new RandomValueRange(3, false))),
                Collections.singletonList(new ItemFilter("defiledlands:scarlite", 0, false))));
        DefaultJsonConfig.registerDefaultGemType("fire_resistance", new GemType("dsd.tooltip.default.fire_resistance", 3, TextFormatting.RED,
                Collections.singletonList(new DDDResistanceEffect(SocketedSlotTypes.BODY, new DDDAttackedActivator(null, true), Collections.singletonList(new SelfTarget(null)), "ddd_fire", new RandomValueRange(0.5F, false))),
                Collections.singletonList(new ItemFilter("minecraft:magma", 0, false))));
    }
}