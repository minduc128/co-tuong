package Run;

import GUI.*;
import GameLogic.Board;
import GameLogic.Move;
import GameLogic.MoveLogger;
import GameLogic.Pieces.Piece;
import GameLogic.Player;
import GameLogic.SimpleBot;

import javax.swing.*;

public class Core {
    private static final int COMPUTER_MOVE_DELAY_MS = 1500;

    private BoardFrame boardFrame;
    private BoardPanel boardPanel;
    private TurnTimerPanel timerPanel;
    private Board board;
    private int counter;
    private Player player1;
    private Player player2;
    private static StartFrame startFrame;
    private EndScreen endScreen;
    private Profile profile;
    private boolean playVsComputer;
    private boolean aiThinking;
    private SimpleBot.Difficulty botDifficulty = SimpleBot.Difficulty.EASY;
    private final SimpleBot bot = new SimpleBot();

    public Core() {
        startFrame = new StartFrame(this);
    }

    public void start(Profile profile, boolean playVsComputer, SimpleBot.Difficulty botDifficulty) {
        this.profile = profile;
        this.playVsComputer = playVsComputer;
        this.aiThinking = false;
        this.botDifficulty = botDifficulty;
        player1 = new Player(1, profile.getP1String(), Piece.Side.DOWN, this);
        player2 = new Player(2, profile.getP2String(), Piece.Side.UP, this);
        board = new Board();
        boardPanel = new BoardPanel(this);
        //boardMenu = new BoardMenu(this);
        timerPanel = new TurnTimerPanel(player1, player2, profile);
        boardFrame = new BoardFrame(this);
        counter = 0;
        player1.startTurnTimer(timerPanel);
        timerPanel.refreshCapturedPieces();
        showMoveStatusBanner();
    }

    public void playMove(Move move) {
        boolean moveAccepted = false;
        if (counter % 2 == 0) {
            if (board.tryMove3(move, player1)) {
                //first round
                player1.stopTurnTimer();
                player2.startTurnTimer(timerPanel);
                counter++;
                moveAccepted = true;
            }
        } else if (counter % 2 == 1) {
                    //player2.startTurnTimer(timerPanel);
                    if (board.tryMove3(move, player2)) {
                        player2.stopTurnTimer();
                        player1.startTurnTimer(timerPanel);
                        counter++;
                        moveAccepted = true;
            }
        }
        getBoardPanel().userRepaint();
        timerPanel.refreshCapturedPieces();
        if (moveAccepted && board.getWinner() == Board.NA) {
            showMoveStatusBanner();
            if (shouldComputerPlayNow()) {
                scheduleComputerMove();
            }
        }
        if (board.getWinner() != Board.NA) {
            System.out.println("GAME OVER");
            callEnd();
        }

    }

    private void showMoveStatusBanner() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null || timerPanel == null || board.getWinner() != Board.NA) {
            return;
        }
        timerPanel.showTurnStatus(currentPlayer.getName());
        if (board.isUpInCheck()) {
            String message = "Chiếu tướng: " + player2.getName();
            System.out.println(message);
            if (boardFrame != null) {
                boardFrame.showCheckBanner(message);
            }
            return;
        }
        if (board.isDownInCheck()) {
            String message = "Chiếu tướng: " + player1.getName();
            System.out.println(message);
            if (boardFrame != null) {
                boardFrame.showCheckBanner(message);
            }
        }
    }

    private Player getCurrentPlayer() {
        if (counter % 2 == 0) {
            return player1;
        }
        return player2;
    }

    private boolean shouldComputerPlayNow() {
        return playVsComputer && getCurrentPlayer() == player2 && board.getWinner() == Board.NA;
    }

    private void scheduleComputerMove() {
        aiThinking = true;
        Thread aiThread = new Thread(() -> {
            try {
                Thread.sleep(COMPUTER_MOVE_DELAY_MS);
                Move computerMove = bot.chooseMove(board.copy(), player2.getPlayerSide(), botDifficulty);
                SwingUtilities.invokeLater(() -> applyComputerMove(computerMove));
            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> aiThinking = false);
                Thread.currentThread().interrupt();
            }
        }, "computer-move-thread");
        aiThread.setDaemon(true);
        aiThread.start();
    }

    private void applyComputerMove(Move computerMove) {
        try {
            if (computerMove == null) {
                if (board.isUpInCheck()) {
                    Board.setWinner(Board.PLAYER1_WINS);
                } else {
                    Board.setWinner(Board.DRAW);
                }
                callEnd();
                return;
            }
            playMove(computerMove);
        } finally {
            aiThinking = false;
        }
    }

    public boolean isHumanTurn() {
        if (board == null || board.getWinner() != Board.NA) {
            return false;
        }
        return !playVsComputer || (getCurrentPlayer() == player1 && !aiThinking);
    }

    public void callEnd() {
        player1.stopTurnTimer();
        player2.stopTurnTimer();
        endScreen = new EndScreen(this, board.getWinner(), profile);
    }

    public void saveGame() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        if (os.contains("mac"))
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Specify where to save game");

        int userSelection = fileChooser.showSaveDialog(parentFrame);

        parentFrame.setVisible(true);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            System.out.println("Current Directory: " + fileChooser.getCurrentDirectory());
            System.out.println("Selected File" + fileChooser.getSelectedFile());
            MoveLogger.saveAllMoves(player1, player2, fileChooser.getSelectedFile());
            System.out.println("Game Saved");
            parentFrame.setVisible(false);
        } else {
            System.out.println("No Selection");
            parentFrame.setVisible(false);
        }
    }
    public void setInvisible(){
        boardFrame.setVisible(false);
    }
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public BoardFrame getBoardFrame() {
        return boardFrame;
    }

    public TurnTimerPanel getTurnTimerPanel() {
        return timerPanel;
    }

    public Board getBoard() {
        return board;
    }

    public void setProfile(Profile newProfile) {
        this.profile = newProfile;
    }

    public Profile getProfile() {
        return this.profile;
    }
}
