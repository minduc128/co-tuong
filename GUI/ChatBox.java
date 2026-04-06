package GUI;

import GameLogic.Board;
import Run.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

class ChatBox extends JPanel {
    private JTextArea systemOutput;

    public ChatBox(Core core) {
        JLabel title = new JLabel("Chat Log");
        systemOutput = new JTextArea(14, 18);
        JScrollPane scrollPane = new JScrollPane(systemOutput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        systemOutput.setCaretPosition(systemOutput.getDocument().getLength());
        setPreferredSize(new Dimension(220, 400));


        JButton endGameBtn = new JButton("End Game");
        endGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Board.setWinner(0);
                core.callEnd();
            }
        });
        add(endGameBtn, BorderLayout.SOUTH);
    }

    public void appendText(final String text) {
        systemOutput.setText(systemOutput.getText() + text);
    }
}

class StreamIntake extends OutputStream {

    private String string = "";
    private ChatBox chat;
    private PrintStream system;

    public StreamIntake(ChatBox chat, PrintStream system) {
        this.system = system;
        this.chat = chat;
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        String value = Character.toString(c);
        string += value;
        if (value.equals("\n")) {
            chat.appendText(string);
            string = "";
        }
        system.print(c);
    }
}