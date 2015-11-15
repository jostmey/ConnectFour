/*
 Author: Jared Ostmeyer

 Disclaimer: Feel free to use and modify the code. My only demand is that you do
 not take credit for this work. You may not claim this code as your own!
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class InterfaceEngine extends Applet implements ActionListener, ComponentListener,  
        MouseListener, MouseMotionListener, Runnable, Game{
    // the below constants determine intial properties of the window--------------------------------------------------
    public final static Color EMPTY_COLOR = Color.WHITE;
    public final static Color BOARD_COLOR = Color.ORANGE;
    public final static Color HUMAN_COLOR = Color.RED;
    public final static Color COMPUTER_COLOR = Color.BLACK;
    public final static Color HIGHLIGHT_COLOR = Color.GREEN;
    
    public final static int WINDOW_WIDTH = 400;
    public final static int WINDOW_HEIGHT = 400;
    
    public final static String WINDOW_NAME = "Java 4";
    
    public final static int FRAME_RATE = 15;
    
    public final int BUTTON_BORDER = 25;
    
    public final static String TITLE = "Java4",
            COMPUTER_WINS = "The computer has won!",
            HUMAN_WINS = "The human has won!",
            BOARD_FULL = "The game has ended.",
            NEW_GAME = "New Game",
            SET_AI = "A.I. Difficulty",
            ABOUT = "About",
            DIALOG_ABOUT = "Java4 was written by Jared Ostmeyer",
            HUMAN_MOVE_FIRST = "Move first",
            HUMAN_MOVE_SECOND = "Move second",
            DIALOG_NEW_GAME = "Do you want to move first?",
            DIALOG_SET_AI = "Choose the difficulty",
            AI_EASY = "Easy",
            AI_MEDIUM = "Medium",
            AI_HARD = "Hard", 
            COMPUTER_TURN = "The computer is thinking.",
            HUMAN_TURN = "Your turn human.";
    
    public final static int AI_PLY_EASY = 3,
            AI_PLY_MEDIUM = 6,
            AI_PLY_HARD = 8;
    public final static double AI_RAND_WEIGHT_EASY = 1.0,
            AI_RAND_WEIGHT_MEDIUM = 0.5,
            AI_RAND_WEIGHT_HARD = 0.1;
    // the above constants determine intial properties of the window--------------------------------------------------
  
    // the below variables are used only by InterfaceEngine-----------------------------------------------------------
    Connect4 c4 = null;
    
    public Color boardColors[][];    
    public int x_;
    
    public Rectangle clientRect, boardRect;
    public int spacing;
    
    public int aI;
    
    Button newGame, setAI, about;
    // the below variables are used only by InterfaceEngine-----------------------------------------------------------

    public void init(){
        boardColors = new Color[8][8];
        x_ = -1;
        
        resetBoardColors();
        
        aI = Game.AI_MEDIUM;

        clientRect = new Rectangle();
        boardRect = new Rectangle();
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setBackground(BOARD_COLOR);
        
        newGame = new Button(NEW_GAME);
        setAI = new Button(SET_AI);
        about = new Button(ABOUT);
       
        add(newGame);
        add(setAI);
        add(about);
        
        newGame.addActionListener(this);
        setAI.addActionListener(this);
        about.addActionListener(this);   
        
        newGame.setBounds(0, 0, 120, 30);

        componentResized(null);

        (new Thread(this)).start();
    }
    public void update(Graphics g){ // override or else there will be flickering!
        paint(g);
    }
    public void paint(Graphics g){
        Rectangle pieceRect;
        
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                pieceRect = getPieceRect(x, y);                
                g.setColor(boardColors[x][y]);
                g.fillOval(pieceRect.x, pieceRect.y,
                        pieceRect.width, pieceRect.height);
            }

            pieceRect = getPieceRect(x, -1);
         
            if(x == x_){
                g.setColor(HIGHLIGHT_COLOR);   
                g.fillRect(pieceRect.x, boardRect.y - (3*spacing)/10,
                        pieceRect.width, spacing/18);
            }
            else{
                g.setColor(BOARD_COLOR);   
                g.fillRect(pieceRect.x, boardRect.y - (3*spacing)/10,
                        pieceRect.width, spacing/18);  
                g.setColor(HIGHLIGHT_COLOR);
                g.drawRect(pieceRect.x, boardRect.y - (3*spacing)/10,
                        pieceRect.width, spacing/18);                     
            }
        }
    }
    public void finalize(){
        c4.terminate();
    }
    
    public void run(){       
        promptNewGame();     
    }
    
    public synchronized void makeMove() throws InterruptedException{
        showStatus(HUMAN_TURN);
        wait();
        int move = x_;
        c4.dropPiece(move);
        animateDrop(move, HUMAN_COLOR);
        showStatus(COMPUTER_TURN);
    }
    public void status(int message) throws InterruptedException{
        switch(message){
            case Game.BOARD_IS_FULL:{
                JOptionPane.showMessageDialog(null, BOARD_FULL, TITLE, 
                        JOptionPane.INFORMATION_MESSAGE);
                showStatus(BOARD_FULL);
            }
            break;
            case Game.COMPUTER_IS_WINNER:{
                JOptionPane.showMessageDialog(null, COMPUTER_WINS, TITLE, 
                        JOptionPane.INFORMATION_MESSAGE);
                showStatus(COMPUTER_WINS);
            }
            break;
            case Game.HUMAN_IS_WINNER:{
                JOptionPane.showMessageDialog(null, HUMAN_WINS, TITLE, 
                        JOptionPane.INFORMATION_MESSAGE);
                showStatus(HUMAN_WINS);
            }
        }
    }
    public void getMove(int move) throws InterruptedException{
        animateDrop(move, COMPUTER_COLOR);
    }

    public void mouseMoved(MouseEvent e){
        x_ = getColumn(e.getX());
        repaint();
    }
    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){
        x_ = -1;
        repaint();
    }
    public void mousePressed(MouseEvent e){}
    public synchronized void mouseReleased(MouseEvent e){
        x_ = getColumn(e.getX());
        notify();
    }
    public void mouseDragged(MouseEvent e){}
    
    public void actionPerformed(ActionEvent e){
        String arg = e.getActionCommand();
        
        if(arg.equals(NEW_GAME))
            promptNewGame();
        else if(arg.equals(SET_AI)){
            Object[] options = {AI_EASY, AI_MEDIUM, AI_HARD};
            int n = JOptionPane.showOptionDialog(this, DIALOG_SET_AI, TITLE,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 
                    options[aI]);
            if(n == 0)
                aI = Game.AI_EASY;
            else if(n == 1)
                aI = Game.AI_MEDIUM;
            else if(n == 2)
                aI = Game.AI_HARD;
            
            c4.setAI(aI);
        }
        else if(arg.equals(ABOUT)){
            JOptionPane.showMessageDialog(null, DIALOG_ABOUT, TITLE, 
                    JOptionPane.INFORMATION_MESSAGE);            
        }
    }
    
    public void componentResized(ComponentEvent e){
        Dimension clientDim = getSize();
        clientRect.setLocation(0, BUTTON_BORDER);
        clientRect.setSize(clientDim.width, clientDim.height-BUTTON_BORDER);
     
        int minDim = clientRect.width > clientRect.height ? clientRect.height : clientRect.width;

        spacing = minDim/10;  

        int xOffSet = (clientRect.width-minDim)/2+spacing,
                yOffSet = (clientRect.height-minDim)/2+spacing;

        boardRect.setLocation(clientRect.x+xOffSet, clientRect.y+yOffSet);
        boardRect.setSize(clientRect.width-2*xOffSet,
                clientRect.height-2*yOffSet);
    }
    public void componentMoved(ComponentEvent e){}
    public void componentShown(ComponentEvent e){}
    public void componentHidden(ComponentEvent e){}
    
    public Rectangle getPieceRect(int column, int row){
        Rectangle pieceRect = new Rectangle();
        
        pieceRect.setLocation(boardRect.x+spacing*column+spacing/20, 
                boardRect.y+boardRect.height-spacing*row-(19*spacing)/20);
        pieceRect.setSize((9*spacing)/10, (9*spacing)/10);
        
        return pieceRect;
    }
    public int getColumn(int xCoor){
        if(xCoor < boardRect.x)
            return -1;
        
        return (xCoor-boardRect.x)/spacing; 
    }
    public int getRow(int yCoor){
        if(yCoor < boardRect.y)
            return -1;
        
        return (yCoor-boardRect.y)/spacing; 
    }

    private void animateDrop(int x, Color col) throws InterruptedException{
        int y = 7;
                
        if(boardColors[x][y] != EMPTY_COLOR)
            return;
        for(; y >= 0; y--){
            boardColors[x][y] = col;
            repaint();
            Thread.sleep((long)(1000/FRAME_RATE));
            
            if(y > 0 && boardColors[x][y-1] == EMPTY_COLOR)
                boardColors[x][y] = EMPTY_COLOR;
            else
                break;
        }
    }    
    public void resetBoardColors(){
        for(int x = 0; x < 8; x++)
            for(int y = 0; y < 8; y++)
                boardColors[x][y] = EMPTY_COLOR;
    }
    public void promptNewGame(){
        if(c4 != null)
            c4.terminate();

        Object[] options = {HUMAN_MOVE_FIRST, HUMAN_MOVE_SECOND};
        int n = JOptionPane.showOptionDialog(this, DIALOG_NEW_GAME, TITLE,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 
                options[0]);
        if(n == 1) // human moves second
            c4 = new Connect4(this, COMPUTER, aI);
        else // human moves first
            c4 = new Connect4(this, HUMAN, aI);
        
        resetBoardColors();
    }
}
 
