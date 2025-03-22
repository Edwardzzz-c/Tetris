package mino;

import main.GamePanel;
import main.KeyHandler;
import main.PlayManager;

import java.awt.*;

public class Mino {

    public Block[] b = new Block[4];
    public Block[] tempB = new Block[4];
    int autoDropCounter = 0;
    public int direction = 1; // There are 4 directions (1/2/3/4)
    boolean leftCollision, rightCollision, bottomCollision;
    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;

    public void create(Color c){
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);
        tempB[0]  = new Block(c);
        tempB[1]  = new Block(c);
        tempB[2]  = new Block(c);
        tempB[3]  = new Block(c);
    }

    public void setXY(int x, int y){}
    public void updateXY(int direction) {
        checkRotationCollision();
        if (!leftCollision && !rightCollision && !bottomCollision) {
            this.direction = direction;
            b[0].x = tempB[0].x;
            b[0].y = tempB[0].y;
            b[1].x = tempB[1].x;
            b[1].y = tempB[1].y;
            b[2].x = tempB[2].x;
            b[2].y = tempB[2].y;
            b[3].x = tempB[3].x;
            b[3].y = tempB[3].y;
        }
    }
    public void getDirection1(){}
    public void getDirection2(){}
    public void getDirection3(){}
    public void getDirection4(){}
    public void checkMovementCollision() {
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        checkStaticBlockCollision();

        // Check frame collision
        for (Block block : b) {
            // left
            if (block.x == PlayManager.left_x) {
                leftCollision = true;
            }
            // right
            if (block.x + Block.SIZE == PlayManager.right_x) {
                rightCollision = true;
            }
            // bottom
            if (block.y + Block.SIZE == PlayManager.bottom_y){
                bottomCollision = true;
            }
        }

    }
    public void checkRotationCollision(){

        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        checkStaticBlockCollision();

        // Check frame collision
        for (Block block : tempB) {
            // left
            if (block.x < PlayManager.left_x) {
                leftCollision = true;
            }
            // right
            if (block.x + Block.SIZE > PlayManager.right_x) {
                rightCollision = true;
            }
            // bottom
            if (block.y + Block.SIZE > PlayManager.bottom_y){
                bottomCollision = true;
            }
        }

    }

    private void checkStaticBlockCollision(){

        for (Block block: PlayManager.staticBlocks){
            int targetX = block.x;
            int targetY = block.y;
            for (Block bc: b){
                if (bc.x == targetX && bc.y + Block.SIZE == targetY){
                    bottomCollision = true;
                }
                if (bc.x - Block.SIZE == targetX && bc.y == targetY){
                    leftCollision = true;
                }
                if (bc.x + Block.SIZE == targetX && bc.y == targetY){
                    rightCollision = true;
                }

            }
        }
    }
    public void update(){

        if (deactivating){
            deactivating();
        }
        // Move the mino
        if (KeyHandler.upPressed) {
            switch (direction) {
                case 1 -> getDirection2();
                case 2 -> getDirection3();
                case 3 -> getDirection4();
                case 4 -> getDirection1();
            }
            KeyHandler.upPressed = false;
            GamePanel.soundEffect.play(3,false);
        }

        // Before we allow movement, check if there is movement collision
        checkMovementCollision();

        if (KeyHandler.downPressed){
            // If the mino's bottom is not hitting, it can go down
            if (!bottomCollision) {
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                autoDropCounter = 0; // When moved down, reset the autoDropCounter
            }
            KeyHandler.downPressed = false;
        }
        if (KeyHandler.leftPressed){
            if (!leftCollision) {
                b[0].x -= Block.SIZE;
                b[1].x -= Block.SIZE;
                b[2].x -= Block.SIZE;
                b[3].x -= Block.SIZE;
            }
            KeyHandler.leftPressed = false;
        }
        if (KeyHandler.rightPressed){
            if (!rightCollision) {
                b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;
            }
            KeyHandler.rightPressed = false;
        }

        if (bottomCollision){
            if(!deactivating){
                GamePanel.soundEffect.play(4,false);
            }
            deactivating = true;
        }
        else {
            autoDropCounter++; // Counter increase every frame
            if (autoDropCounter == PlayManager.dropInterval) {
                // the mino goes down
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                autoDropCounter = 0;
            }
        }

    }

    public void deactivating(){
        deactivateCounter++;
        // Wait 45 frames until deactivate
        if (deactivateCounter == 45){
            deactivateCounter = 0;
            checkMovementCollision(); // Check if the bottom is still hitting
            // deactivate the mino
            if(bottomCollision){
                active = false;
            }
        }
    }
    public void draw(Graphics2D g2){
        int margin = 2;
        g2.setColor(b[0].c);
        g2.fillRect(b[0].x+margin, b[0].y+margin, Block.SIZE-(margin*2),Block.SIZE-(margin*2));
        g2.fillRect(b[1].x+margin, b[1].y+margin, Block.SIZE-(margin*2),Block.SIZE-(margin*2));
        g2.fillRect(b[2].x+margin, b[2].y+margin, Block.SIZE-(margin*2),Block.SIZE-(margin*2));
        g2.fillRect(b[3].x+margin, b[3].y+margin, Block.SIZE-(margin*2),Block.SIZE-(margin*2));
    }

}


