package fr.hcu.chunk.events;

import fr.hcu.chunk.utils.handlers.ChunkData;
import fr.hcu.chunk.utils.handlers.ChunkDataHandler;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber
public class SetBlockEventHandler {

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        if (event.getCommand() != null && event.getCommand().getName().equals("setblock")) {
            ICommandSender sender = event.getSender();
            String[] params = event.getParameters();

            if (params.length >= 4) {
                int x = Integer.parseInt(params[0]);
                int y = Integer.parseInt(params[1]);
                int z = Integer.parseInt(params[2]);

                if (isBlockPlacementAllowed(sender.getServer(), x, y, z)) {
                    Block block = Block.getBlockFromName(params[3]);
                    if (block != null) {
                        World world = sender.getEntityWorld();
                        forceChunk(Objects.requireNonNull(world.getMinecraftServer()), x >> 4, z >> 4);
                        world.setBlockState(new BlockPos(x, y, z), block.getDefaultState(), 2);
                        sender.sendMessage(new TextComponentString("Block placed successfully."));
                    } else {
                        sender.sendMessage(new TextComponentString("Invalid block name."));
                    }
                } else {
                    sender.sendMessage(new TextComponentString("You cannot place blocks outside of forced chunks."));
                }
            }
        }
    }

    private static boolean isBlockPlacementAllowed(MinecraftServer server, int x, int y, int z) {
        List<ChunkData> chunkDataList = ChunkDataHandler.loadChunkData();
        for (ChunkData chunkData : chunkDataList) {
            if (x >= chunkData.getX1() && x <= chunkData.getX2() &&
                    z >= chunkData.getZ1() && z <= chunkData.getZ2()) {
                return true;
            }
        }
        return false;
    }

    private static void forceChunk(MinecraftServer server, int chunkX, int chunkZ) {
        server.getWorld(0).getChunkProvider().provideChunk(chunkX, chunkZ);
    }
}
