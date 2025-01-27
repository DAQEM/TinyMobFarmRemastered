package com.daqem.tinymobfarm;

import com.daqem.yamlconfig.api.config.ConfigExtension;
import com.daqem.yamlconfig.api.config.ConfigType;
import com.daqem.yamlconfig.api.config.IConfigBuilder;
import com.daqem.yamlconfig.api.config.entry.IConfigEntry;
import com.daqem.yamlconfig.impl.config.ConfigBuilder;

public class ConfigTinyMobFarm {

	public static final IConfigEntry<Integer> lassoDurability;
	public static final IConfigEntry<Boolean> allowLassoLooting;

	public static final IConfigEntry<Double> woodFarmSpeed;
	public static final IConfigEntry<Double> stoneFarmSpeed;
	public static final IConfigEntry<Double> ironFarmSpeed;
	public static final IConfigEntry<Double> goldFarmSpeed;
	public static final IConfigEntry<Double> diamondFarmSpeed;
	public static final IConfigEntry<Double> emeraldFarmSpeed;
	public static final IConfigEntry<Double> infernoFarmSpeed;
	public static final IConfigEntry<Double> ultimateFarmSpeed;

	static {
		IConfigBuilder config = new ConfigBuilder(TinyMobFarm.MOD_ID, TinyMobFarm.MOD_ID + "_common", ConfigExtension.YAML, ConfigType.COMMON);

		config.push("lasso");

		lassoDurability = config
				.defineInteger("lassoDurability", 256, 1, Integer.MAX_VALUE)
				.withComments("The durability of the lasso.");

		allowLassoLooting = config
				.defineBoolean("allowLassoLooting", true)
				.withComments("Whether the looting enchantment will be taken into consideration when generating mob loot.");

		config.pop();

		config.push("farms");

		woodFarmSpeed = config
				.defineDouble("woodFarmSpeed", 50.0, 0.001, Double.MAX_VALUE)
				.withComments("The speed of the wood farm.");

		stoneFarmSpeed = config
				.defineDouble("stoneFarmSpeed", 40.0, 0.001, Double.MAX_VALUE)
				.withComments("The speed of the stone farm.");

		ironFarmSpeed = config
				.defineDouble("ironFarmSpeed", 30.0, 0.001, Double.MAX_VALUE)
				.withComments("The speed of the iron farm.");

		goldFarmSpeed = config
				.defineDouble("goldFarmSpeed", 20.0, 0.001, Double.MAX_VALUE)
				.withComments("The speed of the gold farm.");

		diamondFarmSpeed = config
				.defineDouble("diamondFarmSpeed", 10.0, 0.001, Double.MAX_VALUE)
				.withComments("The speed of the diamond farm.");

		emeraldFarmSpeed = config
				.defineDouble("emeraldFarmSpeed", 5.0, 0.001, Double.MAX_VALUE)
				.withComments("The speed of the emerald farm.");

		infernoFarmSpeed = config
				.defineDouble("infernoFarmSpeed", 2.5, 0.001, Double.MAX_VALUE)
				.withComments("The speed of the inferno farm.");

		ultimateFarmSpeed = config
				.defineDouble("ultimateFarmSpeed", 0.5, 0.001, Double.MAX_VALUE)
				.withComments("The speed of the ultimate farm.");

		config.pop();

		config.build();
	}

	public static void init() {
	}
}
