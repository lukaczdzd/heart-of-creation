package ru.jonaloshka.hoc.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import ru.jonaloshka.hoc.HeartOfCreationMod;
import ru.jonaloshka.hoc.item.StoryArtifactItem;
import java.util.*;

public class ModItems {
    public static final List<Item> ALL_ARTIFACTS = new ArrayList<>();
    public static final Item GUIDING_SKINT = reg("guiding_skint", "Кристалл потока Архея");
    public static final Item ELVEN_BRACERS = reg("elven_bracers", "Ослабляют печать Тадмавриэля над Эграсселем");
    public static final Item JDH_MASK = reg("jdh_mask", "Резонатор сознания Джона Дейви Харриса");
    public static final Item TRAVELER_SWORD = reg("traveler_sword", "Клинок мироходца");
    public static final Item SPARK_SHARD = reg("spark_shard", "Частица первородной Искры");
    public static final Item CHRONO_WATCH = reg("chrono_watch", "Замедляют время вокруг");
    public static final Item SEAL_OF_BEGINNING = reg("seal_of_beginning", "Оберег Начала");
    public static final Item ANTISKINT_ELIXIR = reg("antiskint_elixir", "Состав Беренгария");
    public static final Item MONTU_HAMMER = reg("montu_hammer", "Молот Монту");
    public static final Item LUNAR_BOW = reg("lunar_bow", "Лунный Лук Архея");
    public static final Item LAST_REALITY_ORB = reg("last_reality_orb", "Сфера Последней Реальности");
    public static final Item HEART_OF_CREATION = reg("heart_of_creation", "Сердце Мироздания");

    private static Item reg(String id, String lore) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(HeartOfCreationMod.MOD_ID, id));
        Item item = Registry.register(Registries.ITEM, key, new StoryArtifactItem(new Item.Settings().registryKey(key).maxCount(1).rarity(Rarity.EPIC), id, lore));
        ALL_ARTIFACTS.add(item);
        return item;
    }

    public static void registerAll() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(HeartOfCreationMod.MOD_ID, "artifacts"),
                FabricItemGroup.builder().displayName(Text.literal("§6Джоналошка: 12 Артефактов")).icon(() -> new ItemStack(HEART_OF_CREATION)).entries((c, e) -> ALL_ARTIFACTS.forEach(e::add)).build());
    }
}
