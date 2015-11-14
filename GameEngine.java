/*
 <a href="Connect4.html">Back to main page</a>

 Author: Jared Ostmeyer

 Disclaimer: Feel free to use and modify the code. My only demand is that you do
 not take credit for this work. You may not claim this code as your own!
*/

import java.util.Random;

interface AI{
    int PLY[] = {3, 6, 8};
    double RANDOM_WEIGHT[] = {1.0, 0.5, 0.3};
}

interface Game{
    int EMPTY = 0; // DO NOT CHANGE THIS VALUE!    
    int HUMAN = 1; // DO NOT CHANGE THIS VALUE!
    int COMPUTER = -1; // DO NOT CHANGE THIS VALUE!
    
    int HUMAN_IS_WINNER = 0;
    int COMPUTER_IS_WINNER = 1;
    int BOARD_IS_FULL = 2;
    
    int TRUE = 1;
    int FALSE = 0;
    
    int AI_EASY = 0; // DO NOT CHANGE THIS VALUE!
    int AI_MEDIUM = 1; // DO NOT CHANGE THIS VALUE!
    int AI_HARD = 2; // DO NOT CHANGE THIS VALUE!    
    
    void status(int message) throws InterruptedException;
    void getMove(int move) throws InterruptedException;
    void makeMove() throws InterruptedException;    
}

class Connect4 implements Game, AI{
    public int board[][]; // stores the location of each piece
    public int column[]; // stores the number of pieces in each column

    public Connect4(int firstPlayer){
        gt = new GameThread(this, firstPlayer, PLY[AI_MEDIUM], RANDOM_WEIGHT[AI_MEDIUM]);
    }
    public Connect4(Game game, int firstPlayer){
        gt = new GameThread(game, firstPlayer, PLY[AI_MEDIUM], RANDOM_WEIGHT[AI_MEDIUM]);
    }
    public Connect4(int firstPlayer, int setAI){
        gt = new GameThread(this, firstPlayer, PLY[setAI], RANDOM_WEIGHT[setAI]);
    }
    public Connect4(Game game, int firstPlayer, int setAI){
        gt = new GameThread(game, firstPlayer, PLY[setAI], RANDOM_WEIGHT[setAI]);
    }    
    public void status(int message) throws InterruptedException{}
    public void getMove(int move) throws InterruptedException{}
    public void makeMove() throws InterruptedException{}
    public int dropPiece(int move){
        if(gt.turn == HUMAN){
            if(gt.drop(move, HUMAN) == TRUE){
                gt.move = move;
                gt.turn = COMPUTER;
                return TRUE;
            }
            return FALSE;
        }
        return TRUE;
    }
    public void setAI(int setting){
        gt.ply = PLY[setting];
        gt.randWeight = RANDOM_WEIGHT[setting];
    }
    
    public void terminate(){
        try{
            gt.interrupt();
            gt.game = null;
        } catch(Exception e){}
    }

    private class GameThread extends Thread{
        public class ScoreSheet{
            public double score; // used to store how good a move is
            public int flag; // is nonzero when a win is imminent

            ScoreSheet(){
                score = 0.0;
                flag = 0;
            }
            ScoreSheet(double score_, int flag_){
                score = score_;
                flag = flag_;
            }
        }            

        public Game game;        
        public Random rand;
        public int turn;
        public int ply;
        public double randWeight;
        public int move;
        
