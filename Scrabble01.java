import java.lang.reflect.Array;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


class Player01 {

    int score = 0;
    List <Character> bag = new ArrayList<>();

    Player01(){
        this.bag = fillBag();
    }
    
    List<Character> fillBag(){

        List<Character> onePointL = new ArrayList<>(List.of('A', 'E', 'I', 'L', 'N', 'O', 'R', 'S', 'T', 'U'));
        List<Character> twoPointL = new ArrayList<>(List.of('D', 'G', 'M'));
        List<Character> threePointL = new ArrayList<>(List.of('B', 'C', 'P'));
        List<Character> fourPointL = new ArrayList<>(List.of('F', 'H', 'V'));
        List<Character> eightPointL = new ArrayList<>(List.of('J', 'Q'));
        List<Character> tenPointL = new ArrayList<>(List.of('K', 'W', 'X', 'Y', 'Z'));
        
        int bagSize = 7;

        for(int i = 0; i < bagSize; i++){

            double rand = Math.random();
            
            if(rand <= 0.20){
                bag.add(getRandomElement(onePointL));
            } else if(rand > 0.20 && rand <= 0.36){
                bag.add(getRandomElement(twoPointL));
            }else if(rand > 0.36 && rand <= 0.52){
                bag.add(getRandomElement(threePointL));
            }else if(rand > 0.52 && rand <= 0.68){
                bag.add(getRandomElement(fourPointL));
            }else if(rand > 0.68 && rand <= 0.84){
                bag.add(getRandomElement(eightPointL));
            }else if(rand > 0.84 && rand <= 1.0){
                bag.add(getRandomElement(tenPointL));
            }
        }
        return bag;
    }

    char getRandomElement(List<Character> letterList){
        Random random = new Random();
        int rand = random.nextInt(letterList.size()); 
        return letterList.get(rand);
    }
}

class Scrabble01 implements Clerk{
    
        final String ID;
        final int width, height;
        final String libPath = "views/TicTacToe/scrabble01.js";
        LiveView view;

        final Player01 player1;
        final Player01 player2;
        char currentLetter = 'B';
        int currentPlayer = 1;
        char tile;

        List<Character> onePointL = new ArrayList<>(List.of('A', 'E', 'I', 'L', 'N', 'O', 'R', 'S', 'T', 'U'));
        List<Character> twoPointL = new ArrayList<>(List.of('D', 'G', 'M'));
        List<Character> threePointL = new ArrayList<>(List.of('B', 'C', 'P'));
        List<Character> fourPointL = new ArrayList<>(List.of('F', 'H', 'V'));
        List<Character> eightPointL = new ArrayList<>(List.of('J', 'Q'));
        List<Character> tenPointL = new ArrayList<>(List.of('K', 'W', 'X', 'Y', 'Z'));

        //TW = 1, int DW = 2, int TL = 3, DL = 4, NaN
        String[][] board = {
            {"TW", "NaN", "NaN", "DL", "NaN", "NaN", "NaN", "TW", "NaN", "NaN", "NaN", "DL", "NaN", "NaN", "TW"},
            {"NaN", "DW", "NaN", "NaN", "NaN", "TL", "NaN", "NaN", "NaN", "TL", "NaN", "NaN", "NaN", "DW", "NaN"},
            {"NaN", "NaN", "DW", "NaN", "NaN", "NaN", "DL", "NaN", "DL", "NaN", "NaN", "NaN", "DW", "NaN", "NaN"},
            {"DL", "NaN", "NaN", "DW", "NaN", "NaN", "NaN", "DL", "NaN", "NaN", "NaN", "DW", "NaN", "NaN", "DL"},
            {"NaN", "NaN", "NaN", "NaN", "DW", "NaN", "NaN", "NaN", "NaN", "NaN", "DW", "NaN", "NaN", "NaN", "NaN"},
            {"NaN", "TL", "NaN", "NaN", "NaN", "TL", "NaN", "NaN", "NaN", "TL", "NaN", "NaN", "NaN", "TL", "NaN"},
            {"NaN", "NaN", "DL", "NaN", "NaN", "NaN", "DL", "NaN", "DL", "NaN", "NaN", "NaN", "DL", "NaN", "NaN"},
            {"TW", "NaN", "NaN", "DL", "NaN", "NaN", "NaN", "DW", "NaN", "NaN", "NaN", "DL", "NaN", "NaN", "TW"},
            {"NaN", "NaN", "DL", "NaN", "NaN", "NaN", "DL", "NaN", "DL", "NaN", "NaN", "NaN", "DL", "NaN", "NaN"},
            {"NaN", "TL", "NaN", "NaN", "NaN", "TL", "NaN", "NaN", "NaN", "TL", "NaN", "NaN", "NaN", "TL", "NaN"},
            {"NaN", "NaN", "NaN", "NaN", "DW", "NaN", "NaN", "NaN", "NaN", "NaN", "DW", "NaN", "NaN", "NaN", "NaN"},
            {"NaN", "NaN", "NaN", "DW", "NaN", "NaN", "NaN", "DL", "NaN", "NaN", "NaN", "DW", "NaN", "NaN", "NaN"},
            {"NaN", "NaN", "DW", "NaN", "NaN", "NaN", "DL", "NaN", "DL", "NaN", "NaN", "NaN", "DW", "NaN", "NaN"},
            {"NaN", "DW", "NaN", "NaN", "NaN", "TL", "NaN", "NaN", "NaN", "TL", "NaN", "NaN", "NaN", "DW", "NaN"},
            {"TW", "NaN", "NaN", "DL", "NaN", "NaN", "NaN", "TW", "NaN", "NaN", "NaN", "DL", "NaN", "NaN", "TW"}
        };
    
