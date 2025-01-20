import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
/* 
HEUTE:
-> changeTiles schreiben, button hinzufügen <->

NOCH ZU TUN:
-> score Anzeige erhöhen
-> nach Legen tiles wieder ausffüllen
-> Schwierigkeitseinstellungen
-> Hilfsfunktion 
-> Spracheinstellung 
-> map ändern 
-> gehaltenen Buchstaben visualisieren 

SPIELABLAUF:

-Spieler1 legt fertig 
-kann Steine austauschen 
-drückt auf play
-daraufhin Wörtervalidierung
-wenn Wörter nicht validiert: Fehler anzeigen
-score erhöhen 
-Spieler2 kann legen 
-Wenn Fehlermeldung und das Wort noch auf dem Feld liegt, darf nicht nochmal play geklickt werden dürfen 
- Steine bottom rchtig zurücklegen 
- Anfangswort muss nur validiert werden 

Ein Spieler zieht neue Steine, wenn er an der Reihe ist und keine gültigen Wörter legen kann. 
Der Spieler kann beliebig viele Steine austauschen, indem er sie zurück in den Beutel legt und 
dafür dieselbe Anzahl an neuen Steinen zieht.
*/

enum Level{
    EASY, NORMAL, HARD;
}

class Bag{

    List<Character> bag1 = new ArrayList<>();
    List <Character> bag2 = new ArrayList<>();
    Map<Character, Integer> charFrequency = new HashMap<>();//wie häufig jeder Buchstabe im Beutel vorkommt
    char[] alphabet = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'Ä', 'Ö', 'Ü'
    };
    int[] frequencies = {8, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1, 1, 1, 1};
    List<Character> availableLetters = new ArrayList<>();

    Bag(){
        this.fillBag();;
    }
    void fillBag() {
        for (int i = 0; i < this.alphabet.length; i++) {
            this.charFrequency.put(this.alphabet[i], this.frequencies[i]);
            this.availableLetters.add(this.alphabet[i]);
        }
        helpFillBag(this.bag1);
        helpFillBag(this.bag2);
    }

    void helpFillBag(List<Character> bag) {
            for (int i = 0; i < 50; i++) {
                char currLetter = getRandomElement(this.availableLetters);
                int value = this.charFrequency.get(currLetter);
                if (value > 0) {
                    this.charFrequency.put(currLetter, value - 1);
                    value--;
                    bag.add(currLetter);
                    if (value == 0) {
                        this.availableLetters.remove((Character) currLetter); //casten, weil sonst möglicherweise ASCII-Wert verwendet wird
                    }
                }
            }
        }

    char getRandomElement(List<Character> letterList){
        Random random = new Random();
        int rand = random.nextInt(letterList.size());
        return letterList.get(rand);
    }
}

class Player01 {

    int score = 0;
    List <Character> bag = new ArrayList<>(); //Player1.bag = Bag.bag1;
    
    void setBag(List<Character> sourceBag) {
        this.bag = new ArrayList<>(sourceBag); 
    }
}

class Position implements Comparable<Position>{

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
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Position position = (Position) other;
        return x == position.x && y == position.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
public int compareTo(Position other) {
    // Vergleiche nur, wenn x gleich ist, dann nach y sortieren
    if (this.x == other.x) {
        return Integer.compare(this.y, other.y);
    }
    // Wenn y gleich ist, dann nach x sortieren
    if (this.y == other.y) {
        return Integer.compare(this.x, other.x);
    }
    // Falls weder x noch y gleich ist
    throw new IllegalArgumentException("Cannot compare positions");
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
    boolean validated = false;
    List<Position> positions = new ArrayList<>();
    
    Word(String word, Position start, Position end){
        this.word = word;
        this.start = start;
        this.end = end;
    }

    void addPosition(int x, int y){
        this.positions.add(new Position(x, y));
    }

    void clearPositions(){
        this.positions = new ArrayList<>();
    }
    int getSizePosition(){
        return this.positions.size();
    }
    @Override
    public boolean equals(Object other) { 
        if (other == null) return false; // Null abwehren!
        if (other == this) return true; // Bin ich's selbst?
        if (other.getClass() != getClass()) return false; // Andere Klasse?
        Word that = (Word)other; // Casting
        return Objects.equals(new HashSet<>(this.positions), new HashSet<>(that.positions)); // Was definiert Gleichheit?
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "Wort("+ word + " Position{start = " + start + ", end = " + end + "})";
    }
}

class Scrabble01 implements Clerk{

