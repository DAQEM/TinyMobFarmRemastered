package com.daqem.tinymobfarm.util;

import com.daqem.tinymobfarm.ConfigTinyMobFarm;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.item.component.LassoData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;

public class EntityHelper {

    public static List<ItemStack> generateLoot(ServerLevel level, ItemStack stack) {
        Entity entity = getEntityFromLasso(stack, BlockPos.ZERO, level);
        if (entity == null) return new ArrayList<>();
        ResourceKey<LootTable> lootTableKey = entity.getType().getDefaultLootTable();
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableKey);

        LootParams.Builder builder = new LootParams.Builder(level);
        ServerPlayer daniel = FakePlayerHelper.getPlayer(level);

        Holder<Enchantment> registryReference = level.registryAccess()
                .lookup(Enchantments.LOOTING.registryKey())
                .flatMap(lookup -> lookup.get(Enchantments.LOOTING)).get();

        int lootingLevel = EnchantmentHelper.getItemEnchantmentLevel(registryReference, stack);
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        if (ConfigTinyMobFarm.allowLassoLooting.get()) {
            if (lootingLevel > 0) {
                sword.enchant(registryReference, lootingLevel);
            }
        }
        daniel.addItem(sword);

        builder.withParameter(LootContextParams.ATTACKING_ENTITY, daniel);
        builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, daniel);

        LootContextParamSet.Builder setBuilder = new LootContextParamSet.Builder();
        setBuilder.required(LootContextParams.ATTACKING_ENTITY);
        setBuilder.required(LootContextParams.LAST_DAMAGE_PLAYER);

        return lootTable.getRandomItems(builder.create(setBuilder.build()));
    }

    public static Entity getEntityFromLasso(ItemStack lasso, BlockPos pos, Level level) {
        if (!lasso.has(TinyMobFarm.LASSO_DATA.get())) return null;
        LassoData data = lasso.get(TinyMobFarm.LASSO_DATA.get());
        if (data == null) return null;
        CompoundTag mobData = data.mobData();
        ResourceLocation id = data.mobId();

        DoubleTag x = DoubleTag.valueOf(pos.getX() + 0.5);
        DoubleTag y = DoubleTag.valueOf(pos.getY());
        DoubleTag z = DoubleTag.valueOf(pos.getZ() + 0.5);
        ListTag mobPos = new ListTag();
        mobPos.add(x);
        mobPos.add(y);
        mobPos.add(z);
        mobData.put("Pos", mobPos);
        mobData.putString("id", id.toString());

        return EntityType.loadEntityRecursive(mobData, level, entity -> entity);
    }
}
