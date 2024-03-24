package fr.hcu.chunk;

import fr.hcu.chunk.commands.CommandForceChunk;
import fr.hcu.chunk.events.ChunkLoadEventHandler;
import fr.hcu.chunk.events.SetBlockEventHandler;
import fr.hcu.chunk.utils.Constants;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
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
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            registerClientCommands();
        }
        MinecraftForge.EVENT_BUS.register(ChunkLoadEventHandler.class);
        MinecraftForge.EVENT_BUS.register(SetBlockEventHandler.class);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        if (server.isSinglePlayer() || server.isDedicatedServer()) {
            event.registerServerCommand(new CommandForceChunk());
        }
    }

    @SideOnly(Side.CLIENT)
    private void registerClientCommands() {
        MinecraftForge.EVENT_BUS.register(new CommandForceChunk());
    }
}