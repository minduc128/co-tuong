package GUI;

import javax.swing.*;
import java.awt.*;

public final class RulesDialog {
    private RulesDialog() {
    }

    public static void showRules(Component parent) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Luat choi / Rules", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Tieng Viet", createScrollPane(getVietnameseRules()));
        tabs.addTab("English", createScrollPane(getEnglishRules()));

        JButton closeButton = new JButton("Dong / Close");
        closeButton.addActionListener(event -> dialog.dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(closeButton);

        dialog.add(tabs, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setSize(new Dimension(640, 500));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static JScrollPane createScrollPane(String html) {
        JEditorPane content = new JEditorPane();
        content.setContentType("text/html");
        content.setEditable(false);
        content.setText(html);
        content.setCaretPosition(0);
        return new JScrollPane(content);
    }

    private static String getVietnameseRules() {
        return "<html><body style='font-family:Sans-Serif;padding:10px;'>" +
                "<h2>Luat thang thua</h2>" +
                "<ul>" +
                "<li>Chieu bi Tuong doi phuong: thang.</li>" +
                "<li>Doi phuong het thoi gian: thang.</li>" +
                "<li>Neu the co dan den hoa theo luat hien tai cua game: hoa.</li>" +
                "</ul>" +
                "<h2>Cach di tung quan</h2>" +
                "<ul>" +
                "<li><b>Tuong</b>: di 1 o ngang hoac doc, chi trong cung.</li>" +
                "<li><b>Si</b>: di cheo 1 o, chi trong cung.</li>" +
                "<li><b>Tuong/Elephant</b>: di cheo 2 o, khong qua song.</li>" +
                "<li><b>Xe</b>: di ngang hoac doc khong gioi han neu khong bi can.</li>" +
                "<li><b>Ma</b>: di hinh chu L. Neu bi chan chan thi khong di duoc.</li>" +
                "<li><b>Phao</b>: di nhu Xe. Khi an quan phai co dung 1 quan lam ngoi o giua.</li>" +
                "<li><b>Tot</b>: di thang 1 o. Qua song roi moi duoc di ngang, khong di lui.</li>" +
                "</ul>" +
                "<h2>Goi y chien thuat co ban</h2>" +
                "<ul>" +
                "<li>Ra Xe, Phao, Ma som de mo the.</li>" +
                "<li>Giu trung lo va tranh de Tuong lo mat.</li>" +
                "<li>Truoc moi nuoc di, can nhac nuoc phan cong tiep theo cua doi phuong.</li>" +
                "</ul>" +
                "</body></html>";
    }

    private static String getEnglishRules() {
        return "<html><body style='font-family:Sans-Serif;padding:10px;'>" +
                "<h2>Win and Draw Conditions</h2>" +
                "<ul>" +
                "<li>Checkmate the opponent's General: win.</li>" +
                "<li>The opponent runs out of time: win.</li>" +
                "<li>If the current game state leads to a draw under the game's rules: draw.</li>" +
                "</ul>" +
                "<h2>How Each Piece Moves</h2>" +
                "<ul>" +
                "<li><b>General</b>: moves 1 square horizontally or vertically and must stay inside the palace.</li>" +
                "<li><b>Guard</b>: moves 1 square diagonally and must stay inside the palace.</li>" +
                "<li><b>Elephant</b>: moves 2 squares diagonally and cannot cross the river.</li>" +
                "<li><b>Chariot</b>: moves any number of squares horizontally or vertically if not blocked.</li>" +
                "<li><b>Horse</b>: moves in an L-shape. If its leg is blocked, it cannot move.</li>" +
                "<li><b>Cannon</b>: moves like a Chariot. To capture, it must jump over exactly 1 piece.</li>" +
                "<li><b>Soldier</b>: moves forward 1 square. After crossing the river, it may also move sideways, but never backward.</li>" +
                "</ul>" +
                "<h2>Basic Practical Tips</h2>" +
                "<ul>" +
                "<li>Develop Chariots, Cannons, and Horses early.</li>" +
                "<li>Control the center and avoid exposing your General.</li>" +
                "<li>Before every move, consider the opponent's next counterplay.</li>" +
                "</ul>" +
                "</body></html>";
    }
}
