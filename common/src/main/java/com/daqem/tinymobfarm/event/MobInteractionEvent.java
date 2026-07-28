package com.daqem.tinymobfarm.event;

import com.daqem.knot.events.EventResult;
import com.daqem.knot.events.EventsService;
import com.daqem.tinymobfarm.item.LassoItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MobInteractionEvent {

    public static void registerEvent() {
        EventsService.Entity.INTERACT_WITH_ENTITY.register((player, entity, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof LassoItem lassoItem && entity instanceof LivingEntity target) {
                if (lassoItem.interactMob(stack, player, target) == InteractionResult.SUCCESS) {
                    return EventResult.INTERRUPT_TRUE;
                }
            }
            return EventResult.PASS;
        });
    }
}
