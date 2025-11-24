package com.daqem.tinymobfarm.event;

import com.daqem.tinymobfarm.item.LassoItem;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MobInteractionEvent {

    public static void registerEvent() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof LassoItem lassoItem && entity instanceof LivingEntity target) {
                if (lassoItem.interactMob(stack, player, target) == InteractionResult.SUCCESS) {
                    return EventResult.interruptTrue();
                }
            }
            return EventResult.pass();
        });
    }
}
