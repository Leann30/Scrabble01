import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
/* 
SPIELABLAUF:

-Spieler1 legt fertig 
-drückt auf play
-daraufhin Wörtervalidierung ...
-wenn Wörter nicht validiert: Fehler anzeigen
-score erhöhen 
-Spieler2 kann legen 

METHODEN SCHREIBEN:
- nicht zusammenhängende Wörter in currentBoard finden 
- current Board alle Worter holen und Listen vergleichen, wenn in allWord mehr ist wird Wort nicht validiert
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
class Word{

    String word;
    Position start;
    Position end;
    
    Word(String word, Position start, Position end){
        this.word = word;
        this.start = start;
        this.end = end;
    }
    @Override
    public String toString() {
        return "Wort("+ word + " Position{start = " + start + ", end = " + end + "})";
    }
}

class Scrabble01 implements Clerk{
    
        final String ID;
        final int width, height;
        final String libPath = "views/Scrabble01/scrabble01.js";
        LiveView view;

        final Player01 player1;
        final Player01 player2;
        int currentPlayer = 1; //Spieler 1 beginnt
        char tile;
        char tileBoard;
        List<Word> rightWords; //neu hinzugefügte& validierte Wörter


        char[] tilesTop = new char[7];
        char[] tilesBottom = new char[7];

        List<String> specialFields = new ArrayList<>(List.of("DL", "TL", "DW", "TW", "NaN"));

        Map<String, Integer> letterScores = new HashMap<>(Map.ofEntries(
            Map.entry("A", 1), Map.entry("E", 1), Map.entry("I", 1), Map.entry("L", 1),
            Map.entry("N", 1), Map.entry("O", 1), Map.entry("R", 1), Map.entry("S", 1),
            Map.entry("T", 1), Map.entry("U", 1),
            Map.entry("D", 2), Map.entry("G", 2), Map.entry("M", 2),
            Map.entry("B", 3), Map.entry("C", 3), Map.entry("P", 3),
            Map.entry("F", 4), Map.entry("H", 4), Map.entry("V", 4),
            Map.entry("J", 8), Map.entry("Q", 8),
            Map.entry("K", 10), Map.entry("W", 10), Map.entry("X", 10),
            Map.entry("Y", 10), Map.entry("Z", 10)
        ));

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
       String[][] updatedBoard = new String[15][15];
    
        Scrabble01(LiveView view, int width, int height, Player01 player1, Player01 player2) {

            for (int i = 0; i < this.currentBoard.length; i++) {
                for (int j = 0; j < this.currentBoard[i].length; j++) {
                    this.currentBoard[i][j] = "0";
                    this.updatedBoard[i][j] = "0";
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
                this.tilesTop[i] = letter1;
                player1.bag.remove(i);

                char letter2 = player2.bag.get(i);
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
                
                //aktueller Spieler legt Steine
                getTile(x, y);
                updateBoard(x, y);

                 if (endTurn(x, y)){
                    overrideUpdatedBoard();
                
                List<Word> wrongWords = new ArrayList<>(getWrongWords(getAllWords(), getWords(this.updatedBoard)));
                    
                if(wrongWords.isEmpty()){ //dann kann weitergespielt werden
                    //Score mit in dieser Runde gesetzten Wörtern erhöhen
                    List<Word> scoredWords = getWords(this.currentBoard);
                    for(Word scWord: scoredWords){
                        if(this.currentPlayer > 0){
                            player1.score += updateScore(scWord); //Zeichne score mit neuem Wert 
                        } else if(this.currentPlayer < 0){
                            player2.score += updateScore(scWord);
                        } 
                    }
                    overrideBoard();
                    clearCurrentBoard();
                    isGameOver();
                    turn();
                }
               for(Word w: wrongWords){
                    throwError(w);
                }/* 
                sonst Fehlerausgabe an Positionen von getWrongWords 
                 * updatedBoard wieder currentWord-Positionen löschen 
                */
            }
        });
    }

void throwError(Word w){
    System.out.println("startX: " + w.start.x + " ,startY: " + w.start.y + " ,endX: " + w.end.x + " ,endY: "+ w.end.y);
    Clerk.script(view, "scrabble" + ID + ".error(" + w.start.x + ", " + w.start.y + ", " + w.end.x + ", " + w.end.y + ");");
   
}

