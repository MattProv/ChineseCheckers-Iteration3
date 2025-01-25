package org.example.client.GUI;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.example.game_logic.Board;
import org.example.game_logic.Coordinate;

import java.util.HashMap;
import java.util.Map;

public class BoardPane extends Pane {
    public static abstract class CallbacksHandler {
        public void onMoveSelected(Coordinate start, Coordinate end) {}
        public void onMoveCancelled() {}
    }

    private CallbacksHandler callbacksHandler;

    private final Map<Coordinate, NodeRepresentation> nodes = new HashMap<>();
    private final BoardDrawingTool boardDrawingTool = new BoardDrawingTool();

    Label moveIndicator1 = new Label("1");
    Coordinate moveIndicator1Coordinate;
    Label moveIndicator2 = new Label("2");
    Coordinate moveIndicator2Coordinate;

    final BoardPaneMouseEventHandler mouseHandler;

    public BoardPane() {
        mouseHandler = new BoardPaneMouseEventHandler(this);

        setOnMousePressed(mouseHandler);
        setOnMouseReleased(mouseHandler);
        setOnMouseMoved(mouseHandler);
    }

    public void updateBoard(Board board) {
        this.boardDrawingTool.drawBoard(board, this);
        updateIndicators();
    }

    public Map<Coordinate, NodeRepresentation> getNodes() {
        return this.nodes;
    }

    public CallbacksHandler getCallbacksHandler() {
        return callbacksHandler;
    }

    public void setCallbacksHandler(CallbacksHandler callbacksHandler) {
        this.callbacksHandler = callbacksHandler;
    }

    public void cancelMove() {
        mouseHandler.cancelMove();
    }

    public void updateIndicators()
    {
        if(moveIndicator1Coordinate != null)
        {
            setMoveIndicator(moveIndicator1, moveIndicator1Coordinate);
        }
        if(moveIndicator2Coordinate != null)
        {
            setMoveIndicator(moveIndicator2, moveIndicator2Coordinate);
        }
    }

    public void setMoveIndicatorThreadSafe(Label moveIndicator, Coordinate coordinate)
    {
        Platform.runLater(() -> setMoveIndicator(moveIndicator, coordinate));
    }

    private void setMoveIndicator(Label moveIndicator, Coordinate coordinate)
    {
        if(moveIndicator == moveIndicator1)
        {
            moveIndicator1Coordinate = coordinate;
        }
        else if(moveIndicator == moveIndicator2)
        {
            moveIndicator2Coordinate = coordinate;
        }
        NodeRepresentation node = getNodes().get(coordinate);
        double x = node.getX() - moveIndicator.getWidth() / 2;
        double y = node.getY() - moveIndicator.getHeight() / 2;

        if(!getChildren().contains(moveIndicator))
        {
            getChildren().add(moveIndicator);
        }

        moveIndicator.setLayoutX(x);
        moveIndicator.setLayoutY(y);
    }

    public void clearMoveIndicators() {
        moveIndicator1Coordinate = null;
        moveIndicator2Coordinate = null;
        Platform.runLater(() -> {
            getChildren().remove(moveIndicator1);
            getChildren().remove(moveIndicator2);
        });
    }

    static class BoardPaneMouseEventHandler implements EventHandler<MouseEvent> {
        BoardPane pane;

        Coordinate start;
        Coordinate end;

        NodeRepresentation lastHoveredNode;

        BoardPaneMouseEventHandler(BoardPane pane) {
            this.pane = pane;
        }

        private void setStart(Coordinate start) {
            this.start = start;
            pane.setMoveIndicatorThreadSafe(pane.moveIndicator1, start);
        }

        private void setEnd(Coordinate end) {
            this.end = end;
            pane.setMoveIndicatorThreadSafe(pane.moveIndicator2, end);
        }

        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
            {
                if (event.getButton() == MouseButton.PRIMARY)
                {
                    Coordinate nodeCoordinate = getNodeCoordinate(event.getX(), event.getY());
                    if(nodeCoordinate == null)
                    {
                        cancelMove();
                    }
                    else if(end != null)
                    {
                        cancelMove();
                        setStart(nodeCoordinate);
                    }
                    else if(start == null)
                    {
                        setStart(nodeCoordinate);
                    }
                    else
                    {
                        setEnd(nodeCoordinate);
                        if(end != null)
                        {
                            pane.getCallbacksHandler().onMoveSelected(start, end);
                        }
                    }
                }
                else if (event.getButton() == MouseButton.SECONDARY)
                {
                    cancelMove();
                }
            }
            if (event.getEventType() == MouseEvent.MOUSE_MOVED)
            {
                NodeRepresentation nodeRepresentation = getNodeRepresentation(event.getX(), event.getY());
                if(nodeRepresentation != lastHoveredNode)
                {
                    if(lastHoveredNode != null)
                    {
                        lastHoveredNode.setHovered(false);
                    }
                    if(nodeRepresentation != null)
                    {
                        nodeRepresentation.setHovered(true);
                        lastHoveredNode = nodeRepresentation;
                    }
                }
            }
        }

        public void cancelMove() {
            pane.getCallbacksHandler().onMoveCancelled();
            resetSelection();
        }

        private void resetSelection() {
            start = null;
            end = null;

            pane.clearMoveIndicators();
        }

        private Coordinate getNodeCoordinate(double x, double y)
        {
            for(Coordinate c : pane.getNodes().keySet())
            {
                if(pane.getNodes().get(c).isHit(x, y))
                {
                    return c;
                }
            }
            return null;
        }

        private NodeRepresentation getNodeRepresentation(double x, double y)
        {
            for(Coordinate c : pane.getNodes().keySet())
            {
                if(pane.getNodes().get(c).isHit(x, y))
                {
                    return pane.getNodes().get(c);
                }
            }
            return null;
        }
    }
}
