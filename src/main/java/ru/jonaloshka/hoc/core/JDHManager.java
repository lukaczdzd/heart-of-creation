package ru.jonaloshka.hoc.core;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import ru.jonaloshka.hoc.HeartOfCreationMod;
import ru.jonaloshka.hoc.entity.JDHEntity;

public class JDHManager {
    private int mergeTicksRemaining = 0;

    public void tick(MinecraftServer server) {
        SaveManager save = HeartOfCreationMod.getSaveManager();
        if (save == null) return;
        if (save.isJdhMerged() && mergeTicksRemaining > 0) {
            mergeTicksRemaining--;
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 1, false, false));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, 1, false, false));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 1, false, false));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 220, 0, false, false));
                if (p.age % 5 == 0) {
                    p.getServerWorld().spawnParticles(ParticleTypes.ENCHANT, p.getX(), p.getY() + 1.0, p.getZ(), 5, 0.4, 0.8, 0.4, 0.05);
                }
            }
            if (mergeTicksRemaining == 0) {
                save.setJdhMerged(false);
                for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                    p.sendMessage(Text.literal("§6[JDH]: §fСинхронизация завершена."), false);
                    p.getServerWorld().playSound(null, p.getBlockPos(), SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1.0f, 0.8f);
                }
            }
        }
    }

    public void onPlayerInteractWithJdh(ServerPlayerEntity player, JDHEntity jdh) {
        SaveManager save = HeartOfCreationMod.getSaveManager();
        if (save == null) return;
        int trust = save.getJdhTrustLevel();
        if (player.isSneaking()) {
            if (trust >= 50) {
                ServerWorld world = player.getServerWorld();
                save.setJdhMerged(true);
                this.mergeTicksRemaining = 20 * 60;
                world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, jdh.getX(), jdh.getY() + 1, jdh.getZ(), 80, 0.5, 1.0, 0.5, 0.2);
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 0.8f, 1.5f);
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.2f, 1.0f);
                jdh.discard();
                player.sendMessage(Text.literal("§6§l[СЛИЯНИЕ АКТИВИРОВАНО] §eСознание JDH объединено с Джоналошкой!"), false);
            } else {
                player.sendMessage(Text.literal("§6[JDH]: §fНедостаточно синхронизации. (Доверие: " + trust + "/50)"), false);
            }
            return;
        }
        if (trust < 50) {
            save.addJdhTrust(10);
            player.sendMessage(Text.literal("§6[JDH]: §fЛюбопытно. Наша связь усиливается. §e(+10 доверия: " + save.getJdhTrustLevel() + "/50)"), false);
        } else {
            player.sendMessage(Text.literal("§6[JDH]: §fЯ готов к Слиянию. Нажми Shift + ПКМ."), false);
        }
    }
}
