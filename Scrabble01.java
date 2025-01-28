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
-> Schwierigkeitseinstellungen
 -> counter zeichnen -> für demo unendlich
 -> Buchstaben an 100 anpassen 

-> Grenzen richtig in getWords !!
-> letzte Zeile TypeErroe NetworkError
-> Buchstaben umdrehen

NOCH ZU TUN:

-> gehaltenen Buchstaben visualisieren 
-> Tiles Bottom rectangle fill an richtige Position

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
import java.util.Set;
/* 
Clerk.markdown(
    Text.fillOut(
"""
# MCNim.java
_Mia Leann Hübner_, _5546723_

## Enum Level
Die Enumeratione ,,Level", wird verwendet, um die Schwierigkeitseinstellungen des Srabble-Spiels vor dem Spielbeginn einstellen zu können.
Je nach Level, sind die Häufigkeiten der im Buchstaben-Säckchen jedes Spielers unterschiedlich verteilt. Außerdem variiert die 
Häufigkeit, mit der die Hilfsfunktion verwendet werden darf.
    
```java
${Level}
```
## Klasse Bag
Die Klasse Bag, besitzt eine Instanz von Level, um die Frequenzen der Buchstaben (wie oben beschrieben) bestimmen zu können.
Standartmäßig wird das Spiel im Konstruktor mit der Schwierigkeit "normal" initialisiert.
Daraufhin wird entsprechend dem Level ,,normal" die Frequenz gesetzt.  

```java
${BagKonstruktor}
```
Außerdem gibt es eine ArrayList bag1 und bag2, welche dann jeweils den zwei Spielenden zugewiesen wird und mit den Buchstaben befüllt werden.
Dazu gibt es eine HashMap charFrequencies, welche in der Methode getFrequencies mit den Buchstaben des Alphabets und deren Häufigkeit gefüllt wird.

```java
${GetFrequencies}
```
Daraufhin werden in der Methode fillBag() die beiden Bags der Reihe nach mit den Buchstaben befüllt.

## Klasse Player01

Jeder Spieler besitzt einen Score, einen bag und ein Methode setBag() um den bag als eine bestimmte ArrayList zu setzen.

```java
${Player}
```      
## Klasse Position

Die Klasse Position hat ein x und y für die jeweilige Position im Feld. Außerdem zwei boolean-Felder, um zu markieren, welcher Spieler 
einen Stein auf die Position gelegt hat.
Zum Vergleich der Position-Objekte, gibt es eine equals-Methode, die auf den x und y Wert prüft.

```java
${equalsPosition}
```

### equals

Da ich später ein HashSet verwende, um doppelte Elemente zu vermeiden, habe ich eine Hashcode-Methode erstellt.

```java
${hashPosition}
```
### compareTo 

In der compareTo-Methode, werden die Position-Objekte aufsteigend entweder nach x oder y sortiert. Dies ist sinnvoll, da ein Wort entweder in einer 
Reihe oder in einer Spalte steht, wodurch entweder die x-Werte alle gleich sind oder die y-Werte, während sich nur jeweils die y-Werte oder x-Werte voneinander unterscheiden.

```java
${compareToPosition}
```

## Klasse Word

Die Klasse Word besitzt ein Feld für die Start- und Endposition eines Wortes, eine ArrayList für alle Positionen eines Wortes, sowie einen String für das Wort selbst.
Eine Methode, um Positionen zur ArrayList positions hinzufügen zu können. Hier wird nach jedem einfügen sortiert. 
Außerdeme eine Methode clearPosition(), um alle Positionen wieder löschen zu können.

```java
${EditPositions}
```

### equals

In der equals-Methode für Word wird auf die Start- und Endposition, dem String und allen Positionen aus der Liste geprüft.

```java
${EqualsWord}
```
Es gibt außerdem wieder eine hash-Methode, welche genauso aufgabaut ist wie in der Klasse Position.

## Klasse Scrabble01

### Scrabble01 Konstruktor

Im Konstruktor wird das Feld gezeichnet und die zweidimensionalen Arrrays für currentBoard, updatedBoard und board initialisiert.
Dabei ist das currentBoard dazu da, die im selben Zug gelegten Steine anzuzeigen, um nach Validierung den Score hochzuzählen.
Da updatedBoard beinhaltet alle bereits gelegten und validierten Wörter sowie die gerade gelegten Buchstaben, um daraus alle Wörter 
extrahieren zu können. Wenn alle extrahierten Wörter validiert werden, bleiben die gerade gelegten Buchstaben auf dem updatedBoard und 
werden auf das board geschrieben. 
Werden die extrahierten Wörter nicht validiert, dann werden die gerade gelegten Buchstaben wieder vom updatedBoard gelöscht und eine Fehlermeldung
ausgegeben.
Das board existiert auch um den festen Stand des Spiels zu dokumentieren, aber hauptsächlich, um die Werte der Felder darzustellen. 
Es gibt "DL" = doppelter Buchstabenwert
        "TL" = dreifacher Buchstabenwert
        "DW" = doppelter Wortwert
        "TW" = dreifacher Wortwert

Mithilfe des boards wird der Score jedes Spielers nach seinem Zug mit der updateScore()-Methode berechnet und auf dem Spielfeld gezeichnet.

```java
${UpdateScore}
```

Weiterhin werden im Konstruktor die "bags" für Player01 und Player02 gefüllt.

### Scrabble01 Click-Event

Nach jedem Click wird eine Reihe von Anweisungen durchgeführt.
Dazu wird den Spielenden zuerst die Möglichkeit gegeben einmalig das Level auszuwählen, mit einem boolean-Feld wird kontrolliert, dass es nur einmal und direkt am 
Anfang ausgewählt werden kann. Nach dem ersten Click, wird "isLevelChosen" auf true gesetzt und falls keine andere Einstellung gewählt wurde, die Schwierigkeit "normal" als default-Wert verwendet.

```java
${IsLevelChosen}
```

Wird eine Einstellung gewählt, wird die Häufigkeit der Buchstaben in bag neu gesetzt, sowie der counter für die Hilfsfunktion auf den vorgegebenen Wert gesetzt.

```java
${ChooseLevel}
```

Nun wird die Hilfsfunktion aufgerufen, für den Fall, dass auf den "help"-Button geklickt wurde.
Die Hilfsfunktion funktioniert so:
    Es wird jede Kombination von gewählten Buchstaben verwendet:
        1111111 - jeder Buchstabe kommt einmal vor: generiere aus ihnen alle Permutationen
        0111111 - der erste Buchstabe kommt nicht vor: " "
        1011111 - der zweite Buchstabe kommt nicht von
        0011111 - nur 5 Buchstaben kommen vor
        1010111
        ...
        0001111 - nur 4 Buchstaben 
        ...
        0000111 - nur 3 Buchstaben 
        ...
        0000011 - nur 2 Buchstaben 
    
    So erhalten wir alle Teilmengen.

    AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH


    Nun wird das Scrabble-Feld nach dem aktuellen Stand neu gezeichnet, um Fehlermeldungen nach dem letzten Legen wieder zu entfernen.
    Es wird geprüft, ob das Spiel zuende ist.
    Die Bedingungen für das Ende des Spiels sind:
        - Es wurde auf Aufgeben (Quit) gedrückt.
        - Der Beutel eines Spielers sowie tilesTop für Spieler1 und tilesBottom für Spieler2 sind leer.

```java
${EndBedingungen}
```
Wenn eine der Bedingungen erfüllt ist, wird das Spiel beendet, indem eine Nachricht ausgegeben wird, welcher Spieler gewonnen hat oder dass das 
Spiel unentschieden geendet hat.

Für den Fall, dass auf den Button ,,change" gedrückt wird, werden die gegebenen Buchstabenplättchen (tilesTop, tilesBottom) ausgetauscht und random mit 
Buchstaben aus dem Säckchen des aktuellen Spielers befüllt. Die Bedingung, um die Steine tauschen zu können, ist dass in dem Zug noch kein Stein auf das Spielfeld gelegt wurde, 
da sonst beliebig getauscht und auf das Feld gelegt werden könnte.

```java
${Refill}
```

Nun werden die Methoden getTile() und updateBoard() aufgerufen, welche zum Aufheben eines Buchstabenplättchens und zum Setzen von diesem ist.
Zum Holen eines Steins von tilesTop/ tilesBottom, wird ,,tile" auf den genommenen char gesetzt, während tileBoard auf leer (' ') gesetzt wird. 

```java
${GetTileTop}
```
Zum Holen eines Steins vom Board, wird "tile" auf leer gesetzt und tileBoard auf den char vom Spielfeld an der geklickten Position.

```java
${LegeSteinTop}
```

```java
${LegeSteinBoard}
```

Im ersten Zug wird das erste Wort zusammengesetzt und in "firstWord" gespeichert. 
Es werden in fPos die ersten Clicks gespeichert, solange sie in einer Reihe oder Spalte sind.
Falls noch mal auf die gleiche Position geklickt wird, entferne diese wieder. Wenn also auf eine Position geklickt wurde, die bereits in fPos gespeichert ist.
Wenn außerhalb des Feldes geklickt wird, beispielsweise auf play, dann wird in den else-Fall gegangen und die Positionen werden sortiert, bevor 
das Wort aus den gespeicherten Positionen zusammengesetzt wird. 
Dann wird geprüft, ob das erste Wort validiert wird. Ist dies der Fall, wird es zu validatedWords hinzugefügt, sonst zu wrongWords.

```java
${GetFirstWord}
```

Wenn auf den Button "play" geklickt wird, wird in den endTurn()-Abschnitt gegangen.
Die Inhalte aus dem currentBoard werden auf das updatedBoard übertragen.
Wenn in validatedWords noch keine Einträge sind, wird anhand der Positionen des ersten Worts die mit dem firstWord zusammenhängenden Wörter  
vom Spielfeld extrahiert (mit getWords()). 
Wenn in validatedWords Einträge sind, dann wird dies anhand der Positionen in validatedWords getan.
Mit allen Positionen vom Board (boardPositions), werden alle Wörter die auf dem Feld stehen, extrahiert.

```java
${EndTurn}
```
### Methode getWrongWords

In der Methode getWrongWords werden die Inhalte dieser beiden Listen verglichen, um die Wörter zu erhalten die nicht an den anderen Wörtern hängen.
Diese sind nicht erlaubt, dementsprechend werden sie zu wrongWords hinzugefügt, sodass eine Fehlerausgabe stattfinden kann.
Die anderen Wörter werden auf ihre Validierung überprüft. Wenn sie validiert werden zu validatedWords hinzugefügt, sonst zu wrongWords.

```java
${GetWrongWords}
```

### Methode getWords
Um zu vermeiden, dass Wörter mehrmals gespeichert werden, wird ein HashSet "uniqueWords" verwendet.
Es werden alle an die Methode übergebenen Positionen durchlaufen.

#### Methoden extractWordHorizontal/ vertical

Auf jede einzelne Position werden die Methoden extractWordsHorizontal und extractWordsVertical aufgerufen, in denen von der 
gegebenen Position aus nach links bzw. nach oben gelaufen wird, soweit bis im Board eine "0" steht.
Dann wird das Wort nach rechts, bzw. unten zusammengesetzt und das Wort mit seinen Positionen erstellt.

```java
${ExtractWord}
```

Wenn die Länge des extrahierten Wortes größer als 1 ist, wird das Wort zu uniqueWords hinzugefügt. 
Da einzelne Buchstaben nicht validiert werden sollen, müssen diese für die Fehlermeldung gespeichert werden.
Wenn ein einzelner Buchstabe extrahiert wurde, wird überprüft, ob die herumliegenden Felder leer sind.
Falls sie leer sind, wird der Buchstabe unique-Words hinzugefügt.
Die Methode gibt eine Liste der extrahierten Wörter zurück.

Nachdem jetzt die wrongWords und validatedWords mithilfe von getWords() gespeichert wurden, wird überprüft, 
ob überhaupt falsche Wörter auf dem Spielfeld liegen (indem geschaut wird, ob wrongWords leer ist).
Ist dies der Fall, kann der aktuelle Zug abgeschlossen werden.
Die Plättchen werden aufgefüllt, der neue Score gezeichnet, das Board mit den gelegten Buchstaben aktualisiert, das
currentBoard geleert und der turn() durchgeführt (durch *(-1) des currentPlayers). Player1 = 1, Player2 = -1.

Im anderen Fall (wrongWords ist nicht empty), wird eine Fehlermeldung auf das Board gezeichnet.
Die Liste wrongWords wird geleert und die gelegten Buchstaben werden aus dem updatedBoard entfernt,
indem die Buchstaben aus dem currentBoard vom updatedBoard gelöscht werden.

## Methode validateWords

Hier findet die Überprüfung der Wörter statt. Es wird auf die Wörterbuch-API vom DWDS zugegriffen.
Zwei inputs für die Methode isValidWord werden bestimmt, einmal das übergebene Wort mit großem Anfangsbuchstaben und 
einmal das Wort klein geschrieben. Wenn isValidWord bei einem der beiden Eingaben true ausgibt, so wird das Wort validiert.

```java
${ValidateWord}
```

## Methode isValidWord

Es wird ein GET-Request an die DWDS-API gesendet, wobei eine URL verwendet werden kann an die hinten der input angefügt wird.
Bei richtiger Eingabe kommt die Antwort im JSON-Format.
Beispiel:
    [{"input":"Puma","url":"https://www.dwds.de/wb/Puma","lemma":"Puma","wortart":"Substantiv"}]
Deshalb wird überprüft ob die Antwort ein lemma enthält und der boolean zurückgegeben.

```java
${IsValid}
```
""",Map.of("Level", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//level"),
        "BagKonstruktor", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//bagKonstruktor"),
        "GetFrequencies", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//getFriequencies"),
        "Player", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//Player"),
        "equalsPosition", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//equalsPosition"),
        "hashPosition", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//hashPosition"),
        "compareToPosition", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//compareToPosition"),
        "EditPositions", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//editPositions"),
        "EqualsWord", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//equalsWord"),
        "UpdateScore", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//updateScore"),
        "IsLevelChosen", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//isLevelChosen"),
        "ChooseLevel", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//chooseLevel"),
        "EndBedingungen", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//endBedingungen"),
        "Refill", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//refill"),
        "GetTileTop", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//getTileTop"),
        "GetTileBoard", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//getTileBoard"),
        "GetFirstWords", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//getFirstWord"),
        "EndTurn", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//endTurn"),
        "ExtractWord", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//extractHorizontal"),
        "GetWrongWords", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//getWrongWords"),
        "ValidateWord", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//validateWord"),
        "IsValid", Text.cutOut("./views/Scrabble01/Scrabble01.java", "//isValid"))));
*/
//level
enum Level{
    EASY, NORMAL, HARD;
}
//level


