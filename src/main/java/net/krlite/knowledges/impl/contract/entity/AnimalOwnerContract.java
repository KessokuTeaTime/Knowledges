package net.krlite.knowledges.impl.contract.entity;

import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.api.contract.EntityContract;
import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.krlite.knowledges.api.contract.caster.NbtStringCaster;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AnimalOwnerContract implements EntityContract {
    public static final NbtStringCaster OWNER = new NbtStringCaster("Owner");

    @Override
    public boolean isApplicableTo(Entity entity) {
        return entity instanceof Ownable;
    }

    @Override
    public void append(NbtCompound data, EntityRepresentable representable) {
        MinecraftServer server = representable.world().getServer();
        Entity entity = representable.entity();
        if (server != null && server.isHost(representable.player().getGameProfile()) && entity instanceof TameableEntity)
            return;

        if (entity instanceof Tameable tameable && tameable.getOwnerUuid() != null) {
            UUID ownerUuid = tameable.getOwnerUuid();
            KnowledgesCommon.CACHE_USERNAME.get(ownerUuid).ifPresent(name ->
                    OWNER.put(data, name)
            );
        }
    }

    @Override
    public @NotNull String partialPath() {
        return "animal_owner";
    }
}
