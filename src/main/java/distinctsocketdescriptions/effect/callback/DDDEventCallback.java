package distinctsocketdescriptions.effect.callback;

import net.minecraft.entity.EntityLivingBase;
import socketed.api.socket.gem.effect.activatable.callback.GenericEventCallback;
import yeelp.distinctdamagedescriptions.event.classification.DDDClassificationEvent;

import javax.annotation.Nullable;

public class DDDEventCallback<T extends DDDClassificationEvent> extends GenericEventCallback<T> {
    @Nullable
    private final EntityLivingBase attacker;

    public DDDEventCallback(T event, @Nullable EntityLivingBase attacker) {
        super(event);
        this.attacker = attacker;
    }

    /**
     * @return the living attacker whose damage is modified, prioritizing the immediate attacker, null if there is none
     */
    @Nullable
    public EntityLivingBase getAttacker() {
        return this.attacker;
    }
}
