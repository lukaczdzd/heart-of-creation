package ru.jonaloshka.hoc.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.data.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import ru.jonaloshka.hoc.HeartOfCreationMod;
import ru.jonaloshka.hoc.core.*;

public class StoryCharacterEntity extends PathAwareEntity {
    private static final TrackedData<String> CHAR_ID = DataTracker.registerData(StoryCharacterEntity.class, TrackedDataHandlerRegistry.STRING);

    public StoryCharacterEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setCustomNameVisible(true);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CHAR_ID, "alfred");
    }

    public static DefaultAttributeContainer.Builder createCharAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 100.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ATTACK_DAMAGE, 8.0);
    }

    public void setCharacterId(String id) {
        this.dataTracker.set(CHAR_ID, id);
        CharacterProfile p = HeartOfCreationMod.getCharacterManager().getProfile(id);
        if (p != null) {
            this.setCustomName(Text.literal(p.displayName()));
            this.setInvulnerable(!p.isMainAntagonist());
        }
    }

    public String getCharacterId() { return this.dataTracker.get(CHAR_ID); }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld sw && this.age % 15 == 0) {
            SaveManager save = HeartOfCreationMod.getSaveManager();
            String id = getCharacterId();
            if ("tadmavriel".equals(id)) {
                boolean drained = save == null || !save.hasMemoryFlag("egrassel_freed");
                if (drained) {
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 2, false, false));
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, 2, false, false));
                    sw.spawnParticles(ParticleTypes.CRIMSON_SPORE, this.getX(), this.getY() + 1.0, this.getZ(), 10, 0.5, 0.8, 0.5, 0.02);
                }
            } else if ("the_first".equals(id) || "the_beginning".equals(id)) {
                sw.spawnParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.0, this.getZ(), 4, 0.4, 0.7, 0.4, 0.01);
            }
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && hand == Hand.MAIN_HAND && player instanceof ServerPlayerEntity sp) {
            SaveManager save = HeartOfCreationMod.getSaveManager();
            String id = getCharacterId();
            CharacterProfile p = HeartOfCreationMod.getCharacterManager().getProfile(id);
            if (p != null && save != null) {
                if ("egrassel".equals(id) && save.hasMemoryFlag("egrassel_seal_weakened") && !save.hasMemoryFlag("egrassel_freed")) {
                    save.addMemoryFlag("egrassel_freed");
                    save.addSparkResonance(25);
                    sp.sendMessage(Text.literal("§2§l[Эграссель освобождён!] §aПечать Тадмавриэля разрушена! Главный злодей лишился подпитки силой! (+25 Искры)"), false);
                    return ActionResult.SUCCESS;
                }
                save.modifyRelation(id, 5);
                sp.sendMessage(Text.literal(p.displayName() + " §7(" + p.title() + "): §f" + p.greetingLine()), false);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) { super.writeCustomDataToNbt(nbt); nbt.putString("CharId", getCharacterId()); }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) { super.readCustomDataFromNbt(nbt); if (nbt.contains("CharId")) setCharacterId(nbt.getString("CharId")); }
    @Override
    public boolean cannotDespawn() { return true; }
}
