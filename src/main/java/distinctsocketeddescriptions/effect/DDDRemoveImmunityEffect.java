package distinctsocketeddescriptions.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.activatable.callback.GenericEventCallback;
import socketed.common.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.common.socket.gem.effect.activatable.target.GenericTarget;
import socketed.common.socket.gem.effect.slot.ISlotType;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import javax.annotation.Nullable;
import java.util.List;

public class DDDRemoveImmunityEffect extends DDDEffect {
    public static final String TYPE_NAME = "DDD Remove Immunity";

    public DDDRemoveImmunityEffect(ISlotType slotType, GenericActivator activatorType, List<GenericTarget> targets, String typeName) {
        super(slotType, activatorType, targets, typeName);
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
    public void performEffect(@Nullable IEffectCallback callback, EntityPlayer entityPlayer, EntityLivingBase entityLivingBase) {
        if(!(callback instanceof GenericEventCallback)) return;
        if(!(((GenericEventCallback<?>) callback).getEvent() instanceof GatherDefensesEvent)) return;
        GatherDefensesEvent event = (GatherDefensesEvent) ((GenericEventCallback<?>) callback).getEvent();
        
        if(event.hasImmunity(this.damageType))
            event.removeImmunity(this.damageType);
    }
}
