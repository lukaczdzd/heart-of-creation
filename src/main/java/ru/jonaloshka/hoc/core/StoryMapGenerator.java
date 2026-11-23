package ru.jonaloshka.hoc.core;

import net.minecraft.block.*;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import ru.jonaloshka.hoc.HeartOfCreationMod;
import ru.jonaloshka.hoc.entity.*;
import ru.jonaloshka.hoc.registry.*;

public class StoryMapGenerator {
    public static void buildFullWorld(ServerPlayerEntity p) {
        ServerWorld sw = p.getServerWorld();
        BlockPos c = p.getBlockPos();

        // Дороги от Эшхолда ко всем 4 локациям
        for (int i = -45; i <= 50; i++) {
            for (int w = -1; w <= 1; w++) {
                sw.setBlockState(c.add(i, -1, w), Blocks.COBBLESTONE.getDefaultState());
                if (i >= -45 && i <= 45) sw.setBlockState(c.add(w, -1, i), Blocks.COBBLESTONE.getDefaultState());
            }
        }

        // 1. Эшхолд (Центр)
        fillArea(sw, c.add(-12, -1, -12), c.add(12, -1, 12), Blocks.GRASS_BLOCK);
        fillArea(sw, c.add(-12, 0, -12), c.add(12, 6, 12), Blocks.AIR);
        buildHouse(sw, c.add(-9, 0, -9), 6, 5, Blocks.SPRUCE_PLANKS, Blocks.OAK_LOG);
        buildHouse(sw, c.add(4, 0, -9), 6, 5, Blocks.COBBLESTONE, Blocks.DARK_OAK_LOG);
        sw.setBlockState(c.add(0, 0, 0), Blocks.CAMPFIRE.getDefaultState());
        sw.setBlockState(c.add(-7, 0, -7), Blocks.RED_BED.getDefaultState());
        sw.setBlockState(c.add(6, 0, -7), Blocks.ANVIL.getDefaultState());
        sw.setBlockState(c.add(7, 0, -7), Blocks.BREWING_STAND.getDefaultState());
        sw.setBlockState(c.add(2, 0, 2), Blocks.CHEST.getDefaultState());
        if (sw.getBlockEntity(c.add(2, 0, 2)) instanceof ChestBlockEntity chest) {
            chest.setStack(0, new ItemStack(ModItems.GUIDING_SKINT));
            chest.setStack(1, new ItemStack(ModItems.ANTISKINT_ELIXIR));
            chest.setStack(2, new ItemStack(ModItems.TRAVELER_SWORD));
        }

        // 2. Алтарь Первой Матери и Первых (Север Z -45)
        BlockPos north = c.add(0, 0, -45);
        fillArea(sw, north.add(-10, -1, -10), north.add(10, -1, 10), Blocks.SMOOTH_QUARTZ);
        fillArea(sw, north.add(-10, 0, -10), north.add(10, 8, 10), Blocks.AIR);
        fillArea(sw, north.add(-1, -1, -1), north.add(1, -1, 1), Blocks.GOLD_BLOCK);
        sw.setBlockState(north, Blocks.BEACON.getDefaultState());
        sw.setBlockState(north.add(-4, 0, -4), Blocks.AMETHYST_CLUSTER.getDefaultState());
        sw.setBlockState(north.add(4, 0, -4), Blocks.AMETHYST_CLUSTER.getDefaultState());

        // 3. Собор Церкви Очищения (Запад X -45)
        BlockPos west = c.add(-45, 0, 0);
        fillArea(sw, west.add(-10, -1, -8), west.add(10, -1, 8), Blocks.DEEPSLATE_TILES);
        buildHouse(sw, west.add(-8, 0, -6), 16, 12, Blocks.DEEPSLATE_BRICKS, Blocks.CRIMSON_STEM);
        sw.setBlockState(west.add(-5, 0, 0), Blocks.SOUL_LANTERN.getDefaultState());

        // 4. Цитадель Тадмавриэля и Темница Эграсселя (Восток X +50)
        BlockPos east = c.add(50, 0, 0);
        fillArea(sw, east.add(-12, -1, -12), east.add(12, -1, 12), Blocks.BLACKSTONE);
        buildHouse(sw, east.add(-10, 0, -10), 20, 20, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRYING_OBSIDIAN);
        for (int x = 3; x <= 7; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y <= 3; y++) {
                    if (x == 3 || x == 7 || z == -2 || z == 2) sw.setBlockState(east.add(x, y, z), Blocks.IRON_BARS.getDefaultState());
                }
            }
        }

        // 5. Заброшенная долина Багрянника (Юг Z +45)
        BlockPos south = c.add(0, 0, 45);
        fillArea(sw, south.add(-10, -1, -10), south.add(10, -1, 10), Blocks.CRIMSON_NYLIUM);
        fillArea(sw, south.add(-10, 0, -10), south.add(10, 6, 10), Blocks.AIR);
        sw.setBlockState(south.add(0, 0, 3), Blocks.SHROOMLIGHT.getDefaultState());

        // Спавн JDH в Эшхолде
        JDHEntity jdh = new JDHEntity(ModEntities.JDH, sw);
        jdh.refreshPositionAndAngles(c.getX() + 2.5, c.getY(), c.getZ() - 2.5, 0, 0);
        sw.spawnEntity(jdh);

        // Расстановка всех 19 героев по локациям
        int idx = 0;
        for (CharacterProfile prof : HeartOfCreationMod.getCharacterManager().getAllProfiles()) {
            StoryCharacterEntity npc = new StoryCharacterEntity(ModEntities.STORY_CHARACTER, sw);
            npc.setCharacterId(prof.id());
            BlockPos pos = switch (prof.id()) {
                case "the_beginning" -> north.add(0, 0, -3);
                case "the_first" -> north.add(-3, 0, -2);
                case "never" -> north.add(3, 0, -2);
                case "bishop_lucius" -> west.add(-3, 0, 0);
                case "godrik" -> west.add(0, 0, 3);
                case "tadmavriel" -> east.add(-4, 0, 0);
                case "egrassel" -> east.add(5, 0, 0);
                case "mrkgrshtraklar" -> south.add(0, 0, 1);
                case "vamu" -> south.add(4, 0, 3);
                default -> {
                    double a = (2 * Math.PI * idx++) / 10.0;
                    yield c.add((int)(Math.cos(a) * 6), 0, (int)(Math.sin(a) * 6));
                }
            };
            npc.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            sw.spawnEntity(npc);
        }
        p.sendMessage(Text.literal("§6§l[МИР АРХЕЯ ПОСТРОЕН] §e5 локаций готовы: Эшхолд (центр), Алтарь Начала (север), Собор Очищения (запад), Цитадель Тадмавриэля и Темница Эграсселя (восток), Долина Багрянника (юг)!"), false);
    }

    private static void fillArea(ServerWorld sw, BlockPos from, BlockPos to, Block block) {
        for (int x = Math.min(from.getX(), to.getX()); x <= Math.max(from.getX(), to.getX()); x++) {
            for (int y = Math.min(from.getY(), to.getY()); y <= Math.max(from.getY(), to.getY()); y++) {
                for (int z = Math.min(from.getZ(), to.getZ()); z <= Math.max(from.getZ(), to.getZ()); z++) {
                    sw.setBlockState(new BlockPos(x, y, z), block.getDefaultState());
                }
            }
        }
    }

    private static void buildHouse(ServerWorld sw, BlockPos origin, int w, int d, Block wall, Block pillar) {
        fillArea(sw, origin, origin.add(w, 4, d), Blocks.AIR);
        for (int x = 0; x <= w; x++) {
            for (int z = 0; z <= d; z++) {
                sw.setBlockState(origin.add(x, 4, z), wall.getDefaultState());
                for (int y = 0; y <= 3; y++) {
                    if (x == 0 || x == w || z == 0 || z == d) {
                        boolean isCorner = (x == 0 || x == w) && (z == 0 || z == d);
                        sw.setBlockState(origin.add(x, y, z), (isCorner ? pillar : wall).getDefaultState());
                    }
                }
            }
        }
        sw.setBlockState(origin.add(w / 2, 0, 0), Blocks.AIR.getDefaultState());
        sw.setBlockState(origin.add(w / 2, 1, 0), Blocks.AIR.getDefaultState());
    }
}
