package com.daqem.tinymobfarm.client.gui.components;

import com.daqem.tinymobfarm.TinyMobFarm;
import com.daqem.tinymobfarm.item.component.LassoData;
import com.daqem.tinymobfarm.util.EntityHelper;
import com.daqem.uilib.gui.component.AbstractComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.extractEntityInInventoryFollowsMouse;

public class EntityComponent extends AbstractComponent {

    private final Supplier<ItemStack> itemStackSupplier;
    private LivingEntity cachedEntity;
    private LassoData cachedLassoData;
    private long lastTickTime;

    public EntityComponent(int x, int y, int width, int height, Supplier<ItemStack> itemStackSupplier) {
        super(x, y, width, height);
        this.itemStackSupplier = itemStackSupplier;
    }

    public void renderEntity(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        ItemStack lasso = itemStackSupplier.get();
        if (lasso.isEmpty()) {
            this.cachedEntity = null;
            this.cachedLassoData = null;
            return;
        }
        LassoData lassoData = lasso.get(TinyMobFarm.LASSO_DATA.get());
        if (lassoData == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        if (this.cachedEntity == null || !lassoData.equals(this.cachedLassoData)) {
            this.cachedEntity = (LivingEntity) EntityHelper.getEntityFromLasso(lasso, BlockPos.ZERO, minecraft.level);

            if (this.cachedEntity != null) {
                // The UI renders entities facing 180 degrees by default.
                // We instantly snap the entity to this rotation so it doesn't start backwards.
                float defaultRot = 180.0F;
                this.cachedEntity.setYRot(defaultRot);
                this.cachedEntity.setXRot(0.0F);
                this.cachedEntity.yBodyRot = defaultRot;
                this.cachedEntity.yHeadRot = defaultRot;
                this.cachedEntity.xRotO = defaultRot;
                this.cachedEntity.yRotO = defaultRot;
                this.cachedEntity.yBodyRotO = defaultRot;
                this.cachedEntity.yHeadRotO = defaultRot;

                double px = this.cachedEntity.getX();
                double py = this.cachedEntity.getY();
                double pz = this.cachedEntity.getZ();

                // Fast-forward the entity's AI by 20 ticks.
                // We also force its old positions to match its current position so the game
                // calculates its movement speed as 0. This stops any running animations!
                for (int i = 0; i < 20; i++) {
                    this.cachedEntity.xo = px;
                    this.cachedEntity.yo = py;
                    this.cachedEntity.zo = pz;
                    this.cachedEntity.xOld = px;
                    this.cachedEntity.yOld = py;
                    this.cachedEntity.zOld = pz;
                    this.cachedEntity.setDeltaMovement(0, 0, 0);

                    this.cachedEntity.tickCount++;
                    try {
                        this.cachedEntity.tick();
                    } catch (Exception ignored) {}

                    this.cachedEntity.setPos(px, py, pz);
                }
            }

            this.cachedLassoData = lassoData;
        }
        if (this.cachedEntity == null) return;

        // Calculate what the UI is going to force the body rotation to be based on the mouse position
        float centerX = (float)(getTotalX() + getTotalX() + getWidth()) / 2.0F;
        float centerY = (float)(getTotalY() + getTotalY() + getHeight()) / 2.0F;
        float xAngle = (float)Math.atan((centerX - mouseX) / 40.0F);
        float yAngle = (float)Math.atan((centerY - mouseY) / 40.0F);

        float targetBodyRot = 180.0F + xAngle * 20.0F;
        float targetXRot = -yAngle * 20.0F;

        // Pre-feed these rotations to the entity so multi-part entities know which way they are facing
        this.cachedEntity.yBodyRot = targetBodyRot;
        this.cachedEntity.yHeadRot = targetBodyRot;
        this.cachedEntity.setYRot(targetBodyRot);
        this.cachedEntity.setXRot(targetXRot);

        // Only tick the entity once per game tick (20 times a second) instead of every render frame.
        long currentTime = minecraft.level.getGameTime();
        if (currentTime != this.lastTickTime) {
            this.lastTickTime = currentTime;

            double px = this.cachedEntity.getX();
            double py = this.cachedEntity.getY();
            double pz = this.cachedEntity.getZ();

            // Keep forcing the old position to equal the current position so the entity
            // never thinks it is moving. This keeps it in a permanent idle animation state.
            this.cachedEntity.xo = px;
            this.cachedEntity.yo = py;
            this.cachedEntity.zo = pz;
            this.cachedEntity.xOld = px;
            this.cachedEntity.yOld = py;
            this.cachedEntity.zOld = pz;
            this.cachedEntity.setDeltaMovement(0, 0, 0);

            this.cachedEntity.tickCount++;
            try {
                this.cachedEntity.tick();
            } catch (Exception ignored) {}

            this.cachedEntity.setPos(px, py, pz);
        }

        // Calculate dynamic size based on the entity's bounding box to fit the UI space
        float maxDimension = Math.max(this.cachedEntity.getBbWidth(), this.cachedEntity.getBbHeight());
        int size = (int) (30.0F * (1.8F / Math.max(maxDimension, 1.8F)));

        extractEntityInInventoryFollowsMouse(
                guiGraphics,
                getTotalX(),
                getTotalY(),
                getTotalX() + getWidth(),
                getTotalY() + getHeight(),
                size,
                0.0625F,
                mouseX,
                mouseY,
                this.cachedEntity
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, int parentWidth, int parentHeight) {
        renderEntity(guiGraphics, mouseX, mouseY);
    }
}