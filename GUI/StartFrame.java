package GUI;

import GameLogic.SimpleBot;
import Run.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;

public class StartFrame extends JFrame {
    private static final String START_BACKGROUND_PATH = "/Pictures/start_background.jpg";

    private JPanel tippyTop;
    private JPanel top;
    private JPanel middle;
    private JPanel bottom;
    private JPanel mainPanel;
    private JPanel topLeftPanel;
    private JPanel topRightPanel;
    private JPanel p1Colors;
    private JPanel p2Colors;
    private JPanel p1Names;
    private JPanel p2Names;
    private JPanel bgColors;
    private JPanel timers;
    private JPanel fgColors;
    private JPanel lineColors;
    private JButton p1Chooser;
    private JButton p2Chooser;
    private JButton bgChooser;
    private JButton begin;
    private JButton loadGame;
    private JButton fgChooser;
    private JButton lineChooser;
    private JTextField p1Name;
    private JTextField p2Name;
    private JCheckBox playVsComputer;
    private JComboBox<String> difficultySelector;
    private BoardFrame boardFrame;
    private JSpinner minutes;
    private Profile[] themes;
    private JComboBox profileSelector;
    private Image logo;
    private Profile profile;

    public StartFrame(Core core) {
        super("Menu");
        setContentPane(new StartBackgroundPanel());
        setLayout(new BorderLayout());

        logo = new ImageIcon(getClass().getResource("/Pictures/LogoDHGTVT.png")).getImage();
        JLabel logoPic = new JLabel(new ImageIcon(logo.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));

        profile = new Profile();
        Profile dark = new Profile(Color.magenta, new Color(0, 255, 132), Color.DARK_GRAY, Color.LIGHT_GRAY, Color.BLACK);
        Profile light = new Profile(Color.pink, Color.LIGHT_GRAY, new Color(230, 216, 195), Color.white, Color.DARK_GRAY);
        Profile minimal = new Profile(Color.LIGHT_GRAY, Color.LIGHT_GRAY, new Color(86, 86, 86), Color.BLACK, Color.white);
        Profile basic = new Profile();

        themes = new Profile[4];
        themes[0] = basic;
        themes[1] = dark;
        themes[2] = light;
        themes[3] = minimal;

        String[] themeNames = new String[]{"Basic", "Dark", "Light", "Minimal"};

        Font bigFont = new Font("Sans_Serif", Font.BOLD, 34);
        Font mediumFont = new Font("Sans_Serif", Font.BOLD, 17);
        Font labelFont = new Font("Sans_Serif", Font.BOLD, 20);
        Font smallTitleFont = new Font("Sans_Serif", Font.BOLD, 18);

        tippyTop = new JPanel(new BorderLayout(20, 0));
        JLabel rightTitle = new JLabel("CO TUONG");
        JLabel bottomTitle = new JLabel("Nhom 15");
        rightTitle.setFont(bigFont);
        bottomTitle.setFont(smallTitleFont);
        rightTitle.setHorizontalAlignment(SwingConstants.CENTER);
        logoPic.setHorizontalAlignment(SwingConstants.CENTER);
        bottomTitle.setHorizontalAlignment(SwingConstants.CENTER);
        rightTitle.setForeground(new Color(32, 23, 12));
        bottomTitle.setForeground(new Color(84, 50, 16));


        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 6));
        titlePanel.add(rightTitle);
        titlePanel.add(bottomTitle);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        titlePanel.setOpaque(false);
        rightTitle.setHorizontalAlignment(SwingConstants.CENTER);
        bottomTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.add(logoPic);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        logoPanel.setOpaque(false);

        tippyTop.add(logoPanel, BorderLayout.WEST);
        tippyTop.add(titlePanel, BorderLayout.CENTER);

        JPanel p1TitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        JLabel p1Title = new JLabel("Player 1:");
        p1Title.setFont(labelFont);
        p1Title.setForeground(new Color(46, 29, 12));
        p1TitlePanel.add(p1Title);

        p1Names = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        JLabel p1NameLabel = new JLabel("Name: ");
        p1NameLabel.setFont(mediumFont);
        p1NameLabel.setForeground(new Color(58, 35, 14));
        p1Names.add(p1NameLabel);
        p1Name = new JTextField("Player 1", 12);
        p1Name.setFont(new Font("Sans_Serif", Font.PLAIN, 17));
        p1Names.add(p1Name);

        p1Colors = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        p1Colors.add(new JLabel("Color: "));
        p1Chooser = new JButton("Select");

        topLeftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        topLeftPanel.add(p1TitlePanel);
        topLeftPanel.add(p1Names);

        JPanel p2TitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        JLabel p2Title = new JLabel("Player 2:");
        p2Title.setFont(labelFont);
        p2Title.setForeground(new Color(46, 29, 12));
        p2TitlePanel.add(p2Title);

        p2Names = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        JLabel p2NameLabel = new JLabel("Name: ");
        p2NameLabel.setFont(mediumFont);
        p2NameLabel.setForeground(new Color(58, 35, 14));
        p2Names.add(p2NameLabel);
        p2Name = new JTextField("Player 2", 12);
        p2Name.setFont(new Font("Sans_Serif", Font.PLAIN, 17));
        p2Names.add(p2Name);

        playVsComputer = new JCheckBox("Player vs Computer");
        playVsComputer.setFont(new Font("Sans_Serif", Font.BOLD, 16));
        playVsComputer.setForeground(new Color(46, 29, 12));
        playVsComputer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleOpponentMode();
            }
        });

        difficultySelector = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultySelector.setFont(new Font("Sans_Serif", Font.BOLD, 13));
        difficultySelector.setEnabled(false);

        p2Colors = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        p2Colors.add(new JLabel("Color: "));
        p2Chooser = new JButton("Select");

        topRightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        topRightPanel.add(p2TitlePanel);
        topRightPanel.add(p2Names);
        topRightPanel.add(playVsComputer);
        topRightPanel.add(difficultySelector);

        top = new JPanel(new GridLayout(0, 2));
        top.add(topLeftPanel);
        top.add(topRightPanel);

        bgColors = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 5));
        JLabel bgLabel = new JLabel("Background Color");
        bgLabel.setFont(mediumFont);
        bgLabel.setForeground(new Color(58, 35, 14));
        bgColors.add(bgLabel);
        bgChooser = new JButton("Select");
        bgChooser.setFont(new Font("Sans_Serif", Font.BOLD, 16));
        bgColors.add(bgChooser);

        fgColors = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 5));
        JLabel fgLabel = new JLabel("Foreground Color");
        fgLabel.setFont(mediumFont);
        fgLabel.setForeground(new Color(58, 35, 14));
        fgColors.add(fgLabel);
        fgChooser = new JButton("Select");
        fgChooser.setFont(new Font("Sans_Serif", Font.BOLD, 16));
        fgColors.add(fgChooser);

        lineColors = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 5));
        JLabel lineLabel = new JLabel("Line Colors");
        lineLabel.setFont(mediumFont);
        lineLabel.setForeground(new Color(58, 35, 14));
        lineColors.add(lineLabel);
        lineChooser = new JButton("Select");
        lineChooser.setFont(new Font("Sans_Serif", Font.BOLD, 16));
        lineColors.add(lineChooser);

        timers = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 5));
        JLabel timeLabel = new JLabel("Time Limit");
        timeLabel.setFont(mediumFont);
        timeLabel.setForeground(new Color(58, 35, 14));
        timers.add(timeLabel);
        minutes = new JSpinner(new SpinnerNumberModel(10, 1, 60, 1));
        minutes.setFont(new Font("Sans_Serif", Font.BOLD, 16));
        timers.add(minutes);

        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 10));
        profileSelector = new JComboBox(themeNames);
        profileSelector.setFont(new Font("Sans_Serif", Font.BOLD, 13));
        comboPanel.add(profileSelector);
        comboPanel.add(timers);

        middle = new JPanel(new GridLayout(0, 2));
        JPanel middleLeftHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        middleLeftHolder.add(bgColors);
        middleLeftHolder.add(fgColors);
        middleLeftHolder.add(lineColors);
        JPanel middleRightHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        middleRightHolder.add(comboPanel);

        middle.add(middleLeftHolder);
        middle.add(middleRightHolder);

        bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        begin = new JButton("Start New Game");
        begin.setFont(new Font("Sans_Serif", Font.BOLD, 18));
        loadGame = new JButton("Load Game");
        loadGame.setEnabled(false);

        JPanel previewPanel = new PreviewPanel();
        previewPanel.setPreferredSize(new Dimension(300, 130));

        JPanel beginPanel = new JPanel();
        beginPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        beginPanel.add(begin, CENTER_ALIGNMENT);

        bottom.add(previewPanel);
        bottom.add(beginPanel);

        p1Chooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color temp = JColorChooser.showDialog(null, "Choose Player 1 Color", profile.getP1Color());
                if (temp != null) {
                    profile.setP1Color(temp);
                    previewPanel.repaint();
                }
            }
        });

        p2Chooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color temp = JColorChooser.showDialog(null, "Choose Player 2 Color", profile.getP2Color());
                if (temp != null) {
                    profile.setP2Color(temp);
                    previewPanel.repaint();
                }
            }
        });

        bgChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color temp = JColorChooser.showDialog(null, "Choose Background Color", profile.getBackground());
                if (temp != null) {
                    profile.setBackGround(temp);
                    previewPanel.repaint();
                }
            }
        });

        fgChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color temp = JColorChooser.showDialog(null, "Choose Foreground Color", profile.getForeGround());
                if (temp != null) {
                    profile.setForeGround(temp);
                    previewPanel.repaint();
                }
            }
        });

        lineChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color temp = JColorChooser.showDialog(null, "Choose Line Color", profile.getLineColor());
                if (temp != null) {
                    profile.setLineColor(temp);
                    previewPanel.repaint();
                }
            }
        });

        profileSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseProfile();
                previewPanel.repaint();
            }
        });

        begin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                profile.setMinutes((int) minutes.getValue());
                profile.setP1String(p1Name.getText());
                profile.setP2String(p2Name.getText());
                core.start(profile, playVsComputer.isSelected(), getSelectedDifficulty());

                boardFrame = core.getBoardFrame();
                boardFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                boardFrame.setSize(900, 700);
                boardFrame.setVisible(true);
                setVisible(false);
            }
        });

        makePanelTransparent(tippyTop);
        makePanelTransparent(top);
        makePanelTransparent(middle);
        makePanelTransparent(bottom);
        makePanelTransparent(topLeftPanel);
        makePanelTransparent(topRightPanel);
        makePanelTransparent(p1Names);
        makePanelTransparent(p2Names);
        makePanelTransparent(p1Colors);
        makePanelTransparent(p2Colors);
        makePanelTransparent(bgColors);
        makePanelTransparent(fgColors);
        makePanelTransparent(lineColors);
        makePanelTransparent(timers);
        makePanelTransparent(comboPanel);
        makePanelTransparent(beginPanel);
        makePanelTransparent(previewPanel);
        makePanelTransparent(playVsComputer);
        makePanelTransparent(p1TitlePanel);
        makePanelTransparent(p2TitlePanel);
        makePanelTransparent(middleLeftHolder);
        makePanelTransparent(middleRightHolder);

        mainPanel = new JPanel(new GridLayout(4, 0));
        mainPanel.setOpaque(false);
        mainPanel.add(tippyTop);
        mainPanel.add(top);
        mainPanel.add(middle);
        mainPanel.add(bottom);

        add(mainPanel, BorderLayout.CENTER);
        makeLabelsTransparent(mainPanel);
        styleAllLabels(mainPanel, mediumFont);
        styleControlPanel(topLeftPanel);
        styleControlPanel(topRightPanel);
        styleControlPanel(middleLeftHolder);
        styleControlPanel(middleRightHolder);
        styleControlPanel(beginPanel);
        setPreferredSize(new Dimension(700, 650));
        pack();
        setResizable(false);
        setVisible(true);
    }

    private void chooseProfile() {
        this.profile = themes[profileSelector.getSelectedIndex()];
    }

    private void toggleOpponentMode() {
        if (playVsComputer.isSelected()) {
            p2Name.setText("Computer");
            p2Name.setEnabled(false);
            difficultySelector.setEnabled(true);
        } else {
            p2Name.setEnabled(true);
            difficultySelector.setEnabled(false);
            if ("Computer".equals(p2Name.getText())) {
                p2Name.setText("Player 2");
            }
        }
    }

    private SimpleBot.Difficulty getSelectedDifficulty() {
        if (!playVsComputer.isSelected()) {
            return SimpleBot.Difficulty.EASY;
        }

        String selected = (String) difficultySelector.getSelectedItem();
        if ("Hard".equals(selected)) {
            return SimpleBot.Difficulty.HARD;
        }
        if ("Medium".equals(selected)) {
            return SimpleBot.Difficulty.MEDIUM;
        }
        return SimpleBot.Difficulty.EASY;
    }

    private void makePanelTransparent(JComponent component) {
        component.setOpaque(false);
    }

    private void makeLabelsTransparent(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                ((JLabel) component).setOpaque(false);
            }
            if (component instanceof Container) {
                makeLabelsTransparent((Container) component);
            }
        }
    }

    private void styleAllLabels(Container container, Font defaultFont) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getFont() == null || label.getFont().getSize() < defaultFont.getSize()) {
                    label.setFont(defaultFont);
                }
                if (label.getForeground() == null || Color.BLACK.equals(label.getForeground())) {
                    label.setForeground(new Color(58, 35, 14));
                }
            }
            if (component instanceof Container) {
                styleAllLabels((Container) component, defaultFont);
            }
        }
    }

    private void styleControlPanel(JComponent panel) {
        panel.setOpaque(true);
        panel.setBackground(new Color(255, 248, 235, 115));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 248, 230, 170), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    class PreviewPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(profile.getBackground());
            int xOffset = 45;
            int yOffset = 10;
            g2.fill(new Rectangle2D.Double(xOffset, yOffset, 200, 100));

            g2.setColor(profile.getLineColor());
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(xOffset, 50 + yOffset, 200 + xOffset, 50 + yOffset);
            g2.drawLine(50 + xOffset, yOffset, 50 + xOffset, 100 + yOffset);
            g2.drawLine(150 + xOffset, yOffset, 150 + xOffset, 100 + yOffset);

            g2.setColor(profile.getForeGround());
            g2.fill(new Ellipse2D.Double(xOffset + 10, 10 + yOffset, 80, 80));
            g2.fill(new Ellipse2D.Double(xOffset + 110, 10 + yOffset, 80, 80));

            g2.setStroke(new BasicStroke(5));
            g2.setColor(profile.getP1Color());
            g2.draw(new Ellipse2D.Double(10 + xOffset, 10 + yOffset, 80, 80));
            g2.drawString("General", 27 + xOffset, 55 + yOffset);

            g2.setColor(profile.getP2Color());
            g2.draw(new Ellipse2D.Double(110 + xOffset, 10 + yOffset, 80, 80));
            g2.drawString("Elephant", 125 + xOffset, 55 + yOffset);
        }
    }

    class StartBackgroundPanel extends JPanel {
        private final Image backgroundImage;

        StartBackgroundPanel() {
            URL resource = getClass().getResource(START_BACKGROUND_PATH);
            backgroundImage = resource == null ? null : new ImageIcon(resource).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                GradientPaint paint = new GradientPaint(0, 0, new Color(248, 242, 223),
                        getWidth(), getHeight(), new Color(222, 206, 171));
                g2.setPaint(paint);
                g2.fillRect(0, 0, getWidth(), getHeight());

                int boxWidth = getWidth() - 80;
                int boxHeight = 140;
                int boxX = 40;
                int boxY = getHeight() - boxHeight - 30;

                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 24, 24);
                g2.setColor(new Color(120, 88, 48));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 24, 24);

                g2.setFont(new Font("Sans_Serif", Font.BOLD, 18));
                g2.drawString("Background placeholder", boxX + 20, boxY + 42);
                g2.setFont(new Font("Sans_Serif", Font.PLAIN, 15));
                g2.drawString("Dat anh tai: " + START_BACKGROUND_PATH, boxX + 20, boxY + 74);
                g2.drawString("Thu muc that: Pictures\\start_background.png", boxX + 20, boxY + 102);
            }

            g2.dispose();
        }
    }
}
