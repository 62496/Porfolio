package g62496.dev3.oxono.model;

import g62496.dev3.oxono.util.BotStrategy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class FacadeTest {

    @org.junit.jupiter.api.Test
    void isGameOver() {
    }

    @org.junit.jupiter.api.Test
    void isDrawMatch() {
    }

    @org.junit.jupiter.api.Test
    void start() {
    }

    @org.junit.jupiter.api.Test
    void checkTotemFirstClick() {
    }
    @Test
    void testCheckTotemFirstClick_invalidClick() {
        Board board = new Board(5, 5);
        Totem totemX = new Totem(Symbol.X,new Position(3,3));
        Totem totemO = new Totem(Symbol.O,new Position(2,2));
        board.initialiseTotem(totemX);
        board.initialiseTotem(totemO);

        Oxono oxono = new Oxono(board, null);

        List<Position> positions = new ArrayList<>();
        positions.add(new Position(0, 0));

        List<Position> result = oxono.checkTotemFirstClick(positions);

        assertNotNull(result);
        assertEquals(-1, result.get(0).dx);
        assertEquals(-1, result.get(0).dy);
    }
    @Test
    void testCheckTotemFirstClick_validClick() {
        Board board = new Board(5, 5);
        Totem totemX = new Totem(Symbol.X,new Position(3,3));
        Totem totemO = new Totem(Symbol.O,new Position(2,2));
        board.initialiseTotem(totemX);
        board.initialiseTotem(totemO);

        Oxono oxono = new Oxono(board, null);

        List<Position> positions = new ArrayList<>();
        positions.add(new Position(3, 3));

        List<Position> result = oxono.checkTotemFirstClick(positions);
        assertEquals(result ,board.checkTotemBox(new Position(3, 3)));
    }
    @Test
    void testCheckTotemSecondClick_ValidMove() {
        Board board = new Board(5, 5);
        Facade facade = new Facade(board);
        Oxono oxono = new Oxono(board, facade);
        Position totemPos = new Position(2, 2);
        Totem totem = new Totem(Symbol.X, totemPos);
        board.initialiseTotem(totem);

        List<Position> posList = new ArrayList<>();
        posList.add(totemPos);
        facade.checkTotemFirstClick(posList);
        posList.add(new Position(3, 2)); // Nouvelle position

        facade.checkTotemSecondClick(posList);
        assertTrue(board.getTokenAt(2, 3) instanceof Totem);
        assertNull(board.getTokenAt(2, 2));
        assertTrue(oxono.isTotemTurn());
    }
    @Test
    void testAddToken_Success() {
        Board board = new Board(6,6);
        Position position = new Position(2, 2);
        Piece piece = new Piece(Color.BLACK, Symbol.X);
        board.setPiece(piece, position.dx, position.dy);

        assertEquals(piece, board.getTokenAt(position.dy, position.dx));
        assertEquals(1, board.getBlackXPieces()); // Supposant que Symbol.X correspond à blackXPieces
    }

    @Test
    void testAddToken_BoxOccupied_ThrowsException() {
        Board board = new Board(6,6);
        Position position = new Position(1, 1);
        Piece piece1 = new Piece(Color.PINK, Symbol.O);
        Piece piece2 = new Piece(Color.PINK, Symbol.X);
        board.setPiece(piece1, position.dx, position.dy);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            board.setPiece(piece2, position.dx, position.dy);
        });
        assertEquals("this box is not empty", exception.getMessage());
    }

    @Test
    void testMoveTotem_Success() {
        Board board = new Board(6,6);
        Position originalPos = new Position(0, 0);
        Position newPos = new Position(1, 1);
        Totem totem = new Totem(Symbol.X, originalPos);
        board.initialiseTotem(totem);
        Totem newTotem = new Totem(Symbol.X,newPos);
        board.moveTotem(newTotem, originalPos.dx, originalPos.dy);

        assertNull(board.getTokenAt(originalPos.dy, originalPos.dx));
        assertEquals(totem.getType(), ((Totem )board.getTokenAt(newPos.dy, newPos.dx)).getType());
        assertEquals(newPos, newTotem.getPosition());
    }

    @Test
    void testMoveTotem_BoxOccupied_ThrowsException() {
        Board board = new Board(6,6);
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(1, 1);
        Totem totem1 = new Totem(Symbol.X, pos1);
        Totem totem2 = new Totem(Symbol.O, pos2);
        board.initialiseTotem(totem1);
        board.initialiseTotem(totem2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            board.moveTotem(totem1, pos2.dx, pos2.dy);
        });

        assertEquals("this box is not empty", exception.getMessage());
    }
    @Test
    void testEndGame_HorizontalWin() {
        Board board = new Board(6,6);
        for (int i = 0; i < 4; i++) {
            board.setPiece(new Piece(Color.PINK, Symbol.X), 0, i);
        }
        System.out.println(board.getTokenAt(1,0));
        Facade facade = new Facade(board);

        Oxono oxono = new Oxono(board, facade);
        assertTrue(facade.isGameOver());
    }

    @Test
    void testEndGame_VerticalWin() {
        Board board = new Board(6,6);
        for (int i = 0; i < 4; i++) {
            board.setPiece(new Piece(Color.PINK, Symbol.X), 0, i);
        }
        Facade facade = new Facade(board);
        Oxono oxono = new Oxono(board, facade);
        assertTrue(oxono.isGameOver());
    }
    @Test
    void testEndGame_NoWin() {
        Board board = new Board(4,4);
        Position pos1 = new Position(1, 1);
        Position pos2 = new Position(2, 2);
        Totem totem1 = new Totem(Symbol.X, pos1);
        Totem totem2 = new Totem(Symbol.O, pos2);
        board.initialiseTotem(totem1);
        board.initialiseTotem(totem2);
        for (int i = 0; i < 4; i++) {
            if (i!=1) {
                board.setPiece(new Piece(Color.BLACK, Symbol.X), 1, i);
                board.setPiece(new Piece(Color.BLACK, Symbol.X), i, 1);
            }
            if (i!=2 &&i!=1){
                board.setPiece(new Piece(Color.PINK, Symbol.X), 2, i);
                board.setPiece(new Piece(Color.PINK, Symbol.X), i, 2);
            }
        }
        Oxono oxono = new Oxono(board, null);
        assertTrue(oxono.isDrawMatch());
    }
    @Test
    void testCheckTotemBox_PositionsAvailable() {
        Position totemPos = new Position(2, 2);
        Totem totem = new Totem(Symbol.X, totemPos);
        Board board = new Board(6,6);
        board.initialiseTotem(totem);

        List<Position> availablePositions = board.checkTotemBox(totemPos);
        List<Position> rightPosition =new ArrayList<>();
        rightPosition.add(new Position(0, 2));
        rightPosition.add(new Position(1, 2));
        rightPosition.add(new Position(3, 2));
        rightPosition.add(new Position(4, 2));
        rightPosition.add(new Position(5, 2));
        rightPosition.add(new Position(2, 0));
        rightPosition.add(new Position(2, 1));
        rightPosition.add(new Position(2, 3));
        rightPosition.add(new Position(2, 4));
        rightPosition.add(new Position(2, 5));

        assertEquals(10, availablePositions.size());
        assertTrue(rightPosition.contains(availablePositions.get(1)));
        assertTrue(availablePositions.containsAll(rightPosition));
    }

    @Test
    void testCheckTotemBoxEnclaved_NoPositionsAvailable() {
        Position totemPos = new Position(2, 2);
        Totem totem = new Totem(Symbol.X, totemPos);
        Board board = new Board(6,6);
        board.initialiseTotem(totem);

        // Remplir toutes les positions autour
        board.setPiece(new Piece(Color.BLACK, Symbol.X), 3, 2);
        board.setPiece(new Piece(Color.BLACK, Symbol.X), 1, 2);
        board.setPiece(new Piece(Color.BLACK, Symbol.X), 2, 3);
        board.setPiece(new Piece(Color.BLACK, Symbol.X), 2, 1);

        List<Position> availablePositions = board.checkTotemBox(totemPos);

        assertEquals(4, availablePositions.size());
        assertTrue(availablePositions.contains(new Position(4, 2)));
        assertTrue(availablePositions.contains(new Position(0, 2)));
        assertTrue(availablePositions.contains(new Position(2, 4)));
        assertTrue(availablePositions.contains(new Position(2, 0)));
    }

    @Test
    void testCheckPiece_ValidMove() {
        Board board = new Board(6,6);
        Facade facade = new Facade(board);
        Oxono oxono = new Oxono(board, facade);
        Position totemPos = new Position(2, 2);
        Totem totem = new Totem(Symbol.X, totemPos);
        board.initialiseTotem(totem);

        List<Position> posList = new ArrayList<>();
        posList.add(totemPos);
        posList.add(new Position(2, 3));
        oxono.checkPiece(posList);
        boolean result =  oxono.checkPiece(posList)== null;
        assertTrue(result);
        //assertEquals(1, board.getPinkXPieces());
    }

    @Test
    void testCheckPiece_InvalidMove() {
        Board board = new Board(6,6);
        Facade facade = new Facade(board);
        Oxono oxono = new Oxono(board, facade);
        Position totemPos = new Position(2, 2);
        Totem totem = new Totem(Symbol.X, totemPos);
        board.initialiseTotem(totem);

        List<Position> posList = new ArrayList<>();
        posList.add(totemPos);
        oxono.checkPiece(posList);
        posList.add(new Position(4, 4));

        List<Position> result = oxono.checkPiece(posList);

        assertNotNull(result);
        assertEquals(-1, result.get(0).dx);
        assertEquals(-1, result.get(0).dy);
    }
    @Test
    void testBotGames_PlacementPiece() {
        Board board = new Board(6,6);
        Totem totem = new Totem(Symbol.X, new Position(3,3));
        board.initialiseTotem(totem);
        Totem totemO = new Totem(Symbol.O, new Position(2,2));
        board.initialiseTotem(totemO);
        Facade facade = new Facade(board);
        Oxono oxono = new Oxono(board, facade);

        List<Position> posList = new ArrayList<>();
        posList.add(new Position(2,2));
        facade.checkTotemFirstClick(posList);
        posList.add(new Position(3, 2));
        facade.checkTotemSecondClick(posList);

        BotStrategy botStrategy = new BoteasyStrategy(board,facade);

        List<Position> rightPosList = board.checkPieceBox(new Position(2, 2));
        assertFalse(rightPosList.isEmpty());

        botStrategy.play(oxono);

        long count = board.getBlackXPieces();
        assertEquals(1, count);
    }

    @Test
    void testBotTotemGame_DeplacementTotem() {
        Board board = new Board(6,6);
        Facade facade = new Facade(board);
        Oxono oxono = new Oxono(board,facade);
        BotStrategy botStrategy = new BoteasyStrategy(board,facade);

        Totem totem1 = new Totem(Symbol.X, new Position(1, 1));
        Totem totem2 = new Totem(Symbol.O, new Position(3, 3));
        board.initialiseTotem(totem1);
        board.initialiseTotem(totem2);

        botStrategy.play(oxono);

        Totem currentTotem = oxono.getCurrentTotem();
        assertNotNull(currentTotem);
        assertTrue(currentTotem.getPosition().equals(new Position(1, 2)) ||
                currentTotem.getPosition().equals(new Position(3, 4)));
    }

    @org.junit.jupiter.api.Test
    void checkTotemSecondClick() {
    }

    @org.junit.jupiter.api.Test
    void checkPieceClick() {
    }

    @org.junit.jupiter.api.Test
    void moveTotem() {
    }

    @org.junit.jupiter.api.Test
    void insertToken() {
    }
    @Test
    void testInsertToken_validMove() {
        Board board = new Board(5, 5);
        Totem totemX = new Totem(Symbol.X,new Position(3,3));
        Totem totemO = new Totem(Symbol.O,new Position(2,2));
        board.initialiseTotem(totemX);
        board.initialiseTotem(totemO);
        Facade facade = new Facade(board);
        Position position = new Position(2, 4);
        Position position2 = new Position(2, 2);
        Piece piece = new Piece(Color.BLACK, Symbol.X);

        boolean result = facade.insertToken(piece, position,board);

        assertTrue(result);
        assertNotEquals(piece, board.getTokenAt(2, 2));
    }


    @org.junit.jupiter.api.Test
    void undo() {
    }

    @org.junit.jupiter.api.Test
    void redo() {
    }
}