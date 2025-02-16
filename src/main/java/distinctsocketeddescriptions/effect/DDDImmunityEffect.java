package distinctsocketeddescriptions.effect;

import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.slot.ISlotType;
import socketed.common.socket.gem.util.RandomValueRange;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

//Same as DDDResistanceEffect just adding Immunity instead of resistance on GatherDefensesEvent
public class DDDImmunityEffect extends DDDResistanceEffect {
    public static final String TYPE_NAME = "DDD Immunity";

    public DDDImmunityEffect(ISlotType slotType, GenericActivator activatorType, String typeName, RandomValueRange amountRange) {
        super(slotType, activatorType, typeName, amountRange);
    }

    public DDDImmunityEffect(DDDImmunityEffect effect) {
        super(effect);
    }

    //TODO
    @Override
    public String getTooltipString(boolean b) {
        return "";
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public void performEffect(GatherDefensesEvent event) {
        if(!event.hasImmunity(type))
            event.addImmunity(type);
    }
}
