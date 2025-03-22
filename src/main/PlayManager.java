package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlayManager {

    // Main.Main play area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // Others
    public static int dropInterval = 60; // Mino drop every 60 frames
    boolean gameOver;
    // Effect
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // Score
    int level = 1;
    int lines;
    int score;

    // instruction
    final static String INSTRUCTION = """
            -> Use up for rotation
            -> Use arrow keys to adjust blocks
            -> Use space to pause/unpause""";
    // Font
    private static final Font TITLE_FONT = new Font("Impact", Font.BOLD, 60);
    private static final Font INSTRUCTION_FONT = new Font("Verdana", Font.PLAIN, 20);


    public PlayManager(){
        // Initialize main play area frame
        left_x = (GamePanel.WIDTH/2) - (WIDTH / 2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        // Initialize current Mino
        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;
        // Set the starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);

        // Initialize next Mino
        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X,NEXTMINO_Y);
    }

    public void draw(Graphics2D g2){
        // Draw play area frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);

        // Draw minor frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x,y,200,200);
        g2.setFont(new Font("Arial",Font.PLAIN,30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT",x+60,y+60);

        // Draw score frame
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: "+ level, x, y); y += 70;
        g2.drawString("LINES: "+ lines, x, y); y += 70;
        g2.drawString("SCORE: "+ score, x, y);

        // Draw the currentMino
        if (currentMino != null){
            currentMino.draw(g2);
        }
        // Draw the nextMino in the waiting box
        nextMino.draw(g2);

        // Draw Static Blocks
        for (Block block: staticBlocks){
            block.draw(g2);
        }

        // Draw delete effect
        if (effectCounterOn) {
            effectCounter++;

            // Calculate alpha to gradually fade the effect out (1.0f = fully opaque, 0.0f = fully transparent)
            float alpha = Math.max(0f, 1f - (effectCounter / 20f));
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);

            // Draw a gradient effect for each line that should be deleted
            for (Integer yLine : effectY) {
                // Create a gradient from bright yellow to vivid red
                GradientPaint gp = new GradientPaint(
                        left_x, yLine, Color.YELLOW,
                        left_x + WIDTH, yLine, Color.RED, true);
                g2.setPaint(gp);
                g2.fillRect(left_x, yLine, WIDTH, Block.SIZE);
            }

            // Reset the composite so subsequent drawing isn't affected
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // End the effect after 15 frames
            if (effectCounter >= 15) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        // Draw pause or game over
        g2.setColor(Color.red);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (gameOver){
            x = left_x + 25;
            y = top_y + 320;
            g2.drawString("GAME OVER",x, y);
        }
        else if (KeyHandler.pausePressed){
            x = left_x + 80;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }

        // Draw title and instructions
        double time = System.currentTimeMillis() / 1000.0;
        float baseHue = (float) (time % 1.0);

        g2.setFont(TITLE_FONT);
        FontMetrics fmTitle = g2.getFontMetrics();

        x = 60;
        y = top_y + 100;
        String title = "Tetris";

        for (int i = 0; i < title.length(); i++) {
            char c = title.charAt(i);
            float hue = (baseHue + i * 0.08f) % 1.0f;
            g2.setColor(Color.getHSBColor(hue, 1f, 1f));
            g2.drawString(String.valueOf(c), x, y);
            x += fmTitle.charWidth(c);
        }

        g2.setFont(INSTRUCTION_FONT);
        FontMetrics fmInst = g2.getFontMetrics();
        String[] lines = INSTRUCTION.split("\n");

        y += 50;
        x = 60;

        g2.setColor(Color.white);
        for (String line : lines) {
            g2.drawString(line, x, y);
            y += fmInst.getHeight();
        }
    }

    private Mino pickMino(){
        // Pick a random mino
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch (i){
            case 0 -> mino = new Mino_L1();
            case 1 -> mino = new Mino_L2();
            case 2 -> mino = new Mino_Square();
            case 3 -> mino = new Mino_Bar();
            case 4 -> mino = new Mino_T();
            case 5 -> mino = new Mino_Z1();
            case 6 -> mino = new Mino_Z2();
        }
        return mino;
    }
    public void update(){
        if (!currentMino.active){
            // if the current mino is inactive, add it to the static blocks
             staticBlocks.add(currentMino.b[0]);
             staticBlocks.add(currentMino.b[1]);
             staticBlocks.add(currentMino.b[2]);
             staticBlocks.add(currentMino.b[3]);

             // Check if game is over
            if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
                // this means currentMino immediately collided and cannot move since it was generated
                gameOver = true;
                GamePanel.music.stop();
                GamePanel.soundEffect.play(2,false);
            }

             currentMino.deactivating = false;

             // replace it with the next mino
             currentMino = nextMino;
             currentMino.setXY(MINO_START_X,MINO_START_Y);
             nextMino = pickMino();
             nextMino.setXY(NEXTMINO_X,NEXTMINO_Y);

             // when a mino becomes inactive, check if lines can be deleted
            checkDelete();
        }
        else {
            currentMino.update();
        }
    }
    private void checkDelete(){
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        // scan every grid by row
        while(x < right_x && y < bottom_y){
            for (int i = 0; i < staticBlocks.size(); i++) {
                // increase the count if there is a static block
                if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y){
                    blockCount ++;
                }
            }

            x += Block.SIZE;
            // if the blockCount hits 12, the current y line is filled
            // We can delete the line
            if(x == right_x) {
                if (blockCount == 12) {

                    effectCounterOn = true;
                    effectY.add(y);

                    for (int i = staticBlocks.size() - 1; i >= 0; i--) {
                        // remove all blocks in the current y line
                        if (staticBlocks.get(i).y == y) {
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    lines++;

                    // Drop speed
                    // For every 10 lines deleted, increase the drop speed
                    // Decrease rate reduce when interval < 10 and caps at 1
                    if (lines % 10 == 0 && dropInterval > 1) {
                        level++;
                        if (dropInterval > 10) {
                            dropInterval -= 10;
                        } else {
                            dropInterval -= 1;
                        }
                    }

                    // a line has been deleted so need to slide down all lines above
                    for (Block staticBlock : staticBlocks) {
                        if (staticBlock.y < y) {
                            staticBlock.y += Block.SIZE;
                        }
                    }
                }
                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }
        // Calculate score
        if (lineCount > 0){
            GamePanel.soundEffect.play(1,false);
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }
}
