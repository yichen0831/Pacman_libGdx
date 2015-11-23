package com.ychstudio.ai.astar;

public class AStarMap {

    public static final int EMPTY = 0;
    public static final int SOURCE = 1;
    public static final int TARGET = 2;
    public static final int WALL = 3;

    private int[][] map;

    private final int width;
    private final int height;

    public AStarMap(int width, int height) {
        this.width = width;
        this.height = height;

        map = new int[height][width];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getXY(int x, int y) {
        return map[y][x];
    }

    public void setXY(int x, int y, int value) {
        map[y][x] = value;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (map[y][x]) {
                    case EMPTY:
                        stringBuilder.append(" ");
                        break;
                    case SOURCE:
                        stringBuilder.append("S");
                        break;
                    case TARGET:
                        stringBuilder.append("T");
                        break;
                    case WALL:
                        stringBuilder.append("B");
                        break;
                    default:
                        break;
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}