boolean isGameOver(){
    return ((this.player1.bag.size() != 0) || (this.player2.bag.size() != 0)); // oder button für Aufgeben drücken
    /*Strafpunkte: Jeder Spieler bekommt Strafpunkte für die Buchstaben, die er noch auf seinem Regal hat.
    Bonus für das Ablegen aller Buchstaben: Der Spieler, der alle seine Buchstaben abgelegt hat, erhält eine Bonuspunktzahl, 
    die die Summe der verbleibenden Buchstaben seiner Mitspieler ist.
    /o lvp.java
    /o Views/Scrabble01/Scrabble01.java
    Scrabble01 scrabble = new Scrabble01()
    */
}
boolean endTurn(int x, int y){
    if(x >= 16 && x <= 18 && y == 16){
        return true;
    } else {
        return false;
    }
}

void clearCurrentBoard(){
    //currentBoard wieder auf 0 setzen
    for (int i = 0; i < this.currentBoard.length; i++) {
        for (int j = 0; j < this.currentBoard[i].length; j++) {
            this.currentBoard[i][j] = "0";
        }
    }
}
void overrideUpdatedBoard(){
    for (int i = 0; i < currentBoard.length; i++) {
        for (int j = 0; j < currentBoard[i].length; j++) {
            if(this.updatedBoard[i][j].equals("0")){
                this.updatedBoard[i][j] = this.currentBoard[i][j];
            }
        }
    }
}
void overrideBoard(){
    for (int i = 0; i < currentBoard.length; i++) {
        for (int j = 0; j < currentBoard[i].length; j++) {
            if(this.specialFields.contains(this.board[i][j]) && !(this.currentBoard[i][j].equals("0"))){
                this.board[i][j] = this.currentBoard[i][j];
            }
        }
    }
}

void turn(){
    this.currentPlayer *= -1;
    this.emptyTiles();
}

void emptyTiles(){
    this.tile = ' ';
    this.tileBoard  = ' ';
}

void updateBoard(int x, int y) {
    // Spieler 1 legt oder verschiebt Stein auf Board
    if (x >= 0 && x <= 14 && y >= 3 && y <= 17 && this.currentPlayer > 0) {
        if (setzeTileBoard(x, y)) {
            Clerk.script(view, "scrabble" + ID + ".setTile1(" + x + ", '" + y + "', '" + this.tile + "');");
            this.currentBoard[x][y-3] = "" + this.tile;
            this.emptyTiles();
        }
    }
    // Spieler 2 legt oder verschiebt Stein auf Board
    else if (x >= 0 && x <= 14 && y >= 3 && y <= 17 && this.currentPlayer < 0) {
        if (setzeTileBoard(x, y)) {
            Clerk.script(view, "scrabble" + ID + ".setTile2(" + x + ", '" + y + "', '" + this.tile + "');");
            this.currentBoard[x][y-3] = "" + this.tile;
            this.emptyTiles();
        }
    }
    // Spieler 1 legt Stein zurück zu TopTiles
    else if (x >= 7 && x <= 13 && y == 2 && this.currentPlayer > 0 && setzeTileTop(x, y)) {
        this.tilesTop[x-7] = this.tileBoard;
        Clerk.script(view, "scrabble" + ID + ".setTile1(" + x + ", '" + y + "', '" + this.tileBoard + "');");
        this.emptyTiles();
    }
    // Spieler 2 legt Stein zurück zu BottomTiles
    else if (x >= 1 && x <= 7 && y == 18 && this.currentPlayer < 0 && setzeTileBottom(x, y)) {
        this.tilesBottom[x-1] = this.tileBoard;
        Clerk.script(view, "scrabble" + ID + ".setTile2(" + x + ", '" + y + "', '" + this.tileBoard + "');");
        this.emptyTiles();
    }
}

boolean setzeTileBoard(int x, int y) {
    return this.tile != ' ' && this.tileBoard == ' ' 
           && this.currentBoard[x][y-3].equals("0") 
           && this.specialFields.contains(this.board[x][y-3]);
}

boolean setzeTileTop(int x, int y) {
    return this.tile == ' ' && this.tileBoard != ' ' 
           && this.tilesTop[x-7] == '0';
}

boolean setzeTileBottom(int x, int y) {
    return this.tile == ' ' && this.tileBoard != ' ' 
           && this.tilesBottom[x-1] == '0';
}
boolean holeTileTop(int x, int y){
    if(this.tile == ' ' && this.tileBoard == ' ' && this.tilesTop[x-7] != '0'){ 
        return true;
    } else {
        return false;
    }
}
boolean holeTileBottom(int x, int y){
    if(this.tile == ' ' && this.tileBoard == ' ' && this.tilesBottom[x-1] != '0'){
        return true;
    } else {
        return false;
    }
}
boolean holeTileBoard(int x, int y){
    if(this.tile == ' ' && this.tileBoard == ' ' && this.specialFields.contains(this.board[x][y-3]) && !(this.currentBoard[x][y-3].equals("0"))){
        return true;
    } else {
        return false;
    }
}

