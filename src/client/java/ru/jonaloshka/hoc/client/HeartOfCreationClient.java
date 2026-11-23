package ru.jonaloshka.hoc.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import ru.jonaloshka.hoc.client.render.JDHRenderer;
import ru.jonaloshka.hoc.registry.ModEntities;

public class HeartOfCreationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.JDH, JDHRenderer::new);
    }
}
