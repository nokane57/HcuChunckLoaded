package fr.hcu.chunk.utils.handlers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChunkLoadedHandler {

    private final File chunkFolder;
    private final File chunkFile;
    private JsonObject chunksJson;
    private int nextChunkNumber = -1;

    public ChunkLoadedHandler() {
        chunkFolder = new File("config/HCU/Chunk/");
        chunkFile = new File(chunkFolder, "Chunk.json");
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
    }

    private void createFileIfNeeded() {
        if (!chunkFile.exists()) {
            try {
                chunkFile.createNewFile();
                chunksJson = new JsonObject();
                saveChunkToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            chunksJson = loadChunksFromFile();
        }
    }

    private JsonObject loadChunksFromFile() {
        JsonObject jsonObject = new JsonObject();
        try (FileReader reader = new FileReader(chunkFile)) {
            JsonParser parser = new JsonParser();
            jsonObject = parser.parse(reader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void saveChunk(float x1, float y1) {
        createFileIfNeeded();
        String chunkName = "Chunk-" + nextChunkNumber;
        JsonObject chunkJson = new JsonObject();
        chunkJson.addProperty("x1", x1);
        chunkJson.addProperty("y1", y1);
        chunksJson.add(chunkName, chunkJson);
        nextChunkNumber++;
        saveChunkToFile();
    }

    public void removeChunk(float x, float y) {
        String chunkNameToRemove = findChunkName(x, y);
        if (chunkNameToRemove != null) {
            chunksJson.remove(chunkNameToRemove);
            saveChunkToFile();
        }
    }

    private String findChunkName(float x, float y) {
        for (Map.Entry<String, com.google.gson.JsonElement> entry : chunksJson.entrySet()) {
            JsonObject chunkData = entry.getValue().getAsJsonObject();
            float chunkX = chunkData.get("x1").getAsFloat();
            float chunkY = chunkData.get("y1").getAsFloat();
            if (x == chunkX && y == chunkY) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void saveChunkToFile() {
        try (FileWriter writer = new FileWriter(chunkFile)) {
            chunksJson.entrySet().stream()
                    .forEach(entry -> entry.getValue().getAsJsonObject().entrySet().removeIf(
                            e -> e.getValue().isJsonNull() || e.getValue().isJsonPrimitive() && ((JsonPrimitive) e.getValue()).getAsString().isEmpty()
                    ));
            writer.write(chunksJson.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isChunkLoaded(int x, int z) {
        String chunkName = findChunkName(x, z);
        return chunkName != null;
    }

    public Set<ChunkPos> getLoadedChunks() {
        Set<ChunkPos> loadedChunks = new HashSet<>();
        for (Map.Entry<String, JsonElement> entry : chunksJson.entrySet()) {
            JsonObject chunkData = entry.getValue().getAsJsonObject();
            int x = chunkData.get("x1").getAsInt();
            int z = chunkData.get("y1").getAsInt();
            loadedChunks.add(new ChunkPos(x, z));
        }
        return loadedChunks;
    }

    public JsonObject getChunksJson() {
        createFileIfNeeded();
        return chunksJson;
    }
}

