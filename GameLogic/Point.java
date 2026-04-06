package GameLogic;

import GameLogic.Pieces.Piece;

public class Point {

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.piece = null;
        if (y > 4) {
            this.side = riverSide.downRiver;
        } else {
            this.side = riverSide.upRiver;
        }
    }

    private int x;
    private int y;

    private int x2;
    private int y2;

    private Piece piece;
    private riverSide side;

    enum riverSide {
        upRiver,
        downRiver
    }


    public Piece getPiece() {
        return this.piece;
    }

    public void setPiece(Piece newPiece) {
        this.piece = newPiece;
    }

    public int getX() {
        return this.x2;
    }

    public int getY() {
        return this.y2;
    }

    public void setPosition(int x, int y) {
        this.x2 = x;
        this.y2 = y;
    }

}
