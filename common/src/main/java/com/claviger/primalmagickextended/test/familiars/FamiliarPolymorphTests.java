package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.familiars.FamiliarBinding;
import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.claviger.primalmagickextended.common.familiars.StoredFamiliar;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.common.misc.EntitySwapper;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests that a familiar transformed by Primal Magick's Polymorph spell keeps a consistent binding when
 * the spell's queued swap-back restores its original form.
 */
public class FamiliarPolymorphTests extends AbstractBaseTest {
    private static final Vec3 PLAYER_POS = new Vec3(2.5D, 1.0D, 2.5D);
    private static final Vec3 PET_POS = new Vec3(1.5D, 1.0D, 1.5D);
    private static final Vec3 OTHER_PET_POS = new Vec3(3.5D, 1.0D, 1.5D);
    /** Polymorph length in ticks; long enough that the swap-back cannot fire before the test acts. */
    private static final int POLYMORPH_TICKS = 40;
    /** The attachment compound NeoForge writes into an entity save, and this mod's binding entry inside it. */
    private static final String ATTACHMENTS_KEY = "neoforge:attachments";
    private static final String BINDING_KEY = "primalmagickextended:familiar_binding";

    private static ServerPlayer player(GameTestHelper helper) {
        ServerPlayer player = FamiliarTestUtils.placePlayer(helper, makeMockServerPlayer(helper, true), PLAYER_POS);
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }

    private static StoredFamiliar stored(ServerPlayer player) {
        return ServicesPMX.CAPABILITIES.familiar(player).orElseThrow().getStored().orElseThrow();
    }

    /**
     * Queues a swap into a sheep on the familiar exactly as Primal Magick's Polymorph payload does: the target's
     * full save is kept as the swapper's original data, and a swap-back is queued when the swap runs. Also checks
     * that the save carries the binding under the attachment keys the manager rewrites.
     */
    private static void polymorphIntoSheep(GameTestHelper helper, Wolf wolf) {
        CompoundTag originalData = FamiliarTestUtils.snapshot(wolf);
        FamiliarBinding binding = ServicesPMX.CAPABILITIES.binding(wolf).orElseThrow();
        Optional<FamiliarBinding> saved = originalData.getCompound(ATTACHMENTS_KEY)
                .flatMap(attachments -> attachments.read(BINDING_KEY, FamiliarBinding.MAP_CODEC.codec()));
        assertValueEqual(helper, Optional.of(binding), saved, "Binding in the entity save under " + ATTACHMENTS_KEY + "/" + BINDING_KEY);
        EntitySwapper.enqueue(wolf, new EntitySwapper(EntityType.SHEEP, originalData, Optional.of(POLYMORPH_TICKS), 0));
    }

    /** Waits for the polymorphed sheep, checks it is the player's adopted familiar and stores it in the holder. */
    private static void awaitAdoptedSheep(GameTestHelper helper, ServerPlayer player, AtomicReference<Sheep> holder) {
        List<Sheep> sheep = helper.getEntities(EntityType.SHEEP);
        assertValueEqual(helper, 1, sheep.size(), "Sheep after the polymorph");
        Sheep polymorphed = sheep.get(0);
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(polymorphed).filter(b -> b.isOwnedBy(player.getUUID())).isPresent(), "Polymorphed sheep is not bound to the player");
        assertValueEqual(helper, Optional.of(polymorphed.getUUID()), stored(player).activeEntityId(), "Active familiar after the polymorph");
        holder.set(polymorphed);
    }

    public static void polymorph_swap_back_keeps_current_binding(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        polymorphIntoSheep(helper, wolf);
        AtomicReference<Sheep> sheep = new AtomicReference<>();
        AtomicReference<Wolf> restored = new AtomicReference<>();
        helper.startSequence()
                .thenWaitUntil(() -> awaitAdoptedSheep(helper, player, sheep))
                .thenExecute(() -> {
                    FamiliarManager.dismiss(player, sheep.get());
                    assertTrue(helper, sheep.get().isRemoved(), "Sheep not dismissed");
                    assertTrue(helper, FamiliarManager.summon(player), "Summon of the polymorphed familiar failed");
                })
                .thenWaitUntil(() -> {
                    List<Wolf> wolves = helper.getEntities(EntityType.WOLF);
                    assertValueEqual(helper, 1, wolves.size(), "Wolves after the swap-back");
                    assertValueEqual(helper, 0, helper.getEntities(EntityType.SHEEP).size(), "Sheep after the swap-back");
                    restored.set(wolves.get(0));
                })
                .thenExecuteAfter(25, () -> {
                    Wolf familiar = restored.get();
                    assertFalse(helper, familiar.isRemoved(), "Swapped-back familiar was discarded");
                    StoredFamiliar stored = stored(player);
                    FamiliarBinding binding = ServicesPMX.CAPABILITIES.binding(familiar).orElseThrow();
                    assertValueEqual(helper, stored.generation(), binding.generation(), "Swapped-back familiar's generation");
                    assertValueEqual(helper, Optional.of(familiar.getUUID()), stored.activeEntityId(), "Active familiar after the swap-back");
                    assertValueEqual(helper, 1, helper.getEntities(EntityType.WOLF).size(), "Wolves 25 ticks after the swap-back");
                })
                .thenSucceed();
    }

    public static void release_rewrites_queued_swapper_binding(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        polymorphIntoSheep(helper, wolf);
        AtomicReference<Sheep> sheep = new AtomicReference<>();
        AtomicReference<Wolf> newPet = new AtomicReference<>();
        AtomicReference<Wolf> restored = new AtomicReference<>();
        helper.startSequence()
                .thenWaitUntil(() -> awaitAdoptedSheep(helper, player, sheep))
                .thenExecute(() -> {
                    newPet.set(FamiliarTestUtils.tamedWolf(helper, player, OTHER_PET_POS));
                    assertTrue(helper, FamiliarManager.bind(player, newPet.get(), FamiliarTestUtils.collar()), "Binding the new pet failed");
                    assertTrue(helper, ServicesPMX.CAPABILITIES.binding(sheep.get()).isEmpty(), "Released sheep is still bound");
                })
                .thenWaitUntil(() -> {
                    assertValueEqual(helper, 0, helper.getEntities(EntityType.SHEEP).size(), "Sheep after the swap-back");
                    List<Wolf> unbound = helper.getEntities(EntityType.WOLF).stream()
                            .filter(w -> w != newPet.get())
                            .toList();
                    assertValueEqual(helper, 1, unbound.size(), "Swapped-back former familiars");
                    restored.set(unbound.get(0));
                })
                .thenExecuteAfter(25, () -> {
                    Wolf released = restored.get();
                    assertFalse(helper, released.isRemoved(), "Swapped-back former familiar was discarded");
                    assertTrue(helper, ServicesPMX.CAPABILITIES.binding(released).isEmpty(), "Swapped-back former familiar is still bound");
                    assertTrue(helper, released.isTame() && released.getOwnerReference() != null && released.getOwnerReference().getUUID().equals(player.getUUID()), "Swapped-back former familiar is not the player's pet");
                    assertValueEqual(helper, Optional.of(newPet.get().getUUID()), stored(player).activeEntityId(), "Active familiar after the swap-back");
                })
                .thenSucceed();
    }
}
