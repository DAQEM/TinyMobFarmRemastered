package com.daqem.tinymobfarm.util;

import com.daqem.tinymobfarm.ConfigTinyMobFarm;
import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.item.component.LassoData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;

public class EntityHelper {

    public static List<ItemStack> generateLoot(ServerLevel level, ItemStack stack) {
        Entity entity = getEntityFromLasso(stack, BlockPos.ZERO, level);
        if (entity == null) return new ArrayList<>();

        ResourceKey<LootTable> lootTableKey = entity.getType().getDefaultLootTable();
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableKey);

        ServerPlayer daniel = FakePlayerHelper.getPlayer(level);
        if (daniel == null) {
            throw new IllegalStateException("Failed to retrieve fake player");
        }

        Holder<Enchantment> registryReference = level.registryAccess()
                .lookup(Enchantments.LOOTING.registryKey())
                .flatMap(lookup -> lookup.get(Enchantments.LOOTING))
                .orElseThrow(() -> new IllegalStateException("Looting enchantment not found"));

        int lootingLevel = EnchantmentHelper.getItemEnchantmentLevel(registryReference, stack);
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);

        if (ConfigTinyMobFarm.allowLassoLooting.get() && lootingLevel > 0) {
            sword.enchant(registryReference, lootingLevel);
        }
        daniel.addItem(sword);

        Holder<DamageType> damageTypeHolder = level.registryAccess().lookup(Registries.DAMAGE_TYPE)
                .flatMap(lookup -> lookup.get(DamageTypes.PLAYER_ATTACK))
                .orElseThrow(() -> new IllegalStateException("Damage type not found"));
        DamageSource damageSource = new DamageSource(damageTypeHolder, daniel);

        LootParams lootParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ATTACKING_ENTITY, daniel)
                .withParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, daniel)
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, daniel)
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(LootContextParams.ORIGIN, entity.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .create(LootContextParamSets.ENTITY);

        return lootTable.getRandomItems(lootParams);
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
