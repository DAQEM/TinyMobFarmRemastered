package com.daqem.tinymobfarm.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record LassoData(String mobName, ResourceLocation mobId, CompoundTag mobData, float mobHealth, float mobMaxHealth, boolean mobHostile, ResourceLocation mobLootTableLocation) {

    public static final Codec<LassoData> CODEC = Codec.lazyInitialized(() ->
        RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("mobName").forGetter(LassoData::mobName),
                ResourceLocation.CODEC.fieldOf("mobId").forGetter(LassoData::mobId),
                CompoundTag.CODEC.fieldOf("mobData").forGetter(LassoData::mobData),
                Codec.FLOAT.fieldOf("mobHealth").forGetter(LassoData::mobHealth),
                Codec.FLOAT.fieldOf("mobMaxHealth").forGetter(LassoData::mobMaxHealth),
                Codec.BOOL.fieldOf("mobHostile").forGetter(LassoData::mobHostile),
                ResourceLocation.CODEC.fieldOf("mobLootTableLocation").forGetter(LassoData::mobLootTableLocation)
        ).apply(instance, LassoData::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, LassoData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull LassoData decode(RegistryFriendlyByteBuf buf) {
            if (buf.readBoolean()) {
                return null;
            }
            return new LassoData(
                    buf.readUtf(),
                    buf.readResourceLocation(),
                    buf.readNbt(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readResourceLocation()
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, @Nullable LassoData packet) {
            buf.writeBoolean(packet == null);
            if (packet == null) {
                return;
            }
            buf.writeUtf(packet.mobName());
            buf.writeResourceLocation(packet.mobId());
            buf.writeNbt(packet.mobData());
            buf.writeFloat(packet.mobHealth());
            buf.writeFloat(packet.mobMaxHealth());
            buf.writeBoolean(packet.mobHostile());
            buf.writeResourceLocation(packet.mobLootTableLocation());
        }
    };
}
