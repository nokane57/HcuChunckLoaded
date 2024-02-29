package fr.hcu.chunk;

import fr.hcu.chunk.commands.ForceLoadCommand;
import fr.hcu.chunk.utils.Constants;
import fr.hcu.chunk.utils.handlers.ChunkLoadedHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION)
public class Main {

    @Mod.Instance
    public static Main instance;
    public static Logger logger;

    @Mod.EventHandler
    public static void preLoad(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public static void load(FMLInitializationEvent event) {}

    @Mod.EventHandler
    public static void postLoad(FMLPostInitializationEvent event) {}

    @SideOnly(Side.SERVER)
    @Mod.EventHandler
    public static void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new ForceLoadCommand(new ChunkLoadedHandler()));
    }

    public Main getInstance() {return instance;}
}
