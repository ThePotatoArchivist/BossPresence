package archives.tater.bosspresence;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.IntRule;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BossPresence implements ModInitializer {
	public static final String MOD_ID = "bosspresence";

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final TagKey<EntityType<?>> BOSSES_TAG = TagKey.of(RegistryKeys.ENTITY_TYPE, id("bosses"));
	public static final TagKey<Structure> BOSS_STRUCTURES_TAG = TagKey.of(RegistryKeys.STRUCTURE, id("boss_structures"));

	public static final GameRules.Key<IntRule> BOSS_DISTANCE_GAMERULE = GameRuleRegistry.register(
			id("boss_prevent_spawnpoint_distance").toString(),
			GameRules.Category.PLAYER, GameRuleFactory.createIntRule(128, 0, 256)
	);

	public static final String BOSS_SLEEP_MESSAGE = MOD_ID + ".boss_nearby.sleep";
	public static final String BOSS_ANCHOR_MESSAGE = MOD_ID + ".boss_nearby.respawn_anchor";

	public static boolean isBossNearby(ServerWorld world, BlockPos pos) {
		var distance = world.getGameRules().get(BOSS_DISTANCE_GAMERULE).get();
        if (!world.getEntitiesByClass(
                MobEntity.class,
                Box.of(pos.toCenterPos(), 2 * distance, 2 * distance, 2 * distance),
                entity -> entity.isAlive() && entity.getType().isIn(BOSSES_TAG)
        ).isEmpty()) return true;

		var structurePos = world.locateStructure(BOSS_STRUCTURES_TAG, pos, 0, false);
        if (structurePos == null) return false;

        return pos.isWithinDistance(structurePos, distance);
    }

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}
}