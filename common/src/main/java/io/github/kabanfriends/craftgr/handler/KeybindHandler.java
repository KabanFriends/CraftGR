package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.RadioStream;
import net.minecraft.client.KeyMapping;

public class KeybindHandler {

    public static KeyMapping toggleMuteKey;

    public static void tick() {
        if (CraftGR.getInstance().getMinecraft().screen == null) {
            while (toggleMuteKey.consumeClick()) {
                KeybindHandler.togglePlayback();
            }
        }
    }

    private static void togglePlayback() {
        RadioStream stream = CraftGR.getInstance().getRadioStream();
        RadioStream.State state = stream.getState();

        if (state == RadioStream.State.PLAYING) {
            stream.disconnect();
        } else if (state != RadioStream.State.CONNECTING && state != RadioStream.State.RELOADING) {
            stream.start();
        }
    }
}