        Level level;
        final String ID;
        final int width, height;
        final String libPath = "views/Scrabble01/scrabble01.js";
        LiveView view;

        Bag bag = new Bag(); 
        final Player01 player1;
        final Player01 player2;
        int currentPlayer = 1; //Spieler 1 beginnt
        char tile;
        char tileBoard;
        List<Word> rightWords; //neu hinzugefügte& validierte Wörter
        List<Word> wrongWords;

        char[] tilesTop = new char[7];
        char[] tilesBottom = new char[7];
        boolean isFirstWord = true;
        Word firstWord = new Word("", null, null); 
        Position boardPositions = new Position(0, 0);
        //Position fPos = new Position(0,0);
        
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
    
        Scrabble01(LiveView view, int width, int height, Player01 player1, Player01 player2, Bag bag) {

            for (int i = 0; i < this.currentBoard.length; i++) {
                for (int j = 0; j < this.currentBoard[i].length; j++) {
                    this.currentBoard[i][j] = "0";
                    this.updatedBoard[i][j] = "0";
                }
            }
            for (int row = 0; row < this.board.length; row++) {
                for (int col = 0; col < this.board[0].length; col++) {
                    this.boardPositions.addPosition(row, col);
                }
            }
            this.view = view;
            this.width  = Math.max(1, Math.abs(width));  // width is at least of size 1
            this.height = Math.max(1, Math.abs(height)); // height is at least of size 1
            this.player1 = player1;
            this.player2 = player2;
            this.tile = ' ';
            this.tileBoard  = ' ';
            this.bag.fillBag();
            this.player1.setBag(bag.bag1); //Fülle Bag von player1 mit bag1 aus Bag
            this.player2.setBag(bag.bag2);
            
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

                //ich hole Wörter von updated Board, wenn Fehlerausgabe, Buchstaben auf die nach Fehlerausgabe geklickt wurden, müssen auscurrentBoard und updatedBoard gelöscgt werden
                //Dann muss updatedBoard wieder mit currentBoard überschrieben werden.
                if(this.isFirstWord){ //hole bei ersten Cklicks das erste Wort
                    getFirstWord(x, y); 
                  //  System.out.println("erstes Wort: " + this.firstWord);
                   // System.out.println("ist es erstes Wort?: " + this.isFirstWord);
                }
                 if(endTurn(x, y)){
                    overrideUpdatedBoard(); //currentBoard wird hinzugefügt
                    this.wrongWords = getWrongWords(getWords(this.updatedBoard, this.boardPositions.positions), getWords(this.updatedBoard, this.firstWord.positions)); //alle Wörter durch jede Position des Bords, nur zusammenhängende duch firstWord-Positionen
                    //System.out.println("wrongWords" + this.wrongWords);

                    if(this.wrongWords.isEmpty()){ //dann kann weitergespielt werden
                        //Score mit in dieser Runde gesetzten Wörtern erhöhen 
                        List<Word> scoredWords = getWords(this.currentBoard, this.boardPositions.positions);
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
                    } else {
                        for(Word w: this.wrongWords){
                            //System.out.println("wrong word" + w);
                            throwError(w);
                        }
                        this.wrongWords = null;
                        deleteCurrentFromUpdated();
                        //clearCurrentBoard();//so können tiles nicht mehr vom board genommen werden
               /* 
                sonst Fehlerausgabe an Positionen von getWrongWords 
                 * updatedBoard wieder currentWord-Positionen löschen 
              */
                }  
            }
        });
    } 

void changeTiles(int x, int y){

}
void deleteCurrentFromUpdated(){
    for (int row = 0; row < this.updatedBoard.length; row++) {
        for (int col = 0; col < this.updatedBoard[row].length; col++) {
            if(this.updatedBoard[row][col].equals(this.currentBoard[row][col])){
                this.updatedBoard[row][col] = "0";
            }
        }
    }
    
}

