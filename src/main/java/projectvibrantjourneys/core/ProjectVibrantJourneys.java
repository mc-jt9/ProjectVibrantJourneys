package projectvibrantjourneys.core;

import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Features;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import projectvibrantjourneys.common.world.FeatureManager;
import projectvibrantjourneys.init.PVJBlocks;
import projectvibrantjourneys.init.PVJEvents;
import projectvibrantjourneys.init.PVJVanillaIntegration;

@Mod(ProjectVibrantJourneys.MOD_ID)
public class ProjectVibrantJourneys {
	
	public static final String MOD_ID = "projectvibrantjourneys";
	public static final Logger LOGGER = LogManager.getLogManager().getLogger(MOD_ID);
	
	public ProjectVibrantJourneys() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PVJConfig.COMMON_CONFIG);
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		
		PVJConfig.loadConfig(PVJConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("projectvibrantjourneys-common.toml"));
	}
	
	private void commonSetup(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onBiomeLoad);
		MinecraftForge.EVENT_BUS.register(new PVJEvents());
		FeatureManager.init();
	}
	
	private void clientSetup(FMLClientSetupEvent event) {
		PVJBlocks.registerRenderers();
		PVJBlocks.registerColors();
	}
	
	private void loadComplete(FMLLoadCompleteEvent event) {
		FeatureManager.init();
		PVJVanillaIntegration.init();
	}
	
	private void onBiomeLoad(BiomeLoadingEvent event) {
		RegistryKey<Biome> biome = RegistryKey.getOrCreateKey(ForgeRegistries.Keys.BIOMES, event.getName());
		Set<BiomeDictionary.Type> biomeTypes = BiomeDictionary.getTypes(biome);
		
		if(event.getCategory() == Biome.Category.NETHER) {
			if(PVJConfig.charredBones.get())
				event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_DECORATION).add(() -> FeatureManager.charredBonesFeature);
		} else if(event.getCategory() != Biome.Category.THEEND) {
			//plants
			if(event.getCategory() == Biome.Category.BEACH || event.getCategory() == Biome.Category.OCEAN && PVJConfig.seaOats.get())
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.seaOatsFeature);
			if(!hasType(biomeTypes, Type.OCEAN, Type.BEACH) && event.getCategory() != Biome.Category.DESERT && PVJConfig.cattails.get()) {
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.cattailFeature);
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.waterCattailsFeature);
			}
			
			//groundcover
			if(hasType(biomeTypes, Type.FOREST, Type.PLAINS)) {
				if(PVJConfig.twigs.get())
					event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.twigsFeature);
				
				if(PVJConfig.fallenLeaves.get())
					event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.fallenLeavesFeature);
			}
			if(hasType(biomeTypes, Type.SNOWY) && PVJConfig.iceChunks.get()) {
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.iceChunksFeature);
			}
			if(hasType(biomeTypes, Type.CONIFEROUS) && PVJConfig.pinecones.get()) {
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.pineconesFeature);
			}
			if(hasType(biomeTypes, Type.OCEAN, Type.BEACH) && PVJConfig.seashells.get()) {
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.seashellsFeature);
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.oceanSeashellsFeature);
			}
			
			if(PVJConfig.rocks.get())
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.rocksFeature);
			
			if(PVJConfig.bones.get())
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.bonesFeature);
			
			if(hasType(biomeTypes, Type.PLAINS) && PVJConfig.bushes.get()) {
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.bushFeature);
			}
			
			if(PVJConfig.barkMushrooms.get())
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.barkMushroomFeature);
			
			if(PVJConfig.cobwebs.get())
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> FeatureManager.cobwebsFeature);
			
			//other
			if(event.getCategory() != Biome.Category.DESERT
					&& event.getCategory() != Biome.Category.MESA
					&& event.getCategory() != Biome.Category.RIVER
					&& event.getCategory() != Biome.Category.OCEAN
					&& PVJConfig.moreSeagrass.get()) {
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> Features.SEAGRASS_RIVER);
			}
			if(event.getCategory() == Biome.Category.RIVER && PVJConfig.moreGrassInRivers.get()) {
				event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> Features.PATCH_GRASS_PLAIN);
			}
			if((event.getCategory() == Biome.Category.JUNGLE || hasType(biomeTypes, Type.JUNGLE)) && PVJConfig.jungleTropicalFish.get()) {
				event.getSpawns().getSpawner(EntityClassification.WATER_AMBIENT).add(new MobSpawnInfo.Spawners(EntityType.TROPICAL_FISH, 20, 1, 8));
			}
		}
	}
	
	private boolean hasType(Set<BiomeDictionary.Type> list, BiomeDictionary.Type...types) {
		for(BiomeDictionary.Type t : types) {
			if(list.contains(t)) return true;
		}
		return false;
	}
}