char getTile(int x, int y) {
    // Hole Stein für ersten Klick (von TopTiles)
    if (x >= 7 && x <= 13 && y == 2 && this.currentPlayer > 0 && holeTileTop(x, y)) {  
        this.tile = this.tilesTop[x-7];  // Hole Stein auf den geklickt wurde
        this.tileBoard = ' ';
        this.tilesTop[x-7] = '0'; // Entferne Stein von TopTiles   
        Clerk.script(view, "scrabble" + ID + ".removeTile("+ x + ", '" + y + "');");
    } 
    // Hole Stein für ersten Klick (von BottomTiles)
    else if (x >= 1 && x <= 7 && y == 18 && this.currentPlayer < 0 && holeTileBottom(x, y)) { 
        this.tile = this.tilesBottom[x-1];
        this.tileBoard = ' ';
        this.tilesBottom[x-1] = '0'; // Entferne Stein von BottomTiles
        Clerk.script(view, "scrabble" + ID + ".removeTile("+ x + ", '" + y + "');");
    } 
    // Hole Stein vom Board
    else if (x >= 0 && x <= 14 && y >= 3 && y <= 17 && holeTileBoard(x, y)) {
        this.tileBoard = this.currentBoard[x][y-3].charAt(0); // Hole Stein vom Board
        this.tile = ' ';
        this.currentBoard[x][y-3] = "0"; // Entferne Stein vom Board
        Clerk.script(view, "scrabble" + ID + ".setColor("+ (y-3) + ", '" + x + "');"); 
        Clerk.script(view, "scrabble" + ID + ".setText("+ (y-3) + ", '" + x + "');");
    }
    return this.tile;
}

int updateScore(Word word){

    int startX = word.start.x;
    int startY = word.start.y;
    int endX = word.end.x;
    int endY = word.end.y;

    int mulValue = 1;
    int score = 0;

    if(startX == endX){
        for (int i = startY; i <= endY; i++) {
            score += addiereScore(this.currentBoard[startX][i], startX, i);
            mulValue += mulScore(startX, i);
        }
    } else if(startY == endY){
        for (int i = startX; i <= endX; i++) {
            score += addiereScore(this.currentBoard[i][startY], i, startY);
            mulValue += mulScore(i, startY);
        }
    }
    if(mulValue > 1){
        mulValue--;
    }
    return mulValue * score;
}


int addiereScore(String currentLetter, int x, int y){
    int counter = 0;

    if(letterScores.get(currentLetter) == 1) { // double letter

        if(this.board[x][y].equals("DL")){ 
        counter += 2;
        }else if(this.board[x][y].equals("TL")){
            counter += 3;
        }else if(this.board[x][y].equals("NaN") || this.board[x][y].equals("DW") || this.board[x][y].equals("TW")){
            counter += 1;
        }

    }else {
        if(this.board[x][y].equals("DL")){
        counter += 2 * letterScores.get(currentLetter);
        } else if(this.board[x][y].equals("TL")){
            counter += 3 * letterScores.get(currentLetter);
        } else if(this.board[x][y].equals("NaN") || this.board[x][y].equals("DW") || this.board[x][y].equals("TW")){
            counter += letterScores.get(currentLetter);
        }
    }
    return counter;
}

