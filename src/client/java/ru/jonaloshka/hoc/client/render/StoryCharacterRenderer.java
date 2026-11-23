package ru.jonaloshka.hoc.client.render;

import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import ru.jonaloshka.hoc.entity.StoryCharacterEntity;

public class StoryCharacterRenderer extends BipedEntityRenderer<StoryCharacterEntity, PlayerEntityRenderState, PlayerEntityModel> {
    private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/player/slim/alex.png");
    public StoryCharacterRenderer(EntityRendererFactory.Context context) {
        super(context, new PlayerEntityModel(context.getPart(EntityModelLayers.PLAYER_SLIM), true), 0.5f);
    }
    @Override
    public PlayerEntityRenderState createRenderState() { return new PlayerEntityRenderState(); }
    @Override
    public Identifier getTexture(PlayerEntityRenderState state) { return SKIN; }
}
