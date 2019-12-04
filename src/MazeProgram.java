import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MazeProgram extends JPanel implements KeyListener {

    private boolean GAMEOVER = false;
    private Wall[][] maze = new Wall[41][43];
    private int direction = 3; // 1 is north, 2 is east, 3 is south, 4 is west
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //get screen size
    private int width = (int) screenSize.getWidth();
    private int height = (int) screenSize.getHeight();
    private int size = this.width / 82;   //size of walls and explorer
    private Explorer explorer = new Explorer(size, size, size, maze);
    private int moves = 0;
    private int lives = 2;
    private int xlocation = (int) (0.51 * width);
    private int ylocation = (int) (0.21 * size * 41);
    private ArrayList<Polygon> polygons;
    private int luminance = 255;
    private int darkness = 64;

    private MazeProgram() {
        setBoard();
        JFrame frame = new JFrame("Sreekar's Cool Maze");
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setUndecorated(true); //true fullscreen
        frame.setSize(this.width, this.height); // fill screen
        frame.setVisible(true);
        frame.addKeyListener(this);
    }

    public static void main(String[] args) throws Exception {
        new MazeProgram();
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("src/orion.wav"));
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.width, this.height);  //fill rectangle of screen size

        g.setColor(new Color(0, luminance, 0));
        for (Wall[] walls : maze) {
            for (Wall wall : walls) {
                try {
                    Rectangle rect = wall.getRect();
                    g.fillRect(
                            (int) rect.getX(),
                            (int) rect.getY(),
                            (int) rect.getWidth(),
                            (int) rect.getHeight());
                } catch (NullPointerException ignored) {
                }
            }
        }
        //// ESC TO EXIT
        g.setColor(Color.YELLOW);
        g.fillOval(size * 32, size * 33, size, size);
        g.fillOval(size * 18, size * 11, size, size);
        g.fillOval(size * 19, size * 31, size, size);

        g.setColor(Color.BLUE); //health
        g.fillRect(size, size * 39, size * 7, size);
        g.fillRect(size * 39, size * 15, size, size * 3);

        g.setColor(Color.RED); //fill explorer color
        g.fillRect(explorer.getX(), explorer.getY(), size, size);

        g.setColor(Color.WHITE); //lightbulbs
        g.fillOval(size * 9, size * 13, size, size);
        g.fillOval(size * 29, size * 13, size, size);
        g.fillOval(size * 19, size * 15, size, size);
        g.fillOval(size * 29, size * 25, size, size);
        g.fillOval(size * 5, size * 25, size, size);
        g.fillOval(size * 33, size * 3, size, size);

        g.setColor(Color.MAGENTA); //death traps
        g.fillRect(size * 29, size * 39, size * 2, size);
        g.fillRect(size * 21, size * 3, size * 2, size);

        Color lines = Color.BLACK;

        g.setColor(new Color(darkness, darkness, darkness)); //background ceiling and floor
        g.fillRect(xlocation - 2, ylocation - 2, 494, 494);
        g.setColor(lines);
        g.drawRect(xlocation, ylocation, 490, 490);

        for (int i = ylocation + 70; i <= ylocation + 420; i += 70) {
            g.drawLine(xlocation, i, xlocation + 490, i);
        }

        g.setColor(Color.BLACK);
        for (int i = 1; i < 4; i++) {
            g.fillRect(xlocation + 70 * (i - 1), ylocation + 70 * i, 70, 350 - 70 * 2 * (i - 1));
        }
        for (int i = 1; i < 4; i++) {
            g.fillRect(xlocation + 420 - 70 * (i - 1), ylocation + 70 * i, 70, 350 - 70 * 2 * (i - 1));
        }

        g.fillRect(xlocation + 210, ylocation + 210, 70, 70);
        g.setColor(lines);
        g.drawRect(xlocation + 210, ylocation + 210, 70, 70);

        for (Polygon polygon : polygons) {
            g.setColor(new Color(0, luminance, 0));
            g.fillPolygon(polygon);
            g.setColor(lines);
            g.drawPolygon(polygon);
        }

        g.setColor(Color.RED);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, size * 4));
        g.drawString(String.valueOf(moves), xlocation + 520, ylocation + 250);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, size * 4));

        String d = "";
        switch (direction) {
            case 1:
                d = "North";
                break;
            case 2:
                d = "East";
                break;
            case 3:
                d = "South";
                break;
            case 4:
                d = "West";
                break;
        }
        g.drawString(d, xlocation + 165, ylocation - 45);
        g.setColor(Color.CYAN);
        g.drawString("Lives : " + lives, xlocation + 100, ylocation + 570);


        if (explorer.getY() == size * 11 && explorer.getX() == size * 18) {
            explorer.setX(size);
            explorer.setY(size);
            direction = 3;
            lives--;
            setWalls();
            repaint();
        }

        if (explorer.getY() == size * 39 && explorer.getX() == size * 30) {
            lives = 0;
        }

        if (explorer.getY() == size * 3 && explorer.getX() == size * 22) {
            lives = 0;
        }

        if (explorer.getY() == size * 33 && explorer.getX() == size * 32) {
            explorer.setX(size * 37);
            explorer.setY(size * 11);
            direction = 1;
            lives--;
            setWalls();
            repaint();
        }

        if (explorer.getY() == size * 31 && explorer.getX() == size * 19) {
            explorer.setX(size * 25);
            explorer.setY(size * 11);
            direction = 2;
            lives--;
            setWalls();
            repaint();
        }

        if (explorer.getY() == size * 15 && explorer.getX() == size * 39) {
            explorer.setX(size * 5);
            explorer.setY(size * 11);
            direction = 2;
            lives++;
            luminance = 255;
            darkness = 64;
            setWalls();
            repaint();
        }

        if (explorer.getY() == size * 39 && explorer.getX() == size * 5) {
            explorer.setX(size * 29);
            explorer.setY(size * 19);
            direction = 4;
            luminance = 255;
            darkness = 64;
            lives++;
            setWalls();
            repaint();
        }

        if (explorer.getX() == size * 9 && explorer.getY() == size * 13) {
            direction = 1;
            luminance = 255;
            darkness = 64;
            setWalls();
            repaint();
            g.fillRect(explorer.getX(), explorer.getY(), size, size);
        }
        if (explorer.getX() == size * 29 && explorer.getY() == size * 13) {
            direction = 4;
            luminance = 255;
            darkness = 64;
            setWalls();
            repaint();
            g.fillRect(explorer.getX(), explorer.getY(), size, size);
        }
        if (explorer.getX() == size * 19 && explorer.getY() == size * 15) {
            direction = 3;
            luminance = 255;
            darkness = 64;
            setWalls();
            repaint();
            g.fillRect(explorer.getX(), explorer.getY(), size, size);
        }
        if (explorer.getX() == size * 33 && explorer.getY() == size * 3) {
            direction = 4;
            luminance = 255;
            darkness = 64;
            setWalls();
            repaint();
            g.fillRect(explorer.getX(), explorer.getY(), size, size);
        }
        if (explorer.getX() == size * 29 && explorer.getY() == size * 25) {
            direction = 3;
            luminance = 255;
            darkness = 64;
            setWalls();
            repaint();
            g.fillRect(explorer.getX(), explorer.getY(), size, size);
        }

        if (explorer.getX() == size * 5 && explorer.getY() == size * 25) {
            direction = 3;
            luminance = 255;
            darkness = 64;
            setWalls();
            repaint();
            g.fillRect(explorer.getX(), explorer.getY(), size, size);
        }


        if (explorer.getY() == size * 39 && explorer.getX() > size * 40) {
            GAMEOVER = true;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, this.width, this.height);
            g.setColor(Color.MAGENTA);
            g.setFont(new Font("Comic Sans MS", Font.PLAIN, size * 9));
            g.drawString("Congratulations !", width / 12, height / 4);
            g.setColor(Color.RED);
            g.setFont(new Font("Comic Sans MS", Font.PLAIN, size * 5));
            if (moves > 250) g.drawString("You took forever !", width / 4, height / 2);
            else g.drawString("That was quick !", width / 4, height / 2);
            g.setColor(Color.ORANGE);
            g.drawString(moves + " steps to finish", width / 4, 2 * height / 3);
            g.setColor(Color.GREEN);
            g.drawString("PRESS SPACE TO PLAY AGAIN", width / 20, 7 * height / 8);
        }


        if (lives == 0) {
            GAMEOVER = true;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, this.width, this.height);
            g.setColor(Color.RED);
            g.setFont(new Font("Comic Sans MS", Font.PLAIN, size * 9));
            g.drawString("YOU DIED", width / 4, height / 3);
            g.setColor(Color.GREEN);
            g.setFont(new Font("Comic Sans MS", Font.PLAIN, size * 5));
            g.drawString("PRESS SPACE TO TRY AGAIN", width / 12, 3 * height / 4);
        }


    }

    private void setBoard() {
        File name = new File("src/Maze.txt");

        try {
            BufferedReader input = new BufferedReader(new FileReader(name));
            String text;
            int line = 0;

            while ((text = input.readLine()) != null) {
                String[] temp = text.split("");

                for (int position = 0; position < temp.length; position++) {
                    if (temp[position].equals("#")) maze[line][position] = new Wall(line, position, size);
                }

                line++;

            }

        } catch (IOException io) {
            System.err.println("File error");
        }

        setWalls();
    }

    private void setWalls() {

        polygons = new ArrayList<>();
        int x = explorer.getX() / size;
        int y = explorer.getY() / size;

        switch (direction) {

            case 1:
                if (y - 3 >= 0) {
                    for (int i = 1; i <= 3; i++) {
                        if (maze[y - i][x - 1] != null)
                            polygons.add(generatePolygon(i));
                        if (maze[y - i][x + 1] != null)
                            polygons.add(generatePolygon(i + 3));
                    }
                    if (maze[y - 3][x] != null)
                        polygons.add(generatePolygon(7));
                    if (maze[y - 2][x] != null)
                        polygons.add(generatePolygon(8));
                    if (maze[y - 1][x] != null)
                        polygons.add(generatePolygon(9));
                } else if (y - 2 >= 0) {
                    for (int i = 1; i <= 2; i++) {
                        if (maze[y - i][x - 1] != null)
                            polygons.add(generatePolygon(i));
                        if (maze[y - i][x + 1] != null)
                            polygons.add(generatePolygon(i + 3));
                    }
                    if (maze[y - 2][x] != null)
                        polygons.add(generatePolygon(8));
                    if (maze[y - 1][x] != null)
                        polygons.add(generatePolygon(9));
                } else {
                    if (maze[y - 1][x - 1] != null)
                        polygons.add(generatePolygon(1));
                    if (maze[y - 1][x + 1] != null)
                        polygons.add(generatePolygon(4));
                    if (maze[y - 1][x] != null)
                        polygons.add(generatePolygon(9));
                }
                break;
            case 2:
                if (x + 3 < maze[0].length) {
                    for (int i = 1; i <= 3; i++) {
                        if (maze[y - 1][x + i] != null)
                            polygons.add(generatePolygon(i));
                        if (maze[y + 1][x + i] != null)
                            polygons.add(generatePolygon(i + 3));
                    }
                    if (maze[y][x + 3] != null)
                        polygons.add(generatePolygon(7));
                    if (maze[y][x + 2] != null)
                        polygons.add(generatePolygon(8));
                    if (maze[y][x + 1] != null)
                        polygons.add(generatePolygon(9));
                } else if (x + 2 < maze[0].length) {
                    for (int i = 1; i <= 2; i++) {
                        if (maze[y - 1][x + i] != null)
                            polygons.add(generatePolygon(i));
                        if (maze[y + 1][x + i] != null)
                            polygons.add(generatePolygon(i + 3));
                    }
                    if (maze[y][x + 2] != null)
                        polygons.add(generatePolygon(8));
                    if (maze[y][x + 1] != null)
                        polygons.add(generatePolygon(9));
                } else {
                    if (maze[y - 1][x + 1] != null)
                        polygons.add(generatePolygon(1));
                    if (maze[y + 1][x + 1] != null)
                        polygons.add(generatePolygon(4));
                    if (maze[y][x + 1] != null)
                        polygons.add(generatePolygon(9));
                }
                break;
            case 3:
                if (y + 3 < maze.length) {
                    for (int i = 1; i <= 3; i++) {
                        if (maze[y + i][x + 1] != null)
                            polygons.add(generatePolygon(i));
                        if (maze[y + i][x - 1] != null)
                            polygons.add(generatePolygon(i + 3));
                    }
                    if (maze[y + 3][x] != null)
                        polygons.add(generatePolygon(7));
                    if (maze[y + 2][x] != null)
                        polygons.add(generatePolygon(8));
                    if (maze[y + 1][x] != null)
                        polygons.add(generatePolygon(9));
                } else if (y + 2 < maze.length) {
                    for (int i = 1; i <= 2; i++) {
                        if (maze[y + i][x + 1] != null)
                            polygons.add(generatePolygon(i));
                        if (maze[y + i][x - 1] != null)
                            polygons.add(generatePolygon(i + 3));
                    }
                    if (maze[y + 2][x] != null)
                        polygons.add(generatePolygon(8));
                    if (maze[y + 1][x] != null)
                        polygons.add(generatePolygon(9));
                } else {
                    if (maze[y + 1][x + 1] != null)
                        polygons.add(generatePolygon(1));
                    if (maze[y + 1][x - 1] != null)
                        polygons.add(generatePolygon(4));
                    if (maze[y + 1][x] != null)
                        polygons.add(generatePolygon(9));
                }
                break;
            case 4:
                if (x - 3 >= 0) {
                    for (int i = 1; i <= 3; i++) {
                        if (maze[y + 1][x - i] != null)
                            polygons.add(generatePolygon(i));
                        if (maze[y - 1][x - i] != null)
                            polygons.add(generatePolygon(i + 3));
                    }
                    if (maze[y][x - 3] != null)
                        polygons.add(generatePolygon(7));
                    if (maze[y][x - 2] != null)
                        polygons.add(generatePolygon(8));
                    if (maze[y][x - 1] != null)
                        polygons.add(generatePolygon(9));
                } else if (x - 2 >= 0) {
                    for (int i = 1; i <= 2; i++) {
                        if (maze[y + 1][x - i] != null)
                            polygons.add(generatePolygon(i));
                        if (maze[y - 1][x - i] != null)
                            polygons.add(generatePolygon(i + 3));
                    }
                    if (maze[y][x - 2] != null)
                        polygons.add(generatePolygon(8));
                    if (maze[y][x - 1] != null)
                        polygons.add(generatePolygon(9));
                } else {
                    if (maze[y + 1][x - 1] != null)
                        polygons.add(generatePolygon(1));
                    if (maze[y - 1][x - 1] != null)
                        polygons.add(generatePolygon(4));
                    if (maze[y][x - 1] != null)
                        polygons.add(generatePolygon(9));
                }
                break;
        }
    }

    private Polygon generatePolygon(int type) {
        Polygon poly = new Polygon();
        switch (type) {
            case 1:
                poly.addPoint(xlocation, ylocation);
                poly.addPoint(xlocation + 70, ylocation + 70);
                poly.addPoint(xlocation + 70, ylocation + 420);
                poly.addPoint(xlocation, ylocation + 490);
                break;
            case 2:
                poly.addPoint(xlocation + 70, ylocation + 70);
                poly.addPoint(xlocation + 140, ylocation + 140);
                poly.addPoint(xlocation + 140, ylocation + 350);
                poly.addPoint(xlocation + 70, ylocation + 420);
                break;
            case 3:
                poly.addPoint(xlocation + 140, ylocation + 140);
                poly.addPoint(xlocation + 210, ylocation + 210);
                poly.addPoint(xlocation + 210, ylocation + 280);
                poly.addPoint(xlocation + 140, ylocation + 350);
                break;
            case 4:
                poly.addPoint(xlocation + 490, ylocation);
                poly.addPoint(xlocation + 420, ylocation + 70);
                poly.addPoint(xlocation + 420, ylocation + 420);
                poly.addPoint(xlocation + 490, ylocation + 490);
                break;
            case 5:
                poly.addPoint(xlocation + 420, ylocation + 70);
                poly.addPoint(xlocation + 350, ylocation + 140);
                poly.addPoint(xlocation + 350, ylocation + 350);
                poly.addPoint(xlocation + 420, ylocation + 420);
                break;
            case 6:
                poly.addPoint(xlocation + 350, ylocation + 140);
                poly.addPoint(xlocation + 280, ylocation + 210);
                poly.addPoint(xlocation + 280, ylocation + 280);
                poly.addPoint(xlocation + 350, ylocation + 350);
                break;
            case 7:
                poly.addPoint(xlocation + 140, ylocation + 140);
                poly.addPoint(xlocation + 350, ylocation + 140);
                poly.addPoint(xlocation + 350, ylocation + 350);
                poly.addPoint(xlocation + 140, ylocation + 350);
                break;
            case 8:
                poly.addPoint(xlocation + 70, ylocation + 70);
                poly.addPoint(xlocation + 420, ylocation + 70);
                poly.addPoint(xlocation + 420, ylocation + 420);
                poly.addPoint(xlocation + 70, ylocation + 420);
                break;
            case 9:
                poly.addPoint(xlocation, ylocation);
                poly.addPoint(xlocation + 490, ylocation);
                poly.addPoint(xlocation + 490, ylocation + 490);
                poly.addPoint(xlocation, ylocation + 490);
                break;
        }

        return poly;
    }


    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);

        if (GAMEOVER && e.getKeyCode() == KeyEvent.VK_SPACE) {
            explorer.setX(size);
            explorer.setY(size);
            direction = 3;
            lives = 2;
            moves = 0;
            luminance = 255;
            darkness = 64;
            GAMEOVER = false;
            setWalls();
            repaint();
        }

        if (!GAMEOVER) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    direction--;
                    if (direction < 1) direction = 4;
                    moves++;
                    break;
                case KeyEvent.VK_RIGHT:
                    direction++;
                    if (direction > 4) direction = 1;
                    moves++;
                    break;
                case KeyEvent.VK_UP:
                    if (explorer.move(direction)) {
                        this.moves++;
                        luminance -= 7;
                        darkness -= 3;
                    }
                    if (luminance < 0) luminance = 0;
                    if (darkness < 0) darkness = 0;
                    break;
            }
        }
        setWalls();
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

}