int mulScore(int x, int y){

    if (this.board[x][y].equals("DW")){
        return 2;
    } else if(this.board[x][y].equals("TW")){
        return 3;
    } else {
        return 0;
    }
}

    /*
/o lvp.java
/o Views/Scrabble01/Scrabble01.java
Scrabble01 scrabble = new Scrabble01()
System.out.println(scrabble.toString());

 scrabble.currentBoard[0][0] = "B"
 scrabble.currentBoard[0][1] = "A"
 scrabble.currentBoard[1][0] = "A"
 scrabble.currentBoard[0][2] = "U"
 scrabble.currentBoard[2][0] = "U"
 scrabble.currentBoard[0][3] = "M"
 scrabble.currentBoard[3][0] = "M"

scrabble.currentBoard[0][0] = "A";
        scrabble.currentBoard[0][1] = "B";
        scrabble.currentBoard[0][2] = "B";
        scrabble.currentBoard[0][3] = "I";
        scrabble.currentBoard[0][4] = "L";
        scrabble.currentBoard[0][5] = "D";
        scrabble.currentBoard[0][6] = "U";
        scrabble.currentBoard[0][7] = "N";
        scrabble.currentBoard[0][8] = "G";
        scrabble.currentBoard[0][9] = "S";
        scrabble.currentBoard[0][10] = "E";
        scrabble.currentBoard[0][11] = "B";
        scrabble.currentBoard[0][12] = "E";
        scrabble.currentBoard[0][13] = "N";
        scrabble.currentBoard[0][14] = "E";

scrabble.currentBoard[0][14] = "E";
        scrabble.currentBoard[1][14] = "B";
        scrabble.currentBoard[2][14] = "E";
        scrabble.currentBoard[3][14] = "N";
        scrabble.currentBoard[4][14] = "H";
        scrabble.currentBoard[5][14] = "O";
        scrabble.currentBoard[6][14] = "L";
        scrabble.currentBoard[7][14] = "Z";
        scrabble.currentBoard[8][14] = "G";
        scrabble.currentBoard[9][14] = "E";
        scrabble.currentBoard[10][14] = "W";
        scrabble.currentBoard[11][14] = "Ä";
        scrabble.currentBoard[12][14] = "C";
        scrabble.currentBoard[13][14] = "H";
        scrabble.currentBoard[14][14] = "S";

        scrabble.board[0][0] = "A";
        scrabble.board[0][1] = "B";
        scrabble.board[0][2] = "B";
        scrabble.board[0][3] = "I";
        scrabble.board[0][4] = "L";
        scrabble.board[0][5] = "D";
        scrabble.board[0][6] = "U";
        scrabble.board[0][7] = "N";
        scrabble.board[0][8] = "G";
        scrabble.board[0][9] = "S";
        scrabble.board[0][10] = "E";
        scrabble.board[0][11] = "B";
        scrabble.board[0][12] = "E";
        scrabble.board[0][13] = "N";
        scrabble.board[0][14] = "E";

        scrabble.board[0][14] = "E";
        scrabble.board[1][14] = "B";
        scrabble.board[2][14] = "E";
        scrabble.board[3][14] = "N";
        scrabble.board[4][14] = "H";
        scrabble.board[5][14] = "O";
        scrabble.board[6][14] = "L";
        scrabble.board[7][14] = "Z";
        scrabble.board[8][14] = "G";
        scrabble.board[9][14] = "E";
        scrabble.board[10][14] = "W";
        scrabble.board[11][14] = "Ä";
        scrabble.board[12][14] = "C";
        scrabble.board[13][14] = "H";
        scrabble.board[14][14] = "S";

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
scrabble.board[3][4] = "R"

scrabble.currentBoard[3][14] = "P"
scrabble.currentBoard[4][14] = "F"
scrabble.currentBoard[5][14] = "A"
scrabble.currentBoard[6][14] = "U"

scrabble.getWords()
*/
List<Word> getWords(String[][] actualBoard) { //wholeWords von updated Board
    List<Word> extractedWords = new ArrayList<>();

    Position pos = new Position(0, 0);
    // neue Positionen speichern
    for (int i = 0; i < actualBoard.length; i++) {
        for (int j = 0; j < actualBoard[i].length; j++) {
            if (!actualBoard[i][j].equals("0")) {
                pos.addPosition(i, j);
            }
        }
    }
    for (Position position : pos.positions) {
        int x = position.x;
        int y = position.y;

        if (position.checkLeft) {
            Word wordH = extractWordHorizontal(x, y, pos, actualBoard);

            if (wordH.word.length() > 1){
                extractedWords.add(wordH);
            }

        }
        if (position.checkDown) {
            Word wordV = extractWordVertical(x, y, pos, actualBoard);
            if (wordV.word.length() > 1){
                extractedWords.add(wordV);
            }
        }
    }
    pos.clearPositions();
    return extractedWords;
}

Word extractWordHorizontal(int x, int y, Position pos, String[][] board1) {
    StringBuilder w = new StringBuilder();

    while (y > 0 && !(board1[x][y - 1].equals("0"))){
        y--;
    }

    Position start = new Position(x, y);
    //gehe nach rechts setze wort zusammen
    while (y < board1.length && !(board1[x][y].equals("0"))) {
        w.append(board1[x][y]);
        for (Position position : pos.positions) {
            if(position.x == x && position.y == y){
                position.checkLeft = false; //wenn bereits an pos vorbeigelaufen, muss nicht mehr nach links überprüft werden (Reihe schon geprüft)
            }
        }
        y++;
    }
    Position end = new Position(x, y-1);
    Word word2 = new Word(w.toString(), start, end);
    return word2;
}

