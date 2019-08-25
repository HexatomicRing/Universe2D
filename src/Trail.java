import java.awt.*;

public class Trail {
    public int x1,x2,y1,y2,t;
    Color c;
    public Trail(V2d pos,V2d pos2, Color c){
        x1 = (int)pos.x;
        y1 = (int)pos.y;
        x2 = (int)pos2.x;
        y2 = (int)pos2.y;
        t = 300;
        this.c = c;
    }
    void show(Graphics g){
        g.setColor(c);
        g.drawLine(x1,y1,x2,y2);
    }
}
