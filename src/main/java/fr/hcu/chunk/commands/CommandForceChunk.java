package fr.hcu.chunk.commands;

import fr.hcu.chunk.utils.handlers.ChunkData;
import fr.hcu.chunk.utils.handlers.ChunkDataHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandForceChunk extends CommandBase {

    @Override
    public String getName() {
        return "forcechunk";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/forcechunk add <x1> <z1> [<x2> <z2>] | /forcechunk remove <x> <z> | /forcechunk query <x> <z> | /forcechunk removeall";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        if (!player.canUseCommand(4, "forcechunk")) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "You can not permission"));
            return;
        }

        String subCommand = args[0];
        int x, z, x2 = 0, z2 = 0;

        switch (subCommand) {
            case "add":
                if (args.length < 5) {
                    sender.sendMessage(new TextComponentString("Missing arguments. Usage: /forcechunk add <x1> <z1> <x2> <z2>"));
                    return;
                }
                try {
                    x = Integer.parseInt(args[1]);
                    z = Integer.parseInt(args[2]);
                    x2 = Integer.parseInt(args[3]);
                    z2 = Integer.parseInt(args[4]);
                    addForceChunk(sender, x, z, x2, z2);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new TextComponentString("Invalid coordinates provided."));
                }
                break;
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(new TextComponentString("Missing arguments. Usage: /forcechunk remove <x> <z>"));
                    return;
                }
                try {
                    x = Integer.parseInt(args[1]);
                    z = Integer.parseInt(args[2]);
                    removeForceChunk(sender, x, z);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new TextComponentString("Invalid coordinates provided."));
                }
                break;
            case "query":
                if (args.length < 3) {
                    sender.sendMessage(new TextComponentString("Missing arguments. Usage: /forcechunk query <x> <z>"));
                    return;
                }
                try {
                    x = Integer.parseInt(args[1]);
                    z = Integer.parseInt(args[2]);
                    queryForceChunk(sender, x, z);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new TextComponentString("Invalid coordinates provided."));
                }
                break;
            case "removeall":
                removeAllForceChunks(sender);
                break;
            default:
                sender.sendMessage(new TextComponentString("Invalid sub-command."));
                sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
    }

    private void addForceChunk(ICommandSender sender, int x1, int z1, int x2, int z2) {
        loadForcedChunks(x1, z1, x2, z2);
        List<ChunkData> chunkDataList = new ArrayList<>(ChunkDataHandler.loadChunkData());
        chunkDataList.add(new ChunkData(x1, z1, x2, z2));
        ChunkDataHandler.saveChunkData(chunkDataList);

        sender.sendMessage(new TextComponentString("Force chunk added from (" + x1 + ", " + z1 + ") to (" + x2 + ", " + z2 + ")"));
    }

    private void removeForceChunk(ICommandSender sender, int x, int z) {
        List<ChunkData> chunkDataList = new ArrayList<>(ChunkDataHandler.loadChunkData());
        chunkDataList.removeIf(chunkData -> chunkData.getX1() == x && chunkData.getZ1() == z);
        ChunkDataHandler.saveChunkData(chunkDataList);
        sender.sendMessage(new TextComponentString("Force chunk removed at (" + x + ", " + z + ")"));
    }

    private void queryForceChunk(ICommandSender sender, int x, int z) {
        List<ChunkData> chunkDataList = ChunkDataHandler.loadChunkData();
        boolean isForced = chunkDataList.stream().anyMatch(chunkData -> x >= chunkData.getX1() && x <= chunkData.getX2() &&
                z >= chunkData.getZ1() && z <= chunkData.getZ2());
        sender.sendMessage(new TextComponentString("Chunk at (" + x + ", " + z + ") is " + (isForced ? "force loaded" : "not force loaded")));
    }

    private void removeAllForceChunks(ICommandSender sender) {
        ChunkDataHandler.clearChunkData();
        sender.sendMessage(new TextComponentString("All force chunks removed."));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, net.minecraft.util.math.BlockPos targetPos) {
        if (args.length == 1) {
            return Arrays.asList("add", "remove", "query", "removeall");
        }
        return Collections.emptyList();
    }

    private void loadForcedChunks(int x1, int z1, int x2, int z2) {
        WorldServer world = DimensionManager.getWorld(0);
        if (world != null) {
            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    world.getChunkProvider().provideChunk(x, z);
                }
            }
        }
    }
}