        GameThread(Game game_, int firstPlayer_, int ply_, double randWeight_){
            super();
            rand = new Random();            
            turn = firstPlayer_;            
            ply = ply_;
            randWeight = randWeight_;
            game = game_;
               
            board = new int[8][8]; // creating the 8 x 8 connect 4 board
            column = new int[8]; // setting the board to have 8 columns

            for(int x = 0; x < 8; x++){ // setting board so it is intially empty and the columns to have height zero
                for(int y = 0; y < 8; y++)
                    board[x][y] = EMPTY;
                column[x] = 0;
            }
            setPriority(Thread.MAX_PRIORITY);
            start();
        }
        public void run(){
            try{
                if(turn == HUMAN)
                    while(turn == HUMAN)
                        game.makeMove();
                while(true){
                    move = findOptimalMove(COMPUTER, ply, randWeight);
                    drop(move, COMPUTER);
                    game.getMove(move);
                    
                    if(win(move, COMPUTER) == TRUE){
                        game.status(COMPUTER_IS_WINNER);
                        return;
                    }                    
                    if(full() == TRUE){
                        game.status(BOARD_IS_FULL);
                        return;
                    }
                    
                    turn = HUMAN;
                    while(turn == HUMAN)
                        game.makeMove();
                    
                    if(win(move, HUMAN) == TRUE){
                        game.status(HUMAN_IS_WINNER);
                        return;
                    }
                    if(full() == TRUE){
                        game.status(BOARD_IS_FULL);
                        return;
                    }
                }
            } catch(InterruptedException e){}
        }

