package fr.hcu.chunk.events;

import fr.hcu.chunk.utils.handlers.ChunkData;
import fr.hcu.chunk.utils.handlers.ChunkDataHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Mod.EventBusSubscriber
public class ChunkLoadEventHandler {

    @Mod.EventHandler
    public static void onServerStarting(FMLServerStartingEvent event) {
        loadForcedChunks(event.getServer());
    }

    private static void loadForcedChunks(MinecraftServer server) {
        List<ChunkData> chunkDataList = ChunkDataHandler.loadChunkData();
        for (ChunkData chunkData : chunkDataList) {
            int startX = chunkData.getX1();
            int startZ = chunkData.getZ1();
            int endX = chunkData.getX2();
            int endZ = chunkData.getZ2();

            for (int x = startX; x <= endX; x++) {
                for (int z = startZ; z <= endZ; z++) {
                    forceChunk(server, x, z);
                }
            }
        }
    }

    private static void forceChunk(MinecraftServer server, int x, int z) {
        WorldServer world = server.getWorld(0);
        Chunk chunk = world.getChunkProvider().getLoadedChunk(x, z);
        if (chunk == null || !chunk.isLoaded()) {
            world.getChunkProvider().loadChunk(x, z);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            WorldServer world = (WorldServer) event.world;
            List<ChunkData> chunkDataList = ChunkDataHandler.loadChunkData();
            for (ChunkData chunkData : chunkDataList) {
                int startX = chunkData.getX1();
                int startZ = chunkData.getZ1();
                int endX = chunkData.getX2();
                int endZ = chunkData.getZ2();

                for (int x = startX; x <= endX; x++) {
                    for (int z = startZ; z <= endZ; z++) {
                        forceChunk(world.getMinecraftServer(), x, z);
                    }
                }
            }
        }
    }
}