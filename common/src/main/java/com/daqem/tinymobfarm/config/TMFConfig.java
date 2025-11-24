package com.daqem.tinymobfarm.config;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.yamlconfig.YamlConfigExpectPlatform;
import com.daqem.yamlconfig.api.config.ConfigExtension;
import com.daqem.yamlconfig.api.config.ConfigType;
import com.daqem.yamlconfig.api.config.IConfigBuilder;
import com.daqem.yamlconfig.api.config.entry.IConfigEntry;
import com.daqem.yamlconfig.impl.config.ConfigBuilder;

import java.util.List;

public class TMFConfig {

    public static final IConfigEntry<Integer> lassoDurability;
    public static final IConfigEntry<Boolean> allowLassoLooting;

    // Farm Speeds
    public static final IConfigEntry<Double> woodFarmSpeed;
    public static final IConfigEntry<Double> stoneFarmSpeed;
    public static final IConfigEntry<Double> ironFarmSpeed;
    public static final IConfigEntry<Double> goldFarmSpeed;
    public static final IConfigEntry<Double> diamondFarmSpeed;
    public static final IConfigEntry<Double> emeraldFarmSpeed;
    public static final IConfigEntry<Double> infernoFarmSpeed;
    public static final IConfigEntry<Double> ultimateFarmSpeed;

    // Farm Enabled Toggles
    public static final IConfigEntry<Boolean> woodFarmEnabled;
    public static final IConfigEntry<Boolean> stoneFarmEnabled;
    public static final IConfigEntry<Boolean> ironFarmEnabled;
    public static final IConfigEntry<Boolean> goldFarmEnabled;
    public static final IConfigEntry<Boolean> diamondFarmEnabled;
    public static final IConfigEntry<Boolean> emeraldFarmEnabled;
    public static final IConfigEntry<Boolean> infernoFarmEnabled;
    public static final IConfigEntry<Boolean> ultimateFarmEnabled;

    // Farm Can Capture Hostile Mobs Toggles
    public static final IConfigEntry<Boolean> woodFarmAllowsHostile;
    public static final IConfigEntry<Boolean> stoneFarmAllowsHostile;
    public static final IConfigEntry<Boolean> ironFarmAllowsHostile;
    public static final IConfigEntry<Boolean> goldFarmAllowsHostile;
    public static final IConfigEntry<Boolean> diamondFarmAllowsHostile;
    public static final IConfigEntry<Boolean> emeraldFarmAllowsHostile;
    public static final IConfigEntry<Boolean> infernoFarmAllowsHostile;
    public static final IConfigEntry<Boolean> ultimateFarmAllowsHostile;

    public static final IConfigEntry<List<String>> blacklistedMobs;

    static {
        IConfigBuilder builder = new ConfigBuilder(
                TinyMobFarm.MOD_ID,
                TinyMobFarm.MOD_ID + "_common",
                ConfigExtension.YAML,
                ConfigType.COMMON,
                YamlConfigExpectPlatform.getConfigDirectory().resolve(TinyMobFarm.MOD_ID)
        );

        builder.push("lasso");
        lassoDurability = builder.defineInteger("lassoDurability", 256, 1, Integer.MAX_VALUE)
                .withComments("The durability of the lasso.");
        allowLassoLooting = builder.defineBoolean("allowLassoLooting", true)
                .withComments("Whether looting works on generated loot.");
        builder.pop();

        builder.push("farms");

        // Wood
        builder.push("wood");
        woodFarmSpeed = builder.defineDouble("speed", 50.0, 0.001, Double.MAX_VALUE);
        woodFarmEnabled = builder.defineBoolean("enabled", true);
        woodFarmAllowsHostile = builder.defineBoolean("allows_hostile", false);
        builder.pop();

        // Stone
        builder.push("stone");
        stoneFarmSpeed = builder.defineDouble("speed", 40.0, 0.001, Double.MAX_VALUE);
        stoneFarmEnabled = builder.defineBoolean("enabled", true);
        stoneFarmAllowsHostile = builder.defineBoolean("allows_hostile", false);
        builder.pop();

        // Iron
        builder.push("iron");
        ironFarmSpeed = builder.defineDouble("speed", 30.0, 0.001, Double.MAX_VALUE);
        ironFarmEnabled = builder.defineBoolean("enabled", true);
        ironFarmAllowsHostile = builder.defineBoolean("allows_hostile", true);
        builder.pop();

        // Gold
        builder.push("gold");
        goldFarmSpeed = builder.defineDouble("speed", 20.0, 0.001, Double.MAX_VALUE);
        goldFarmEnabled = builder.defineBoolean("enabled", true);
        goldFarmAllowsHostile = builder.defineBoolean("allows_hostile", true);
        builder.pop();

        // Diamond
        builder.push("diamond");
        diamondFarmSpeed = builder.defineDouble("speed", 10.0, 0.001, Double.MAX_VALUE);
        diamondFarmEnabled = builder.defineBoolean("enabled", true);
        diamondFarmAllowsHostile = builder.defineBoolean("allows_hostile", true);
        builder.pop();

        // Emerald
        builder.push("emerald");
        emeraldFarmSpeed = builder.defineDouble("speed", 5.0, 0.001, Double.MAX_VALUE);
        emeraldFarmEnabled = builder.defineBoolean("enabled", true);
        emeraldFarmAllowsHostile = builder.defineBoolean("allows_hostile", true);
        builder.pop();

        // Inferno
        builder.push("inferno");
        infernoFarmSpeed = builder.defineDouble("speed", 2.5, 0.001, Double.MAX_VALUE);
        infernoFarmEnabled = builder.defineBoolean("enabled", true);
        infernoFarmAllowsHostile = builder.defineBoolean("allows_hostile", true);
        builder.pop();

        // Ultimate
        builder.push("ultimate");
        ultimateFarmSpeed = builder.defineDouble("speed", 0.5, 0.001, Double.MAX_VALUE);
        ultimateFarmEnabled = builder.defineBoolean("enabled", true);
        ultimateFarmAllowsHostile = builder.defineBoolean("allows_hostile", true);
        builder.pop();

        builder.pop(); // End farms

        builder.push("blacklist");
        blacklistedMobs = builder.defineStringList("blacklisted_mobs", List.of(
                "minecraft:ender_dragon",
                "minecraft:wither",
                "minecraft:warden"
        )).withComments("A list of mob IDs that cannot be captured by lassos.");
        builder.pop();

        builder.build();
    }

    public static void init() {
    }
}
