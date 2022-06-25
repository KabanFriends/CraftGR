package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "stop()V", at = @At("HEAD"))
    public void onClientStop(CallbackInfo ci) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        if (handler.isPlaying()) handler.stopPlayback();

        try {
            CraftGR.getHttpClient().close();
        } catch (IOException e)  {
            CraftGR.log(Level.ERROR, "Error while closing the http client!");
            e.printStackTrace();
        }
    }

}
