package distinctsocketdescriptions.effect.activator;

import com.google.gson.annotations.SerializedName;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import socketed.api.socket.gem.effect.activatable.ActivatableGemEffect;
import socketed.api.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.activatable.condition.GenericCondition;

import javax.annotation.Nullable;

/**
 * Activates during the DDD DetermineDamageEvent and GatherDefensesEvent of a damage event
 */
public abstract class DDDActivator extends GenericActivator {
    @SerializedName("Directly Activated")
    protected Boolean directlyActivated;

    protected DDDActivator(@Nullable GenericCondition condition, boolean directlyActivated) {
        super(condition);
        this.directlyActivated = directlyActivated;
    }

    public boolean isDirectlyActivated() {
        return this.directlyActivated;
    }

    /**
     * @param player the player that is the source of the effect
     * @param other the other entity involved in the damage event, the attacker (Attacked) or the defender (Attacking)
     * @param directlyActivated if the source of the effect is the used weapon or the hit armor rather than the player's effect cache
     */
    public void attemptActivation(ActivatableGemEffect effect, IEffectCallback callback, EntityPlayer player, EntityLivingBase other, boolean directlyActivated) {
        if(directlyActivated != this.directlyActivated) return;
        if(this.testCondition(callback, player, other)) {
            effect.affectTargets(callback, player, other);
        }
    }

    /**
     * Directly Activated: Optional, default false
     */
    @Override
    public boolean validate() {
        if(this.directlyActivated == null) this.directlyActivated = false;
        return super.validate();
    }
}
