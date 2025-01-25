package org.example.client.GUI;

import org.example.game_logic.Board;
import org.example.game_logic.Coordinate;
import org.example.game_logic.Node;

public class BoardDrawingTool {
    public BoardDrawingTool() {
    }

    public void drawBoard(Board board, BoardPane boardPane) {
        this.validateBoard(board, boardPane);
        this.updateBoard(board, boardPane);
    }

    private void validateBoard(Board board, BoardPane boardPane) {
        for(Coordinate key : board.getNodes().keySet()) {
            if (!boardPane.getNodes().containsKey(key)) {
                this.initializeBoard(board, boardPane);
                break;
            }
        }

    }

    private void updateBoard(Board board, BoardPane boardPane) {
        double size = Math.min(boardPane.getWidth(), boardPane.getHeight()) - (double)20.0F;
        double xOffset = (boardPane.getWidth() - size) / (double)2.0F;
        if( boardPane.getLayoutX() / 2 < xOffset) {
            xOffset -= boardPane.getLayoutX() / 2;
        }
        else {
            xOffset = 0;
        }
        double yOffset = (boardPane.getHeight() - size) / (double)2.0F;

        for(Coordinate key : board.getNodes().keySet()) {
            Node node = board.getNodes().get(key);
            NodeRepresentation nodeRepresentation = boardPane.getNodes().get(key);
            int elementsX = 25;
            int elementsY = 17;
            double x = (double)key.getX() * size / (double)elementsX;
            double y = (double)key.getY() * size / (double)elementsY;
            double radius = Math.sqrt(Math.pow(size / (double)elementsX / 2.0F, 2.0F) + Math.pow(size / (double)elementsY / 2.0F, 2.0F)) - 4.0F;
            nodeRepresentation.setPos(x + radius/2 + xOffset, y + radius/2 + yOffset);
            nodeRepresentation.updateNode(node);
            nodeRepresentation.setRadius(radius);
        }

    }

    private void initializeBoard(Board board, BoardPane boardPane) {
        boardPane.getChildren().clear();
        boardPane.getNodes().clear();

        for(Coordinate key : board.getNodes().keySet()) {
            NodeRepresentation node = new NodeRepresentation();
            boardPane.getNodes().put(key, node);
            boardPane.getChildren().add(node);
        }

    }
}
