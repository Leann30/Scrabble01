import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
/* 
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
*/

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
        
        int bagSize = 50;

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

class Position{

    int x;
    int y;
    boolean checkDown = true;
    boolean checkLeft = true;
    List<Position> positions = new ArrayList<>();

    Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    void addPosition(int x, int y){
        this.positions.add(new Position(x, y));
    }

    void clearPositions(){
        positions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Position{x = " + x + ", y = " + y + "}";
    }

}

class Scrabble01 implements Clerk{
    
        final String ID;
        final int width, height;
        final String libPath = "views/Scrabble01/scrabble01.js";
        LiveView view;

        final Player01 player1;
        final Player01 player2;
        char currentLetter = 'B';
        int currentPlayer = 1;
        char tile;
        char tileBoard;

        char[] currentTilesTop = new char[7];
        char[] currentTilesBottom = new char[7];
        char[] tilesTop = new char[7];
        char[] tilesBottom = new char[7];

        List<String> specialFields = new ArrayList<>(List.of("DL", "TL", "DW", "TW", "NaN"));

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

       String[][] currentBoard = new String[15][15];
    
        Scrabble01(LiveView view, int width, int height, Player01 player1, Player01 player2) {

            for (int i = 0; i < this.currentBoard.length; i++) {
                for (int j = 0; j < this.currentBoard[i].length; j++) {
                    this.currentBoard[i][j] = "0";
                }
            }
        
            this.view = view;
            this.width  = Math.max(1, Math.abs(width));  // width is at least of size 1
            this.height = Math.max(1, Math.abs(height)); // height is at least of size 1
            this.player1 = player1;
            this.player2 = player2;
            this.tile = ' ';
            this.tileBoard  = ' ';

            
            Clerk.load(view, libPath);
            ID = Clerk.getHashID(this);
    
            Clerk.write(view, "<canvas id='boardCanvas" + ID + "' width='" + this.width + "' height='" + this.height + "' style='border:1px solid #FFFFFF;'></canvas>");
            Clerk.script(view, "const scrabble" + ID + " = new Scrabble01(document.getElementById('boardCanvas" + ID + "'), 'scrabble" + ID + "');");
       
           Clerk.script(view, "scrabble" + ID + ".drawScoreTop(" + player1.score + ");");
           Clerk.script(view, "scrabble" + ID + ".drawScoreBottom(" + player2.score + ");");


            for (int i = 0; i < 7; i++) {
                char letter1 = player1.bag.get(i);
                this.currentTilesTop[i] = letter1;
                this.tilesTop[i] = letter1;
                player1.bag.remove(i);

                char letter2 = player2.bag.get(i);
                this.currentTilesBottom[i] = letter2;
                this.tilesBottom[i] = letter2;
                player2.bag.remove(i);
                Clerk.script(view, "scrabble" + ID + ".textTilesBottom(" + i + ", '" + letter2 + "');");
                Clerk.script(view, "scrabble" + ID + ".textTilesTop(" + i + ", '" + letter1 + "');");
            }
            
            this.view.createResponseContext("/scrabble" + ID, response -> {    
                //System.out.println(response);
                String[] temp = response.split("x");
                int x = Integer.parseInt(temp[0]);
                int y = Integer.parseInt(temp[1]);
                System.out.println("" + x +" + " + y);
                
                updateBoard(x, y);
                getTile(x, y);
                updateScore(x, y, this.tile);
            });
        }

void fillTopTiles(){

}

void fillBottomTiles(){
    
}
void turn(){
    this.currentPlayer *= -1;
    this.tile = ' ';
    this.tileBoard  = ' ';
}

char getTile(int x, int y){

    //Hole Stein für ersten Klick 
    if(x >= 7 && x <= 13 &&  y == 2 && this.currentPlayer > 0 && this.tile == ' '){  //auf top tiles geklickt
        this.tile = this.tilesTop[x-7]; //hole Stein auf den geklickt wurde
        this.tileBoard = ' ';
        this.currentTilesTop[x-7] = '0';
        Clerk.script(view, "scrabble" + ID + ".removeTile("+ x + ", '" + y + "');");

    } else if(x >= 1 && x <= 7 &&  y == 18 && this.currentPlayer < 0 && this.tile == ' '){ //auf bottom tiles 
        this.tile = this.tilesBottom[x-1];
        this.tileBoard = ' ';
        this.currentTilesBottom[x-1] = '0';
        Clerk.script(view, "scrabble" + ID + ".removeTile("+ x + ", '" + y + "');");
        
    } else if(x >= 0 && x <= 14 && y >= 3 && y <= 17 && this.specialFields.contains(this.board[x][y-3]) && !(this.currentBoard[x][y-3].equals("0")) && this.tileBoard == ' ' && this.tile != ' '){ //auf board
        this.tileBoard = this.currentBoard[x][y-3].charAt(0);
        this.tile = ' ';
        this.currentBoard[x][y-3] = "0";
        Clerk.script(view, "scrabble" + ID + ".setColor("+ x + ", '" + (y-3) + "');"); 
        Clerk.script(view, "scrabble" + ID + ".setText("+ x + ", '" + (y-3) + "');");
    }
    return this.tile;
}

void updateBoard(int x, int y){

        if(x >= 0 && x <= 14 && y >= 3 && y <= 17 && this.currentPlayer > 0 && this.currentBoard[x][y-3].equals("0") && this.specialFields.contains(this.board[x][y-3]) && this.tile != ' ' && this.tileBoard == ' '){ //Spieler1 innerhalb Board
            Clerk.script(view, "scrabble" + ID + ".setTile1("+ x + ", '" + y + "', '" + this.tile + "');");
            this.currentBoard[x][y-3] = "" + this.tile;
            this.tile = ' ';
            this.tileBoard = ' ';

        } else if(x >= 0 && x <= 14 && y >= 3 && y <= 17 && this.currentPlayer < 0 && this.currentBoard[x][y-3].equals("0") && this.specialFields.contains(this.board[x][y-3]) && this.tile != ' ' && this.tileBoard == ' '){
            Clerk.script(view, "scrabble" + ID + ".setTile2(" + x + ", '" + y + "', '" + this.tile + "');");
            this.currentBoard[x][y-3] = "" + this.tile;
            this.tile = ' ';
            this.tileBoard = ' ';

        } else if(x >= 7 && x <= 13 &&  y == 2 && this.currentPlayer > 0 && this.currentTilesTop[x-7] == '0' && this.tile == ' ' && this.tileBoard != ' '){ //Spieler1 innerhalb topTiles
            Clerk.script(view, "scrabble" + ID + ".setTile1("+ x + ", '" + y + "', '" + this.tileBoard + "');");
            this.tile = ' ';
            this.tileBoard = ' ';
        
        } else if(x >= 1 && x <= 7 &&  y == 18 && this.currentPlayer < 0 && this.currentTilesBottom[x-1] == '0' && this.tile == ' ' && this.tileBoard != ' '){ //Spieler2 innerhalb bottomTiles
            Clerk.script(view, "scrabble" + ID + ".setTile2("+ x + ", '" + y + "', '" + this.tileBoard + "');");
            this.tile = ' ';
            this.tileBoard = ' ';
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


    /*
 scrabble.currentBoard[0][0] = "B"
 scrabble.currentBoard[0][1] = "A"
 scrabble.currentBoard[1][0] = "A"
 scrabble.currentBoard[0][2] = "U"
 scrabble.currentBoard[2][0] = "U"
 scrabble.currentBoard[0][3] = "M"
 scrabble.currentBoard[3][0] = "M"

 scrabble.board[0][0] = "B"
 scrabble.board[0][1] = "A"
 scrabble.board[1][0] = "A"
 scrabble.board[0][2] = "U"
 scrabble.board[2][0] = "U"
 scrabble.board[0][3] = "M"
 scrabble.board[3][0] = "M"

scrabble.board[3][1] = "A"
scrabble.board[3][2] = "U"
scrabble.board[3][3] = "E"
scrabble.board[3][4] = "R"*/
void getWords() {
    Position pos = new Position(0, 0);
    List<String> currentWords = new ArrayList<>();

    // Collect positions of newly placed tiles
    for (int i = 0; i < this.currentBoard.length; i++) {
        for (int j = 0; j < this.currentBoard.length; j++) {
            if (!this.currentBoard[i][j].equals("0")) {
                pos.addPosition(i, j);
            }
        }
    }

    // Traverse positions and form words
    for (Position position : pos.positions) {
        int x = position.x;
        int y = position.y;

        System.out.println("Position: " + position.toString() + ", checkD: " + position.checkDown + ", checkL: " + position.checkLeft);
        if (position.checkLeft) {
            String wordH = extractWordHorizontal(x, y, pos);
            if (wordH.length() > 1){ 
                currentWords.add(wordH);
            }
        }

        // Check if vertical word should be extracted
        if (position.checkDown) {
            String wordV = extractWordVertical(x, y, pos);
            if (wordV.length() > 1){ 
                currentWords.add(wordV);
            }
        }
    }
    pos.clearPositions();
    System.out.println("currentWords: " + currentWords);
}

private String extractWordHorizontal(int x, int y, Position pos) {
    StringBuilder word = new StringBuilder();

    // Move left to find the starting position
    while (y > 0 && !this.specialFields.contains(this.board[x][y - 1])) y--;

    // Build the word moving right
    while (y < this.board.length && !this.specialFields.contains(this.board[x][y])) {
        word.append(this.board[x][y]);
        for (Position position : pos.positions) {
            if(position.x == x && position.y == y){
                position.checkLeft = false;
            }
        }
        y++;
    }

    return word.toString();
}

private String extractWordVertical(int x, int y, Position pos) {
    StringBuilder word = new StringBuilder();

    // Move up to find the starting position
    while (x > 0 && !this.specialFields.contains(this.board[x - 1][y])) x--;

    // Build the word moving down
    while (x < this.board.length && !this.specialFields.contains(this.board[x][y])) {
        word.append(this.board[x][y]);
        for (Position position : pos.positions) {
            if(position.x == x && position.y == y){
                position.checkDown = false;
            }
        }
        x++;
    }

    return word.toString();
}

  
boolean validateWord(String input) {
   
    try {
        URI uri = URI.create("https://www.dwds.de/api/wb/snippet/?q=" + input);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // JSON als String durchsuchen
        String jsonResponse = response.toString();
        return jsonResponse.contains("\"wortart\":") && jsonResponse.contains("\"lemma\":");

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
} 

String getRandWord(){
    //ersten Buchstaben immer an pos [6][3] setzen (oder random i, j?)
    //Einfach feste Auswahl aus Wörtern nehmen?
    
    try {
        URI uri = URI.create("https://www.dwds.de/api/wb/random");
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // JSON als String durchsuchen
        String jsonResponse = response.toString();
        String key = "\"lemma\":\"";
        int startIndex = jsonResponse.indexOf(key);
        if (startIndex != -1) {
            startIndex += key.length();
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return jsonResponse.substring(startIndex, endIndex);
            }
        }
        return "Lemma not found";

    } catch (Exception e) {
        e.printStackTrace();
        return "Error fetching word";
    }
} 

    Scrabble01(LiveView view) { this(view, 600, 600, new Player01(), new Player01()); }
    Scrabble01(int width, int height) { this(Clerk.view(), width, height, new Player01(), new Player01()); }
    Scrabble01() { this(Clerk.view(), 600, 600, new Player01(), new Player01());}
}

