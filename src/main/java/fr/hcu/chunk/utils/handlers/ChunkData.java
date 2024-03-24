package fr.hcu.chunk.utils.handlers;

public class ChunkData {
    private final int x1, z1, x2, z2;

    public ChunkData(int x1, int z1, int x2, int z2) {
        this.x1 = Math.min(x1, x2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.z2 = Math.max(z1, z2);
    }

    public int getX1() {
        return x1;
    }

    public int getZ1() {
        return z1;
    }

    public int getX2() {
        return x2;
    }

    public int getZ2() {
        return z2;
    }
}