class Bag{

    Level level;
    List<Character> bag1 = new ArrayList<>();
    List <Character> bag2 = new ArrayList<>();
    int[] frequencies;
    List<Character> availableLetters = new ArrayList<>();
    Map<Character, Integer> charFrequency = new HashMap<>();//wie häufig jeder Buchstabe im Beutel vorkommt
    char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M','N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z','Ä', 'Ö', 'Ü'};

//bagKonstruktor
    Bag(){
        this.level = Level.NORMAL;
        setFrequnecies();
        this.fillBag();
    }
//bagKonstruktor

//setFrequencies
    void setFrequnecies(){
        switch(level){
            case EASY: 
                this.frequencies = new int[]{10, 4, 3, 5, 15, 3, 3, 1, 12, 1, 0, 5, 4, 8, 10, 4, 1, 6, 8, 8, 6, 3, 3, 0, 0, 2, 0, 0, 1};
                break;
            case NORMAL: 
                this.frequencies = new int[]{8, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1, 1, 1, 1};
                break;
            case HARD: 
                this.frequencies = new int[]{5, 4, 5, 4, 7, 3, 5, 3, 6, 3, 4, 3, 3, 5, 5, 4, 2, 4, 4, 5, 4, 3, 2, 2, 2, 2, 2, 2, 2};
                break;
        }
        getFrequencies();
    }
//setFrequencies

//getFrequencies
    void getFrequencies() {
        int count = 0;
        for (int i = 0; i < this.alphabet.length; i++) {
            this.charFrequency.put(this.alphabet[i], this.frequencies[i]);
            this.availableLetters.add(this.alphabet[i]);
            count += this.frequencies[i];
        }
        System.out.println(count);
    }
//getFrequencies

//fillBag
    void fillBag(){
       for (int i = 0; i < this.alphabet.length; i++) {
            while(this.charFrequency.get(this.alphabet[i]) > 0){   
                this.bag1.add(this.alphabet[i]);
                this.bag2.add(this.alphabet[i]);
                this.charFrequency.put(this.alphabet[i], this.charFrequency.get(this.alphabet[i])-1);
            }
       }
    }
}
//filBag

