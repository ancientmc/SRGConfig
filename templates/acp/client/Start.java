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
            f.set(null, new File("run"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Sets resource location to build directory. Hasn't been tested yet.
        try {
            Field f1 = ThreadDownloadResources.class.getDeclaredField("resourcesFolder");
            Field.setAccessible(new Field[] { f1 }, true);
            f1.set(null, new File("..\\build\\resources\\"));
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }

        Minecraft.main(args);
    }
}