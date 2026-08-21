package com.daqem.tinymobfarm.item;

import com.daqem.knot.registry.creativetab.ItemPropertiesExtension;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.util.XpUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class XpBlockItem extends BlockItem {

    public XpBlockItem(Block block, Item.Properties properties) {
        super(block, buildProperties(properties));
    }

    private static Item.Properties buildProperties(Item.Properties properties) {
        if (properties instanceof ItemPropertiesExtension extension) {
            properties = extension.knot$tab(TinyMobFarm.TINY_MOB_FARM_TAB.getKey());
        }
        return properties.stacksTo(64);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext tooltipContext, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag) {
        Integer xp = itemStack.get(TinyMobFarm.TANK_XP_DATA.get());
        if (xp != null && xp > 0) {
            consumer.accept(TinyMobFarm.translatable("tooltip.xp_tank.points", ChatFormatting.GRAY, xp));
            consumer.accept(TinyMobFarm.translatable("tooltip.xp_tank.levels", ChatFormatting.GREEN, XpUtils.getLevelForExperience(xp)));
        }
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}