//Player
class Player01 {

    int score = 0;
    List <Character> bag = new ArrayList<>(); //Player1.bag = Bag.bag1;
    
    void setBag(List<Character> sourceBag) {
        this.bag = new ArrayList<>(sourceBag); 
    }
}
//Player

class Position implements Comparable<Position>{

    boolean player1 = false;
    boolean player2 = false;
    int x;
    int y;
   
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

    //equalsPosition
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Position position = (Position) other;
        return this.x == position.x && this.y == position.y;
    }
     //equalsPosition

     //hashPosition
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    //hashPosition

    //compareToPosition
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
    throw new IllegalArgumentException("Positionen sind nicht vergleichbar");
}
 //compareToPosition

    @Override
    public String toString() {
        return "Position{x = " + x + ", y = " + y + "}";
    }

}


class Word implements Comparable<Word>{

    String word;
    Position start;
    Position end;
    boolean validated = false; //benutze ich das??? irgendwo scchon, aber bringt es was für firstWord?
    List<Position> positions = new ArrayList<>();
    
    Word(String word, Position start, Position end){
        this.word = word;
        this.start = start;
        this.end = end;
    }

    //editPositions
    void addPosition(int x, int y){
        this.positions.add(new Position(x, y)); 
        Collections.sort(this.positions);
    }

    void clearPositions(){
        this.positions = new ArrayList<>();
    }
    //editPositions

    int getSizePosition(){
        return this.positions.size(); //benutze ich das ?? ich denke nicht
    }

