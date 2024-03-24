package fr.hcu.chunk.utils.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChunkDataHandler {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String DIRECTORY_PATH = "config/HCU/chunk";
    private static final File DIRECTORY = new File(DIRECTORY_PATH);
    private static final File dataFile = new File(DIRECTORY, "forced_chunks.json");

    static {
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdirs();
        }
    }

    public static List<ChunkData> loadChunkData() {
        List<ChunkData> chunkDataList = new ArrayList<>();
        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile)) {
                chunkDataList = Arrays.asList(gson.fromJson(reader, ChunkData[].class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return chunkDataList;
    }

    public static void saveChunkData(List<ChunkData> chunkDataList) {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(chunkDataList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearChunkData() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            writer.write("[]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}