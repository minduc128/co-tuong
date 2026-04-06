package GameLogic.Pieces;

import GameLogic.Move;

public class Soldier extends Piece {


    public Soldier(Side side) {
        super(side);
        this.type = "Soldier";
        this.canWinAlone = true;
    }

    @Override

    public void checkPattern(Move move) {
        super.checkPattern(move);

        Side curSide;
        if (move.getOriginY() <= 4) {
            curSide = Side.UP;
        } else {
            curSide = Side.DOWN;
        }

        if (side == curSide) {
            if (this.side == Side.UP) {
                if (move.getDy() != 1 || !move.isVertical()) {
                    move.setValid(false);
                }
            }
            if (side == Side.DOWN) {
                if (move.getDy() != -1 || !move.isVertical()) {
                    move.setValid(false);
                }
            }
        }

        if (this.side != curSide) {
            if (!move.isHorizontal() && !move.isVertical()) {
                move.setValid(false);
            }

            if (this.side == Side.UP) {
                if (!(move.getDy() == 1 || Math.abs(move.getDx()) == 1)) {
                    move.setValid(false);
                }
            }
            if (this.side == Side.DOWN) {
                if (!(move.getDy() == -1 || Math.abs(move.getDx()) == 1)) {
                    move.setValid(false);
                }
            }
        }
    }
}