    //equalsWord
    @Override
    public boolean equals(Object other) { 
        if (other == null) return false; // Null abwehren!
        if (other == this) return true; // Bin ich's selbst?
        if (other.getClass() != getClass()) return false; // Andere Klasse?
        Word that = (Word)other; // Casting
        return Objects.equals(this.word, that.word) &&
           Objects.equals(this.start, that.start) &&
           Objects.equals(this.end, that.end) &&
           Objects.equals(this.positions, that.positions); // Was definiert Gleichheit?
    }
     //equalsWord

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
public int compareTo(Word other) {
    return this.word.compareTo(other.word);//Wörter anach dem Alphabet sortieren
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
        int counterPlayer1;
        int counterPlayer2;

        char[] tilesTop = new char[7];
        char[] tilesBottom = new char[7];
        boolean isLevelChosen = false;
        boolean isFirstWord = true;
        Word firstWord = new Word("", null, null); //zu validatedWords umfunktionieren
        Set<Word> validatedWords = new HashSet<>(); //überprüft auch den Hashwert eines Objekts 
        Position boardPositions = new Position(0, 0);
    
        List<String> specialFields = new ArrayList<>(List.of("DL", "TL", "DW", "TW", "NaN"));

        String[] alphabet = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        };
        
        int[] scores = {
            1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 10, 1, 2,
            1, 1, 3, 8, 1, 1, 1, 1, 4, 10, 10, 10, 10
        };

        Map<String, Integer> letterScores = new HashMap<>();
    
  
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

            fillLetterScores();
            initializeBoards();
            initializeBoardPositions();

            this.level = Level.NORMAL;
        
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

            fillTiles();
            flipTiles();
            this.view.createResponseContext("/scrabble" + ID, response -> {    

                String[] temp = response.split("x");
                int x = Integer.parseInt(temp[0]);
                int y = Integer.parseInt(temp[1]);
                System.out.println("" + x +" + " + y);

                //isLevelChosen
                if(!this.isLevelChosen){
                    chooseLevel(x, y);
                }
                //isLevelChosen

                if(x >= 16 && x <= 18 && y == 3){
                    doHelpFunc();
                }
                drawScrabbleField();
                flipTiles();

                if(isGameOver(x, y)){
                    end();
                }
                refillTiles(x, y);
                getTile(x, y);
                updateBoard(x, y);

                if(this.isFirstWord){ //hole bei ersten Cklicks das erste Wort
                    getFirstWord(x, y); 
                }

                //endTurn
                 if(endTurn(x, y)){
                    overrideUpdatedBoard(); //currentBoard wird hinzugefügt
                    if(this.validatedWords.size() > 0) {
                        List<Position> validatedPositions = new ArrayList<>();
                        for (Word w : this.validatedWords) {
                            validatedPositions.addAll(w.positions); // Alle Positionen aus jedem Wort hinzufügen
                        }
                        this.wrongWords = getWrongWords(getWords(this.updatedBoard, this.boardPositions.positions), getWords(this.updatedBoard, validatedPositions));
                    } else {
                        this.wrongWords = getWrongWords(getWords(this.updatedBoard, this.boardPositions.positions), getWords(this.updatedBoard, this.firstWord.positions)); //alle Wörter durch jede Position des Bords, nur zusammenhängende duch firstWord-Positionen
                }
                //endTurn
                if(this.wrongWords.isEmpty()){ //dann kann weitergespielt werden
                        getNewTiles(x, y);
                        turn();
                    } else {
                        for(Word w: this.wrongWords){
                            throwError(w);
                        }
                        this.wrongWords = new ArrayList<>();
                        deleteCurrentFromUpdated();
                    }
                }
            });
        } 

    void flipTiles(){
        int xStart = 0;
        int xEnd = 0;
        int y = 0;
        if(this.currentPlayer > 0){
            xStart = 1;
            xEnd = 7;
            y = 18;
        }else{
            xStart = 7;
            xEnd = 13;
            y = 2;
        }
        for (int i = xStart; i <= xEnd; i++) {
            Clerk.script(view, "scrabble" + ID + ".removeTile("+ i + ", '" + y + "');");
        }
    }
    void initializeBoardPositions(){
        for (int row = 0; row < this.board.length; row++) {
            for (int col = 0; col < this.board[0].length; col++) {
                this.boardPositions.addPosition(row, col);
            }
        }
    }
    void initializeBoards(){
          //initialisiere current und updated
          for (int i = 0; i < this.currentBoard.length; i++) {
            for (int j = 0; j < this.currentBoard[i].length; j++) {
                this.currentBoard[i][j] = "0";
                this.updatedBoard[i][j] = "0";
            }
        }
    }

    void drawScores(){
        List<Word> scoredWords = getWords(this.currentBoard, this.boardPositions.positions);
                        for(Word scWord: scoredWords){
                            if(this.currentPlayer > 0){
                                player1.score += updateScore(scWord); //Zeichne score mit neuem Wert
                                Clerk.script(view, "scrabble" + ID + ".drawScoreTop(" + player1.score + ");");
                            } else if(this.currentPlayer < 0){
                                player2.score += updateScore(scWord);
                                Clerk.script(view, "scrabble" + ID + ".drawScoreBottom(" + player2.score + ");");
                            } 
                        }
        }

    void end(){
        String message = "";
        if(this.player1.score > this.player2.score){
            message = "Spieler 1 gewinnt";
        } else if(this.player1.score < this.player2.score){
            message = "Spieler 2 gewinnt";
        } else{
            message = "unentschieden";
        }
        Clerk.script(view, "scrabble" + ID + ".win(\"" + message + "\");");
    }

    void fillLetterScores(){
         //Befülle map für Buchstabenwerte 
         for (int i = 0; i < alphabet.length; i++) {
            letterScores.put(alphabet[i], scores[i]);
            }
    }
    
    void fillTiles(){
        for (int i = 0; i < 7; i++) {
            Random random = new Random();
            int rand = random.nextInt(player1.bag.size());
            char letter1 = player1.bag.get(rand);
            this.tilesTop[i] = letter1; //fülle tilesTop auf in ausgelagerter Methode, if tilesTop[i] leer?
            player1.bag.remove(rand);

            rand = random.nextInt(player2.bag.size());
            char letter2 = player2.bag.get(rand);
            this.tilesBottom[i] = letter2;
            player2.bag.remove(rand);
            Clerk.script(view, "scrabble" + ID + ".textTilesBottom(" + i + ", '" + letter2 + "');");
            Clerk.script(view, "scrabble" + ID + ".textTilesTop(" + i + ", '" + letter1 + "');");
        }
    }

    //refill
    void refillTiles(int x, int y){ //tausche alle tiles aus, aber mit random elementen aus dem Bag, damit nicht immer dieselben Kombinationen entstehen
        Random random = new Random();
        boolean refill = true;
        if(x >= 16 && x <= 18 && y == 13){
            if(this.currentPlayer > 0){ 
                for(char tile : this.tilesTop){
                    if(tile == '0'){ //man darf nur vor dem Zug einmal alle tiles tauschen
                        refill = false;
                        break;
                    }
                } 
                if(refill){
                    for (int i = 0; i < this.tilesTop.length; i++) {
                        player1.bag.add(this.tilesTop[i]); //befüllen von tiesTpo
                    //char randElement = getRandomElement(player1.bag);
                        int rand = random.nextInt(player1.bag.size());
                        this.tilesTop[i] = player1.bag.get(rand);
                        player1.bag.remove(rand);
                        }
                        drawTiles(); 
                    } 
                } else if(this.currentPlayer < 0){
                    for(char tile : this.tilesBottom){
                        if(tile == '0'){
                            refill = false;
                            break;
                        }
                    }
                    if(refill){
                        for (int i = 0; i < this.tilesBottom.length; i++) {
                        player2.bag.add(this.tilesBottom[i]);
                        int rand = random.nextInt(player2.bag.size());
                        this.tilesBottom[i] = player2.bag.get(rand);
                        player2.bag.remove(rand);
                        }
                        drawTiles(); 
                    }
                }
            }
        }
        //refill

    void drawScrabbleField(){
        //ArrayList<Word> removeW = new ArrayList<>(); 
        
            //y -= 3;
            for(int i = 0; i < this.boardPositions.positions.size(); i++){
                int x = boardPositions.positions.get(i).x;
                int y = boardPositions.positions.get(i).y;
                    if(this.boardPositions.positions.get(i).player1){
                        if(!(this.updatedBoard[x][y].equals("0"))){
                            Clerk.script(view, "scrabble" + ID + ".setTile1(" +  x + ", '" + (y + 3) + "', '" + this.updatedBoard[x][y] + "');");
                        }
                        if(!(this.currentBoard[x][y].equals("0"))){
                            Clerk.script(view, "scrabble" + ID + ".setTile1(" +  x + ", '" + (y + 3) + "', '" + this.currentBoard[x][y] + "');");
                            }
                    } else if(this.boardPositions.positions.get(i).player2){
                        if(!(this.updatedBoard[x][y].equals("0"))){
                            System.out.println("updatedBoardTile: " + this.updatedBoard[x][y]);
                            Clerk.script(view, "scrabble" + ID + ".setTile2(" + x  + ", '" + (y+3) + "', '" + this.updatedBoard[x][y] + "');");
                        }
                        if(!(this.currentBoard[x][y].equals("0"))){
                            Clerk.script(view, "scrabble" + ID + ".setTile2(" +  x + ", '" + (y + 3) + "', '" + this.currentBoard[x][y] + "');");
                            }
                        }
                    }
            this.wrongWords = new ArrayList<>();
   }

   boolean isBoard(int x, int y){
        return x <= 14 && x >= 0 && y <= 17 && y >= 3;
   }

