package com.claviger.primalmagickextended.mixin;

import com.claviger.primalmagickextended.common.portals.IPortalTraveler;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.LevelData;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sends players travelling through a custom portal through the dimension-change flow, so they see the portal's
 * loading screen. NeoForge-only, because it mirrors NeoForge's patched dimension change. Ported from Custom Portals.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixinNeoforge extends Player {
    @Shadow
    private boolean isChangingDimension;
    @Shadow
    public ServerGamePacketListenerImpl connection;
    @Final
    @Shadow
    public MinecraftServer server;
    @Shadow
    private float lastSentHealth;
    @Shadow
    private int lastSentExp;
    @Shadow
    private int lastSentFood;

    @Shadow
    public abstract ServerLevel serverLevel();
    @Shadow
    protected abstract void triggerDimensionChangeTriggers(ServerLevel oldLevel);
    @Shadow
    public abstract void setServerLevel(ServerLevel level);
    @Shadow
    public abstract CommonPlayerSpawnInfo createCommonSpawnInfo(ServerLevel level);

    public ServerPlayerMixinNeoforge(MinecraftServer minecraftServer, ServerLevel serverWorld, GameProfile gameProfile) {
        super(serverWorld, serverWorld.getSharedSpawnPos(), 0.0F, gameProfile);
    }

    /*
     * Custom-portal teleports always go through the dimension-change flow (respawn packet, loading screen),
     * even within one dimension, where vanilla would only move the player, and play the travel sound. The body
     * mirrors NeoForge's patched ServerPlayer.changeDimension for the cross-dimension branch, including NeoForge's
     * travel event, revive() and changed-dimension event, and re-sends the familiar data. It also calls
     * stopUsingItem(), which 1.21.1's changeDimension does not, for parity with the 26.1 version. Like the original
     * Custom Portals, it skips the transition's post-teleport callback and the nether-entry bookkeeping.
     *
     * ServerPlayer overrides Entity.changeDimension with the same return type, so there is no bridge method.
     */
    @Inject(
            method = "changeDimension(Lnet/minecraft/world/level/portal/DimensionTransition;)Lnet/minecraft/world/entity/Entity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void primalmagickextended$changeDimension(DimensionTransition transition, CallbackInfoReturnable<Entity> cir) {
        if (!((IPortalTraveler) this).primalmagickextended$isInCustomPortal()) {
            return;
        }
        ServerPlayer thisPlayer = (ServerPlayer) (Object) this;

        if (!CommonHooks.onTravelToDimension(thisPlayer, transition.newLevel().dimension())) {
            cir.setReturnValue(null);
            return;
        }
        if (this.isRemoved()) {
            cir.setReturnValue(null);
            return;
        }

        if (transition.missingRespawnBlock()) {
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
        }

        ServerLevel newLevel = transition.newLevel();
        ServerLevel oldLevel = this.serverLevel();
        ResourceKey<Level> lastDimension = oldLevel.dimension();
        // 1.21.1 transitions cannot ask to keep the vehicle, and portal travellers never ride one
        this.removeVehicle();

        this.isChangingDimension = true;
        LevelData levelData = newLevel.getLevelData();
        this.connection.send(new ClientboundRespawnPacket(this.createCommonSpawnInfo(newLevel), (byte) 3));
        this.connection.send(new ClientboundChangeDifficultyPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
        PlayerList playerList = this.server.getPlayerList();
        playerList.sendPlayerPermissionLevel(thisPlayer);
        oldLevel.removePlayerImmediately(thisPlayer, Entity.RemovalReason.CHANGED_DIMENSION);
        this.revive();
        ProfilerFiller profiler = oldLevel.getProfiler();
        profiler.push("moving");
        profiler.pop();
        profiler.push("placing");
        this.setServerLevel(newLevel);
        this.connection.teleport(transition.pos().x, transition.pos().y, transition.pos().z, transition.yRot(), transition.xRot());
        this.connection.resetPosition();
        newLevel.addDuringTeleport(thisPlayer);
        profiler.pop();
        this.triggerDimensionChangeTriggers(oldLevel);
        this.stopUsingItem();
        this.connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
        playerList.sendLevelInfo(thisPlayer, newLevel);
        playerList.sendAllPlayerInfo(thisPlayer);
        playerList.sendActivePlayerEffects(thisPlayer);
        // Always sent; a client that mutes teleport sounds drops it (see LevelRendererMixin).
        this.connection.send(new ClientboundLevelEventPacket(LevelEvent.SOUND_PORTAL_TRAVEL, BlockPos.ZERO, 0, false));
        this.lastSentExp = -1;
        this.lastSentHealth = -1.0F;
        this.lastSentFood = -1;
        // The respawn packet gives the client a new player without the familiar data, so re-send it even within one
        // dimension, where no changed-dimension event fires.
        ServicesPMX.CAPABILITIES.syncFamiliar(thisPlayer);
        if (newLevel.dimension() != lastDimension) {
            EventHooks.firePlayerChangedDimensionEvent(thisPlayer, lastDimension, newLevel.dimension());
        }
        cir.setReturnValue(thisPlayer);
    }
}
