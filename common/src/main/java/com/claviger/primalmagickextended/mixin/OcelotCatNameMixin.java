package com.claviger.primalmagickextended.mixin;

import com.claviger.primalmagickextended.common.entities.CatVariantsPMX;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Names a cat with the Ocelot variant an ocelot. A familiar collar turns an ocelot into such a cat, which must not be
 * called "Cat"; giving it a custom name instead would show a nameplate whenever a player looks at it. A name tag still
 * takes precedence, since an entity's type name is only used when it has no custom name.
 */
@Mixin(Entity.class)
public abstract class OcelotCatNameMixin {
    @Inject(method = "getTypeName", at = @At("HEAD"), cancellable = true)
    private void primalmagickextended$getTypeName(CallbackInfoReturnable<Component> cir) {
        if ((Object) this instanceof Cat cat && cat.getVariant().value() == CatVariantsPMX.OCELOT.get()) {
            cir.setReturnValue(EntityType.OCELOT.getDescription());
        }
    }
}
