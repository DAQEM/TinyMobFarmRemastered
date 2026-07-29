package com.daqem.tinymobfarm;

import com.daqem.knot.Knot;
import com.daqem.knot.registry.Registry;
import com.daqem.knot.registry.RegistryEntry;
import com.daqem.tinymobfarm.block.TinyMobFarmBlocks;
import com.daqem.tinymobfarm.blockentity.MobFarmBlockEntity;
import com.daqem.tinymobfarm.client.gui.MobFarmMenu;
import com.daqem.tinymobfarm.config.TinyMobFarmConfig;
import com.daqem.tinymobfarm.event.MobInteractionEvent;
import com.daqem.tinymobfarm.item.TinyMobFarmItems;
import com.daqem.tinymobfarm.item.component.LassoData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class TinyMobFarm {
    public static final String MOD_ID = "tinymobfarm";
    public static final Knot API = new Knot(MOD_ID);

    public static final Registry<CreativeModeTab> TABS = Knot.REGISTRAR.createRegistry(BuiltInRegistries.CREATIVE_MODE_TAB, MOD_ID);
    public static final RegistryEntry<CreativeModeTab> TINY_MOB_FARM_TAB = TABS.register("tab", () ->
            Knot.CREATIVE_TABS_REGISTRY.build(
                    Component.translatable("itemGroup.tiny_mob_farm"),
                    () -> new ItemStack(TinyMobFarmItems.WOODEN_MOB_FARM.get())
            )
    );

    public static final Registry<MenuType<?>> MENUS = Knot.REGISTRAR.createRegistry(BuiltInRegistries.MENU, MOD_ID);
    public static final RegistryEntry<MenuType<MobFarmMenu>> MOB_FARM_CONTAINER = MENUS.register("mob_farm_menu", () -> new MenuType<>(MobFarmMenu::new, FeatureFlags.VANILLA_SET));

    public static final Registry<BlockEntityType<?>> BLOCK_ENTITIES = Knot.REGISTRAR.createRegistry(BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final RegistryEntry<BlockEntityType<MobFarmBlockEntity>> MOB_FARM_TILE_ENTITY = BLOCK_ENTITIES.register("mob_farm_block_entity", () -> new BlockEntityType<>(MobFarmBlockEntity::new, Set.of(
            TinyMobFarmBlocks.WOODEN_MOB_FARM_BLOCK.get(), TinyMobFarmBlocks.STONE_MOB_FARM_BLOCK.get(), TinyMobFarmBlocks.IRON_MOB_FARM_BLOCK.get(), TinyMobFarmBlocks.GOLD_MOB_FARM_BLOCK.get(), TinyMobFarmBlocks.DIAMOND_MOB_FARM_BLOCK.get(),
            TinyMobFarmBlocks.EMERALD_MOB_FARM_BLOCK.get(), TinyMobFarmBlocks.INFERNAL_MOB_FARM_BLOCK.get(), TinyMobFarmBlocks.ULTIMATE_MOB_FARM_BLOCK.get())));

    public static final Registry<DataComponentType<?>> COMPONENTS = Knot.REGISTRAR.createRegistry(BuiltInRegistries.DATA_COMPONENT_TYPE, MOD_ID);
    public static final RegistryEntry<DataComponentType<LassoData>> LASSO_DATA = COMPONENTS.register("lasso_data", () -> ((DataComponentType.Builder) DataComponentType.builder()).persistent(LassoData.CODEC).networkSynchronized(LassoData.STREAM_CODEC).build());


    public static void init() {
        TinyMobFarmConfig.init();
        MobInteractionEvent.registerEvent();
        TinyMobFarmBlocks.BLOCKS.register();
        TinyMobFarmItems.ITEMS.register();
        TABS.register();
        MENUS.register();
        BLOCK_ENTITIES.register();
        COMPONENTS.register();
    }

    public static MutableComponent translatable(String s) {
        return Component.translatable(MOD_ID + "." + s);
    }

    public static MutableComponent translatable(String s, Object... objects) {
        return Component.translatable(MOD_ID + "." + s, objects);
    }

    public static MutableComponent translatable(String s, ChatFormatting color) {
        return translatable(s).withStyle(color);
    }

    public static MutableComponent translatable(String s, ChatFormatting color, Object... objects) {
        return translatable(s, objects).withStyle(color);
    }

    public static Component literal(String str) {
        return Component.literal(str);
    }

    public static Identifier getId(String str) {
        return Identifier.fromNamespaceAndPath(MOD_ID, str);
    }
}