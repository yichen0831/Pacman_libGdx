package com.ychstudio.ai.astar;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;

public class AStartPathFinding {

    private AStartPathFinding() {

    }

    public static Node findPath(Vector2 source, Vector2 target, AStarMap aStarMap) {
        int sourceX = MathUtils.floor(source.x);
        int sourceY = MathUtils.floor(source.y);
        int targetX = MathUtils.floor(target.x);
        int targetY = MathUtils.floor(target.y);

        if (aStarMap == null
                || sourceX < 0 || sourceX >= aStarMap.getWidth()
                || sourceY < 0 || sourceY >= aStarMap.getHeight()
                || targetX < 0 || targetX >= aStarMap.getWidth()
                || targetY < 0 || targetY >= aStarMap.getHeight()) {
            return null;
        }

        Array<Node> openNodes = new Array<>();

        int width = aStarMap.getWidth();
        int height = aStarMap.getHeight();

        Node[][] nodes = new Node[height][width];
        Node targetNode = new Node(targetX, targetY);
        nodes[targetY][targetX] = targetNode;

        Node sourceNode = new Node(sourceX, sourceY, true);
        nodes[sourceY][sourceX] = sourceNode;
        openNodes.add(sourceNode);

        while (openNodes.size != 0) {
            openNodes.sort(new Comparator<Node>() {
                @Override
                public int compare(Node node1, Node node2) {
                    return node1.getfCost() - node2.getfCost();
                }
            });

            Node node = openNodes.get(0);

            // check neighbors
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    if ((x == 0 && y == 0) || (Math.abs(x) == 1 && Math.abs(y) == 1)) {
                        // do not count diagonal neighbors and self
                        continue;
                    }

                    int neighborX = node.x + x;
                    int neighborY = node.y + y;

                    if (neighborX < 0 || neighborX >= width || neighborY < 0 || neighborY >= height) {
                        continue;
                    }

                    if (aStarMap.getXY(neighborX, neighborY) == AStarMap.WALL) {
                        continue;
                    }

                    if (nodes[neighborY][neighborX] == null) {
                        Node neighborNode = new Node(neighborX, neighborY);
                        neighborNode.setgCost(node.getgCost() + 10);
                        neighborNode.sethCost(Math.abs(targetX - neighborX) + Math.abs(targetY - neighborY));
                        neighborNode.prev = node;
                        nodes[neighborY][neighborX] = neighborNode;

                        openNodes.add(neighborNode);
                    } else {
                        Node neighborNode = nodes[neighborY][neighborX];
                        if (neighborNode == targetNode) {
                            // target reached
                            targetNode.prev = node;
                            node.next = targetNode;

                            while (node != sourceNode) {
                                // find the sourceNode's next node
                                Node tmp = node.prev;
                                tmp.next = node;
                                node = tmp;
                            }
                            return node.next;
                        }

                        if (!neighborNode.closed) {
                            // update gCost
                            int gCost = node.getgCost() + 10;
                            if (neighborNode.getgCost() > gCost) {
                                neighborNode.setgCost(gCost);
                                neighborNode.prev = node;
                            }
                        }
                    }
                }
            }

            node.closed = true;
            openNodes.removeValue(node, true);
        }

        // no path found
        return null;
    }

}
