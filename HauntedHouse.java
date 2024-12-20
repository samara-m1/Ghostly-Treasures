import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class HauntedHouse extends JPanel implements ActionListener, KeyListener {
    int windowHeight = 400;
    int windowWidth = 900;
    //loading in images:
    Image backgroundImage = new ImageIcon("/Users/samaramansoor/Downloads/bowsercastle2.png").getImage();
    Image ghostImage = new ImageIcon("/Users/samaramansoor/Downloads/ghost-removebg-preview.png").getImage();
    Image fireImage = new ImageIcon("/Users/samaramansoor/Downloads/fire2.png").getImage();
    Image coinImage = new ImageIcon("/Users/samaramansoor/Downloads/coin.png").getImage();
    // attributes of ghost character
    int ghostX = windowWidth / 8;
    int ghostY = windowHeight / 3;
    int ghostWidth = 80;
    int ghostHeight = 65;
    //velocity variables to say how much the images move
    //ghost velocities begin at 0 because it is not moving
    int velocityY = 0;
    int velocityX = 0;
    //fire velocity because the fire will constantly move left
    int fireVelocity = -4;
    int score = 0; //to hold score
    boolean youWon = false; //boolean that says if player won the game
    //lists with the fire obstacles and coins 
    ArrayList<Fire> fires = new ArrayList<>();
    ArrayList<Coin> coins = new ArrayList<>();
    //making a random number to determine placement of coins and fires
    Random random = new Random();
    //class for ghost object
    class Ghost {
        int x;
        int y; 
        int height;
        int width;
        Image image;
        Ghost(Image image) {
            this.image = image;
        }
        Ghost(int x, int y, int height, int width, Image image) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.image = image;
        }
    }
    //class for fire object
    class Fire {
        int x;
        int y; 
        int height;
        int width;
        Image image;

        Fire(int x, int y, int height, int width, Image image) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.image = image;
        }
    }
    //class for coin object
    class Coin {
        int x;
        int y;
        int height;
        int width;
        Image image;

        Coin(int x, int y, int height, int width, Image image) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.image = image;
        }
    }
    Ghost ghost = new Ghost(ghostImage); //create ghost object
    Timer gameLoop = new Timer(1000 / 60, this); //loop so that the background keeps going
    Timer placeFireTimer = new Timer(1500, new ActionListener(){ //loop so that the fire keeps scrolling to the left
        @Override
        public void actionPerformed(ActionEvent e) {
            placeFire();
        }
    });

    Timer placeCoinTimer = new Timer(2000, new ActionListener() { //loop so coins also keep scrolling to the left
        @Override
        public void actionPerformed(ActionEvent e) {
            placeCoin();
        }
    });

    HauntedHouse() { //setup of game
        setPreferredSize(new Dimension(windowWidth, windowHeight)); //size for the game
        //loops for game:
        gameLoop.start();
        placeFireTimer.start();
        placeCoinTimer.start();
        setFocusable(true);
        addKeyListener(this); //keylistener so that ghost can move
    }

    public void placeFire() {
        int x = windowWidth; //fires spawn at the right side of the screen
        int y = random.nextInt(windowHeight - 40); //starts at a random y position
        int height = 80;
        int width = 80;
        fires.add(new Fire(x, y, height, width, fireImage)); //add new fire object to list of fires
    }

    public void placeCoin() {
        int x = windowWidth; //coins also spawn at the right side of the screen
        int y = random.nextInt(windowHeight - 40); //start at a random y position
        int height = 40;
        int width = 40;
        coins.add(new Coin(x, y, height, width, coinImage)); //add a new coin object to the list of fires
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //draw background and ghost
        g.drawImage(backgroundImage, 0, 0, windowWidth, windowHeight, null);
        g.drawImage(ghostImage, ghostX, ghostY, ghostWidth, ghostHeight, null);

        //draw the fires
        for (Fire fire : fires) {
            g.drawImage(fire.image, fire.x, fire.y, fire.width, fire.height, null);
        }

        //draw the coins
        for (Coin coin : coins) {
            g.drawImage(coin.image, coin.x, coin.y, coin.width, coin.height, null);
        }

        //writing and displaying the score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        g.drawString("Score: " + score, 20, 30);

        if(youWon){ //tells you if you won
            g.setFont(new Font("Comic Sans MS", Font.PLAIN, 50));
            g.drawString("YOU WON!", 300, 200);

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move(); 
        repaint();
        if (youWon) { //game ends if you win
            placeFireTimer.stop();
            placeCoinTimer.stop();
            gameLoop.stop();
        }
    }

    public void move() {
        //moving ghost by adding velocity to ghost positions
        //setting maximums and minimums for ghost positions so that they can not go off the screen
        ghostY += velocityY;
        ghostY = Math.max(ghostY, 0);
        ghostY = Math.min(ghostY, windowHeight - ghostHeight);

        ghostX += velocityX;
        ghostX = Math.max(ghostX, 0);
        ghostX = Math.min(ghostX, windowWidth - ghostWidth);

        //moving the fires and checking for collision
        for (int i = 0; i < fires.size(); i++) {
            Fire fire = fires.get(i);
            fire.x += fireVelocity;

            //decrease score if there is a collision
            if (collision(new Ghost(ghostX, ghostY, ghostHeight, ghostWidth, ghostImage), fire)) {
                score--;
            }

            //remove fire from list after it moves off screen
            if (fire.x + fire.width < 0) {
                fires.remove(i);
                i--; //adjust index
            }
        }

        //moving the coins and checking for collision
        for (int i = 0; i < coins.size(); i++) {
            Coin coin = coins.get(i);
            coin.x += fireVelocity; //coins move at same rate of fire

            //increase score if we collide with a coin
            if (collision(new Ghost(ghostX, ghostY, ghostHeight, ghostWidth, ghostImage), coin)) {
                score++;
                coins.remove(i); //remove the coin since ghost got it
                i--; 
            }
            //remove off-screen coins
            if (coin.x + coin.width < 0) {
                coins.remove(i);
                i--;
            }
            //do not allow for negative scores
            if(score<0){
                score = 0;
            }
            //player wins if they earn 15 points
            if(score==15){
                youWon = true;
            }
        }
    }
//detecting collision with fire
    public boolean collision(Ghost a, Fire b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    //detecting collision with coin
    public boolean collision(Ghost a, Coin c) {
        return a.x < c.x + c.width &&
               a.x + a.width > c.x &&
               a.y < c.y + c.height &&
               a.y + a.height > c.y;
    }

    //key pressed determines if player goes up, down, left, right
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            velocityY = 3;
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            velocityY = -3;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            velocityX = -3;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            velocityX = 3;
        }
    }

    //ghost stops moving once player lets go of the keys
    @Override
    public void keyReleased(KeyEvent e) {
        velocityX = 0;
        velocityY = 0;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
