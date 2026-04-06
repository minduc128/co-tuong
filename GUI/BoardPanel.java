package GUI;

import GameLogic.Board;
import GameLogic.Move;
import GameLogic.Point;
import Run.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.net.URL;


public class BoardPanel extends JPanel {
	private static final String GAME_BACKGROUND_PATH = "/Pictures/game_background.png";
	protected Board board;
	private String language = "English";
	private Icons[][] pointIcons = new Icons[10][9];
	private boolean[][] legalTargets = new boolean[10][9];
	private int squareWidth;
	private int[] pressLoc = new int[2], releaseLoc = new int[2];
	private boolean pressed = false, pressIsValid = false;
	private Core core;
	private Profile profile;
	private Image backgroundImage;

	public BoardPanel(Core core) {
		this.core = core;
		this.board = core.getBoard();
		this.profile = core.getProfile();
		this.setBackground(profile.background());
		setOpaque(false);
		URL backgroundResource = getClass().getResource(GAME_BACKGROUND_PATH);
		if (backgroundResource != null) {
			backgroundImage = new ImageIcon(backgroundResource).getImage();
		}
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				pointIcons[y][x] = new Icons(x, y, board.getPoint(x, y));
				add(pointIcons[y][x]);
			}
		}
		setLayout(null);
	}

	void setLanguage(String language) {
		this.language = language;
	}

	public void paintComponent(Graphics g) {
		int xDisplacement, yDisplacement;

		if (8 * getHeight() > 9 * getWidth())
			squareWidth = getWidth() / 10;
		else
			squareWidth = getHeight() / 10;
		int radius = squareWidth * 4 / 10;

		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				xDisplacement = (4 - x) * squareWidth;
				yDisplacement = (10 - 2 * y) * squareWidth / 2 - squareWidth / 3;
				board.getPoint(x, y).setPosition(getWidth() / 2 - xDisplacement, 8 + getHeight() / 2 - yDisplacement);
				pointIcons[y][x].setLocation(board.getPoint(x, y).getX() - radius, board.getPoint(x, y).getY() - radius);
				pointIcons[y][x].setSize(radius * 2, radius * 2);
			}
		}
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		drawBackground(g2);
		drawBoard(g2);
		drawRiver(g2);
	}

	private void drawBackground(Graphics2D g2) {
		if (backgroundImage != null) {
			g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		} else {
			GradientPaint paint = new GradientPaint(0, 0, new Color(236, 223, 187),
					getWidth(), getHeight(), new Color(183, 144, 92));
			g2.setPaint(paint);
			g2.fillRect(0, 0, getWidth(), getHeight());

			g2.setColor(new Color(255, 255, 255, 95));
			g2.fillRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 28, 28);
			g2.setColor(new Color(102, 72, 35, 150));
			g2.setStroke(new BasicStroke(2f));
			g2.drawRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 28, 28);

			g2.setColor(new Color(96, 58, 20, 185));
			g2.setFont(new Font("Sans_Serif", Font.BOLD, 20));
			g2.drawString("Background placeholder", 40, getHeight() - 72);
			g2.setFont(new Font("Sans_Serif", Font.PLAIN, 15));
			g2.drawString("Dat anh tai: " + GAME_BACKGROUND_PATH, 40, getHeight() - 44);
			g2.drawString("Thu muc that: Pictures\\game_background.png", 40, getHeight() - 20);
		}

		g2.setColor(new Color(255, 248, 230, 105));
		g2.fillRoundRect(35, 25, getWidth() - 70, getHeight() - 50, 24, 24);
	}

	private void drawBoard(Graphics2D g2) {
		g2.setColor(profile.getLineColor());
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				if (x == 8)
					continue;
				g2.drawLine(board.getPoint(x, y).getX(), board.getPoint(x, y).getY(), board.getPoint(x + 1, y).getX(), board.getPoint(x + 1, y).getY());
			}
			for (int x = 0; x < 9; x++) {
				if (y == 9)
					continue;
				else if (y == 4 && x < 8 && x > 0)
					continue;
				g2.drawLine(board.getPoint(x, y).getX(), board.getPoint(x, y).getY(), board.getPoint(x, y + 1).getX(), board.getPoint(x, y + 1).getY());
			}
		}

		g2.drawLine(board.getPoint(3, 0).getX(), board.getPoint(3, 0).getY(), board.getPoint(5, 2).getX(), board.getPoint(5, 2).getY());
		g2.drawLine(board.getPoint(5, 0).getX(), board.getPoint(5, 0).getY(), board.getPoint(3, 2).getX(), board.getPoint(3, 2).getY());
		g2.drawLine(board.getPoint(3, 7).getX(), board.getPoint(3, 7).getY(), board.getPoint(5, 9).getX(), board.getPoint(5, 9).getY());
		g2.drawLine(board.getPoint(5, 7).getX(), board.getPoint(5, 7).getY(), board.getPoint(3, 9).getX(), board.getPoint(3, 9).getY());

		drawPoint(g2, board.getPoint(1, 2), 0, 360);
		drawPoint(g2, board.getPoint(7, 2), 0, 360);
		drawPoint(g2, board.getPoint(0, 3), -90, 90);
		drawPoint(g2, board.getPoint(2, 3), 0, 360);
		drawPoint(g2, board.getPoint(4, 3), 0, 360);
		drawPoint(g2, board.getPoint(6, 3), 0, 360);
		drawPoint(g2, board.getPoint(8, 3), 90, 270);

		drawPoint(g2, board.getPoint(0, 6), -90, 90);
		drawPoint(g2, board.getPoint(2, 6), 0, 360);
		drawPoint(g2, board.getPoint(4, 6), 0, 360);
		drawPoint(g2, board.getPoint(6, 6), 0, 360);
		drawPoint(g2, board.getPoint(8, 6), 90, 270);
		drawPoint(g2, board.getPoint(1, 7), 0, 360);
		drawPoint(g2, board.getPoint(7, 7), 0, 360);
	}


	private void drawPoint(Graphics2D g2, Point point, int startAngle, int endAngle) {
		for (int x = startAngle / 90; x < endAngle / 90; x++) {
			g2.drawLine(point.getX() + (int) (Math.cos(Math.toRadians(45 + 90 * x)) * squareWidth / 10),
					point.getY() + (int) (Math.sin(Math.toRadians(45 + 90 * x)) * squareWidth / 10),
					point.getX() + (int) (Math.cos(Math.toRadians(45 + 90 * x)) * squareWidth / 10),
					point.getY() + (int) (Math.sin(Math.toRadians(45 + 90 * x)) * squareWidth / 5));
			g2.drawLine(point.getX() + (int) (Math.cos(Math.toRadians(45 + 90 * x)) * squareWidth / 10),
					point.getY() + (int) (Math.sin(Math.toRadians(45 + 90 * x)) * squareWidth / 10),
					point.getX() + (int) (Math.cos(Math.toRadians(45 + 90 * x)) * squareWidth / 5),
					point.getY() + (int) (Math.sin(Math.toRadians(45 + 90 * x)) * squareWidth / 10));
		}
	}

	private void drawRiver(Graphics2D g2) {
		if (language.equals("English")) {
			g2.setFont(new Font("Sans_Serif", Font.PLAIN, 18));
			FontMetrics metrics = g2.getFontMetrics();
			int xCoord = (getWidth() - metrics.stringWidth("River")) / 2;
			int yCoord = getHeight() / 2 - metrics.getHeight() / 2 + metrics.getAscent();
			g2.drawString("River", xCoord, yCoord);
		}
	}

	private void updateLegalTargets() {
		if (!pressIsValid) {
			clearLegalTargets();
			return;
		}
		legalTargets = board.getLegalTargets(pressLoc[0], pressLoc[1]);
	}

	private void clearLegalTargets() {
		legalTargets = new boolean[10][9];
	}

	public void userRepaint() {
		repaint();
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				pointIcons[y][x].repaint();
			}
		}
	}

	private class Icons extends JLabel implements MouseListener {
		private int x, y;
		private Point point;

		Icons(int x, int y, Point point) {
			this.x = x;
			this.y = y;
			this.point = point;
			setHorizontalAlignment(JLabel.CENTER);
			setVerticalAlignment(JLabel.CENTER);
			addMouseListener(this);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			this.point = board.getPoint(x, y);
			for (int y = 0; y < 10; y++) {
				for (int x = 0; x < 9; x++) {
					pointIcons[y][x].setIcon(null);
				}
			}

			setPieceImages(g2, "/Pictures/chinese_");
			drawTargetMarker(g2);
		}

		void drawTargetMarker(Graphics2D g2) {
			if (!pressed || !pressIsValid || !legalTargets[y][x]) {
				return;
			}
			if (pressLoc[0] == x && pressLoc[1] == y) {
				return;
			}

			if (point.getPiece() == null) {
				int markerSize = Math.max(10, getWidth() / 3);
				int markerX = (getWidth() - markerSize) / 2;
				int markerY = (getHeight() - markerSize) / 2;
				g2.setColor(new Color(46, 204, 113, 170));
				g2.fillOval(markerX, markerY, markerSize, markerSize);
				return;
			}

			Stroke oldStroke = g2.getStroke();
			g2.setStroke(new BasicStroke(Math.max(2, getWidth() / 14)));
			g2.setColor(new Color(231, 76, 60, 190));
			g2.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
			g2.setStroke(oldStroke);
		}

		void setPieceImages(Graphics2D g2, String fileName) {
			try {
				if (point.getPiece() != null) {
					fileName += point.getPiece().getImageName();
					Image scaledImage = new ImageIcon(getClass().getResource(fileName)).getImage();
					scaledImage = scaledImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);

					setIcon(new ImageIcon(scaledImage));
					g2.setClip(new Ellipse2D.Float(2, 2, getWidth() - 4, getHeight() - 4));
					g2.drawImage(scaledImage, 0, 0, null);
					if (pressed && point.getPiece().equals(board.getPoint(pressLoc[0], pressLoc[1]).getPiece())) {
						BasicStroke s = new BasicStroke(3);
						g2.setStroke(s);
						g2.setColor(Color.yellow);
						g2.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
					}
				}
			} catch (Exception e) {
				System.out.print(e);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!core.isHumanTurn()) {
				return;
			}
			if (!pressed) {
				if (storePressed()) {
					pressed = true;
					updateLegalTargets();
				}
			}
			else {
				if (storeReleased()) {
					if (board.getPoint(pressLoc[0], pressLoc[1]).getPiece() != null &&
							board.getPoint(releaseLoc[0], releaseLoc[1]).getPiece() != null &&
							(board.getPoint(pressLoc[0], pressLoc[1]).getPiece().getSide() ==
									board.getPoint(releaseLoc[0], releaseLoc[1]).getPiece().getSide())) {
						storePressed();
						updateLegalTargets();
					} else {
						pressed = false;
						clearLegalTargets();
						sendMove(new Move(pressLoc[0], pressLoc[1], releaseLoc[0], releaseLoc[1]));
					}
				}
			}
			BoardPanel.this.repaint();
			for (int y = 0; y < 10; y++) {
				for (int x = 0; x < 9; x++) {
					pointIcons[y][x].repaint();
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (!pressed) {
				storePressed();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		void sendMove(Move move) {
			core.playMove(move);
		}

		boolean storePressed() {
			if (board.getPoint(x, y).getPiece() == null)
				pressIsValid = false;
			else {
				pressIsValid = true;
				pressLoc[0] = x;
				pressLoc[1] = y;
				return true;
			}
			clearLegalTargets();
			return false;
		}

		boolean storeReleased() {
			if (pressIsValid) {
				releaseLoc[0] = x;
				releaseLoc[1] = y;
				return true;
			}
			return false;
		}
	}
}
