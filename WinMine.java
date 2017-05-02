/*
David Kalish
CSCI-E10b
final project
Minesweeper
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

enum Face {
    OK,
    NERVOUS,
    DEAD,
    WON
}

enum MOUSE_STATUS {
    PRESSED,
    RELEASED,
    EXITED
}

class MineButton extends JButton {
    private int adj_mines;
    private boolean isMine;
    private boolean flagged;
    private boolean revealed;

    MineButton(String s, int myAdjMines, boolean myIsMine) {
        super(s);
        super.setRolloverEnabled(false);
        super.setFocusPainted(false);
        this.adj_mines = myAdjMines;
        this.isMine = myIsMine;
        this.flagged = false;
        this.revealed = false;
    }

    MineButton(int myAdjMines, boolean myIsMine) {
        this("", myAdjMines, myIsMine);
    }

    public void decorateClicked() {
        super.setContentAreaFilled(false);
        super.setBorderPainted(true);
        super.setOpaque(false);
        this.revealed = true;

        ImageIcon tileIcon;
        if (hasMine()) {
            tileIcon = new ImageIcon(getClass().getResource("img/mine.png"));
        } else {
            tileIcon = new ImageIcon(getClass().getResource("img/num-2.png"));
        }
        this.setIcon(tileIcon);
    }

    public boolean hasMine() {
        return this.isMine;
    }

    public boolean getRevealed() {
        return this.revealed;
    }

    public void toggleFlag() {
        if (this.flagged) this.flagged = false;
        else this.flagged = true;

        this.decorateFlag();
    }

    public boolean getFlagged() {
        return this.flagged;
    }

    private void decorateFlag() {
        if (getFlagged()) {
            ImageIcon flagIcon = new ImageIcon(
                getClass().getResource("img/flag.png"));
            this.setIcon(flagIcon);
        } else {
            this.setIcon(null);
        }

        // keep the button looking unpressed and normal
        super.setModel(new DefaultButtonModel() {
            @Override
            public boolean isArmed()
            {
                return false;
            }

            @Override
            public boolean isPressed()
            {
                return false;
            }
        });
    }
}

class FaceButton extends JButton {
    Face face;

    FaceButton(Face myFace) {
        this.face = myFace;
        super.setFocusPainted(false);
        showFace(this.face);
    }
    FaceButton() {
        this(Face.OK);
    }

    void showFace(Face myface) {
        this.face = myface;
        ImageIcon faceImg;
        switch (face) {
            case OK:
                faceImg = new ImageIcon(
                    getClass().getResource("img/face-ok.png"));
                break;
            case NERVOUS:
                faceImg = new ImageIcon(
                    getClass().getResource("img/face-nervous.png"));
                break;
            case WON:
                faceImg = new ImageIcon(
                    getClass().getResource("img/face-won.png"));
                break;
            case DEAD:
                faceImg = new ImageIcon(
                    getClass().getResource("img/face-dead.png"));
                break;
            default:
                faceImg = new ImageIcon(
                    getClass().getResource("img/face-ok.png"));
                break;
        }
        // System.out.println(faceImg);
        // System.out.println(faceImg.getImageLoadStatus());
        this.setIcon(faceImg);

    }
}

class WinMine {
    public static JFrame WM_WINDOW;
    public static JTextField MINES_LEFT = new JTextField(2);
    public static JTextField TIMER = new JTextField(2);
    public static FaceButton FACEBUTTON;
    public static JPanel MINESREMAININGDISPLAY;
    public static MineButton[][] MINE_GRID;

    public static void main(String[] args) {
        doLayout();
        System.out.println("main()");
    }

    public WinMine() {
        doLayout();
        System.out.println("WinMine");
    }

    private static void doLayout() {
        // set up JFrame
        WM_WINDOW = new JFrame();
        WM_WINDOW.setResizable(false);
        WM_WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // make the layout.
        // make the upper panels
        JPanel timerDisplay = makeDisplay(MINES_LEFT);
        makeFaceButton();
        JPanel minesRemainingDisplay = makeDisplay(TIMER);

        // put upper panel things into a panel
        JPanel upperPanel = new JPanel();
        BorderLayout bordLay = new BorderLayout(0,0);
        upperPanel.setLayout(bordLay);
        upperPanel.add(timerDisplay, bordLay.LINE_START);
        upperPanel.add(FACEBUTTON, bordLay.CENTER);
        upperPanel.add(minesRemainingDisplay, bordLay.LINE_END);

        // make the game board grid
        JPanel buttonPanel = makeButtonGrid(WM_WINDOW, 8, 8, 10);

        // build the window
        WM_WINDOW.add(upperPanel, BorderLayout.PAGE_START);
        WM_WINDOW.add(buttonPanel, BorderLayout.CENTER);

        // pack and display window
        WM_WINDOW.pack();
        WM_WINDOW.setVisible(true);
    }

    private static void makeFaceButton() {
        FACEBUTTON = new FaceButton();
        FACEBUTTON.setPreferredSize(new Dimension(34, 34));
        // make an action listener to start new game when clicked
        FACEBUTTON.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newGame();
            }
        });
    }

    private static void newGame() {
        // Start a new game
        WM_WINDOW.dispose();
        doLayout();
    }

    private static ArrayList<Integer> mineLocs(int numMines, int numButtons) {
        // determine where all the mines will be
        ArrayList<Integer> mineLocations = new ArrayList<Integer>();
        Random rnd = new Random();
        int randInt;
        for (int x = 0; x < numMines; x++) {
            do {
                randInt = rnd.nextInt(numButtons);
            // make sure there's only 1 occurence of each number
            } while (Collections.frequency(mineLocations, randInt) > 0);
            mineLocations.add(randInt);
        }
        // sort the mine locations (not necessary but makes cheating easier)
        Collections.sort(mineLocations);
        // System.out.println(mineLocations);
        return mineLocations;
    }

    private static JPanel makeButtonGrid(JFrame window, int rows, int cols,
                                         int numMines) {
        JPanel butPan = new JPanel(new GridLayout(rows, cols));
        MINE_GRID = new MineButton[rows][cols];
        boolean mine;

        // create and distribute mine tiles
        ArrayList<Integer> mineList = mineLocs(numMines, rows * cols);
        // cheat sheet:
        System.out.println("*****cheats!*****\n" + mineList);
        // make the buttons
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                // see if this tile should be a mine
                if (mineList.contains((rows * x) + y)) mine = true;
                else mine = false;
                MINE_GRID[x][y] = new MineButton(1, mine);
                // MINE_GRID[x][y] = new MineButton(Integer.toString((rows * x) + y), 1, true);
                butPan.add(MINE_GRID[x][y]);
                MINE_GRID[x][y].setPreferredSize(new Dimension(25, 25));

                // set up mouse listeners for button clicks
                MINE_GRID[x][y].addMouseListener(new MouseAdapter(){
                    MOUSE_STATUS mouseStatus;

                    public void mousePressed(MouseEvent me) {
                        if (SwingUtilities.isLeftMouseButton(me)) {
                            mouseStatus = MOUSE_STATUS.PRESSED;
                            mouseLeftPressedHandler(me);
                        }
                        else if (SwingUtilities.isRightMouseButton(me)) {
                            mouseRightPressedHandler(me);
                        }
                    }
                    public void mouseExited(MouseEvent me) {
                        if (SwingUtilities.isLeftMouseButton(me)) {
                            mouseStatus = MOUSE_STATUS.EXITED;
                        }
                    }
                    public void mouseReleased(MouseEvent me) {
                        if (SwingUtilities.isLeftMouseButton(me)) {
                            if (mouseStatus != MOUSE_STATUS.EXITED) {
                                mouseReleasedHandler(me);
                            } else {
                                mouseReleasedFaceHandler(me);
                            }
                        }
                    }
                });
            }
        }
        return butPan;
    }

    private static void mouseLeftPressedHandler(MouseEvent me) {
        MineButton thisButton = (MineButton)me.getSource();
        if (!(thisButton.getFlagged() || thisButton.getRevealed())) {
            FACEBUTTON.showFace(Face.NERVOUS);
        }
    }

    private static void mouseRightPressedHandler(MouseEvent me) {
        MineButton thisButton = (MineButton)me.getSource();
        thisButton.toggleFlag();

    }

    private static void mouseReleasedFaceHandler(MouseEvent me) {
        FACEBUTTON.showFace(Face.OK);
    }

    private static void mouseReleasedHandler(MouseEvent me) {
        MineButton thisButton = (MineButton)me.getSource();
        if (thisButton.getFlagged()) {
            return;
        }
        else {
            if (thisButton.hasMine()) {
                FACEBUTTON.showFace(Face.DEAD);
                gameOver();
            } else {
                FACEBUTTON.showFace(Face.OK);
            }
            thisButton.decorateClicked();
        }
    }


    private static JPanel makeDisplay(JTextField display) {
        JPanel displayPanel = new JPanel();
        // display is the jtextfield
        display.setEditable(false);
        display.setText("123");

        display.setBackground(Color.BLACK);
        Font font = new Font("Courier New", Font.BOLD, 36);
        display.setFont(font);
        display.setForeground(Color.RED);
        display.setHorizontalAlignment(SwingConstants.RIGHT);

        displayPanel.add(display);

        return displayPanel;
    }

    private static void gameOver() {
        // This section modified from Java Docs dialog demo from:
        // https://docs.oracle.com/javase/tutorial/uiswing/examples/components/DialogDemoProject/src/components/DialogDemo.java

        String[] options = {"Retry", "Quit"};
        int n = JOptionPane.showOptionDialog(WM_WINDOW,
                        "Game over. Try again?\n",
                        "Game Over",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);
        if (n == JOptionPane.YES_OPTION) {
            newGame();
        } else if (n == JOptionPane.NO_OPTION) {
            WM_WINDOW.dispose();
        } else {
            System.out.println("Please tell me what you want!");
        }
    }
}