void doHelpFunc(){
    List<String> toValidate = new ArrayList<>();
    if(this.currentPlayer > 0){
        if(this.counterPlayer1 > 0){
        toValidate = new ArrayList<>(help(permutation(this.tilesTop)));
        for(int i = 0; i < toValidate.size(); i++){
            if(!validateWord(toValidate.get(i))){
                toValidate.remove(i);
            }
        }
        System.out.println(bestScore(toValidate));
        String res = bestScore(toValidate);
        Clerk.script(view, "scrabble" + ID + ".helpMessage1(\"" + res + "\");");
        this.counterPlayer1--;
        //HIER COUNTER ZEICHNEN
        }
    } else if(this.currentPlayer < 0){
        if(this.counterPlayer2 > 0){
        toValidate = new ArrayList<>(help(permutation(this.tilesBottom)));
        for(int i = 0; i < toValidate.size(); i++){
            if(!validateWord(toValidate.get(i))){
                toValidate.remove(i);
            }
        }
        System.out.println(bestScore(toValidate));
        String res = bestScore(toValidate);
        Clerk.script(view, "scrabble" + ID + ".helpMessage2(\"" + res + "\");");
        this.counterPlayer2--;
        //HIER COUNTER ZEICHNEN 
        }
    }
}

List<String> help(List<String> targetWords){
        String filePath = "views/Scrabble01/wordlist-german.txt";
        // Wortliste in ein Set laden
        Set<String> wordSet = loadWordSet(filePath);

        // Wörter prüfen
        List<String> matchingWords = new ArrayList<>();
        for (String word : targetWords) {
            if (wordSet.contains(word.toUpperCase())) { // Effiziente Prüfung mit HashSet
                matchingWords.add(word);
            }
        }
        return matchingWords;
    }

    // Methode zum Einlesen der Wortliste in ein HashSet
    Set<String> loadWordSet(String filePath) {
        int counter = 0;
        Set<String> wordSet = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordSet.add(line.trim().toUpperCase()); // Jedes Wort ins Set einfügen
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordSet;
    }


/*wenn ich alle Permutationen habe,jeden Buchstaben einmal entfernen und Funktion mit übrig gebliebenen Buchstaben aufrufen 
 aufrufen, bis letters empty */
List<String> permutation(char[] letters) {
    List<String> results = new ArrayList<>();

    // Generiere alle Kombinationen der Buchstaben
    List<List<Character>> combinations = generateCombinations(letters);

    // Berechne Permutationen für jede Kombination
    for (List<Character> combination : combinations) {
        char[] combinationArray = listToCharArray(combination);
        permute(combinationArray, 0, results);
    }

    return results;
}

