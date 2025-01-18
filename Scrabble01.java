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
NOCH ZU TUN:
-> funktionieren getWords richtig?
-> firstWord als Wort 
-> wrongWords verbessern
-> changeTiles schreiben, button hinzufügen <->
-> Schwierigkeitseinstellungen
-> erhöht sich Score Anzeige?
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
        return "Position{x = " + x + ", y = " + y + "positions: " + positions.toString() + "}";
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
        positions = new ArrayList<>();
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
        Word firstWord; 
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
                getFirstWord(x, y); 
               // updateWrongWords(x, y); //wenn wrongWords nicht leer, und wieder auf Position geklickt wird, entferne dieses 
                
                /* 
                 if (endTurn(x, y)){
                    overrideUpdatedBoard();
                    this.wrongWords = new ArrayList<>(getWrongWords(getAllWords(), getWords(this.updatedBoard))); //wrong words wieder aus updatedBoard löschen 
                    System.out.println("wrongWords" + this.wrongWords);

                    if(this.wrongWords.isEmpty()){ //dann kann weitergespielt werden
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
                    } else {
                        for(Word w: this.wrongWords){
                            System.out.println("wrong word" + w);
                            throwError(w);
                        }
                        //clearCurrentBoard();//so können tiles nicht mehr vom board genommen werden
               
                sonst Fehlerausgabe an Positionen von getWrongWords 
                 * updatedBoard wieder currentWord-Positionen löschen 
              
                }  
            }*/
        });
    } 

void changeTiles(int x, int y){

}
/* 
void updateWrongWords(int x, int y){
    for(Word word: this.wrongWords){
        int startX = word.start.x;
        int startY = word.start.y;
        int endX = word.end.x;
        int endY = word.end.y;

        String strWord = word.word;
        //wenn Position am Anfang oder Ende des Wortes entfernt einfach entfernen
        if(x == startX && y == startY){
            //entferne ersten Buchstaben
            word.word = word.word.substring(1);
        } else if(x == endX && y == endY){
            //entferne letzten Buchstaben 
            word.word = word.word.substring(0, word.word.length() - 1);
        } 
        if(startX == endX && x == startX){ //in einer Reihe
            for(int i = startY; i <= endY; i++){ //gehe wortPositinen Y durch 
               if(i == y){
                String str1 = strWord.substring(0, i - startY);
                String str2 = strWord.substring(i - startY + 1);

                this.wrongWords.add(new Word(str1, new Position(startX, startY), new Position(endX, endY-i)));
                this.wrongWords.add(new Word(str2, new Position(startX, startY+i), new Position(endX, endY)));
                this.wrongWords.remove(word);
               }
                //wenn in der Mitte, splitte das Wort an Positin die entfernt werden soll und speichere als 2 neue Wörter 
            } 
        } else if(startY == endY && y == startY){ //in einer Spalte
            for(int i = startX; i <= endX; i++){ //gehe wortPositinen X durch 
                if(i == x){
                    String str1 = strWord.substring(0, i - startY);
                    String str2 = strWord.substring(i - startY + 1);
                    this.wrongWords.add(new Word(str1, new Position(startX, startY), new Position(endX-i, endY)));
                    this.wrongWords.add(new Word(str2, new Position(startX+i, startY), new Position(endX, endX)));
                    this.wrongWords.remove(word);
                }
                 //wenn in der Mitte, splitte das Wort an Positin die entfernt werden soll und speichere als 2 neue Wörter 
             } 
       //in gleicher Reihe/ Spalte 
       //dementsprechend kleinstes x oder y und größtest
       //prüfe nochmal, ob Wörter validiert werden können
    }
}
}
      /* 
        if(this.firstWord.start.x <= x && this.firstWord.start.y <= y){ //gleich weil in einer Reihe/ Spalte x oder y muss gleich sein
            this.firstWord.start = new Position(x, y);
            fWord.insert(0, this.currentBoard[x][y]);
        } else if(this.firstWord.end.x >= x && this.firstWord.end.y >= y){
            fWord.append(this.currentBoard[x][y]);
        } else if(this.firstWord.start.x >= x && this.firstWord.start.y >= y && this.firstWord.end.x <= x && this.firstWord.end.y <= y){ //liegt in der Mitte
            if(this.firstWord.start.x == x){
                fWord.insert(y - this.firstWord.start.y, this.currentBoard[x][y]);//wenn es in gleicher Reihe ist an Stelle x - start oder y - start
             } else if(this.firstWord.start.y == y){
                fWord.insert(x - this.firstWord.start.x, this.currentBoard[x][y]);
             }
             this.firstWord.word = fWord;
            }*/
