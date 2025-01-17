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
        
        this.boardCanvas.addEventListener("click", (event) => {
            
            let indexY = Math.floor(event.offsetY / this.boardHeight);
            const indexX = Math.floor(event.offsetX / this.boardWidth);
            if(indexY >= 3){
                indexY = Math.floor((event.offsetY-8) / this.boardHeight);
            } else if(indexY > 17){
                indexY = Math.floor((event.offsetY-16) / this.boardHeight);
            }
            const index = indexX.toString() + "x" + indexY.toString();
            console.log(`Clicked at X: ${indexX} Y: ${indexY} => ${index}`);
            fetch(endpoint, {method: "post", body: index}).catch(console.log);
        });
    }

        drawScrabbleField() {
            for (let row = 3; row < this.gridSize + 3; row++) {
                for (let col = 0; col < this.gridSize; col++) {
                    const x = col * this.squareSize;
                    const y = (row * this.squareSize)+8;
    
                    this.ctxBoard.beginPath();
                    this.setColor(row-3, col);
                    this.ctxBoard.fillRect(x, y, this.squareSize, this.squareSize);
                    this.ctxBoard.strokeStyle = "black"; 
                    this.ctxBoard.lineWidth = 1; 
                    this.ctxBoard.strokeRect(x, y, this.squareSize, this.squareSize);
                    this.setText(row-3, col);      
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
    
            const key = `${row},${col}`;
    
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
            this.ctxBoard.fillRect(x * this.squareSize , y * this.squareSize + 8, this.squareSize, this.squareSize);
            this.ctxBoard.strokeStyle = "black"; 
            this.ctxBoard.lineWidth = 2; 
            this.ctxBoard.strokeRect(x * this.squareSize, y * this.squareSize + 8, this.squareSize, this.squareSize);
            const textX = x * this.squareSize + this.squareSize / 3; 
            const textY = y * this.squareSize + (this.squareSize/ 1.5) + 8; 
            this.ctxBoard.font = "bold 14px verdana, sans-serif";
            this.ctxBoard.fillStyle = "black";
            this.ctxBoard.fillText(letter, textX, textY);
    }

    setTile2(x, y, letter){
        this.ctxBoard.beginPath();
        this.ctxBoard.fillStyle = "rgb(191, 245, 239)";
        this.ctxBoard.fillRect(x * this.squareSize , y * this.squareSize + 8, this.squareSize, this.squareSize);
        this.ctxBoard.strokeStyle = "black"; 
        this.ctxBoard.lineWidth = 2; 
        this.ctxBoard.strokeRect(x * this.squareSize, y * this.squareSize + 8, this.squareSize, this.squareSize);
        const textX = x * this.squareSize + this.squareSize / 3; 
        const textY = y * this.squareSize + (this.squareSize/ 1.5) + 8; 
        this.ctxBoard.font = "bold 14px verdana, sans-serif";
        this.ctxBoard.fillStyle = "black";
        this.ctxBoard.fillText(letter, textX, textY);
    }
}