//Kombinationen
List<List<Character>> generateCombinations(char[] letters) {
    List<List<Character>> combinations = new ArrayList<>();
    int n = letters.length;

    for (int mask = 2; mask < (1 << n); mask++) { //die 1 wird n nach links geshiftet, also für 2: 100 = 4, 1000 = 8, 10000 = 16. Mit 7 Buchstaben läuft die Schleife also bis 2^7 = 10000000 = 128
        List<Character> combination = new ArrayList<>(); //nun erstellen wir eine einzelne combination
        for (int i = 0; i < n; i++) { //indem wir n-mal durch die Schleife laufen 
            if ((mask & (1 << i)) != 0) {  //und 
                combination.add(letters[i]);
            }
        }
        combinations.add(combination);
    }
    return combinations;
}
//Kombinationen
/*Der Methode generateCombinations() wird das char-Array des Spielers, der aktuell an der Reihe ist übergeben.
 * Also entweder tilesTop[] oder tilesBottom[].
 * Nun soll aus allen aus den Charactern jede Position generiert werden und in der ArrayList "combinations" gespeichert werden.
 * Es gibt 2^n Kombinationen eines Strings. Wir setzen also n auf die Länge des übergebenen Arrays.
 * In der Schleife beginnen wir mit zwei, um die leere Kombinatinon sowie die einzelnen Buchstaben zu vermeiden.
 * Wir laufen bis 2^n - 1, was durch 1 << n dargstellt ist.
 */
// Rekursive Methode zur Berechnung der Permutationen
void permute(char[] chars, int start, List<String> results) {
    if (start == chars.length - 1) {
        results.add(new String(chars)); // Aktuelle Permutation speichern
        return;
    }

    for (int i = start; i < chars.length; i++) {
        swap(chars, start, i);           // Tausche das aktuelle Zeichen nach vorne
        permute(chars, start + 1, results); // Rekursion für den Rest
        swap(chars, start, i);           // Rücktauschen
    }
}

// Hilfsmethode, um zwei Zeichen im Array zu tauschen
void swap(char[] chars, int i, int j) {
    char temp = chars[i];
    chars[i] = chars[j];
    chars[j] = temp;
}

// Hilfsmethode, um eine List<Character> in ein char[] umzuwandeln
char[] listToCharArray(List<Character> list) {
    char[] array = new char[list.size()];
    for (int i = 0; i < list.size(); i++) {
        array[i] = list.get(i);
    }
    return array;
}


    void getNewTiles(int x, int y) {

        Random random = new Random();
        int rand = 0;
        if (this.currentPlayer > 0) {
            for (int j = 0; j < this.tilesTop.length; j++) {
                rand = random.nextInt(this.player1.bag.size());
                if (this.tilesTop[j] == '0') { 
                    this.tilesTop[j] = player1.bag.get(rand);
                    player1.bag.remove(rand);
                }
            }
            drawTiles();
        } else {
            for (int j = 0; j < this.tilesBottom.length; j++) {
                rand = random.nextInt(this.player2.bag.size());
                if (this.tilesBottom[j] == '0') {
                    this.tilesBottom[j] = player2.bag.get(rand);
                    player2.bag.remove(rand);
                }
            }
            drawTiles();
            }
        }

    String bestScore(List<String> list){
        int score = 0;
        int currScore = 0;
        String bestWord = "";

        for(String str : list){
            for(int i = 0; i < str.length(); i++){
                currScore += this.letterScores.get("" + str.charAt(i));
            }
            if(currScore > score){
                score = currScore;
                currScore = 0;
                bestWord = str;
            }
        }
    if(score == 0){
        return "kein Wort gefunden";
    } else {
        return bestWord;
    }
}


    char getRandomElement(List<Character> letterList){
        Random random = new Random();
        int rand = random.nextInt(letterList.size());
        return letterList.get(rand);
    }

