import java.awt.*;
public class Explorer
{
    private Location loc;
    private int size;
    private int x,y;
    private Wall[][] maze;
    Explorer(int r, int c, int size, Wall[][] maze)
    {
        loc=new Location(r,c);
        this.size=size;
        this.x=c;
        this.y=r;
        this.maze=maze;
    }
    public Rectangle getRect()
    {
        return new Rectangle(loc.getCol()*size,loc.getRow()*size,size,size);
    }

    boolean move(int direction) {
         switch(direction) {
             case 1:
                 if(canMove(x,y-size)) { y-=size; return true; }
                 break;
             case 2:
                 if(canMove(x+size,y)) {x+=size;  return true; }
                 break;
             case 3:
                 if(canMove(x,y+size)) {y+=size;  return true; }
                 break;
             case 4:
                 if(canMove(x-size,y)) {x-=size;  return true; }
                 break;
         }
        return false;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    private boolean canMove(int x, int y) {
        return maze[y / size][x / size] == null;
    }


}