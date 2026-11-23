package ru.jonaloshka.hoc.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import ru.jonaloshka.hoc.HeartOfCreationMod;

public class JDHEntity extends PathAwareEntity {
    public JDHEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setCustomName(Text.literal("§6JDH"));
        this.setCustomNameVisible(true);
        this.setInvulnerable(true);
    }
    public static DefaultAttributeContainer.Builder createJdhAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 200.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.FOLLOW_RANGE, 48.0);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 12.0f));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && this.age % 10 == 0) {
            ((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 1.0, this.getZ(), 2, 0.25, 0.5, 0.25, 0.01);
        }
    }
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && hand == Hand.MAIN_HAND && player instanceof ServerPlayerEntity sp) {
            HeartOfCreationMod.getJdhManager().onPlayerInteractWithJdh(sp, this);
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }
    @Override
    public boolean cannotDespawn() { return true; }
}
