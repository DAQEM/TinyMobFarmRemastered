package com.daqem.tinymobfarm;

import com.daqem.tinymobfarm.block.MobFarmBlock;
import com.daqem.tinymobfarm.blockentity.MobFarmBlockEntity;
import com.daqem.tinymobfarm.client.gui.MobFarmMenu;
import com.daqem.tinymobfarm.event.MobInteractionEvent;
import com.daqem.tinymobfarm.item.LassoItem;
import com.daqem.tinymobfarm.item.MobFarmBlockItem;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class TinyMobFarm {
    public static final String MOD_ID = "tinymobfarm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static final Registrar<CreativeModeTab> TABS = MANAGER.get().get(Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> TINY_MOB_FARM_TAB = TABS.register(getId(MOD_ID + "_tab"), () ->
            CreativeTabRegistry.create(Component.translatable("itemGroup.tiny_mob_farm"),
                    () -> new ItemStack(TinyMobFarm.WOODEN_MOB_FARM.get())));

    public static final Registrar<MenuType<?>> MENUS = MANAGER.get().get(Registries.MENU);

    public static final RegistrySupplier<MenuType<MobFarmMenu>> MOB_FARM_CONTAINER = MENUS.register(getId("mob_farm_menu"), () -> new MenuType<>(MobFarmMenu::new, FeatureFlags.VANILLA_SET));

    public static final Registrar<Block> BLOCKS = MANAGER.get().get(Registries.BLOCK);

    public static final RegistrySupplier<MobFarmBlock> WOODEN_MOB_FARM_BLOCK = block(getId("wood_farm"), p -> new MobFarmBlock(MobFarmType.WOOD, p));
    public static final RegistrySupplier<MobFarmBlock> STONE_MOB_FARM_BLOCK = block(getId("stone_farm"), p -> new MobFarmBlock(MobFarmType.STONE, p));
    public static final RegistrySupplier<MobFarmBlock> IRON_MOB_FARM_BLOCK = block(getId("iron_farm"), p -> new MobFarmBlock(MobFarmType.IRON, p));
    public static final RegistrySupplier<MobFarmBlock> GOLD_MOB_FARM_BLOCK = block(getId("gold_farm"), p -> new MobFarmBlock(MobFarmType.GOLD, p));
    public static final RegistrySupplier<MobFarmBlock> DIAMOND_MOB_FARM_BLOCK = block(getId("diamond_farm"), p -> new MobFarmBlock(MobFarmType.DIAMOND, p));
    public static final RegistrySupplier<MobFarmBlock> EMERALD_MOB_FARM_BLOCK = block(getId("emerald_farm"), p -> new MobFarmBlock(MobFarmType.EMERALD, p));
    public static final RegistrySupplier<MobFarmBlock> INFERNAL_MOB_FARM_BLOCK = block(getId("inferno_farm"), p -> new MobFarmBlock(MobFarmType.INFERNAL, p));
    public static final RegistrySupplier<MobFarmBlock> ULTIMATE_MOB_FARM_BLOCK = block(getId("ultimate_farm"), p -> new MobFarmBlock(MobFarmType.ULTIMATE, p));

    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITIES = MANAGER.get().get(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<MobFarmBlockEntity>> MOB_FARM_TILE_ENTITY = BLOCK_ENTITIES.register(getId("mob_farm_block_entity"), () -> new BlockEntityType<>(MobFarmBlockEntity::new, Set.of(TinyMobFarm.WOODEN_MOB_FARM_BLOCK.get(), TinyMobFarm.STONE_MOB_FARM_BLOCK.get(), TinyMobFarm.IRON_MOB_FARM_BLOCK.get(), TinyMobFarm.GOLD_MOB_FARM_BLOCK.get(), TinyMobFarm.DIAMOND_MOB_FARM_BLOCK.get(), TinyMobFarm.EMERALD_MOB_FARM_BLOCK.get(), TinyMobFarm.INFERNAL_MOB_FARM_BLOCK.get(), TinyMobFarm.ULTIMATE_MOB_FARM_BLOCK.get())));

    public static final Registrar<DataComponentType<?>> COMPONENTS = MANAGER.get().get(Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<LassoData>> LASSO_DATA = COMPONENTS.register(getId("lasso_data"), () -> ((DataComponentType.Builder) DataComponentType.builder()).persistent(LassoData.CODEC).networkSynchronized(LassoData.STREAM_CODEC).build());

    public static final Registrar<Item> ITEMS = MANAGER.get().get(Registries.ITEM);

    public static final RegistrySupplier<LassoItem> LASSO = item(getId("lasso"), LassoItem::new);

    public static final RegistrySupplier<MobFarmBlockItem> WOODEN_MOB_FARM = blockItem(getId("wood_farm"), p -> new MobFarmBlockItem(TinyMobFarm.WOODEN_MOB_FARM_BLOCK.get(), p));
    public static final RegistrySupplier<MobFarmBlockItem> STONE_MOB_FARM = blockItem(getId("stone_farm"), p -> new MobFarmBlockItem(TinyMobFarm.STONE_MOB_FARM_BLOCK.get(), p));
    public static final RegistrySupplier<MobFarmBlockItem> IRON_MOB_FARM = blockItem(getId("iron_farm"), p -> new MobFarmBlockItem(TinyMobFarm.IRON_MOB_FARM_BLOCK.get(), p));
    public static final RegistrySupplier<MobFarmBlockItem> GOLD_MOB_FARM = blockItem(getId("gold_farm"), p -> new MobFarmBlockItem(TinyMobFarm.GOLD_MOB_FARM_BLOCK.get(), p));
    public static final RegistrySupplier<MobFarmBlockItem> DIAMOND_MOB_FARM = blockItem(getId("diamond_farm"), p -> new MobFarmBlockItem(TinyMobFarm.DIAMOND_MOB_FARM_BLOCK.get(), p));
    public static final RegistrySupplier<MobFarmBlockItem> EMERALD_MOB_FARM = blockItem(getId("emerald_farm"), p -> new MobFarmBlockItem(TinyMobFarm.EMERALD_MOB_FARM_BLOCK.get(), p));
    public static final RegistrySupplier<MobFarmBlockItem> INFERNAL_MOB_FARM = blockItem(getId("inferno_farm"), p -> new MobFarmBlockItem(TinyMobFarm.INFERNAL_MOB_FARM_BLOCK.get(), p));
    public static final RegistrySupplier<MobFarmBlockItem> ULTIMATE_MOB_FARM = blockItem(getId("ultimate_farm"), p -> new MobFarmBlockItem(TinyMobFarm.ULTIMATE_MOB_FARM_BLOCK.get(), p));

    static <T extends Block> RegistrySupplier<T> block(ResourceLocation id, Function<BlockBehaviour.Properties, T> constructor) {
        return BLOCKS.register(id, () -> constructor.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id))));
    }

    static <T extends Item> RegistrySupplier<T> blockItem(ResourceLocation id, Function<Item.Properties, T> constructor) {
        return ITEMS.register(id, () -> constructor.apply(new Item.Properties().arch$tab(TINY_MOB_FARM_TAB).setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix()));
    }

    static <T extends Item> RegistrySupplier<T> item(ResourceLocation id, Function<Item.Properties, T> constructor) {
        return ITEMS.register(id, () -> constructor.apply(new Item.Properties().arch$tab(TINY_MOB_FARM_TAB).setId(ResourceKey.create(Registries.ITEM, id))));
    }

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
