package distinctsocketdescriptions.effect.activator;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import socketed.api.socket.gem.effect.activatable.condition.GenericCondition;

import javax.annotation.Nullable;

public class DDDAttackedActivator extends DDDActivator {
    public static final String TYPE_NAME = "DDD Attacked";

    public DDDAttackedActivator(@Nullable GenericCondition condition, boolean directlyActivated) {
        super(condition, directlyActivated);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getTooltipString() {
        return I18n.format("socketed.tooltip.activator.attacked");
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }
}
