package ru.jonaloshka.hoc;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.*;
import ru.jonaloshka.hoc.core.*;
import ru.jonaloshka.hoc.entity.*;
import ru.jonaloshka.hoc.registry.*;

public class HeartOfCreationMod implements ModInitializer {
    public static final String MOD_ID = "hoc";
    public static final Logger LOGGER = LoggerFactory.getLogger("HeartOfCreation");
    private static StoryManager storyManager;
    private static QuestManager questManager;
    private static DialogueManager dialogueManager;
    private static CharacterManager characterManager;
    private static CutsceneManager cutsceneManager;
    private static DimensionManager dimensionManager;
    private static JDHManager jdhManager;
    private static SaveManager saveManager;
    private static SparkManager sparkManager;
    private static DreamManager dreamManager;

    @Override
    public void onInitialize() {
        dimensionManager = new DimensionManager();
        characterManager = new CharacterManager();
        dialogueManager = new DialogueManager();
        questManager = new QuestManager();
        jdhManager = new JDHManager();
        sparkManager = new SparkManager();
        cutsceneManager = new CutsceneManager();
        storyManager = new StoryManager();
        dreamManager = new DreamManager();
        ModEntities.registerAll();
        ModItems.registerAll();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> { saveManager = SaveManager.getServerState(server); });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> { if (saveManager != null) saveManager.markDirty(); });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            storyManager.tick(server);
            jdhManager.tick(server);
            sparkManager.tick(server);
            cutsceneManager.tick(server);
        });

        EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> {
            if (entity instanceof ServerPlayerEntity sp) dreamManager.triggerDream(sp);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> registerCommands(dispatcher));
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> d) {
        d.register(CommandManager.literal("hoc")
            .then(CommandManager.literal("dream").executes(ctx -> {
                ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
                dreamManager.triggerDream(p);
                return 1;
            }))
            .then(CommandManager.literal("status").executes(ctx -> {
                ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
                SaveManager s = getSaveManager();
                p.sendMessage(Text.literal("§6=== Джоналошка: Сердце Мироздания ==="), false);
                p.sendMessage(Text.literal("§eГлава: §f" + s.getCurrentChapter() + " | §eИскра: §d" + s.getSparkResonance() + "/100"), false);
                p.sendMessage(Text.literal("§eДоверие JDH: §6" + s.getJdhTrustLevel() + "/50 | §eСлияние: §f" + (s.isJdhMerged() ? "АКТИВНО" : "Нет")), false);
                p.sendMessage(Text.literal("§eЭграссель: §f" + (s.hasMemoryFlag("egrassel_freed") ? "§aОсвобождён (Тадмавриэль ослаблен)" : "§cВ плену у Тадмавриэля")), false);
                return 1;
            }))
            .then(CommandManager.literal("buildmap").executes(ctx -> {
                ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
                buildStoryMap(p);
                return 1;
            }))
        );
    }

    private void buildStoryMap(ServerPlayerEntity p) {
        ServerWorld sw = p.getServerWorld();
        BlockPos c = p.getBlockPos();
        for (int x = -14; x <= 14; x++) {
            for (int z = -14; z <= 14; z++) {
                sw.setBlockState(c.add(x, -1, z), (Math.abs(x) == 14 || Math.abs(z) == 14) ? Blocks.CHISELED_STONE_BRICKS.getDefaultState() : Blocks.SMOOTH_QUARTZ.getDefaultState());
                for (int y = 0; y <= 5; y++) sw.setBlockState(c.add(x, y, z), Blocks.AIR.getDefaultState());
            }
        }
        sw.setBlockState(c.add(0, 0, -5), Blocks.BEACON.getDefaultState());
        sw.setBlockState(c.add(0, -1, -5), Blocks.DIAMOND_BLOCK.getDefaultState());
        // Темница Эграсселя на востоке
        for (int x = 8; x <= 12; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y <= 3; y++) {
                    if (x == 8 || x == 12 || z == -2 || z == 2) sw.setBlockState(c.add(x, y, z), Blocks.IRON_BARS.getDefaultState());
                }
            }
        }
        // Спавн JDH рядом с игроком
        JDHEntity jdh = new JDHEntity(ModEntities.JDH, sw);
        jdh.refreshPositionAndAngles(c.getX() + 2.5, c.getY(), c.getZ() + 2.5, 0, 0);
        sw.spawnEntity(jdh);

        // Спавн всех 19 персонажей по локациям
        int idx = 0;
        for (CharacterProfile prof : characterManager.getAllProfiles()) {
            StoryCharacterEntity npc = new StoryCharacterEntity(ModEntities.STORY_CHARACTER, sw);
            npc.setCharacterId(prof.id());
            if ("egrassel".equals(prof.id())) {
                npc.refreshPositionAndAngles(c.getX() + 10.5, c.getY(), c.getZ() + 0.5, 90, 0);
            } else if ("tadmavriel".equals(prof.id())) {
                npc.refreshPositionAndAngles(c.getX() + 0.5, c.getY(), c.getZ() - 11.5, 0, 0);
            } else {
                double angle = (2 * Math.PI * idx++) / 17.0;
                npc.refreshPositionAndAngles(c.getX() + Math.cos(angle) * 8.0, c.getY(), c.getZ() + Math.sin(angle) * 8.0, 0, 0);
            }
            sw.spawnEntity(npc);
        }
        p.giveItemStack(new ItemStack(ModItems.GUIDING_SKINT));
        p.sendMessage(Text.literal("§6§l[КАРТА ПОСТРОЕНА] §eСвятилище Начала, темница Эграсселя, JDH и все 19 героев призваны! Используй /hoc dream для сна или открой вкладку 12 Артефактов."), false);
    }

    public static StoryManager getStoryManager() { return storyManager; }
    public static QuestManager getQuestManager() { return questManager; }
    public static DialogueManager getDialogueManager() { return dialogueManager; }
    public static CharacterManager getCharacterManager() { return characterManager; }
    public static CutsceneManager getCutsceneManager() { return cutsceneManager; }
    public static DimensionManager getDimensionManager() { return dimensionManager; }
    public static JDHManager getJdhManager() { return jdhManager; }
    public static SaveManager getSaveManager() { return saveManager; }
    public static SparkManager getSparkManager() { return sparkManager; }
    public static DreamManager getDreamManager() { return dreamManager; }
}
