package GUI;

import GameLogic.Pieces.Piece;
import GameLogic.Player;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TurnTimerPanel extends JPanel {

    private JPanel timerDisplayPanel;
    private JPanel redTimerPanel;
    private JPanel blackTimerPanel;
    private JPanel statusPanel;
    private JPanel capturedPanel;

    private JPanel redNumberPanel;
    private JPanel blackNumberPanel;

    private JLabel redTimerLabel;
    private JLabel blackTimerLabel;
    private JLabel statusLabel;
    private JLabel redCapturedLabel;
    private JLabel blackCapturedLabel;

    private JLabel timerLabel;

    private Player redPlayer;
    private Player blackPlayer;

    private long redTime;
    private long blackTime;

    Thread newThread;

    private int timeLimit;

    public TurnTimerPanel(Player player1, Player player2, Profile profile) {
        setLayout(new BorderLayout(0, 10));

        if (player1.getPlayerSide() == Piece.Side.DOWN) {
            this.redPlayer = player1;
            this.blackPlayer = player2;
        } else {
            this.redPlayer = player2;
            this.blackPlayer = player1;
        }

        timeLimit = profile.getMinutes();

        redTime = 0;
        blackTime = 0;


        timerLabel = new JLabel("Time");
        timerLabel.setFont(new Font("Sans_Serif", Font.PLAIN, 40));

        Border blackLine = BorderFactory.createLineBorder(profile.getP2Color(), 2);

        blackTimerLabel = new JLabel(blackPlayer.elapsedTimeToString(timeLimit));
        blackTimerLabel.setForeground(Color.LIGHT_GRAY);
        blackTimerLabel.setFont(new Font("Sans_Serif", Font.PLAIN, 40));

        blackNumberPanel = new JPanel();
        blackNumberPanel.add(blackTimerLabel);

        blackTimerPanel = new JPanel(new BorderLayout());
        blackTimerPanel.setBorder(blackLine);
        blackTimerPanel.add(blackNumberPanel, BorderLayout.CENTER);

        Border redLine = BorderFactory.createLineBorder(profile.getP1Color(), 2);

        redTimerLabel = new JLabel(redPlayer.elapsedTimeToString(timeLimit));
        redTimerLabel.setFont(new Font("Sans_Serif", Font.PLAIN, 40));

        redNumberPanel = new JPanel();
        redNumberPanel.add(redTimerLabel);

        redTimerPanel = new JPanel(new BorderLayout());
        redTimerPanel.setBorder(redLine);
        redTimerPanel.add(redNumberPanel, BorderLayout.CENTER);

        timerDisplayPanel = new JPanel(new GridLayout(3, 1, 0, 3)); //2 rows, 1 col
        timerDisplayPanel.add(timerLabel);
        timerDisplayPanel.add(blackTimerPanel);
        timerDisplayPanel.add(redTimerPanel);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Sans_Serif", Font.BOLD, 18));
        statusLabel.setForeground(Color.WHITE);

        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusPanel.setBackground(new Color(41, 128, 185));
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        blackCapturedLabel = new JLabel();
        blackCapturedLabel.setForeground(profile.getP2Color());
        blackCapturedLabel.setFont(new Font("Sans_Serif", Font.BOLD, 16));

        redCapturedLabel = new JLabel();
        redCapturedLabel.setForeground(profile.getP1Color());
        redCapturedLabel.setFont(new Font("Sans_Serif", Font.BOLD, 16));

        capturedPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        capturedPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("So quan bi mat"),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        capturedPanel.add(blackCapturedLabel);
        capturedPanel.add(redCapturedLabel);

        this.add(timerDisplayPanel, BorderLayout.NORTH);
        this.add(statusPanel, BorderLayout.CENTER);
        this.add(capturedPanel, BorderLayout.SOUTH);

        this.setPreferredSize(new Dimension(220, 320));
        this.setMinimumSize(new Dimension(220, 320));
        refreshCapturedPieces();

    }

    public void updateRedTime() {

        blackTimerLabel.setForeground(Color.LIGHT_GRAY);
        redTimerLabel.setForeground(Color.BLACK);

        newThread = new Thread(() -> {
            while (redPlayer.isTimerRunning()) {
                SwingUtilities.invokeLater(() -> redTimerLabel.setText(redPlayer.elapsedTimeToString(timeLimit)));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        newThread.start();

    }

    public void updateBlackTime() {

        blackTimerLabel.setForeground(Color.BLACK);
        redTimerLabel.setForeground(Color.LIGHT_GRAY);

        newThread = new Thread(() -> {
            while (blackPlayer.isTimerRunning()) {
                SwingUtilities.invokeLater(() -> blackTimerLabel.setText(blackPlayer.elapsedTimeToString(timeLimit)));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        newThread.start();

    }

    public void showTurnStatus(String playerName) {
        updateStatusLabel("Đến lượt: " + playerName, new Color(41, 128, 185));
    }

    public void refreshCapturedPieces() {
        SwingUtilities.invokeLater(() -> {
            blackCapturedLabel.setText(formatCapturedPieces(blackPlayer.getName(), redPlayer.getPiecesCaptured()));
            redCapturedLabel.setText(formatCapturedPieces(redPlayer.getName(), blackPlayer.getPiecesCaptured()));
            capturedPanel.repaint();
        });
    }

    private String formatCapturedPieces(String playerName, List<Piece> capturedPieces) {
        if (capturedPieces.isEmpty()) {
            return playerName + ": 0";
        }

        Map<String, Integer> pieceCounts = new LinkedHashMap<>();
        for (Piece piece : capturedPieces) {
            pieceCounts.merge(piece.toString(), 1, Integer::sum);
        }

        StringBuilder details = new StringBuilder();
        for (Map.Entry<String, Integer> entry : pieceCounts.entrySet()) {
            if (details.length() > 0) {
                details.append(", ");
            }
            details.append(entry.getValue()).append(" ").append(entry.getKey());
        }

        return "<html><div style='width:170px;'>" + playerName + ": " + capturedPieces.size()
                + " (" + details + ")</div></html>";
    }

    private void updateStatusLabel(String message, Color backgroundColor) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("<html><div style='text-align:center;'>" + message + "</div></html>");
            statusPanel.setBackground(backgroundColor);
            statusLabel.setBackground(backgroundColor);
            statusPanel.repaint();
        });
    }

}