        Scrabble01(LiveView view, int width, int height, Player01 player1, Player01 player2) {

            this.view = view;
            this.width  = Math.max(1, Math.abs(width));  // width is at least of size 1
            this.height = Math.max(1, Math.abs(height)); // height is at least of size 1
            this.player1 = player1;
            this.player2 = player2;

            Clerk.load(view, libPath);
            ID = Clerk.getHashID(this);
    
            Clerk.write(view, "<canvas id='boardCanvas" + ID + "' width='" + this.width + "' height='" + this.height + "' style='border:1px solid #FFFFFF;'></canvas>");
            Clerk.script(view, "const scrabble" + ID + " = new Scrabble01(document.getElementById('boardCanvas" + ID + "'), 'scrabble" + ID + "');");
       
           Clerk.script(view, "scrabble" + ID + ".drawScoreTop(" + player1.score + ");");
           Clerk.script(view, "scrabble" + ID + ".drawScoreBottom(" + player2.score + ");");


            for (int i = 0; i < player2.bag.size(); i++) {
                char letter1 = player1.bag.get(i);
                char letter2 = player2.bag.get(i);
                Clerk.script(view, "scrabble" + ID + ".textTilesBottom(" + i + ", '" + letter2 + "');");
                Clerk.script(view, "scrabble" + ID + ".textTilesTop(" + i + ", '" + letter1 + "');");
            }
            
            this.view.createResponseContext("/scrabble" + ID, response -> {    
                //System.out.println(response);
                String[] temp = response.split("x");
                int x = Integer.parseInt(temp[0]);
                int y = Integer.parseInt(temp[1]);
                System.out.println("" + x +" + " + y);
                getTile(x, y);
                updateScore(x, y, this.tile);
                updateBoard(x, y);
            });
        }

void turn(){
    this.currentPlayer *= -1;
}

char getTile(int x, int y){

    //Hole Stein für ersten Klick 
    if(x >= 7 && x <= 13 &&  y == 2 && this.currentPlayer > 0){  //auf top tiles geklickt
        this.tile = player1.bag.get(x-7); 
    } else if(x >= 7 && x <= 13 &&  y == 2 && this.currentPlayer < 0){  //auf top tiles geklickt
        this.tile = player2.bag.get(x-7); 
    } else if(x >= 1 && x <= 7 &&  y == 18 && this.currentPlayer > 0){ //auf bottom tiles 
        this.tile = player1.bag.get(x);
    } else if(x >= 1 && x <= 7 &&  y == 18 && this.currentPlayer < 0){ //auf bottom tiles 
        this.tile = player2.bag.get(x);
    }
    return this.tile;
}

void updateBoard(int x, int y){
    if((this.tile != ' ')){
        if(x >= 0 && x <= 14 && y >= 3 && y <= 17 && this.currentPlayer > 0 && (this.board[x][y-3] == "TW" || this.board[x][y-3] == "DW" || this.board[x][y-3] == "TL" || this.board[x][y-3] == "DL" || this.board[x][y-3] == "NaN")){
            Clerk.script(view, "scrabble" + ID + ".setTile1("+ x + ", '" + y + "', '" + this.tile + "');");
            this.board[x][y-3] = "" + this.tile;
        } else if(x >= 0 && x <= 14 && y >= 3 && y <= 17 && this.currentPlayer < 0 && (this.board[x][y+3] == "TW" || this.board[x][y+3] == "DW" || this.board[x][y+3] == "TL" || this.board[x][y+3] == "DL" || this.board[x][y+3] == "NaN")){
            Clerk.script(view, "scrabble" + ID + ".setTile2(" + x + ", '" + y + "', '" + this.tile + "');");
            this.board[x][y+3] = "" + this.tile;
        }
    }
}
 
int updateScore(int x, int y, char currentLetter){

    int counter = 0;

    if(board[x][y] == "DL" && onePointL.contains(currentLetter)){//doubleletter
        counter += 2+1;
    } else if(board[x][y] == "DL" && twoPointL.contains(currentLetter)){
        counter = 2*2;
    } else if(board[x][y] == "DL" && threePointL.contains(currentLetter)){
        counter = 2*3;
    } else if(board[x][y] == "DL" && fourPointL.contains(currentLetter)){
        counter = 2*4;
    }else if(board[x][y] == "DL" && eightPointL.contains(currentLetter)){
        counter = 2*8;
    }else if(board[x][y] == "DL" && tenPointL.contains(currentLetter)){
        counter = 2*10;
    }

    else if(board[x][y] == "TL" && onePointL.contains(currentLetter)){ //triple letter+onepointletter
        counter = 1+3;
    } else if(board[x][y] == "TL" && twoPointL.contains(currentLetter)){
        counter = 2*3;
    } else if(board[x][y] == "TL" && threePointL.contains(currentLetter)){
        counter = 3*3;
    } else if(board[x][y] == "TL" && fourPointL.contains(currentLetter)){
        counter = 3*4;
    }else if(board[x][y] == "TL" && eightPointL.contains(currentLetter)){
        counter = 3*8;
    }else if(board[x][y] == "TL" && tenPointL.contains(currentLetter)){
        counter = 3*10;
    }
    return counter;
}


    Scrabble01(LiveView view) { this(view, 600, 600, new Player01(), new Player01()); }
    Scrabble01(int width, int height) { this(Clerk.view(), width, height, new Player01(), new Player01()); }
    Scrabble01() { this(Clerk.view(), 600, 600, new Player01(), new Player01());}
}


