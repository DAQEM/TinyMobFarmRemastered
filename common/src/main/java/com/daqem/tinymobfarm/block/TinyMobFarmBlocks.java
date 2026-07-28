package com.daqem.tinymobfarm.block;

import com.daqem.knot.Knot;
import com.daqem.knot.registry.Registry;
import com.daqem.knot.registry.RegistryEntry;
import com.daqem.tinymobfarm.MobFarmType;
import com.daqem.tinymobfarm.TinyMobFarm;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public interface TinyMobFarmBlocks {

    Registry<Block> BLOCKS = Knot.REGISTRAR.createRegistry(BuiltInRegistries.BLOCK, TinyMobFarm.MOD_ID);

    RegistryEntry<MobFarmBlock> WOODEN_MOB_FARM_BLOCK = block("wood_farm", p -> new MobFarmBlock(MobFarmType.WOOD, p));
    RegistryEntry<MobFarmBlock> STONE_MOB_FARM_BLOCK = block("stone_farm", p -> new MobFarmBlock(MobFarmType.STONE, p));
    RegistryEntry<MobFarmBlock> IRON_MOB_FARM_BLOCK = block("iron_farm", p -> new MobFarmBlock(MobFarmType.IRON, p));
    RegistryEntry<MobFarmBlock> GOLD_MOB_FARM_BLOCK = block("gold_farm", p -> new MobFarmBlock(MobFarmType.GOLD, p));
    RegistryEntry<MobFarmBlock> DIAMOND_MOB_FARM_BLOCK = block("diamond_farm", p -> new MobFarmBlock(MobFarmType.DIAMOND, p));
    RegistryEntry<MobFarmBlock> EMERALD_MOB_FARM_BLOCK = block("emerald_farm", p -> new MobFarmBlock(MobFarmType.EMERALD, p));
    RegistryEntry<MobFarmBlock> INFERNAL_MOB_FARM_BLOCK = block("inferno_farm", p -> new MobFarmBlock(MobFarmType.INFERNAL, p));
    RegistryEntry<MobFarmBlock> ULTIMATE_MOB_FARM_BLOCK = block("ultimate_farm", p -> new MobFarmBlock(MobFarmType.ULTIMATE, p));

    static <T extends Block> RegistryEntry<T> block(String id, Function<BlockBehaviour.Properties, T> constructor) {
        return BLOCKS.register(id, (key) -> constructor.apply(BlockBehaviour.Properties.of().setId(key)));
    }
}