Word extractWordVertical(int x, int y, Position pos, String[][] board1) {
    StringBuilder w = new StringBuilder();

    //nach oben
    while (x > 0 && !(board1[x][y - 1].equals("0"))){
        x--;
    }
    Position start = new Position(x, y);

    //nach unten und wort zusammensetzen
    while (x < board1.length && !(board1[x][y - 1].equals("0"))) {
        w.append(board1[x][y]);
        for (Position position : pos.positions) {
            if(position.x == x && position.y == y){
                position.checkDown = false;
            }
        }
        x++;
    }
    Position end = new Position(x-1, y);
    Word word2 = new Word(w.toString(), start, end);
    return word2;
}

List<Word> getAllWords() {
    List<Word> words = new ArrayList<>();
    StringBuilder w = new StringBuilder();
    Position start = null;
    Position end = null;
    // Wörter aus Reihen extrahieren
    for (int row = 0; row < this.updatedBoard.length; row++) {
        for (int col = 0; col < this.updatedBoard[0].length; col++) {
            String letter = this.updatedBoard[row][col];

            if (!letter.equals("0")) {
                if (w.length() == 0) {
                    start = new Position(row, col);
                }
                if(col + 1 >= this.updatedBoard[row].length || updatedBoard[row][col+1].equals("0")){
                    end = new Position(row, col);
                }
                w.append(letter);
            } else if (w.length() > 1) {
                words.add(new Word(w.toString(), start, end));
                w.setLength(0);
            }
        }
        // Falls nach der letzten Spalte noch ein Wort übrig ist
        if (w.length() > 1) {
            words.add(new Word(w.toString(), start, end));
        }
        w.setLength(0);
    }

    // Wörter aus Spalten extrahieren
    for (int col = 0; col < this.updatedBoard[0].length; col++) {
        for (int row = 0; row < this.updatedBoard.length; row++) {
            String letter = this.updatedBoard[row][col];
            if (!letter.equals("0")) {
                if (w.length() == 0) {
                    start = new Position(row, col);
                }
                if(col + 1 >= this.updatedBoard[col].length || updatedBoard[row+1][col].equals("0")){
                    end = new Position(row, col);
                }
                w.append(letter);
            } else if (w.length() > 1) {
                words.add(new Word(w.toString(), start, end));
                w.setLength(0);
            }
        }
        // Falls nach der letzten Zeile noch ein Wort übrig ist
        if (w.length() > 1) {
            words.add(new Word(w.toString(), start, end));
        }
        w.setLength(0);
    }
    return words;
}

List<Word> getWrongWords(List<Word> allWords, List<Word> words){
    List<Word> allWords2 = new ArrayList<>(allWords);
    List<Word> words2 = new ArrayList<>(words);

    for (int i = 0; i < allWords2.size(); i++) {
        for (int j = 0; j < words2.size(); j++) {
            System.out.println("i: " + i + "j" + j);
            if(allWords2.get(i).word.equals(words2.get(j).word)){
                allWords2.remove(i);
            }
        }
    }
    for (Word word : words2) {
        if(!validateWord(word.word)){
            allWords2.add(word);
        }
    }
    return allWords2;
}
/*
List<Word> words = List.of(
        new Word("apple", new Position(1, 2), new Position(3, 4)),
        new Word("banana", new Position(5, 6), new Position(7, 8)),
        new Word("cherry", new Position(9, 10), new Position(11, 12))
    );

    List<Word> allWords = List.of(
        new Word("apple", new Position(1, 2), new Position(3, 4)),
        new Word("banana", new Position(5, 6), new Position(7, 8)),
        new Word("cherry", new Position(0, 0), new Position(0, 5)),
        new Word("cherry", new Position(9, 10), new Position(11, 12))
    );
 */
boolean validateWord(String input) {
   
    assert input.length() > 1;
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder bs = new StringBuilder();
    
        // currentBoard formatieren
        sb.append("Current Board:\n");
        for (int i = 0; i < this.currentBoard.length; i++) {
            sb.append("  "); // Einrückung für jede Zeile
            for (int j = 0; j < this.currentBoard[i].length; j++) {
                sb.append(this.currentBoard[i][j]).append(" | ");  // Leerzeichen zwischen Zeichen
            }
            sb.append(" | "+ "\n"); // Neue Zeile nach jeder Reihe
        }
    
        // board formatieren
        bs.append("Board:\n");
        for (int i = 0; i < this.board.length; i++) {
            bs.append("  "); // Einrückung für jede Zeile
            for (int j = 0; j < this.board[i].length; j++) {
                bs.append(this.board[i][j]).append(" | ");  // Leerzeichen zwischen Zeichen
            }
            bs.append(" | "+ "\n"); // Neue Zeile nach jeder Reihe
        }
    
        return sb.append("\n").append(bs).toString();
    }
} 