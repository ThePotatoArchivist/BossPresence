package archives.tater.bosspresence.mixin;

import archives.tater.bosspresence.BossPresence;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public abstract ServerWorld getServerWorld();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(
            method = "trySleep",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkBosses(BlockPos pos, CallbackInfoReturnable<Either<SleepFailureReason, Unit>> cir) {
        if (!BossPresence.isBossNearby(getServerWorld(), pos)) return;

        sendMessage(Text.translatable(BossPresence.BOSS_SLEEP_MESSAGE), true);
        cir.setReturnValue(Either.left(SleepFailureReason.OTHER_PROBLEM));
    }
}