void getFirstWord(int x, int y){
    if(this.isFirstWord && x >= 0 && x <= 14 && y >= 3 && y <= 17){
                    
    int lastPos = 0;
    y -= 3;
    System.out.println("y: " + y);
                     //System.out.println("positionen des erstenWorts: " + this.firstWord.getSizePosition());
                     /* hole Positionen für firstWord
                      * setze von dort aus erstes Wort zusammen 
                    */
                     //baue String zusammen und speichere start und end-Position (Buchstaben hinzufügen)
    if(this.firstWord.positions.size() > 1){
        lastPos = this.firstWord.positions.size()-2;
                         //System.out.println("x: " + x + " , vorheriges x: " + this.firstWord.positions.get(lastPos).x);
                         //System.out.println("y: " + y + " , vorheriges y: " + this.firstWord.positions.get(lastPos).y);
                    
                      
                     //Positionen und Buchstaben entfernen
        }
        if(!(this.firstWord.positions.size() > 0)) {
            this.firstWord.addPosition(x, y);
        } else {
            if(((x) == (this.firstWord.positions.get(lastPos).x) && (x) == (this.firstWord.positions.get(0).x)) || ((y) == (this.firstWord.positions.get(lastPos).y) && (y) == (this.firstWord.positions.get(0).y))){ //Wenn startPosition und vorletzte Position von x oder y gleich, dann ist es immernoch das erste Wort
                         //System.out.println("reingeschafft");
                         
                if(this.firstWord.positions.contains(new Position(x, y))){
                    this.firstWord.positions.remove(new Position(x, y));
                } else if(!(this.firstWord.positions.contains(new Position(x, y)))){
                    this.firstWord.addPosition(x, y);
                }
                            //falls noch mal auf die gleiche Position geklickt wird, entferne diese wieder, wenn auf Position geklickt wurde, die bereits in fPos gespeichert ist
                         //entferne Buchstaben an der Position, entferne entweder vorderen oder hinteren Teilstring 
                         //firstClick in fPos an erster Stelle 
                         //dann passiert nichts, vom ersten Wort aus die getWords verwenden. Wenn Buchstabe wieder vom Board genommen wird
            } else { //wenn auf play gedrückt wurde auch nicht mehr first Word
                this.isFirstWord = false;
                StringBuilder fWord = new StringBuilder(); 
                Collections.sort(this.firstWord.positions); //wenn Positionen nicht aneinanderhängend sind?
                        
                for(Position pos: this.firstWord.positions){
                    x = pos.x;
                    y = pos.y;
                            //System.out.println("currentBoard: " + this.currentBoard[x][y]);
                    fWord.append(this.updatedBoard[x][y]);
                }
                this.firstWord.word = fWord.toString(); 
            }
        }
    }
}

void chooseLevel(int x, int y){

    if(x >= 16 && x <= 18 && y == 5){
        this.level = Level.EASY;
    } else if(x >= 16 && x <= 18 && y == 6){
        this.level = Level.NORMAL;
    } else if(x >= 16 && x <= 18 && y == 7){
        this.level = Level.HARD;
    } else{
        this.level = Level.NORMAL;
    }
}

