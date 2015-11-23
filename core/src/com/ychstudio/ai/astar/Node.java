package com.ychstudio.ai.astar;

public class Node {

    public int x;
    public int y;

    public Node prev;
    public Node next;

    private int gCost;
    private int hCost;
    private int fCost;

    public boolean closed;

    public Node(int x, int y) {
        this(x, y, false);
    }

    public Node(int x, int y, boolean closed) {
        this.x = x;
        this.y = y;
        this.closed = closed;

        gCost = 0;
        hCost = 0;
        fCost = 0;
    }

    public int getgCost() {
        return gCost;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
        fCost = gCost + hCost;
    }

    public int gethCost() {
        return hCost;
    }

    public void sethCost(int hCost) {
        this.hCost = hCost;
        fCost = gCost + hCost;
    }

    public int getfCost() {
        return fCost;
    }

    @Override
    public String toString() {
        return "Node: (" + x + ", " + y + ")";
    }

}
