package fr.hcu.chunk.commands;


import fr.hcu.chunk.utils.handlers.ChunkLoadedHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.server.permission.PermissionAPI;

public class ForceLoadCommand extends CommandBase {

    private final ChunkLoadedHandler chunkHandler;
    private static final int MAX_CHUNK_LIMIT = 256;

    public ForceLoadCommand(ChunkLoadedHandler chunkHandler) {
        this.chunkHandler = chunkHandler;
    }

    @Override
    public String getName() {
        return "forceload";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/forceload add <x1> <z1> <x2> <z2>\n" + // Ajouter un carré de chunks à charger
                "/forceload remove <x1> <z1> <x2> <z2>\n" + // Supprimer un carré de chunks chargés
                "/forceload query <x> <z>\n" + // Vérifier si un chunk est chargé
                "/forceload list"; // Lister les chunks chargés
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Seul un joueur peut exécuter cette commande !"));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;

        if (!PermissionAPI.hasPermission(player, "hcu.forceload.admin")) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Vous n'avez pas la permission d'utiliser cette command !"));
            return;
        }

        if (args.length < 4 || args.length > 5) {
            throw new WrongUsageException(getUsage(player));
        }

        String action = args[0];
        ChunkPos chunkPos1;
        ChunkPos chunkPos2;

        switch (action) {
            case "add":
            case "remove":
            case "query":
                if (args.length != 3 && args.length != 5) {
                    throw new WrongUsageException(getUsage(player));
                }

                if (args.length == 3) {
                    int x = parseInt(args[1]);
                    int z = parseInt(args[2]);
                    chunkPos1 = new ChunkPos(x, z);
                    chunkPos2 = null;
                } else {
                    int x1 = parseInt(args[1]);
                    int z1 = parseInt(args[2]);
                    int x2 = parseInt(args[3]);
                    int z2 = parseInt(args[4]);

                    // Vérification de la taille du carré de chunks
                    int width = Math.abs(x2 - x1) + 1;
                    int height = Math.abs(z2 - z1) + 1;
                    if (width > MAX_CHUNK_LIMIT || height > MAX_CHUNK_LIMIT) {
                        throw new CommandException("La taille du carré de chunks ne peut pas dépasser " + MAX_CHUNK_LIMIT + " chunks.");
                    }

                    chunkPos1 = new ChunkPos(Math.min(x1, x2), Math.min(z1, z2));
                    chunkPos2 = new ChunkPos(Math.max(x1, x2), Math.max(z1, z2));
                }
                break;
            default:
                throw new WrongUsageException(getUsage(player));
        }

        switch (action) {
            case "add":
                handleAddChunk(sender, player, chunkPos1, chunkPos2);
                break;
            case "remove":
                handleRemoveChunk(sender, player, chunkPos1, chunkPos2);
                break;
            case "query":
                handleQueryChunk(sender, player, chunkPos1);
                break;
            case "list":
                handleListChunks(sender, player);
                break;
            default:
                throw new WrongUsageException(getUsage(player));
        }
    }

    private void handleAddChunk(ICommandSender sender, EntityPlayer player, ChunkPos chunkPos1, ChunkPos chunkPos2) throws CommandException {
        if (chunkPos2 == null) {
            chunkHandler.saveChunk(chunkPos1.x, chunkPos1.z);
            player.sendMessage(new TextComponentString("Chunk ajouté avec succès en (" + chunkPos1.x + ", " + chunkPos1.z + ")"));
        } else {
            for (int x = chunkPos1.x; x <= chunkPos2.x; x++) {
                for (int z = chunkPos1.z; z <= chunkPos2.z; z++) {
                    chunkHandler.saveChunk(x, z);
                }
            }
            player.sendMessage(new TextComponentString("Carré de chunks ajouté avec succès."));
        }
    }

    private void handleRemoveChunk(ICommandSender sender, EntityPlayer player, ChunkPos chunkPos1, ChunkPos chunkPos2) throws CommandException {
        if (chunkPos2 == null) {
            chunkHandler.removeChunk(chunkPos1.x, chunkPos1.z);
            player.sendMessage(new TextComponentString("Chunk supprimé avec succès en (" + chunkPos1.x + ", " + chunkPos1.z + ")"));
        } else {
            for (int x = chunkPos1.x; x <= chunkPos2.x; x++) {
                for (int z = chunkPos1.z; z <= chunkPos2.z; z++) {
                    chunkHandler.removeChunk(x, z);
                }
            }
            player.sendMessage(new TextComponentString("Carré de chunks supprimé avec succès."));
        }
    }

    private void handleQueryChunk(ICommandSender sender, EntityPlayer player, ChunkPos chunkPos) throws CommandException {
        boolean loaded = chunkHandler.isChunkLoaded(chunkPos.x, chunkPos.z);
        player.sendMessage(new TextComponentString("Le chunk en (" + chunkPos.x + ", " + chunkPos.z + ") est " + (loaded ? "chargé" : "non chargé")));
    }

    private void handleListChunks(ICommandSender sender, EntityPlayer player) throws CommandException {
        player.sendMessage(new TextComponentString("Liste des chunks chargés :"));
        for (ChunkPos chunkPos : chunkHandler.getLoadedChunks()) {
            player.sendMessage(new TextComponentString("(" + chunkPos.x + ", " + chunkPos.z + ")"));
        }
    }

}





