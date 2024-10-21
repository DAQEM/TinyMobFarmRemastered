package com.daqem.tinymobfarm;

import com.daqem.tinymobfarm.client.gui.MobFarmMenu;
import com.daqem.tinymobfarm.block.MobFarmBlock;
import com.daqem.tinymobfarm.event.MobInteractionEvent;
import com.daqem.tinymobfarm.item.MobFarmBlockItem;
import com.daqem.tinymobfarm.item.LassoItem;
import com.daqem.tinymobfarm.blockentity.MobFarmBlockEntity;
import com.daqem.tinymobfarm.item.component.LassoData;
import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class TinyMobFarm {
    public static final String MOD_ID = "tinymobfarm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static final Registrar<CreativeModeTab> TABS = MANAGER.get().get(Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> JOBSPLUS_TOOLS_TAB = TABS.register(getId(MOD_ID + "_tab"), () ->
            CreativeTabRegistry.create(Component.translatable("itemGroup.tiny_mob_farm"),
                    () -> new ItemStack(TinyMobFarm.WOODEN_MOB_FARM.get())));

    public static final Registrar<MenuType<?>> MENUS = MANAGER.get().get(Registries.MENU);

    public static final RegistrySupplier<MenuType<MobFarmMenu>> MOB_FARM_CONTAINER = MENUS.register(getId("mob_farm_menu"), () -> new MenuType<>(MobFarmMenu::new, FeatureFlags.VANILLA_SET));

    public static final Registrar<Block> BLOCKS = MANAGER.get().get(Registries.BLOCK);

    public static final RegistrySupplier<MobFarmBlock> WOODEN_MOB_FARM_BLOCK = BLOCKS.register(getId("wood_farm"), () -> new MobFarmBlock(MobFarmType.WOOD));
    public static final RegistrySupplier<MobFarmBlock> STONE_MOB_FARM_BLOCK = BLOCKS.register(getId("stone_farm"), () -> new MobFarmBlock(MobFarmType.STONE));
    public static final RegistrySupplier<MobFarmBlock> IRON_MOB_FARM_BLOCK = BLOCKS.register(getId("iron_farm"), () -> new MobFarmBlock(MobFarmType.IRON));
    public static final RegistrySupplier<MobFarmBlock> GOLD_MOB_FARM_BLOCK = BLOCKS.register(getId("gold_farm"), () -> new MobFarmBlock(MobFarmType.GOLD));
    public static final RegistrySupplier<MobFarmBlock> DIAMOND_MOB_FARM_BLOCK = BLOCKS.register(getId("diamond_farm"), () -> new MobFarmBlock(MobFarmType.DIAMOND));
    public static final RegistrySupplier<MobFarmBlock> EMERALD_MOB_FARM_BLOCK = BLOCKS.register(getId("emerald_farm"), () -> new MobFarmBlock(MobFarmType.EMERALD));
    public static final RegistrySupplier<MobFarmBlock> INFERNAL_MOB_FARM_BLOCK = BLOCKS.register(getId("inferno_farm"), () -> new MobFarmBlock(MobFarmType.INFERNAL));
    public static final RegistrySupplier<MobFarmBlock> ULTIMATE_MOB_FARM_BLOCK = BLOCKS.register(getId("ultimate_farm"), () -> new MobFarmBlock(MobFarmType.ULTIMATE));

    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITIES = MANAGER.get().get(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<MobFarmBlockEntity>> MOB_FARM_TILE_ENTITY = BLOCK_ENTITIES.register(getId("mob_farm_block_entity"), () -> BlockEntityType.Builder.of(MobFarmBlockEntity::new, TinyMobFarm.WOODEN_MOB_FARM_BLOCK.get(), TinyMobFarm.STONE_MOB_FARM_BLOCK.get(), TinyMobFarm.IRON_MOB_FARM_BLOCK.get(), TinyMobFarm.GOLD_MOB_FARM_BLOCK.get(), TinyMobFarm.DIAMOND_MOB_FARM_BLOCK.get(), TinyMobFarm.EMERALD_MOB_FARM_BLOCK.get(), TinyMobFarm.INFERNAL_MOB_FARM_BLOCK.get(), TinyMobFarm.ULTIMATE_MOB_FARM_BLOCK.get()).build(null));

    public static final Registrar<DataComponentType<?>> COMPONENTS = MANAGER.get().get(Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<LassoData>> LASSO_DATA = COMPONENTS.register(getId("lasso_data"), () -> ((DataComponentType.Builder) DataComponentType.builder()).persistent(LassoData.CODEC).networkSynchronized(LassoData.STREAM_CODEC).build());

    public static final Registrar<Item> ITEMS = MANAGER.get().get(Registries.ITEM);

    public static final RegistrySupplier<LassoItem> LASSO = ITEMS.register(getId("lasso"), () -> new LassoItem(new Item.Properties()));

    public static final RegistrySupplier<MobFarmBlockItem> WOODEN_MOB_FARM = ITEMS.register(getId("wood_farm"), () -> new MobFarmBlockItem(TinyMobFarm.WOODEN_MOB_FARM_BLOCK.get(), new Item.Properties()));
    public static final RegistrySupplier<MobFarmBlockItem> STONE_MOB_FARM = ITEMS.register(getId("stone_farm"), () -> new MobFarmBlockItem(TinyMobFarm.STONE_MOB_FARM_BLOCK.get(), new Item.Properties()));
    public static final RegistrySupplier<MobFarmBlockItem> IRON_MOB_FARM = ITEMS.register(getId("iron_farm"), () -> new MobFarmBlockItem(TinyMobFarm.IRON_MOB_FARM_BLOCK.get(), new Item.Properties()));
    public static final RegistrySupplier<MobFarmBlockItem> GOLD_MOB_FARM = ITEMS.register(getId("gold_farm"), () -> new MobFarmBlockItem(TinyMobFarm.GOLD_MOB_FARM_BLOCK.get(), new Item.Properties()));
    public static final RegistrySupplier<MobFarmBlockItem> DIAMOND_MOB_FARM = ITEMS.register(getId("diamond_farm"), () -> new MobFarmBlockItem(TinyMobFarm.DIAMOND_MOB_FARM_BLOCK.get(), new Item.Properties()));
    public static final RegistrySupplier<MobFarmBlockItem> EMERALD_MOB_FARM = ITEMS.register(getId("emerald_farm"), () -> new MobFarmBlockItem(TinyMobFarm.EMERALD_MOB_FARM_BLOCK.get(), new Item.Properties()));
    public static final RegistrySupplier<MobFarmBlockItem> INFERNAL_MOB_FARM = ITEMS.register(getId("inferno_farm"), () -> new MobFarmBlockItem(TinyMobFarm.INFERNAL_MOB_FARM_BLOCK.get(), new Item.Properties()));
    public static final RegistrySupplier<MobFarmBlockItem> ULTIMATE_MOB_FARM = ITEMS.register(getId("ultimate_farm"), () -> new MobFarmBlockItem(TinyMobFarm.ULTIMATE_MOB_FARM_BLOCK.get(), new Item.Properties()));

    public static void init() {
        ConfigTinyMobFarm.init();
        MobInteractionEvent.registerEvent();
    }

    public static MutableComponent translatable(String s) {
        return translatable(s, new Object[0]);
    }

    public static MutableComponent translatable(String s, Object... objects) {
        return Component.translatable(MOD_ID + "." + s, objects);
    }

    public static MutableComponent translatable(String s, ChatFormatting color) {
        MutableComponent component = translatable(s);
        component.withStyle(color);
        return component;
    }

    public static MutableComponent translatable(String s, ChatFormatting color, Object... objects) {
        MutableComponent component = translatable(s, objects);
        component.withStyle(color);
        return component;
    }

    public static Component literal(String str) {
        return Component.literal(str);
    }

    public static ResourceLocation getId(String str) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, str);
    }

}
