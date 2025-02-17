package distinctsocketeddescriptions.activator;

import com.google.gson.annotations.SerializedName;
import distinctsocketeddescriptions.DistinctSocketedDescriptions;
import distinctsocketeddescriptions.compat.FirstAidCompat;
import distinctsocketeddescriptions.compat.ModLoaded;
import distinctsocketeddescriptions.effect.DDDResistanceEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import socketed.common.capabilities.effectscache.CapabilityEffectsCacheHandler;
import socketed.common.capabilities.effectscache.ICapabilityEffectsCache;
import socketed.common.capabilities.socketable.CapabilitySocketableHandler;
import socketed.common.capabilities.socketable.ICapabilitySocketable;
import socketed.common.socket.gem.effect.GenericGemEffect;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.slot.SocketedSlotTypes;
import yeelp.distinctdamagedescriptions.api.DDDAPI;
import yeelp.distinctdamagedescriptions.capability.IDDDCombatTracker;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import java.util.ArrayList;
import java.util.List;

public class DDDDefenseActivator extends GenericActivator {
    public static final String TYPE_NAME = "DDD Resistance Activator";
    @SerializedName("Directly Activated")
    protected Boolean directlyActivated;

    public DDDDefenseActivator(boolean directlyActivated) {
        this.directlyActivated = directlyActivated;
    }

    public boolean getDirectlyActivated() {
        return this.directlyActivated;
    }

    public void attemptResistanceActivation(DDDResistanceEffect effect, GatherDefensesEvent event, boolean directlyActivated) {
        //Check if direct activation is required
        if (directlyActivated != this.getDirectlyActivated()) return;
        effect.performEffect(event);
    }

    //TODO add tooltip
    @Override
    public String getTooltipString() {
        return "";
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public boolean validate() {
        //Default value for directlyActivated: false
        if (this.directlyActivated == null) {
            this.directlyActivated = false;
        }

        return true;
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onGatherDefenses(GatherDefensesEvent event) {
            //if(event.getDefender() == null) return;
            if (event.getDefender().world.isRemote) return;

            EntityLivingBase target = event.getDefender();

            //ResistanceActivator handling
            if (target instanceof EntityPlayer)
                handleDefending((EntityPlayer) target, event);
        }

        private static void handleDefending(EntityPlayer player, GatherDefensesEvent event) {
            ICapabilityEffectsCache cachedEffects = player.getCapability(CapabilityEffectsCacheHandler.CAP_EFFECTS_CACHE, null);
            if (cachedEffects == null) return;

            //Handle cached effects
            for (GenericGemEffect effect : cachedEffects.getActiveEffects()) {
                if (effect instanceof DDDResistanceEffect) {
                    DDDResistanceEffect DDDResistanceEffect = (DDDResistanceEffect) effect;
                    GenericActivator activator = DDDResistanceEffect.getActivatorType();
                    if (activator instanceof DDDDefenseActivator) {
                        DDDDefenseActivator resistanceActivator = ((DDDDefenseActivator) activator);
                        resistanceActivator.attemptResistanceActivation(DDDResistanceEffect, event, false);
                    }
                }
            }

            //Handle direct activation effects
            List<ItemStack> directActivationStacks = new ArrayList<>();

            //Get all hit body parts from DDD combat tracker -> armor classification -> armor map
            //Assumption: armor map only stores the equipment slots for body parts that actually got hit
            //-> first aid (body part that got hit) / magma blocks (feet) / falling blocks+anvil (head)
            if(!ModLoaded.isFirstAidLoaded()) {
                DDDAPI.accessor.getDDDCombatTracker(player).flatMap(IDDDCombatTracker::getArmorClassification).ifPresent(
                        ac -> ac.forEachArmorMap(
                                (entityEquipmentSlot, armorMap) ->
                                        directActivationStacks.add(player.getItemStackFromSlot(entityEquipmentSlot))
                        )
                );
            } else {
                List<EntityEquipmentSlot> affectedSlots = FirstAidCompat.getAndClearAffectedBodySlots();
                affectedSlots.forEach(slot -> directActivationStacks.add(player.getItemStackFromSlot(slot)));
            }

            //DistinctSocketedDescriptions.LOGGER.info("Hit body parts: {}",directActivationStacks.size());
            //directActivationStacks.forEach(stack -> player.sendMessage(new TextComponentString(stack.getItem().getRegistryName().toString() )));

            for(ItemStack stack : directActivationStacks) {
                ICapabilitySocketable sockets = stack.getCapability(CapabilitySocketableHandler.CAP_SOCKETABLE, null);
                if (sockets == null) return;

                for (GenericGemEffect effect : sockets.getAllActiveEffects(SocketedSlotTypes.BODY)) {
                    if (effect instanceof DDDResistanceEffect) {
                        DDDResistanceEffect DDDResistanceEffect = (DDDResistanceEffect) effect;
                        GenericActivator activator = DDDResistanceEffect.getActivatorType();
                        if (activator instanceof DDDDefenseActivator) {
                            DDDDefenseActivator resistanceActivator = ((DDDDefenseActivator) activator);
                            resistanceActivator.attemptResistanceActivation(DDDResistanceEffect, event, true);
                        }
                    }
                }
            }
        }
    }
}