void throwError(Word w){
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
        for (int j = 0; j < this.currentBoard[i].length; j++) {
            if(this.updatedBoard[i][j].equals("0")){
                this.updatedBoard[i][j] = this.currentBoard[i][j];
            }
        }
    }
}
void overrideBoard(){
    for (int i = 0; i < this.currentBoard.length; i++) {
        for (int j = 0; j < this.currentBoard[i].length; j++) {
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

List<Word> getWords(String[][] actualBoard, List<Position> positions) { 
    //wholeWords von updated Board
    //Wörter müssen alle an firstWord hängen, von Positionen des ersten Wortes alle anhängenden Wörter holen
    List<Word> extractedWords = new ArrayList<>();
    Set<Word> uniqueWords = new HashSet<>();
    
    //Position pos = new Position(0, 0);
    for (Position position : positions) { 
      
        if (position.checkLeft) { 
           Word wordH = extractWordHorizontal(position.x, position.y, positions, actualBoard);
          // System.out.println("wordH: " + wordH);
           if (wordH.word.length() > 1){
             uniqueWords.add(wordH);
            } 
        }
    
        if (position.checkDown) {
            Word wordV = extractWordVertical(position.x, position.y, positions, actualBoard);
           // System.out.println("wordV: " + wordV);
    
            if (wordV.word.length() > 1){
              uniqueWords.add(wordV);
           
            } else if(wordV.word.length() == 1){
                int x = wordV.positions.get(0).x;
                int y = wordV.positions.get(0).y;

                //(x < 14 && x > 1 && y < 14 && y > 1)
                if(x < 1 && x < 14 && y < 14 && y > 1){
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x][y+1].equals("0") && actualBoard[x][y-1].equals("0")){
                        uniqueWords.add(wordV);
                
                    }
                } else if(x > 13 && x < 1 && y < 14 && y > 1){
                    if(actualBoard[x-1][y].equals("0") && actualBoard[x][y+1].equals("0") && actualBoard[x][y-1].equals("0")){
                        uniqueWords.add(wordV);
                 
                    }
                } else if(y < 1 && x > 1 && x < 14 && y < 14){
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x-1][y].equals("0") && actualBoard[x][y+1].equals("0")){
                        uniqueWords.add(wordV);
                   
                    }
                } else if(y > 13 && x < 1 && y < 14 && y > 1){
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x-1][y].equals("0") && actualBoard[x][y-1].equals("0")){
                        uniqueWords.add(wordV);
                      
                    }
                } else if(x < 1 && y < 1 && x < 14 && y < 14){
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x][y+1].equals("0")){
                        uniqueWords.add(wordV);
                        
                    }
                } else if(x < 1 && y > 13 && x < 14 && y > 1){
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x][y-1].equals("0")){
                        uniqueWords.add(wordV);
                       
                    }
                } else if(x > 13 && y < 1  && x > 1 && y < 14){
                    if(actualBoard[x-1][y].equals("0") && actualBoard[x][y+1].equals("0")){
                        uniqueWords.add(wordV);
                   
                    }
                } else if(x > 13 && y > 13 && x > 1  && y > 1){
                    if(actualBoard[x-1][y].equals("0") && actualBoard[x][y-1].equals("0")){
                        uniqueWords.add(wordV);
                      
                    }
                } else {
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x-1][y].equals("0") && actualBoard[x][y+1].equals("0") && actualBoard[x][y-1].equals("0")){
                        uniqueWords.add(wordV);
                      
                }
            }
        }
            
    }

    //position.clearPositions();
    }

    for(Position position: positions){
        position.checkLeft = true;
        position.checkDown = true;
    }
   
    for(Word w: uniqueWords){
        extractedWords.add(w);
    }
    return extractedWords;
}

Word extractWordHorizontal(int x, int y, List<Position> posi, String[][] board1) {
    StringBuilder w = new StringBuilder();
    List<Position> positions = new ArrayList<>();

    while (y > 0 && !(board1[x][y - 1].equals("0"))){
        y--;
    }

    Position start = new Position(x, y);
  
    //gehe nach rechts setze wort zusammen
    while (y < board1.length && !(board1[x][y].equals("0"))) {
        w.append(board1[x][y]);
        positions.add(new Position(x, y));
        for(Position position: posi){
            if(position.x == x && position.y == y){
                position.checkLeft = false; //wenn bereits an pos vorbeigelaufen, muss nicht mehr nach links überprüft werden (Reihe schon geprüft)
            }
        }
        y++;
    }
    Position end = new Position(x, y-1);
    Word word2 = new Word(w.toString(), start, end);
    word2.positions = positions;
    return word2;
}

Word extractWordVertical(int x, int y, List<Position> posi, String[][] board1) {
    StringBuilder w = new StringBuilder();
    List<Position> positions = new ArrayList<>();

    //nach oben
    while (x > 0 && !(board1[x - 1][y].equals("0"))){
        x--;
    }
    Position start = new Position(x, y);

    //nach unten und wort zusammensetzen
    while (x < board1.length && !(board1[x][y].equals("0"))) {
        w.append(board1[x][y]);
        positions.add(new Position(x, y));
        for(Position position: posi){
            if(position.x == x && position.y == y){
                position.checkDown = false; //wenn bereits an pos vorbeigelaufen, muss nicht mehr nach links überprüft werden (Reihe schon geprüft)
            }
        }
        x++;
    }
    Position end = new Position(x-1, y);
    Word word2 = new Word(w.toString(), start, end);
    word2.positions = positions;
    //Sout.println("word2 psoitions: " + word2.positions);
    return word2;
}
 