void drawTiles(){
    if(this.currentPlayer > 0){
        Clerk.script(view, "scrabble" + ID + ".drawTilesTop();");
        for (int j = 0; j < this.tilesTop.length; j++) {
            char letter = this.tilesTop[j];
            Clerk.script(view, "scrabble" + ID + ".textTilesTop(" + j + ", '" + letter + "');");
        }
    } else if(this.currentPlayer < 0){
        Clerk.script(view, "scrabble" + ID + ".drawTilesBottom();");
    for (int j = 0; j < this.tilesBottom.length; j++) {
        char letter = this.tilesBottom[j];
        Clerk.script(view, "scrabble" + ID + ".textTilesBottom(" + j + ", '" + letter + "');");
        }
    }
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

//getFirstWord
void getFirstWord(int x, int y){
    if(this.isFirstWord && x >= 0 && x <= 14 && y >= 3 && y <= 17){
                    
    int lastPos = 0;
    y -= 3;
   
    if(this.firstWord.positions.size() > 1){
        lastPos = this.firstWord.positions.size()-2;
        }
        if(!(this.firstWord.positions.size() > 0)) {
            this.firstWord.addPosition(x, y);
        } else {
            if(((x) == (this.firstWord.positions.get(lastPos).x) && (x) == (this.firstWord.positions.get(0).x)) || ((y) == (this.firstWord.positions.get(lastPos).y) && (y) == (this.firstWord.positions.get(0).y))){ //Wenn startPosition und vorletzte Position von x oder y gleich, dann ist es immernoch das erste Wort
                if(this.firstWord.positions.contains(new Position(x, y))){
                    this.firstWord.positions.remove(new Position(x, y));
                } else if(!(this.firstWord.positions.contains(new Position(x, y)))){
                    this.firstWord.addPosition(x, y);
                }        
            } else { 
                this.isFirstWord = false;
                StringBuilder fWord = new StringBuilder(); 
                Collections.sort(this.firstWord.positions); 
                        
                for(Position pos: this.firstWord.positions){
                    x = pos.x;
                    y = pos.y;
                    fWord.append(this.updatedBoard[x][y]);
                }
                this.firstWord.word = fWord.toString(); 
                this.firstWord.start = this.firstWord.positions.get(0);
                this.firstWord.end = this.firstWord.positions.get(this.firstWord.positions.size()-1);

                if(validateWord(this.firstWord.word)){
                    this.validatedWords.add(this.firstWord);
                } else {
                    this.wrongWords.add(this.firstWord);
                    this.isFirstWord = true;
                }
            }
        }
    }
}
//getFirstWord

//chooseLevel
void chooseLevel(int x, int y){
    
    if(x >= 16 && x <= 18 && y == 5){
        this.level = Level.EASY;
        bag.level = Level.EASY;
        bag.level = Level.EASY;
    } else if(x >= 16 && x <= 18 && y == 6){
        this.level = Level.NORMAL;
        bag.level = Level.NORMAL;
        bag.level = Level.NORMAL;
    } else if(x >= 16 && x <= 18 && y == 7){
        this.level = Level.HARD;
        bag.level = Level.HARD;
        bag.level = Level.HARD;
    } 
    bag.setFrequnecies();
    this.setHelpFrequency();
    this.isLevelChosen = true;
}
//chooseLevel

void setHelpFrequency(){
    switch(level){
        case EASY:
            this.counterPlayer1 = 25;
            this.counterPlayer2 = 25;
            break;
        case NORMAL: 
            this.counterPlayer1 = 15;
            this.counterPlayer2 = 15;
            break;
        case HARD:
            this.counterPlayer1 = 5;
            this.counterPlayer2 = 5;
    }
}

void throwError(Word w){
    Clerk.script(view, "scrabble" + ID + ".error(" + w.start.x + ", " + w.start.y + ", " + w.end.x + ", " + w.end.y + ");");
}

//endBedingungen
boolean isGameOver(int x, int y){
    return ((this.player1.bag.size() == 0 && areTilesTopEmpty()) || (this.player2.bag.size() == 0 && areTilesBottomEmpty()) || (x >= 16 && x <= 18 && y == 19)); 
}
//endBedingungen
boolean areTilesTopEmpty(){
    for(char tile : this.tilesTop){
        if(tile != '0'){
            return false;
        }
    }
    return true;
}

boolean areTilesBottomEmpty(){
    for(char tile : this.tilesBottom){
        if(tile != '0'){
            return false;
        }
    }
    return true;
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
    drawScores();
    this.currentPlayer *= -1;
    this.emptyTiles();
    drawTiles();
    flipTiles();
    overrideBoard();
    clearCurrentBoard();  
}

void emptyTiles(){
    this.tile = ' ';
    this.tileBoard  = ' ';
}

void updateBoard(int x, int y) {
    // Spieler 1 legt Stein auf das Board
    //legeSteinBoard
    if (x >= 0 && x <= 14 && y >= 3 && y <= 17 && this.currentPlayer > 0) {
        if (setzeTileBoard(x, y)) {
            Clerk.script(view, "scrabble" + ID + ".setTile1(" + x + ", '" + y + "', '" + this.tile + "');");
            this.currentBoard[x][y-3] = "" + this.tile;
            
            for(Position pos : this.boardPositions.positions){ //markiere, welcher Spieler auf welches Feld gelegt hat
                if(pos.x == x && pos.y == y-3){
                    pos.player1 = true;
                }
            }
            this.emptyTiles();
        }
    }
     //legeSteinBoard

    // Spieler 2 legt Stein auf das Board
    else if (x >= 0 && x <= 14 && y >= 3 && y <= 17 && this.currentPlayer < 0) {
        if (setzeTileBoard(x, y)) {
            Clerk.script(view, "scrabble" + ID + ".setTile2(" + x + ", '" + y + "', '" + this.tile + "');");
            this.currentBoard[x][y-3] = "" + this.tile;

            for(Position pos : this.boardPositions.positions){ //markiere, welcher Spieler auf welches Feld gelegt hat
                if(pos.x == x && pos.y == y-3){
                    pos.player2 = true;
                }
            }

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
//getTileTop
char getTile(int x, int y) {
    // Hole Stein für ersten Klick (von TopTiles)
    if (x >= 7 && x <= 13 && y == 2 && this.currentPlayer > 0 && holeTileTop(x, y)) {  
        this.tile = this.tilesTop[x-7];  // Hole Stein auf den geklickt wurde
        this.tileBoard = ' ';
        this.tilesTop[x-7] = '0'; // Entferne Stein von TopTiles   
        Clerk.script(view, "scrabble" + ID + ".removeTile("+ x + ", '" + y + "');");
    } 
//getTileTop
    else if (x >= 1 && x <= 7 && y == 18 && this.currentPlayer < 0 && holeTileBottom(x, y)) { 
        this.tile = this.tilesBottom[x-1];
        this.tileBoard = ' ';
        this.tilesBottom[x-1] = '0'; // Entferne Stein von BottomTiles
        Clerk.script(view, "scrabble" + ID + ".removeTile("+ x + ", '" + y + "');");
    } 
//getTileBoard
    // Hole Stein vom Board
    else if (x >= 0 && x <= 14 && y >= 3 && y <= 17 && holeTileBoard(x, y)) {
        this.tileBoard = this.currentBoard[x][y-3].charAt(0); // Hole Stein vom Board
        this.tile = ' ';
        this.currentBoard[x][y-3] = "0"; // Entferne Stein vom Board

        for(Position pos : this.boardPositions.positions){ //nehme Markierung vom Board
            if(pos.x == x && pos.y == y-3){
                pos.player1 = false;
                pos.player2 = false;
            }
        }
//getTileBoard
        Clerk.script(view, "scrabble" + ID + ".setColor("+ (y-3) + ", '" + x + "');"); 
        Clerk.script(view, "scrabble" + ID + ".setText("+ (y-3) + ", '" + x + "');");
    }
    return this.tile;
}

//updateScore

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

//updateScore

List<Word> getWords(String[][] actualBoard, List<Position> positions) { 

    List<Word> extractedWords = new ArrayList<>();
    Set<Word> uniqueWords = new HashSet<>();
   
    for (Position position : positions) { 
           Word wordH = extractWordHorizontal(position.x, position.y, positions, actualBoard);
           if (wordH.word.length() > 1){
             uniqueWords.add(wordH);
            } 
            Word wordV = extractWordVertical(position.x, position.y, positions, actualBoard);
            if (wordV.word.length() > 1){
              uniqueWords.add(wordV);
             } else if(wordV.word.length() == 1){ //es wird geprüft ob der Buchstabe alleine steht (keine Buchstaben um sich herum hat) und für den Fall der Wortliste hinzugefügt, damit ein Fehler ausgegeben wird
                int x = wordV.positions.get(0).x;
                int y = wordV.positions.get(0).y;
                if(x == 0){ //am linken Rand 
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x][y+1].equals("0") && actualBoard[x][y-1].equals("0")){ 
                        uniqueWords.add(wordV);
                    }
                } else if(y == 17){ //letzte Zeile
                    if(actualBoard[x-1][y].equals("0") && actualBoard[x][y+1].equals("0") && actualBoard[x][y-1].equals("0")){
                        uniqueWords.add(wordV);
                    }
                } else if(y == 1){ //erste Zeile
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x-1][y].equals("0") && actualBoard[x][y+1].equals("0")){
                        uniqueWords.add(wordV);
                    }
                } else if(x == 14){ //am rechten Rand 
                    if(actualBoard[x][y+1].equals("0") && actualBoard[x-1][y].equals("0") && actualBoard[x][y-1].equals("0")){
                        uniqueWords.add(wordV);
                    }
                } else if(x == 0 && y == 0){ //linke obere Ecke
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x][y+1].equals("0")){
                        uniqueWords.add(wordV);   
                    }
                } else if(x == 14 && y == 0){ //rechte obere Ecke
                    if(actualBoard[x-1][y].equals("0") && actualBoard[x][y+1].equals("0")){
                        uniqueWords.add(wordV);
                    }
                } else if(x == 0 && y == 17){ //linke untere Ecke
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x][y-1].equals("0")){
                        uniqueWords.add(wordV);
                    }
                } else if(x == 14 && y == 17){ //rechte obere Ecke
                    if(actualBoard[x-1][y].equals("0") && actualBoard[x][y-1].equals("0")){
                        uniqueWords.add(wordV);
                    }
                } else {
                    if(actualBoard[x+1][y].equals("0") && actualBoard[x-1][y].equals("0") && actualBoard[x][y+1].equals("0") && actualBoard[x][y-1].equals("0")){ //sind Felder um den Buchstaben frei?
                        uniqueWords.add(wordV);    
                }
            }    
        }
    position.clearPositions();
    }
    for(Word w: uniqueWords){
        extractedWords.add(w);
    }
    return extractedWords;
}

