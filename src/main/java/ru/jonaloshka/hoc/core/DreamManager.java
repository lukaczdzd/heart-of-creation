package ru.jonaloshka.hoc.core;

import net.minecraft.entity.effect.*;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.text.Text;
import ru.jonaloshka.hoc.HeartOfCreationMod;

public class DreamManager {
    public void triggerDream(ServerPlayerEntity player) {
        SaveManager save = HeartOfCreationMod.getSaveManager();
        if (save == null) return;
        ServerWorld sw = player.getServerWorld();
        sw.playSound(null, player.getBlockPos(), SoundEvents.AMBIENT_CAVE.value(), SoundCategory.AMBIENT, 1.2f, 0.5f);
        sw.spawnParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 60, 1.0, 1.0, 1.0, 0.05);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 0, false, false));
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("§5§lСОН МИРОХОДЦА")));

        if (!save.hasMemoryFlag("dream_1_never")) {
            save.addMemoryFlag("dream_1_never");
            save.addSparkResonance(10);
            player.sendMessage(Text.literal("§5[Сон I — Голос в Пустоте]"), false);
            player.sendMessage(Text.literal("§8[Невер]: §7Ты снова балансируешь на грани реальностей, Джоналошка... Пустота помнит твоё имя."), false);
            player.sendMessage(Text.literal("§6[JDH]: §fНе слушай его загадки. Нам нужно понять, кто искажает потоки этого мира."), false);
        } else if (!save.hasMemoryFlag("dream_2_beginning")) {
            save.addMemoryFlag("dream_2_beginning");
            save.addSparkResonance(15);
            player.sendMessage(Text.literal("§5[Сон II — Зов Первой Матери]"), false);
            player.sendMessage(Text.literal("§f§l[Начало]: §7Моя плоть стала этим миром, но тень Церкви Очищения отравляет его кровь... Пробуди Искру."), false);
        } else if (!save.hasMemoryFlag("dream_3_egrassel")) {
            save.addMemoryFlag("dream_3_egrassel");
            save.addJdhTrust(10);
            player.sendMessage(Text.literal("§5[Сон III — Пленник Отца]"), false);
            player.sendMessage(Text.literal("§2[Видение Эграсселя]: §7Тадмавриэль заковал меня в руинах... Он выкачивает мою силу, чтобы стать непобедимым!"), false);
        } else if (!save.hasMemoryFlag("the_first_awakened")) {
            save.addMemoryFlag("the_first_awakened");
            save.addSparkResonance(20);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0, false, false));
            player.sendMessage(Text.literal("§e§l[Сон IV — Шёпот Первых]"), false);
            player.sendMessage(Text.literal("§e[Первые]: §7Мы — те, кто стоял у истоков ткани миров. Твоя Искра не принадлежит одному лишь Архею, дитя двух сознаний."), false);
            player.sendMessage(Text.literal("§e[Первые]: §7Освободи Эграсселя и сокруши гордыню Тадмавриэля — силой Первых мы укрепляем твой дух! (+20 Искры)"), false);
        } else {
            save.addSparkResonance(5);
            player.sendMessage(Text.literal("§5[Сон — Отголоски Первых и Пустоты]: §7Древние голоса укрепляют твою Искру (+5 резонанса)."), false);
        }
    }
}
