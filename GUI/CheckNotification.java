package GUI;

import javax.swing.*;
import java.awt.*;

public class CheckNotification {
    private final Window owner;
    private JWindow window;
    private Timer hideTimer;

    public CheckNotification(Window owner) {
        this.owner = owner;
    }

    public void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            disposeCurrentWindow();

            window = new JWindow(owner);
            window.setBackground(new Color(0, 0, 0, 0));

            JLabel label = new JLabel(message, SwingConstants.CENTER);
            label.setOpaque(true);
            label.setForeground(Color.WHITE);
            label.setBackground(new Color(192, 57, 43, 235));
            label.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
            label.setFont(new Font("Sans_Serif", Font.BOLD, 26));

            JPanel root = new JPanel(new BorderLayout());
            root.setOpaque(false);
            root.add(label, BorderLayout.CENTER);

            window.setContentPane(root);
            window.pack();
            window.setAlwaysOnTop(true);
            window.setLocationRelativeTo(owner);
            window.setVisible(true);

            hideTimer = new Timer(3000, e -> disposeCurrentWindow());
            hideTimer.setRepeats(false);
            hideTimer.start();
        });
    }

    private void disposeCurrentWindow() {
        if (hideTimer != null && hideTimer.isRunning()) {
            hideTimer.stop();
        }
        if (window != null) {
            window.setVisible(false);
            window.dispose();
            window = null;
        }
    }
}
