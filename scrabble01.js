class Scrabble01{

    constructor(boardCanvas, endpoint) {
    
        this.isOver = false;

        this.boardCanvas = boardCanvas;
        this.ctxBoard = boardCanvas.getContext("2d");
        
        this.marginX = this.boardCanvas.width / 20;
        this.marginY = this.boardCanvas.height / 20;
        
        this.gridSize = 15;
        
        //Für dasBoard
        this.fieldWidth = 400;
        this.fieldHeight = 400;

        this.boardWidth = this.fieldWidth / 15;
        this.boardHeight = this.fieldHeight / 15;

        this.squareSize = Math.min(this.fieldWidth, this.fieldHeight) / this.gridSize; // Größe der einzelnen Felder

        this.drawScrabbleField();
        this.drawTilesTop();
        this.drawTilesBottom();
        this.drawSideBar();
        //this.error(0, 0, 0, 3);
        
        this.boardCanvas.addEventListener("click", (event) => {
            
            let indexY = Math.floor(event.offsetY / this.boardHeight);
            const indexX = Math.floor(event.offsetX / this.boardWidth);
           if (indexY > 17) {
                indexY = Math.floor((event.offsetY - 16) / this.boardHeight);
            } else if (indexY >= 3) {
                indexY = Math.floor((event.offsetY - 8) / this.boardHeight);
            }
            const index = indexX.toString() + "x" + indexY.toString();
            console.log(`OffsetY: ${event.offsetY}, Calculated indexY: ${indexY}`);
            console.log(`Clicked at X: ${indexX} Y: ${indexY} => ${index}`);
            fetch(endpoint, {method: "post", body: index}).catch(console.log);
        });
    }

        drawScrabbleField() {
            for (let row = 0; row < this.gridSize; row++) {
                for (let col = 0; col < this.gridSize; col++) {
                    this.ctxBoard.beginPath();
                    console.log("drawrow: " + row + ", " + "col: " + col);
                    this.setColor(row, col);
                    this.setText(row, col);      
                }
            }
        }

        drawTilesTop(){
            for(let i = 7; i < 14; i++){
                this.ctxBoard.beginPath();
                this.ctxBoard.fillStyle = "rgb(255, 192, 203)";
                this.ctxBoard.fillRect(i * this.squareSize, (2*this.squareSize), this.squareSize, this.squareSize);
                this.ctxBoard.strokeStyle = "black"; 
                this.ctxBoard.lineWidth = 2; 
                this.ctxBoard.strokeRect(i * this.squareSize, (2*this.squareSize), this.squareSize, this.squareSize);
            }
        }

        textTilesTop(index, letter){
            const x = (index+7) * this.squareSize; 
            const y = 2*this.squareSize; 
        
            this.ctxBoard.fillStyle = "black"; 
            this.ctxBoard.font = "bold 14px verdana, sans-serif";
            const textX = x + this.squareSize/3; 
            const textY = y + this.squareSize - 8;
            this.ctxBoard.fillText(letter, textX, textY);
        }

        drawScoreTop(score){
           
            const x = this.squareSize;
            const y = 0;
    
            const textX = this.squareSize*2; 
            const textY = y + this.squareSize-8; 

            this.ctxBoard.font = "bold 14px verdana, sans-serif";
            this.ctxBoard.fillStyle = "rgb(255, 192, 203)";
            this.ctxBoard.fillRect(x, y, this.squareSize* 5, (this.squareSize* 3));
            this.ctxBoard.strokeStyle = "black";
            this.ctxBoard.strokeRect(x, y, this.squareSize* 5, (this.squareSize* 3));
            this.ctxBoard.moveTo(x, this.squareSize);
            this.ctxBoard.lineTo(x+this.squareSize*5, this.squareSize);
            this.ctxBoard.stroke();
            this.ctxBoard.fillStyle = "black";
            this.ctxBoard.fillText("SCORE", textX, textY);
            this.ctxBoard.font = "30px verdana, sans-serif";
            this.ctxBoard.fillText(score, textX, textY + 2*this.squareSize-8);
            this.ctxBoard.font = "bold 14px verdana, sans-serif";
            //x, y, width, height
        }

        drawTilesBottom(){
            for(let i = 1; i < 8; i++){
                this.ctxBoard.beginPath();
                this.ctxBoard.fillStyle = "rgb(191, 245, 239)";
                this.ctxBoard.fillRect(i * this.squareSize, (18 * this.squareSize) + 16, this.squareSize, this.squareSize);
                this.ctxBoard.strokeStyle = "black"; 
                this.ctxBoard.lineWidth = 2; 
                this.ctxBoard.strokeRect(i * this.squareSize, (18 * this.squareSize) + 16, this.squareSize, this.squareSize);
            }
        }

        textTilesBottom(index, letter){
            const x = (index+1) * this.squareSize; 
            const y = (18 * this.squareSize) + 16; 
    
            this.ctxBoard.fillStyle = "black"; 
            this.ctxBoard.font = "bold 14px verdana, sans-serif";
            const textX = x + this.squareSize / 3; 
            const textY = y + this.squareSize - 8; 
            this.ctxBoard.fillText(letter, textX, textY);
            }

        drawScoreBottom(score){
            const x = (this.squareSize*9);
            const y = (18 * this.squareSize) + 16;
    
            const textX = x + this.squareSize; 
            const textY = y + this.squareSize-8; 
            
            this.ctxBoard.font = "bold 14px verdana, sans-serif";
            this.ctxBoard.fillStyle = "rgb(191, 245, 239)";
            this.ctxBoard.fillRect(x, y, this.squareSize* 5, (this.squareSize* 3));
            this.ctxBoard.strokeStyle = "black";
            this.ctxBoard.strokeRect(x, y, this.squareSize* 5, (this.squareSize* 3));
            this.ctxBoard.moveTo(x, y + this.squareSize);
            this.ctxBoard.lineTo(x+this.squareSize*5, y + this.squareSize);
            this.ctxBoard.stroke();
            this.ctxBoard.fillStyle = "black";
            this.ctxBoard.fillText("SCORE", textX, textY);
            this.ctxBoard.font = "30px verdana, sans-serif";
            this.ctxBoard.fillText(score, textX, textY + 2*this.squareSize-8);
            this.ctxBoard.font = "bold 14px verdana, sans-serif";
            //x, y, width, height
        }

        setColor(row, col) {
            const tripleWord = new Set(["0,0", "14,0", "0,14", "14,14", "0,7", "7,0", "14,7", "7,14"]);
            const doubleLetter = new Set(["3,0", "0,3", "0,11", "2,6", "6,2", "7,3", "8,2", "2,8", "3,7", "6,6", "8,6", "6,8", "8,8", "11,0", "11,7", "12,6", "6,12", "7,11", "8,12", "12,8", "3,14", "14,3", "14,11", "11,14"]);
            const tripleLetter = new Set(["1,5", "5,1", "1,9", "9,1", "8,8", "5,5", "5,9", "9,5", "9,9", "13,5", "13,9", "9,13", "5,13"]);
            const doubleWord = new Set(["1,1", "2,2", "3,3", "4,4", "7,7", "13,1", "12,2", "11,3", "10,4", "4,10", "3,11", "2,12", "1,13", "10,10", "11,11", "12,12", "13,13"]);
            
            console.log("row: " + row + "," + "col: " + col);
            const key = `${row},${col}`;
            const x = col * this.squareSize;
            const y = ((row+3) * this.squareSize)+8;
            console.log("x: " + x + "," + "y: " + y);
            // Wähle die Farbe basierend auf dem Feldtyp
            if (tripleWord.has(key)) {
                this.ctxBoard.fillStyle = "rgb(205, 38, 38)"; //rot: tw
            } else if (doubleLetter.has(key)) {
                this.ctxBoard.fillStyle = "rgb(173, 216, 230)"; //hellblau: dl
            } else if (tripleLetter.has(key)) {
                this.ctxBoard.fillStyle = "rgb(39, 64, 139)"; //dunkelblau: tl
            } else if (doubleWord.has(key)) {
                this.ctxBoard.fillStyle = "rgb(238, 174, 238)"; //rosa: dw
            } else {
                this.ctxBoard.fillStyle = "rgb(193, 255, 193)"; //sonst grün
            }
            //fürs zeichnen muss y+3
            this.ctxBoard.fillRect(x, y, this.squareSize, this.squareSize);
            this.ctxBoard.strokeStyle = "black"; 
            this.ctxBoard.lineWidth = 1; 
            this.ctxBoard.strokeRect(x, y, this.squareSize, this.squareSize);
        }
            setText(row, col){
                const tripleWord = new Set(["0,0", "14,0", "0,14", "14,14", "0,7", "7,0", "14,7", "7,14"]);
                const doubleLetter = new Set(["3,0", "0,3", "0,11", "2,6", "6,2", "7,3", "8,2", "2,8", "3,7", "6,6", "8,6", "6,8", "8,8", "11,0", "11,7", "12,6", "6,12", "7,11", "8,12", "12,8", "3,14", "14,3", "14,11", "11,14"]);
                const tripleLetter = new Set(["1,5", "5,1", "1,9", "9,1", "8,8", "5,5", "5,9", "9,5", "9,9", "13,5", "13,9", "9,13", "5,13"]);
                const doubleWord = new Set(["1,1", "2,2", "3,3", "4,4", "7,7", "13,1", "12,2", "11,3", "10,4", "4,10", "3,11", "2,12", "1,13", "10,10", "11,11", "12,12", "13,13"]);
        
                const key = `${row},${col}`;
                const x = col * this.squareSize; 
                const y = ((row+3) * this.squareSize)+8; 
    
                this.ctxBoard.fillStyle = "white"; 
                this.ctxBoard.font = "bold 12px verdana, sans-serif";
                const textX = (x - 5) + this.squareSize / 4; 
                const textY = y + this.squareSize / 1.5; 
    
            // Zeichne Text
            if (tripleWord.has(key)) {
                this.ctxBoard.fillText("TW", textX, textY);
            }
            if (doubleLetter.has(key)) {
                this.ctxBoard.fillText(" DL", textX, textY);
            }
            if (tripleLetter.has(key)) {
                this.ctxBoard.fillText(" TL", textX, textY);
            }
            if (doubleWord.has(key)) {
                this.ctxBoard.fillText("DW", textX, textY);
            }
        }

    setTile1(x, y, letter){
        this.ctxBoard.beginPath();
        this.ctxBoard.fillStyle = "rgb(255, 192, 203)";
        
        this.ctxBoard.strokeStyle = "black"; 
        this.ctxBoard.lineWidth = 2; 
        
        const textX = x * this.squareSize + this.squareSize / 3; 
        let textY = y * this.squareSize + (this.squareSize/ 1.5);

        if(y < 3){
            textY = y * this.squareSize + (this.squareSize/ 1.5);
            this.ctxBoard.fillRect(x * this.squareSize , y * this.squareSize, this.squareSize, this.squareSize);
            this.ctxBoard.strokeRect(x * this.squareSize, y * this.squareSize, this.squareSize, this.squareSize);
        } else {
            textY = y * this.squareSize + (this.squareSize/ 1.5) + 8; 
            this.ctxBoard.fillRect(x * this.squareSize , y * this.squareSize + 8, this.squareSize, this.squareSize);
            this.ctxBoard.strokeRect(x * this.squareSize, y * this.squareSize + 8, this.squareSize, this.squareSize);
        }
        this.ctxBoard.font = "bold 14px verdana, sans-serif";
        this.ctxBoard.fillStyle = "black";
        this.ctxBoard.fillText(letter, textX, textY);
    }

    setTile2(x, y, letter){
        this.ctxBoard.beginPath();
        this.ctxBoard.fillStyle = "rgb(191, 245, 239)";
        this.ctxBoard.strokeStyle = "black"; 
        this.ctxBoard.lineWidth = 2; 
        this.ctxBoard.fillRect(x * this.squareSize , y * this.squareSize + 8, this.squareSize, this.squareSize);
        
        const textX = x * this.squareSize + this.squareSize / 3; 
        let textY = y * this.squareSize + (this.squareSize/ 1.5);
     
        if(y < 15){
            textY = y * this.squareSize + (this.squareSize/ 1.5) + 8;
            this.ctxBoard.fillRect(x * this.squareSize , y * this.squareSize + 8, this.squareSize, this.squareSize);
            this.ctxBoard.strokeRect(x * this.squareSize, y * this.squareSize + 8, this.squareSize, this.squareSize);
        } else {
            textY = y * this.squareSize + (this.squareSize/ 1.5) + 16; 
            this.ctxBoard.fillRect(x * this.squareSize , y * this.squareSize + 16, this.squareSize, this.squareSize);
            this.ctxBoard.strokeRect(x * this.squareSize, y * this.squareSize + 16, this.squareSize, this.squareSize);
        }
        this.ctxBoard.font = "bold 14px verdana, sans-serif";
        this.ctxBoard.fillStyle = "black";
        this.ctxBoard.fillText(letter, textX, textY);
    }

    removeTile(x, y){
        this.ctxBoard.beginPath();
        this.ctxBoard.fillStyle = "rgb(128, 128, 128)";
        this.ctxBoard.strokeStyle = "black"; 
        this.ctxBoard.lineWidth = 2; 

        if(y < 3){
            this.ctxBoard.fillRect(x * this.squareSize , y * this.squareSize, this.squareSize, this.squareSize);
            this.ctxBoard.strokeRect(x * this.squareSize, y * this.squareSize, this.squareSize, this.squareSize);
        } else {
            this.ctxBoard.fillRect(x * this.squareSize , y * this.squareSize + 16, this.squareSize, this.squareSize);
            this.ctxBoard.strokeRect(x * this.squareSize, y * this.squareSize + 16, this.squareSize, this.squareSize);
        }
    }

    drawSideBar(){
        let x = 16*this.squareSize;
        let y = 3*this.squareSize+8;
        this.ctxBoard.beginPath();
        this.ctxBoard.fillStyle = "rgb(255, 192, 203)";
        this.ctxBoard.fillRect(x, y, 3 * this.squareSize, this.squareSize);
        this.ctxBoard.strokeStyle = "black";
        this.ctxBoard.strokeRect(x, y, 3 * this.squareSize, this.squareSize);
        const textX = x + this.squareSize / 3; 
        let textY = y + (this.squareSize/ 1.5); 
        this.ctxBoard.font = "bold 14px verdana, sans-serif";
        this.ctxBoard.fillStyle = "black";
        this.ctxBoard.fillText("HELP", textX, textY);

        y = 5*this.squareSize+8;
        this.ctxBoard.strokeRect(x, y, 3 * this.squareSize, this.squareSize);
        this.ctxBoard.fillStyle = "rgb(193, 255, 193)";
        this.ctxBoard.fillRect(x, y, 3 * this.squareSize, this.squareSize);
        textY = y + (this.squareSize/ 1.5); 
        this.ctxBoard.fillStyle = "black";
        this.ctxBoard.fillText("EASY", textX, textY);

        y = 6*this.squareSize+8;
        this.ctxBoard.strokeRect(x, y, 3 * this.squareSize, this.squareSize);
        this.ctxBoard.fillStyle = "rgb(191, 245, 239)";
        this.ctxBoard.fillRect(x, y, 3 * this.squareSize, this.squareSize);
        textY = y + (this.squareSize/ 1.5); 
        this.ctxBoard.fillStyle = "black";
        this.ctxBoard.fillText("NORMAL", textX, textY);

        y = 7*this.squareSize+8;
        this.ctxBoard.strokeRect(x, y, 3 * this.squareSize, this.squareSize);
        this.ctxBoard.fillStyle = "rgb(255, 192, 203)";
        this.ctxBoard.fillRect(x, y, 3 * this.squareSize, this.squareSize);
        textY = y + (this.squareSize/ 1.5); 
        this.ctxBoard.fillStyle = "black";
        this.ctxBoard.fillText("HARD", textX, textY);

        y = 10*this.squareSize+8;
        this.ctxBoard.strokeRect(x, y, 3 * this.squareSize, this.squareSize);
        this.ctxBoard.fillStyle = "rgb(191, 245, 239)";
        this.ctxBoard.fillRect(x, y, 3 * this.squareSize, this.squareSize);
        textY = y + (this.squareSize/ 1.5); 
        this.ctxBoard.fillStyle = "black";
        this.ctxBoard.fillText("GERMAN", textX, textY);

        y = 11*this.squareSize+8;
        this.ctxBoard.strokeRect(x, y, 3 * this.squareSize, this.squareSize);
        this.ctxBoard.fillStyle = "rgb(191, 245, 239)";
        this.ctxBoard.fillRect(x, y, 3 * this.squareSize, this.squareSize);
        textY = y + (this.squareSize/ 1.5); 
        this.ctxBoard.fillStyle = "black";
        this.ctxBoard.fillText("ENGLSH", textX, textY);

        y = 16*this.squareSize+8;
        this.ctxBoard.strokeRect(x, y, 3 * this.squareSize, this.squareSize);
        this.ctxBoard.fillStyle = "rgb(219,112,147)";
        this.ctxBoard.fillRect(x, y, 3 * this.squareSize, this.squareSize);
        textY = y + (this.squareSize/ 1.5); 
        this.ctxBoard.fillStyle = "black";
        this.ctxBoard.fillText("PLAY", textX, textY);
    }

    error(startX, startY, endX, endY){
        this.ctxBoard.beginPath();
        this.ctxBoard.strokeStyle = "red";
        this.ctxBoard.fillStyle = "rgba(255, 0, 0, 0.3)"; 
        this.ctxBoard.lineWidth = 1; 
        if(startX == endX){ //Wort in Spalte
            this.ctxBoard.strokeRect(this.squareSize * startX, (this.squareSize * (startY + 3) + 8), this.squareSize, this.squareSize * (endY-startY+1)); //weil Spielfeld bei 0 beginn
            this.ctxBoard.fillRect(this.squareSize * startX, (this.squareSize * (startY + 3) + 8), this.squareSize, this.squareSize * (endY-startY+1));
        } else if(startY == endY){
            this.ctxBoard.strokeRect(this.squareSize * startX, (this.squareSize * (startY + 3) + 8), this.squareSize * (endX-startX+1), this.squareSize);
            this.ctxBoard.fillRect(this.squareSize * startX, (this.squareSize * (startY + 3) + 8), this.squareSize * (endX-startX+1), this.squareSize);
        }
        this.ctxBoard.lineWidth = 2; 
    }
}