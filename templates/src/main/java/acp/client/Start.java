package acp.client;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ThreadDownloadResources;

import java.io.File;
import java.lang.reflect.Field;

public class Start {
    public static void main(String[] args) {
        try {
            Field f = Minecraft.class.getDeclaredField("minecraftDir");
            Field.setAccessible(new Field[] { f }, true);
            f.set(null, new File("{mc_dir}"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Minecraft.main(args);
    }
}