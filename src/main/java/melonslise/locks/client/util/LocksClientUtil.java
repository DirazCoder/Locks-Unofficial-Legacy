/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package melonslise.locks.client.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public final class LocksClientUtil {
    private LocksClientUtil() {
    }

    public static void drawTexturedRectangle(float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float alpha) {
        float f = 1.0f / (float)textureWidth;
        float f1 = 1.0f / (float)textureHeight;
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bld = tes.getBuffer();
        bld.begin(7, DefaultVertexFormats.POSITION_TEX);
        bld.pos((double)x, (double)(y + (float)height), 0.0).tex((double)((float)u * f), (double)((float)(v + height) * f1)).endVertex();
        bld.pos((double)(x + (float)width), (double)(y + (float)height), 0.0).tex((double)((float)(u + width) * f), (double)((float)(v + height) * f1)).endVertex();
        bld.pos((double)(x + (float)width), (double)y, 0.0).tex((double)((float)(u + width) * f), (double)((float)v * f1)).endVertex();
        bld.pos((double)x, (double)y, 0.0).tex((double)((float)u * f), (double)((float)v * f1)).endVertex();
        tes.draw();
    }

    public static float lerp(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    public static double lerp(double start, double end, double progress) {
        return start + (end - start) * progress;
    }

    public static float cubicBezier1d(float anchor1, float anchor2, float progress) {
        float oneMinusP = 1.0f - progress;
        return 3.0f * oneMinusP * oneMinusP * progress * anchor1 + 3.0f * oneMinusP * progress * progress * anchor2 + progress * progress * progress;
    }
}

