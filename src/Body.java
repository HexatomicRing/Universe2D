import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.PI;

class Body{
    public int id;
    public float mass;
    public float r;
    public V2d pos;
    public V2d vel;
    public Color col;

    ArrayList<V2d> path = new ArrayList<V2d>();
    int MAXPATH=300;
    //构造函数
    public Body(float mass, float x, float y, float vx, float vy,int id ) {
        super();
        this.id = id;
        this.mass = mass;
        this.r = calR(mass);
        this.pos = new V2d(x,y);
        this.vel = new V2d(vx,vy);
        //Random random = new Random(System.currentTimeMillis());
        this.col = new Color(new Random(System.currentTimeMillis() + id * id).nextInt(255),
                new Random(System.currentTimeMillis() - id * id).nextInt(255),
                new Random(System.currentTimeMillis() + id).nextInt(255));
    }


    //计算半径,顺便把max质量恒星赋值
    static float calR(float mass) {
        return (float) Math.pow(mass/PI * (3f/4f),1f/3f)/3;
    }

    //显示球形
    void show(Graphics g){
        g.setColor(col);
        g.fillOval((int)(pos.x - r), (int)(pos.y - r), Math.max((int)r * 2,1) , Math.max((int)r * 2,2));
        if(Main.show_content){
            g.setColor(Color.YELLOW);
            g.drawString(id + "(" + (int)mass +")",(int)pos.x, (int)pos.y);
        }
    }

}