        private int full(){
            for(int x = 0; x < 8; x++)
                if(column[x] != 8)
                    return FALSE;
            
            return TRUE;
        }
        private int drop(int x, int player){ // Drops a piece speciefied by player into column x. Returns 0 if the column is full of pieces
            if(column[x] >= 8) // check to see that the column is not already full
                return FALSE;

            board[x][column[x]] = player; // Reference column to get the y value, then set the place the chip accordingly
            column[x]++; // increment column by one because we have placed a chip

            return TRUE;
        }
        private int undo(int x){ // Removes the last piece dropped in column x. Returns 0 if the column is empty
           if(column[x] <= 0) // check to make sure that column never goes negative
                return FALSE;

            column[x]--; // decrement by one because we are removing a piece
            board[x][column[x]] = EMPTY; // remove the piece from the board

            return TRUE;
        }
        private int win(int x, int player){ // returns a measurement of the moves 'immeadiate advatanges'
            int y = column[x] - 1;

            if(y >= 3 && // checking to see if there is a connect 4 directly below
                    board[x][y-1] == player &&
                    board[x][y-2] == player &&
                    board[x][y-3] == player)
                return TRUE;

            int count = 0;        
            if(x >= 1 && board[x-1][y] == player){ // checking to the left for connect 4
                count++;
                if(x >= 2 && board[x-2][y] == player){
                    count++;
                    if(x >= 3 && board[x-3][y] == player)
                        return TRUE;
                }
            }
            if(x < 7 && board[x+1][y] == player){ // checking to the right for connect 4
                count++;
                if(x < 6 && board[x+2][y] == player){
                    count++;
                    if(x < 5 && board[x+3][y] == player){
                        return TRUE;
                    }
                }
            }
            if(count >= 3) // checking to see if there are enough pieces to the left and right to form a connect 4
                return TRUE;

            count = 0; // reseting count
            if(x >= 1 && y >= 1 && board[x-1][y-1] == player){ // checking the diagnol down-left for a connect 4
                count++;
                if(x >= 2 && y >= 2 && board[x-2][y-2] == player){
                    count++;
                    if(x >= 3 && y >= 3 && board[x-3][y-3] == player)
                        return TRUE;
                }
            }
            if(x < 7 && y < 7 && board[x+1][y+1] == player){ //checking the diagnol up-right for a connect 4
                count++;
                if(x < 6 && y < 6 && board[x+2][y+2] == player){
                    count++;
                    if(x < 5 && y < 5 && board[x+3][y+3] == player)
                        return TRUE;
                }
            }
            if(count >= 3) // checing to see if the diagnol down-left combines with diagnol up-right to form a connect 5
                return TRUE;

            count = 0; // reseting count
            if(x >= 1 && y < 7 && board[x-1][y+1] == player){ // checking the diagnol up-left for a connect 4
                count++;
                if(x >= 2 && y < 6 && board[x-2][y+2] == player){
                    count++;
                    if(x >= 3 && y < 5 && board[x-3][y+3] == player)
                        return TRUE;
                }
            }
            if(x < 7 && y >= 1 && board[x+1][y-1] == player){ //checking the diagnol down-right for a connect 4
                count++;
                if(x < 6 && y >= 2 && board[x+2][y-2] == player){
                    count++;
                    if(x < 5 && y >= 3 && board[x+3][y-3] == player)
                        return TRUE;
                }
            }
            if(count >= 3) // checing to see if the diagnol down-right combines with diagnol up-left to form a connect 5
                return TRUE;        

            return FALSE; // Return that the specified move by the specified player does not yield a win.
        }
        private int findOptimalMove(int player_, int ply_, double randWeight_){ // tells the computer to play out every relevant move ply move's into the future and then to return the optimal move. Returns -1 if the board is full of pieces
            ScoreSheet s1 = new ScoreSheet((double)(-player_*8), -player_),
                    s2 = new ScoreSheet();
            int x_ = -1; // holds the best move found.

            for(int x = 0; x < 8; x++){ // loop through the various moves
                if(drop(x, player_) == FALSE) // make the move specific by index x
                    continue; // skip the proceeding code because the column is already full
                if(win(x, player_) == TRUE){ // check to see if the move yields a win
                    undo(x); // undo the move
                    return x; // this move is trivially the optimal move
                }
                s2 = iterMoves(-player_, ply_-1); // iterate
                undo(x); // undo the preceeding drop
                s2.score += rand.nextDouble()*randWeight*player_;
                if(s2.flag*player_ > s1.flag*player_ || // check to see if the move specified by index x is better than the move stored by x_
                        (s2.score*player_ >= s1.score*player_ &&
                        s2.flag == s1.flag)){
                    x_ = x;
                    s1 = s2;                
                }
            }

            return x_; // return the best move found
        }      
        private ScoreSheet iterMoves(int player, int ply){
            ScoreSheet s1 = new ScoreSheet(),
                    s2 = new ScoreSheet();
            int x; // used to index through the moves

            for(x = 0; x < 8; x++){ // Loop through the possible moves to see if any of them yeild connect 4. If so, return a score sheet indicating connect 4!
                if(drop(x, player) == TRUE){ // drop the piece
                    if(win(x, player) == TRUE){ // test for a win
                        undo(x); // there is a win! Now remove the piece just dropped
                        s1.flag = player;
                        s1.score = 8.0*player;
                        return s1;
                    }
                    undo(x); // undo the last move
                }            
            }

            s1.flag = -player;
            s2.score = 0.0;        
            for(x = 0; x < 8; x++){ // okay, loop through all the possible move again
                if(drop(x, player) == FALSE){ // drop the pieces into the same columns they were in earlier
                    s1.score -= 1.0*player; // the column is full. To keep the score balanced we must decrement (duh?)
                    continue; // since the column is full we can skip the proceeding code
                }
                if(ply > 1){ // check to see if the below code is necessay. It is necessary only if it isn't the last ply'
                    s2 = iterMoves(-player, ply-1); // iterate through the various moves            
                    if(s2.flag == player){ // check to see if a win condition is imminent
                        undo(x); // undo the last move
                        s2.score /= 8.0; // reduce the value because although a win is imminent, it is in a move far, far away
                        return s2;
                    }
                    else{
                        s1.score += s2.score; // okay, so a win is not imminent, still, the computer will ckeck for moves that lead to as many potential connect 4's as possible. That is what this line of code is doing.'
                        if(s2.flag != s1.flag) // check to see if a win is inevitable, no matter what move is made
                            s1.flag = 0; // so a win is not inevitable
                    }             
                }
                else
                    s1.flag = 0; // There is no imminent win

                undo(x); // undo the earlier move
            }

            s1.score /= 64.0; // reduce the score because it is a more far far away
            return s1;
        }            
    }
    
    private GameThread gt;
}