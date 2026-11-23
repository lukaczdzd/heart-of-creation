package ru.jonaloshka.hoc.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import ru.jonaloshka.hoc.HeartOfCreationMod;
import ru.jonaloshka.hoc.entity.JDHEntity;

public class ModEntities {
    public static final RegistryKey<EntityType<?>> JDH_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(HeartOfCreationMod.MOD_ID, "jdh"));
    public static final EntityType<JDHEntity> JDH = Registry.register(
            Registries.ENTITY_TYPE, JDH_KEY,
            EntityType.Builder.create(JDHEntity::new, SpawnGroup.CREATURE).dimensions(0.6f, 1.8f).maxTrackingRange(64).build(JDH_KEY)
    );
    public static void registerAll() {
        FabricDefaultAttributeRegistry.register(JDH, JDHEntity.createJdhAttributes());
    }
}
