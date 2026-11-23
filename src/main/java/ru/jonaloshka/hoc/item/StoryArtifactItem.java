package ru.jonaloshka.hoc.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import ru.jonaloshka.hoc.HeartOfCreationMod;
import ru.jonaloshka.hoc.core.SaveManager;
import java.util.List;

public class StoryArtifactItem extends Item {
    private final String artifactId;
    private final String loreLine;

    public StoryArtifactItem(Settings settings, String artifactId, String loreLine) {
        super(settings);
        this.artifactId = artifactId;
        this.loreLine = loreLine;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ServerWorld sw = player.getServerWorld();
            SaveManager save = HeartOfCreationMod.getSaveManager();
            player.getItemCooldownManager().set(stack, 80);
            sw.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.2f, 1.2f);
            sw.spawnParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(), 25, 0.5, 0.8, 0.5, 0.08);

            switch (artifactId) {
                case "guiding_skint" -> { player.clearStatusEffects(); player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 2)); player.sendMessage(Text.literal("§e[Путеводный Скинт]: §fПотоки Архея очистили разум."), false); }
                case "elven_bracers" -> { if (save != null) save.addMemoryFlag("egrassel_seal_weakened"); player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 1)); player.sendMessage(Text.literal("§2[Эльфийские наручи]: §fБарьер Тадмавриэля ослаблен!"), false); }
                case "jdh_mask" -> { if (save != null) save.addJdhTrust(15); player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600, 0)); player.sendMessage(Text.literal("§6[Маска JDH]: §fСинхронизация усилена (+15 доверия)."), false); }
                case "traveler_sword" -> { Vec3d look = player.getRotationVector().multiply(1.8); player.addVelocity(look.x, 0.35, look.z); player.velocityModified = true; player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 1)); player.sendMessage(Text.literal("§b[Меч Путешественника]: §fРывок сквозь пространство!"), false); }
                case "spark_shard" -> { if (save != null) save.addSparkResonance(20); player.heal(10.0f); player.sendMessage(Text.literal("§d[Осколок Искры]: §fРезонанс Искры вырос (+20)."), false); }
                case "chrono_watch" -> { for (LivingEntity e : sw.getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(10), e -> e != player)) e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 160, 3)); player.sendMessage(Text.literal("§3[Хроно-часы]: §fВремя вокруг застыло."), false); }
                case "seal_of_beginning" -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 400, 2)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 160, 1)); player.sendMessage(Text.literal("§f§l[Печать Начала]: §fЩит Первой Матери активирован."), false); }
                case "antiskint_elixir" -> { player.clearStatusEffects(); player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0)); player.sendMessage(Text.literal("§a[Антискинт Беренгария]: §fЗаражение снято."), false); }
                case "montu_hammer" -> { for (LivingEntity e : sw.getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(6), e -> e != player)) e.damage(sw, sw.getDamageSources().magic(), 8.0f); player.sendMessage(Text.literal("§6[Молот Монту]: §fУдарная волна!"), false); }
                case "lunar_bow" -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 300, 0)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 1)); player.sendMessage(Text.literal("§9[Лунный Лук]: §fТень ночи скрыла тебя."), false); }
                case "last_reality_orb" -> { for (LivingEntity e : sw.getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(8), e -> e != player)) e.damage(sw, sw.getDamageSources().magic(), 12.0f); player.sendMessage(Text.literal("§5[Сфера Последней Реальности]: §fВыброс Пустоты!"), false); }
                case "heart_of_creation" -> { player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 400, 2)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 400, 2)); player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 2)); player.sendMessage(Text.literal("§6§l[СЕРДЦЕ МИРОЗДАНИЯ]: §eАбсолютная сила Искры пробуждена!"), false); }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§7" + loreLine));
    }
}
