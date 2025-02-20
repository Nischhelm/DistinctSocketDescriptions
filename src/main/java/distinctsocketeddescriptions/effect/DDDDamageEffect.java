package distinctsocketeddescriptions.effect.determinedamage;

import distinctsocketeddescriptions.effect.DDDAmountEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.activatable.callback.GenericEventCallback;
import socketed.common.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.common.socket.gem.effect.activatable.target.GenericTarget;
import socketed.common.socket.gem.effect.slot.ISlotType;
import socketed.common.socket.gem.util.RandomValueRange;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;

import javax.annotation.Nullable;
import java.util.List;

public class DDDDamageEffect extends DDDAmountEffect {
    public static final String TYPE_NAME = "DDD Damage";

    public DDDDamageEffect(ISlotType slotType, GenericActivator activatorType, List<GenericTarget> targets, String typeName, RandomValueRange amountRange) {
        super(slotType, activatorType, targets, typeName, amountRange);
    }

    public DDDDamageEffect(DDDDamageEffect effect) {
        super(effect);
        //Instantiates amount in DDDAmountEffect constructor
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

    public DDDDamageEffect instantiate() {
        return new DDDDamageEffect(this);
    }

    @Override
    public void performEffect(@Nullable IEffectCallback callback, EntityPlayer entityPlayer, EntityLivingBase entityLivingBase) {
        if(!(callback instanceof GenericEventCallback)) return;
        if(!(((GenericEventCallback<?>) callback).getEvent() instanceof DetermineDamageEvent)) return;
        DetermineDamageEvent event = (DetermineDamageEvent) ((GenericEventCallback<?>) callback).getEvent();
        
        float currDmg = event.getDamage(damageType);
        event.setDamage(damageType, currDmg + amount);
    }
}
