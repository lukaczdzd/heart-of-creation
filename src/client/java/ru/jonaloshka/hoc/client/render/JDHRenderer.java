package ru.jonaloshka.hoc.client.render;

import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import ru.jonaloshka.hoc.entity.JDHEntity;

public class JDHRenderer extends BipedEntityRenderer<JDHEntity, PlayerEntityRenderState, PlayerEntityModel> {
    private static final Identifier DEFAULT_SKIN = Identifier.ofVanilla("textures/entity/player/wide/steve.png");
    public JDHRenderer(EntityRendererFactory.Context context) {
        super(context, new PlayerEntityModel(context.getPart(EntityModelLayers.PLAYER), false), 0.5f);
    }
    @Override
    public PlayerEntityRenderState createRenderState() { return new PlayerEntityRenderState(); }
    @Override
    public Identifier getTexture(PlayerEntityRenderState state) { return DEFAULT_SKIN; }
}
