package ru.jonaloshka.hoc.core;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import ru.jonaloshka.hoc.HeartOfCreationMod;
import java.util.*;

public class SaveManager extends PersistentState {
    private int currentChapter = 1;
    private String currentStoryNode = "prologue_awakening";
    private int jdhTrustLevel = 0;
    private boolean jdhMerged = false;
    private int sparkResonance = 0;
    private final Set<String> memoryFlags = new HashSet<>();
    private final Map<String, Integer> characterRelations = new HashMap<>();

    public static final Type<SaveManager> TYPE = new Type<>(SaveManager::new, SaveManager::createFromNbt, null);

    public static SaveManager getServerState(MinecraftServer server) {
        PersistentStateManager stateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        SaveManager state = stateManager.getOrCreate(TYPE, HeartOfCreationMod.MOD_ID + "_story_data");
        state.markDirty();
        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.putInt("CurrentChapter", currentChapter);
        nbt.putString("CurrentStoryNode", currentStoryNode);
        nbt.putInt("JdhTrustLevel", jdhTrustLevel);
        nbt.putBoolean("JdhMerged", jdhMerged);
        nbt.putInt("SparkResonance", sparkResonance);
        NbtCompound flagsNbt = new NbtCompound();
        int i = 0;
        for (String flag : memoryFlags) flagsNbt.putString("flag_" + i++, flag);
        nbt.put("MemoryFlags", flagsNbt);
        NbtCompound relNbt = new NbtCompound();
        for (Map.Entry<String, Integer> e : characterRelations.entrySet()) relNbt.putInt(e.getKey(), e.getValue());
        nbt.put("CharacterRelations", relNbt);
        return nbt;
    }

    public static SaveManager createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        SaveManager s = new SaveManager();
        s.currentChapter = nbt.getInt("CurrentChapter");
        s.currentStoryNode = nbt.getString("CurrentStoryNode");
        s.jdhTrustLevel = nbt.getInt("JdhTrustLevel");
        s.jdhMerged = nbt.getBoolean("JdhMerged");
        s.sparkResonance = nbt.getInt("SparkResonance");
        NbtCompound flags = nbt.getCompound("MemoryFlags");
        for (String k : flags.getKeys()) s.memoryFlags.add(flags.getString(k));
        NbtCompound rels = nbt.getCompound("CharacterRelations");
        for (String k : rels.getKeys()) s.characterRelations.put(k, rels.getInt(k));
        return s;
    }

    public int getCurrentChapter() { return currentChapter; }
    public void setCurrentChapter(int c) { this.currentChapter = c; markDirty(); }
    public String getCurrentStoryNode() { return currentStoryNode; }
    public void setCurrentStoryNode(String n) { this.currentStoryNode = n; markDirty(); }
    public int getJdhTrustLevel() { return jdhTrustLevel; }
    public void addJdhTrust(int d) { this.jdhTrustLevel = Math.max(-100, Math.min(100, this.jdhTrustLevel + d)); markDirty(); }
    public boolean isJdhMerged() { return jdhMerged; }
    public void setJdhMerged(boolean m) { this.jdhMerged = m; markDirty(); }
    public int getSparkResonance() { return sparkResonance; }
    public void addSparkResonance(int d) { this.sparkResonance = Math.max(0, Math.min(100, this.sparkResonance + d)); markDirty(); }
    public void addMemoryFlag(String f) { memoryFlags.add(f); markDirty(); }
    public boolean hasMemoryFlag(String f) { return memoryFlags.contains(f); }
    public int getRelation(String id) { return characterRelations.getOrDefault(id, 0); }
    public void modifyRelation(String id, int d) { characterRelations.put(id, getRelation(id) + d); markDirty(); }
}