//extractHorizontal
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
        y++;
    }
    Position end = new Position(x, y-1);
    Word word2 = new Word(w.toString(), start, end);
    word2.positions = positions;
    return word2;
}
//extractHorizontal

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
        x++;
    }
    Position end = new Position(x-1, y);
    Word word2 = new Word(w.toString(), start, end);
    word2.positions = positions;
    return word2;
}
 //getWrongWords
List<Word> getWrongWords(List<Word> allWords, List<Word> words){
    List<Word> allWords2 = new ArrayList<>(allWords);
    List<Word> words2 = new ArrayList<>(words);
    List<Word> vWords = new ArrayList<>();

    Collections.sort(allWords2);
    Collections.sort(words2);
    
    for (int i = 0; i < allWords2.size(); i++) {
        for (int j = 0; j < words2.size(); j++) {
            if(allWords2.get(i).word.equals(words2.get(j).word)){
                vWords.add(allWords2.get(i)); //füge das Wort in validierte Liste 
                allWords2.remove(i); // in allWords stehen nicht-zusammenhängende Wörter, die die übrig bleiben sind nicht validiert
            }
        }
    }
    if(validateWord(this.firstWord.word)){ //wenn firstWord richtig ist, füge es zu validatedWords hinzu und entferne es aus falschen Wörtern 
        validatedWords.add(this.firstWord);
        if(allWords2.contains(this.firstWord)){
            allWords2.remove(allWords2.indexOf(this.firstWord));
        }
    } 
    for (Word word : words2) {
        if(!validateWord(word.word)){
            allWords2.add(word);
        } 
    }
    this.wrongWords = allWords2;
    if(this.wrongWords.isEmpty()){ //validierte Wörter um in nächster Runde zusammenhängende Wörter zu prüfen
        for(Word w : vWords){
            this.validatedWords.add(w); 
        }
    }
    return this.wrongWords;
}
//getWrongWords

//validateWord
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
//validateWord

//isValid
private boolean isValidWord(String word) {
    try {
        URI uri = URI.create("https://www.dwds.de/api/wb/snippet/?q=" + word);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // JSON als String durchsuchen
        String jsonResponse = response.toString();
        System.out.println(jsonResponse);
        return jsonResponse.contains("\"lemma\":");
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
//isValid

    Scrabble01(LiveView view) { this(view, 600, 600, new Player01(), new Player01(), new Bag()); }
    Scrabble01(int width, int height) { this(Clerk.view(), width, height, new Player01(), new Player01(), new Bag()); }
    Scrabble01() { this(Clerk.view(), 600, 600, new Player01(), new Player01(), new Bag());}

    @Override
    public String toString() {
        StringBuilder uboardString = new StringBuilder();
    
        // Construct updated board string
        for (int i = 0; i < this.updatedBoard.length; i++) {
            for (int j = 0; j < this.updatedBoard[i].length; j++) {
                uboardString.append("0".equals(this.updatedBoard[i][j]) ? "." : this.updatedBoard[i][j]);
                if (j < this.updatedBoard[i].length - 1) {
                    uboardString.append(" "); // Separate cells with a space
                }
            }
            uboardString.append("\n"); // Newline after each row
        }
    
        StringBuilder cboardString = new StringBuilder();
    
        // Construct current board string
        for (int i = 0; i < this.currentBoard.length; i++) {
            for (int j = 0; j < this.currentBoard[i].length; j++) {
                cboardString.append("0".equals(this.currentBoard[i][j]) ? "." : this.currentBoard[i][j]);
                if (j < this.currentBoard[i].length - 1) {
                    cboardString.append(" "); // Separate cells with a space
                }
            }
            cboardString.append("\n"); // Newline after each row
        }
    
        StringBuilder boardString = new StringBuilder();
    
        // Construct the main board string
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                boardString.append(this.specialFields.contains(this.board[i][j]) ? "." : this.board[i][j]);
                if (j < this.board[i].length - 1) {
                    boardString.append(" "); // Separate cells with a space
                }
            }
            boardString.append("\n"); // Newline after each row
        }
    
        // Return a combined or specific string
        return "Updated Board:\n" + uboardString +
               "\nCurrent Board:\n" + cboardString +
               "\nOriginal Board:\n" + boardString;
    }
} 