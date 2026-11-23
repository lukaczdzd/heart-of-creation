package ru.jonaloshka.hoc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import org.slf4j.*;
import ru.jonaloshka.hoc.core.*;
import ru.jonaloshka.hoc.registry.ModEntities;

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
        ModEntities.registerAll();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> { saveManager = SaveManager.getServerState(server); });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> { if (saveManager != null) saveManager.markDirty(); });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            storyManager.tick(server);
            jdhManager.tick(server);
            sparkManager.tick(server);
            cutsceneManager.tick(server);
        });
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
}
