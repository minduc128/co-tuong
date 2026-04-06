package GameLogic;

import GameLogic.Pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleBot {
    private static final int HARD_SEARCH_DEPTH = 2;
    private static final int HARD_CANDIDATE_LIMIT = 8;

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private final Random random = new Random();

    public Move chooseMove(Board board, Piece.Side side, Difficulty difficulty) {
        List<Move> legalMoves = getLegalMoves(board, side);
        if (legalMoves.isEmpty()) {
            return null;
        }

        if (difficulty == Difficulty.EASY) {
            return legalMoves.get(random.nextInt(legalMoves.size()));
        }

        if (difficulty == Difficulty.MEDIUM) {
            return chooseBestHeuristicMove(board, legalMoves, side);
        }

        return chooseBestMinimaxMove(board, legalMoves, side);
    }

    private List<Move> getLegalMoves(Board board, Piece.Side side) {
        List<Move> legalMoves = new ArrayList<>();

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece piece = board.getPoint(x, y).getPiece();
                if (piece == null || piece.getSide() != side) {
                    continue;
                }

                for (int targetY = 0; targetY < 10; targetY++) {
                    for (int targetX = 0; targetX < 9; targetX++) {
                        if (x == targetX && y == targetY) {
                            continue;
                        }

                        Move move = new Move(x, y, targetX, targetY);
                        if (!board.isMoveLegalForSidePreview(move, side)) {
                            continue;
                        }

                        legalMoves.add(move);
                    }
                }
            }
        }

        return legalMoves;
    }

    private Move chooseBestHeuristicMove(Board board, List<Move> legalMoves, Piece.Side side) {
        List<Move> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;

        for (Move move : legalMoves) {
            int score = scoreMediumMove(board, move, side);
            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private int scoreMediumMove(Board board, Move move, Piece.Side side) {
        Piece movingPiece = board.getPoint(move.getOriginX(), move.getOriginY()).getPiece();
        Piece capturedPiece = board.getPoint(move.getFinalX(), move.getFinalY()).getPiece();
        Piece.Side opponentSide = opposite(side);

        int score = scoreMove(board, move, side) * 2;
        if (movingPiece == null) {
            return score;
        }

        int ownValue = getPieceValue(movingPiece);
        int captureValue = capturedPiece == null ? 0 : getPieceValue(capturedPiece);
        int beforeMobility = getLegalMoves(board, opponentSide).size();

        board.doMove(move);
        board.updateGenerals();

        int afterMobility = getLegalMoves(board, opponentSide).size();
        boolean givesCheck = side == Piece.Side.UP ? board.isDownInCheck() : board.isUpInCheck();
        boolean destinationAttacked = isSquareAttacked(board, move.getFinalX(), move.getFinalY(), opponentSide);
        int chasedValue = strongestThreatenedEnemy(board, move.getFinalX(), move.getFinalY(), side);
        int supportCount = countFriendlySupport(board, move.getFinalX(), move.getFinalY(), side);

        score += (beforeMobility - afterMobility) * 4;

        if (givesCheck) {
            score += 140;
            if (afterMobility <= 2) {
                score += 120;
            }
        }

        if (chasedValue > ownValue) {
            score += (chasedValue - ownValue) * 6;
        }

        if (captureValue > 0) {
            score += captureValue * 8;
            if (captureValue >= ownValue) {
                score += 30;
            }
        }

        if (destinationAttacked) {
            score -= ownValue * 8;
            if (supportCount > 0) {
                score += supportCount * 10;
            }
            if (captureValue >= ownValue) {
                score += 25;
            }
        } else {
            score += 12;
        }

        score += getPiecePatternBonus(movingPiece, move, givesCheck, afterMobility);

        board.undoMove(move, capturedPiece);
        board.updateGenerals();

        return score;
    }

    private Move chooseBestMinimaxMove(Board board, List<Move> legalMoves, Piece.Side side) {
        List<Move> candidateMoves = selectTopCandidateMoves(board, legalMoves, side);
        List<Move> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;

        for (Move move : candidateMoves) {
            Piece captured = board.getPoint(move.getFinalX(), move.getFinalY()).getPiece();
            board.doMove(move);
            board.updateGenerals();
            int score = minimax(board, opposite(side), side, HARD_SEARCH_DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            score += scoreHardMoveIntent(board, move, side);
            board.undoMove(move, captured);
            board.updateGenerals();

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private List<Move> selectTopCandidateMoves(Board board, List<Move> legalMoves, Piece.Side side) {
        List<Move> candidates = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();

        for (Move move : legalMoves) {
            int score = scoreMediumMove(board, move, side) + scoreMove(board, move, side);
            int insertIndex = 0;
            while (insertIndex < scores.size() && score <= scores.get(insertIndex)) {
                insertIndex++;
            }

            candidates.add(insertIndex, move);
            scores.add(insertIndex, score);

            if (candidates.size() > HARD_CANDIDATE_LIMIT) {
                candidates.remove(candidates.size() - 1);
                scores.remove(scores.size() - 1);
            }
        }

        if (candidates.isEmpty()) {
            return legalMoves;
        }
        return candidates;
    }

    private int minimax(Board board, Piece.Side currentSide, Piece.Side maximizingSide, int depth, int alpha, int beta) {
        List<Move> legalMoves = getLegalMoves(board, currentSide);
        if (depth == 0 || legalMoves.isEmpty()) {
            if (legalMoves.isEmpty()) {
                return currentSide == maximizingSide ? -100000 : 100000;
            }
            return evaluateBoard(board, maximizingSide);
        }

        if (currentSide == maximizingSide) {
            int bestScore = Integer.MIN_VALUE;
            for (Move move : legalMoves) {
                Piece captured = board.getPoint(move.getFinalX(), move.getFinalY()).getPiece();
                board.doMove(move);
                board.updateGenerals();
                int score = minimax(board, opposite(currentSide), maximizingSide, depth - 1, alpha, beta);
                board.undoMove(move, captured);
                board.updateGenerals();

                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestScore;
        }

        int bestScore = Integer.MAX_VALUE;
        for (Move move : legalMoves) {
            Piece captured = board.getPoint(move.getFinalX(), move.getFinalY()).getPiece();
            board.doMove(move);
            board.updateGenerals();
            int score = minimax(board, opposite(currentSide), maximizingSide, depth - 1, alpha, beta);
            board.undoMove(move, captured);
            board.updateGenerals();

            bestScore = Math.min(bestScore, score);
            beta = Math.min(beta, score);
            if (beta <= alpha) {
                break;
            }
        }
        return bestScore;
    }

    private int scoreMove(Board board, Move move, Piece.Side side) {
        Piece targetPiece = board.getPoint(move.getFinalX(), move.getFinalY()).getPiece();
        int score = 0;
        if (targetPiece != null) {
            score += getPieceValue(targetPiece) * 10;
        }

        Piece movingPiece = board.getPoint(move.getOriginX(), move.getOriginY()).getPiece();
        if (movingPiece != null && "Soldier".equals(movingPiece.toString())) {
            score += 2;
        }

        int forwardProgress = movingPiece != null && movingPiece.getSide() == Piece.Side.UP
                ? move.getFinalY() - move.getOriginY()
                : move.getOriginY() - move.getFinalY();
        return score + forwardProgress + positionalBonus(move, side);
    }

    private int evaluateBoard(Board board, Piece.Side maximizingSide) {
        int score = 0;

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece piece = board.getPoint(x, y).getPiece();
                if (piece == null) {
                    continue;
                }

                int pieceScore = getPieceValue(piece) * 10;
                pieceScore += piecePositionScore(piece, x, y);
                if (piece.getSide() == maximizingSide) {
                    score += pieceScore;
                } else {
                    score -= pieceScore;
                }
            }
        }

        score += getLegalMoves(board, maximizingSide).size();
        score -= getLegalMoves(board, opposite(maximizingSide)).size();
        score += evaluateStrategicPatterns(board, maximizingSide);
        score -= evaluateStrategicPatterns(board, opposite(maximizingSide));
        return score;
    }

    private int evaluateStrategicPatterns(Board board, Piece.Side side) {
        int score = 0;
        int controlCenter = 0;
        int supportedPieces = 0;
        int isolatedPieces = 0;

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece piece = board.getPoint(x, y).getPiece();
                if (piece == null || piece.getSide() != side) {
                    continue;
                }

                int support = countFriendlySupport(board, x, y, side);
                if (support > 0) {
                    supportedPieces += Math.min(2, support);
                } else if (!"General".equals(piece.toString())) {
                    isolatedPieces++;
                }

                if (x == 4) {
                    controlCenter += 1;
                }

                score += getFormationBonus(board, piece, x, y, side);
                score += getPressureBonus(board, piece, x, y, side);
            }
        }

        score += controlCenter * 8;
        score += supportedPieces * 6;
        score -= isolatedPieces * 10;
        score += evaluateKingTrap(board, side);
        return score;
    }

    private int getFormationBonus(Board board, Piece piece, int x, int y, Piece.Side side) {
        String type = piece.toString();
        int bonus = 0;

        if ("Cannon".equals(type)) {
            if (x == 4) {
                bonus += 22;
            }
            if (hasAlignedFriendlyCannon(board, x, y, side)) {
                bonus += 28;
            }
            if (isBottomRankPressure(board, x, y, side)) {
                bonus += 24;
            }
        }

        if ("Horse".equals(type)) {
            if (isNgoaTaoMaSquare(x, y, side)) {
                bonus += 35;
            }
            if (hasFriendlyCannonSameWing(board, x, side) || hasFriendlyChariotSameWing(board, x, side)) {
                bonus += 16;
            }
        }

        if ("Chariot".equals(type)) {
            if (isBottomRankPressure(board, x, y, side)) {
                bonus += 26;
            }
            if (x == 3 || x == 5) {
                bonus += 10;
            }
        }

        if ("Soldier".equals(type)) {
            if (x == 4) {
                bonus += 14;
            }
            if (crossedRiver(piece, y)) {
                bonus += 12;
            }
        }

        return bonus;
    }

    private int getPressureBonus(Board board, Piece piece, int x, int y, Piece.Side side) {
        int pressure = 0;
        int threatenedValue = strongestThreatenedEnemy(board, x, y, side);
        if (threatenedValue > 0) {
            pressure += threatenedValue * 2;
        }

        Piece.Side opponent = opposite(side);
        int opponentMobilityBefore = getLegalMoves(board, opponent).size();
        Piece original = board.getPoint(x, y).getPiece();
        board.getPoint(x, y).setPiece(null);
        int mobilityWithoutPiece = getLegalMoves(board, opponent).size();
        board.getPoint(x, y).setPiece(original);
        pressure += Math.max(0, mobilityWithoutPiece - opponentMobilityBefore) * 3;

        if (side == Piece.Side.UP ? board.isDownInCheck() : board.isUpInCheck()) {
            pressure += 30;
        }

        return pressure;
    }

    private int evaluateKingTrap(Board board, Piece.Side side) {
        Piece.Side opponent = opposite(side);
        int mobility = getLegalMoves(board, opponent).size();
        int score = Math.max(0, 25 - mobility);

        if (side == Piece.Side.UP && board.isDownInCheck()) {
            score += 40;
        }
        if (side == Piece.Side.DOWN && board.isUpInCheck()) {
            score += 40;
        }
        return score;
    }

    private int scoreHardMoveIntent(Board board, Move move, Piece.Side side) {
        Piece moved = board.getPoint(move.getFinalX(), move.getFinalY()).getPiece();
        if (moved == null) {
            return 0;
        }

        int score = 0;
        boolean givesCheck = side == Piece.Side.UP ? board.isDownInCheck() : board.isUpInCheck();
        if (givesCheck) {
            score += 80;
        }

        score += getFormationBonus(board, moved, move.getFinalX(), move.getFinalY(), side);
        score += strongestThreatenedEnemy(board, move.getFinalX(), move.getFinalY(), side) * 2;

        if (countFriendlySupport(board, move.getFinalX(), move.getFinalY(), side) > 0) {
            score += 18;
        }

        if (isSquareAttacked(board, move.getFinalX(), move.getFinalY(), opposite(side))) {
            score -= getPieceValue(moved) * 4;
        }

        return score;
    }

    private int strongestThreatenedEnemy(Board board, int fromX, int fromY, Piece.Side side) {
        int bestValue = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece target = board.getPoint(x, y).getPiece();
                if (target == null || target.getSide() == side) {
                    continue;
                }

                Move threatMove = new Move(fromX, fromY, x, y);
                if (board.isMoveLegalForSidePreview(threatMove, side)) {
                    bestValue = Math.max(bestValue, getPieceValue(target));
                }
            }
        }
        return bestValue;
    }

    private boolean isSquareAttacked(Board board, int x, int y, Piece.Side attackingSide) {
        for (int fromY = 0; fromY < 10; fromY++) {
            for (int fromX = 0; fromX < 9; fromX++) {
                Piece attacker = board.getPoint(fromX, fromY).getPiece();
                if (attacker == null || attacker.getSide() != attackingSide) {
                    continue;
                }

                if (board.isMoveLegalForSidePreview(new Move(fromX, fromY, x, y), attackingSide)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int countFriendlySupport(Board board, int x, int y, Piece.Side side) {
        int support = 0;
        for (int fromY = 0; fromY < 10; fromY++) {
            for (int fromX = 0; fromX < 9; fromX++) {
                if (fromX == x && fromY == y) {
                    continue;
                }

                Piece helper = board.getPoint(fromX, fromY).getPiece();
                if (helper == null || helper.getSide() != side) {
                    continue;
                }

                Piece original = board.getPoint(x, y).getPiece();
                board.getPoint(x, y).setPiece(null);
                boolean supports = board.isMoveLegalForSidePreview(new Move(fromX, fromY, x, y), side);
                board.getPoint(x, y).setPiece(original);

                if (supports) {
                    support++;
                }
            }
        }
        return support;
    }

    private int getPiecePatternBonus(Piece movingPiece, Move move, boolean givesCheck, int opponentMobility) {
        String type = movingPiece.toString();
        int bonus = 0;

        if ("Cannon".equals(type)) {
            if (move.getFinalX() == 4) {
                bonus += 28;
            }
            if (givesCheck) {
                bonus += 40;
            }
        }

        if ("Horse".equals(type)) {
            if ((move.getFinalX() == 3 || move.getFinalX() == 5) && (move.getFinalY() == 1 || move.getFinalY() == 8)) {
                bonus += 35;
            }
            if (givesCheck && opponentMobility <= 3) {
                bonus += 45;
            }
        }

        if ("Chariot".equals(type) && givesCheck) {
            bonus += 35;
        }

        if ("Soldier".equals(type)) {
            bonus += 10;
            if (move.getFinalX() == 4) {
                bonus += 12;
            }
        }

        return bonus;
    }

    private boolean hasAlignedFriendlyCannon(Board board, int x, int y, Piece.Side side) {
        for (int row = 0; row < 10; row++) {
            if (row == y) {
                continue;
            }
            Piece piece = board.getPoint(x, row).getPiece();
            if (piece != null && piece.getSide() == side && "Cannon".equals(piece.toString())) {
                return true;
            }
        }
        for (int col = 0; col < 9; col++) {
            if (col == x) {
                continue;
            }
            Piece piece = board.getPoint(col, y).getPiece();
            if (piece != null && piece.getSide() == side && "Cannon".equals(piece.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean isNgoaTaoMaSquare(int x, int y, Piece.Side side) {
        if (side == Piece.Side.UP) {
            return (x == 2 || x == 6) && y == 7;
        }
        return (x == 2 || x == 6) && y == 2;
    }

    private boolean hasFriendlyCannonSameWing(Board board, int x, Piece.Side side) {
        int wingStart = x <= 4 ? 0 : 5;
        int wingEnd = x <= 4 ? 4 : 8;
        for (int row = 0; row < 10; row++) {
            for (int col = wingStart; col <= wingEnd; col++) {
                Piece piece = board.getPoint(col, row).getPiece();
                if (piece != null && piece.getSide() == side && "Cannon".equals(piece.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasFriendlyChariotSameWing(Board board, int x, Piece.Side side) {
        int wingStart = x <= 4 ? 0 : 5;
        int wingEnd = x <= 4 ? 4 : 8;
        for (int row = 0; row < 10; row++) {
            for (int col = wingStart; col <= wingEnd; col++) {
                Piece piece = board.getPoint(col, row).getPiece();
                if (piece != null && piece.getSide() == side && "Chariot".equals(piece.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBottomRankPressure(Board board, int x, int y, Piece.Side side) {
        Piece.Side opponent = opposite(side);
        int targetRank = opponent == Piece.Side.UP ? 0 : 9;
        return y == targetRank || Math.abs(y - targetRank) == 1;
    }

    private boolean crossedRiver(Piece piece, int y) {
        if (piece.getSide() == Piece.Side.UP) {
            return y >= 5;
        }
        return y <= 4;
    }

    private int piecePositionScore(Piece piece, int x, int y) {
        String type = piece.toString();
        int centerBonus = 4 - Math.abs(4 - x);
        if ("Soldier".equals(type)) {
            if (piece.getSide() == Piece.Side.UP) {
                return y * 2 + centerBonus;
            }
            return (9 - y) * 2 + centerBonus;
        }
        if ("General".equals(type)) {
            return 0;
        }
        return centerBonus;
    }

    private int positionalBonus(Move move, Piece.Side side) {
        int centerBefore = 4 - Math.abs(4 - move.getOriginX());
        int centerAfter = 4 - Math.abs(4 - move.getFinalX());
        int bonus = centerAfter - centerBefore;
        if (side == Piece.Side.UP) {
            bonus += move.getFinalY() - move.getOriginY();
        } else {
            bonus += move.getOriginY() - move.getFinalY();
        }
        return bonus;
    }

    private Piece.Side opposite(Piece.Side side) {
        return side == Piece.Side.UP ? Piece.Side.DOWN : Piece.Side.UP;
    }

    private int getPieceValue(Piece piece) {
        String type = piece.toString();
        if ("General".equals(type)) {
            return 1000;
        }
        if ("Chariot".equals(type)) {
            return 90;
        }
        if ("Horse".equals(type)) {
            return 50;
        }
        if ("Cannon".equals(type)) {
            return 45;
        }
        if ("Guard".equals(type) || "Elephant".equals(type)) {
            return 20;
        }
        if ("Soldier".equals(type)) {
            return 10;
        }
        return 1;
    }
}
