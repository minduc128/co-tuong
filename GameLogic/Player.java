package GameLogic;


import java.util.ArrayList;

import GUI.Profile;
import GUI.TurnTimerPanel;
import GameLogic.Pieces.*;
import Run.Core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Player {

    protected Timer timer;

    private int id;
    private String name;
    private Piece.Side side;
    private ArrayList<Piece> piecesCaptured;
    private boolean checkmateStatus;
    private long timeElapsed;
    private Core core;

    private String color;

    public Player(int id, Piece.Side side, Profile profile) {
        this.id = id;
        this.name = "Player" + id;
        this.side = side;
        this.piecesCaptured = new ArrayList<Piece>();
        this.checkmateStatus = false;
        this.timeElapsed = 0;

        timer = new Timer();


        if (side == Piece.Side.DOWN) {
            color = profile.getP1Color().toString();
        } else {
            color = profile.getP2Color().toString();
        }

    }

    public Player(int id, String name, Piece.Side side, Core core) {
        this.id = id;
        this.name = name;
        this.side = side;
        this.piecesCaptured = new ArrayList<Piece>();
        this.checkmateStatus = false;
        this.timeElapsed = 0;
        this.core = core;

        timer = new Timer();

        if (side == Piece.Side.DOWN) {
            color = "Red";
        } else {
            color = "Black";
        }

    }

    public int getId() {
        return this.id;
    }


    public String getName() {
        return this.name;
    }


    public String getColor() {
        return color;
    }


    public Piece.Side getPlayerSide() {
        return this.side;
    }


    public void setPlayerSide(Piece.Side side) {
        this.side = side;
    }



    public void addPieceCaptured(Piece pieceCaptured) {
        piecesCaptured.add(pieceCaptured);
    }

    public ArrayList<Piece> getPiecesCaptured() {
        return piecesCaptured;
    }

    public void printPiecesCaptured() {
        System.out.print("Pieces Captured: ");

        for (int i = 0; i < piecesCaptured.size(); i++) {
            if (piecesCaptured.size() - 1 == i) {
                System.out.println(piecesCaptured.get(i));
            } else {
                System.out.print(piecesCaptured.get(i) + ", ");
            }
        }
    }

    public int getNumPiecesCaptured() {
        return this.piecesCaptured.size();
    }

    public void clearPiecesCaptured() {
        piecesCaptured.clear();
    }

    public boolean getCheckmateStatus() {
        return checkmateStatus;
    }

    public void setCheckmateStatus(Boolean checkmateStatus) {
        this.checkmateStatus = checkmateStatus;
    }

    public void startTurnTimer() {
        timer.start();

    }

    public void startTurnTimer(TurnTimerPanel panel) {
        timer.start();

        if (this.side == Piece.Side.DOWN) { //red
            panel.updateRedTime();

        } else {
            panel.updateBlackTime();

        }
    }

    public void stopTurnTimer() {
        timer.stop();
        timeElapsed += timer.getTime();
    }

    public long getElapsedTime() {
        return timeElapsed;
    }

    public String printElapsedTime() {
        Date date = new Date(timeElapsed);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String formatted = formatter.format(date);
        return formatted;

    }

    public String elapsedTimeToString() {
        Date date = new Date(timer.getTime() + timeElapsed);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String formatted = formatter.format(date);
        return formatted;

    }

    public String elapsedTimeToString(int timeLimit) {

        timeLimit = timeLimit * 60000;
        if (timer.getTime()+ timeElapsed >= timeLimit) {
            if (this.getPlayerSide() == Piece.Side.DOWN) {
                Board.setWinner(Board.PLAYER2_TIMEOUT_WIN);
                core.callEnd();
                return "00:00";

            } else {
                Board.setWinner(Board.PLAYER1_TIMEOUT_WIN);
                core.callEnd();
                return "00:00";
            }

        } else {

            Date date = new Date(timeLimit - (timer.getTime() + timeElapsed));
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
            String formatted = formatter.format(date);
            return formatted;
        }

    }

    public boolean isTimerRunning() {
        return timer.isStillRunning();
    }

}

