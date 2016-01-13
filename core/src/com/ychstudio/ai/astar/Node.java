package com.ychstudio.ai.astar;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;

public class Node implements IndexedNode<Node> {

    public final int x;
    public final int y;
    public boolean isWall;
    private final int index;
    private final Array<Connection<Node>> connections;

    public Node(AStarMap map, int x, int y) {
        this.x = x;
        this.y = y;
        this.index = x * map.getHeight() + y;
        this.isWall = false;
        this.connections = new Array<Connection<Node>>();
    }

    @Override
    public int getIndex () {
        return index;
    }

    @Override
    public Array<Connection<Node>> getConnections () {
        return connections;
    }

    @Override
    public String toString() {
        return "Node: (" + x + ", " + y + ")";
    }

}