void getFirstWord(int x, int y){
    if(this.isFirstWord && x >= 0 && x <= 14 && y >= 3 && y <= 17){
        /*
         * hole Positionen für firstWord
         * setze von dort aus erstes Wort zusammen 
         */
        //baue String zusammen und speichere start und end-Position (Buchstaben hinzufügen)
       if(this.firstWord.positions.size()-1 <= 0 && !(this.firstWord.positions.isEmpty())){
            int lastPos = this.firstWord.positions.size()-1;
            System.out.println("firstWordPos: " + this.firstWord.positions);
            System.out.println("x: " + x + " , vorheriges x: " + this.firstWord.positions.get(lastPos).x + " , erstes x: " + this.firstWord.positions.get(0).x);
            System.out.println("y: " + y + " , vorheriges y: " + this.firstWord.positions.get(lastPos).y + " , erstes y: " + this.firstWord.positions.get(0).y);
        }
       
        
    
        //Positionen und Buchstaben entfernen
       /*  if(((x) == (this.fPos.positions.get(lastPos).x) && (x) == (this.fPos.positions.get(0).x)) || ((y) == (this.fPos.positions.get(lastPos).y) && (y) == (this.fPos.positions.get(0).y)) || (this.fPos.isEmpty())){ //Wenn startPosition und vorletzte Position von x oder y gleich, dann ist es immernoch das erste Wort
            this.fPos.positions.removeIf(e -> e.x == x && e.y == y); //falls noch mal auf die gleiche Position geklickt wird, entferne diese wieder, wenn auf Position geklickt wurde, die bereits in fPos gespeichert ist
            
            //entferne Buchstaben an der Position, entferne entweder vorderen oder hinteren Teilstring 
            //firstClick in fPos an erster Stelle 
            //dann passiert nichts, vom ersten Wort aus die getWords verwenden. Wenn Buchstabe wieder vom Board genommen wird
        } else {
        this.isFirstWord = false;
        StringBuilder fWord = new StringBuilder(); //von erstem Click nach links oder unten gehen 
        if(this.fPos.get(0).x == this.fPos.get(fPos.size()).x){
            while(this.currentBoard[x-1][y] != "0"){
            //laufe x entlang (nach links)
            x--;
            } //setze Wort nach rechts zusammen
            this.firstWord.start = new Position(x, y);
            while (this.currentBoard[x][y] != "0") {
                fWord.append(this.currentBoard[x][y]);
                x++;
            }
            this.firstWord.end = new Position(x - 1, y);
        }
        else if(this.fPos.get(0).y == this.fPos.get(fPos.size()).y){
            while(this.currentBoard[x][y-1] != "0"){
                //laufe x entlang (nach links)
                y--;
                } 
                this.firstWord.start = new Position(x, y);
                while (this.currentBoard[x][y] != "0") {
                    this.fWord.append(this.currentBoard[x][y]);
                    y++;
                }
                this.firstWord.end = new Position(x, y - 1);
            }
        }*/
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
List<Word> getWords(String[][] actualBoard) { 
    //wholeWords von updated Board
    //Wörter müssen alle an firstWord hängen, von Positionen des ersten Wortes alle anhängenden Wörter holen
    List<Word> extractedWords = new ArrayList<>();
    //Position pos = new Position(0, 0);
    for (Position position : this.firstWord.positions) { 

        if (position.checkLeft) { 
           Word wordH = extractWordHorizontal(position.x, position.y, position, actualBoard);

           if (wordH.word.length() > 1){
             extractedWords.add(wordH);
            }

        }
        if (position.checkDown) {
            Word wordV = extractWordVertical(position.x, position.y, position, actualBoard);
            if (wordV.word.length() > 1){
              extractedWords.add(wordV);
            }
        }
    position.clearPositions();
    }
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
            if(pos.x == x && pos.y == y){
                pos.checkLeft = false; //wenn bereits an pos vorbeigelaufen, muss nicht mehr nach links überprüft werden (Reihe schon geprüft)
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
    while (x > 0 && !(board1[x - 1][y].equals("0"))){
        x--;
    }
    Position start = new Position(x, y);

    //nach unten und wort zusammensetzen
    while (x < board1.length && !(board1[x][y].equals("0"))) {
        w.append(board1[x][y]);
        for (Position position : pos.positions) {
            if(pos.x == x && pos.y == y){
                pos.checkDown = false;
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
                if(col + 1 >= this.updatedBoard[row].length || this.updatedBoard[row][col+1].equals("0")){
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
                if(col + 1 >= this.updatedBoard[col].length || this.updatedBoard[row+1][col].equals("0")){
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
    List<Word> validatedWords = new ArrayList<>();

    for (int i = 0; i < allWords2.size(); i++) {
        for (int j = 0; j < words2.size(); j++) {
            System.out.println("i: " + i + "j" + j);
            if(allWords2.get(i).equals(this.firstWord)){
                allWords2.get(i).validated = true; //das erste Wort muss nicht überprüft werden, ob es zusammenhängend ist
            }
            if(allWords2.get(i).word.equals(words2.get(j).word) && allWords2.get(i).validated == false){
                allWords2.get(i).validated = true;
                validatedWords.add(allWords2.get(i)); //füge das Wort in validierte Liste 
                allWords2.remove(i); // in allWords stehen nicht-zusammenhängende Wörter, die die übrig bleiben sind nicht validiert
            }
        }
    }
    for (Word word : words2) {
        if(!validateWord(word.word)){
            allWords2.add(word);
        } 
    }
    this.wrongWords = new ArrayList<>(allWords);
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
        String jsonResponse = response.toString().toLowerCase();
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