/*List<Word> getAllWords(String[][] acutualBoard) {  //warum holt es sich nicht zusammenhängende Buchstaben als Würter, wenn sie in einer Reihe sind
    List<Word> words = new ArrayList<>();
    List<Position> positions = new ArrayList<>();
    Word word = null;
    StringBuilder w = new StringBuilder();
    Position start = null;
    Position end = null;
    // Wörter aus Reihen extrahieren
    for (int row = 0; row < acutualBoard.length; row++) {
        for (int col = 0; col < acutualBoard[0].length; col++) {
            String letter = acutualBoard[row][col];

            if (!letter.equals("0")) {
                if (w.length() == 0) {
                    start = new Position(row, col);
                }
                if(col + 1 >= acutualBoard[row].length || acutualBoard[row][col+1].equals("0")){
                    end = new Position(row, col);
                }
                w.append(letter);
                positions.add(new Position(row, col));
            } else if (w.length() > 1) {
                word = new Word(w.toString(), start, end);
                word.positions = positions;
                words.add(word);
                w.setLength(0);
                positions = new ArrayList<>();
            }
        }
        // Falls nach der letzten Spalte noch ein Wort übrig ist
        if (w.length() > 1) {
            word = new Word(w.toString(), start, end);
            word.positions = positions;
            words.add(word);
        }
        positions = new ArrayList<>();
        w.setLength(0);
    }
    // Wörter aus Spalten extrahieren
    for (int col = 0; col < acutualBoard[0].length; col++) {
        for (int row = 0; row < acutualBoard.length; row++) {
            String letter = acutualBoard[row][col];
            if (!letter.equals("0")) {
                if (w.length() == 0) {
                    start = new Position(row, col);
                }
                if(col + 1 >= acutualBoard[col].length || acutualBoard[row+1][col].equals("0")){
                    end = new Position(row, col);
                }
                w.append(letter);
                positions.add(new Position(row, col));
            } else if (w.length() > 1) {   
                word = new Word(w.toString(), start, end);
                word.positions = positions;
                words.add(word);
                w.setLength(0);
                positions = new ArrayList<>();
            }
        }
        // Falls nach der letzten Zeile noch ein Wort übrig ist
        if (w.length() > 1) {
            word = new Word(w.toString(), start, end);
            word.positions = positions;
            words.add(word);
        }
        positions = new ArrayList<>();
        w.setLength(0);
    }
    return words;
}*/


List<Word> getWrongWords(List<Word> allWords, List<Word> words){
    List<Word> allWords2 = new ArrayList<>(allWords);
    List<Word> words2 = new ArrayList<>(words);
    List<Word> validatedWords = new ArrayList<>();
    //System.out.println("allWords2: " + allWords2);
    //System.out.println("words2: " + words2);
    for (int i = 0; i < allWords2.size(); i++) {
        for (int j = 0; j < words2.size(); j++) {
            //System.out.println("i: " + i + "j" + j);
           /*  if(allWords2.get(i).word.equals(this.firstWord.word)){
                allWords2.get(i).validated = true; //das erste Wort muss nicht überprüft werden, ob es zusammenhängend ist
            }*/
            if(allWords2.get(i).word.equals(words2.get(j).word) && allWords2.get(i).validated == false){
                validatedWords.add(allWords2.get(i)); //füge das Wort in validierte Liste 
                allWords2.remove(i); // in allWords stehen nicht-zusammenhängende Wörter, die die übrig bleiben sind nicht validiert
                //System.out.println("validatedWords: " + validatedWords);
               // System.out.println("wrongWords: " + allWords2);
            }
        }
    }
    if(validateWord(this.firstWord.word)){
        validatedWords.add(this.firstWord);
        allWords2.remove(this.firstWord);
    }
   // System.out.println("falsche Wörter: " + allWords2);
    for (Word word : words2) {
        if(!validateWord(word.word)){
            allWords2.add(word);
        } 
    }
   // System.out.println("validatedWords: " + validatedWords);
    //System.out.println("falsche Wörter: " + allWords2);
    this.wrongWords = allWords2;
    return this.wrongWords;
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
    // Großer Anfangsbuchstabe, kleiner Anfangsbuchstabe. Wenn eins von beiden passt, passts
    if(input.length() < 2){
        return false;
    } else {

    String input1 = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    String input2 = input.toLowerCase();

    // Funktion für die Validierung des Wortes
    return isValidWord(input1) || isValidWord(input2);
}
}

private boolean isValidWord(String word) {
    try {
        URI uri = URI.create("https://www.dwds.de/api/wb/snippet/?q=" + word);
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
        //System.out.println(jsonResponse);
        return jsonResponse.contains("\"lemma\":");
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

    Scrabble01(LiveView view) { this(view, 600, 600, new Player01(), new Player01(), new Bag()); }
    Scrabble01(int width, int height) { this(Clerk.view(), width, height, new Player01(), new Player01(), new Bag()); }
    Scrabble01() { this(Clerk.view(), 600, 600, new Player01(), new Player01(), new Bag());}


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