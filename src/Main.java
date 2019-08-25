import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class Main extends JFrame implements Runnable{
    private static final int width = 1920,height = 1080,FPS0 = 100,r = 400;
    private static final float G = 0.5f;//引力常数
    private static long time1,time2,time_all;
    private static boolean play = false;
    private static boolean show_trail = true,show_keys = false,show_msg = true,hide_all = false;
    static boolean show_content = true;
    private static Image iBuffer;
    private static float total_mass;
    private static Graphics gBuffer;
    private static Body main_star;
    private static ArrayList<Body> stars = new ArrayList<>();
    private static ArrayList<Trail> trails = new ArrayList<>();
    private static ArrayList<String> msg = new ArrayList<>();
    private static int startBodyCount = 1000; //星体数量
    private static V2d startBodyMass = new V2d(5,50); //星体质量范围
    private static float startBodySpeed = 0.1f; //星体速度范围
    public static void main(String[] args){
        new Thread(new Main()).run();
    }
    public void run(){
        while(true){
            repaint();
            try {
                Thread.sleep(1000 / FPS0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public Main(){
        super("Universe 2D");
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        addKeyListener(new KL());
        init();
    }
    private void init(){
        stars = new ArrayList<>();
        trails = new ArrayList<>();
        msg = new ArrayList<>();
        time_all = 0;
        msg.add("");
        msg.add("");
        msg.add("");
        Random random = new Random(System.currentTimeMillis());
        main_star = new Body(200000,width/2,height/2,0,0,0);
        stars.add(main_star);
        total_mass = main_star.mass;
        for(int i=0 ; i < startBodyCount-1 ; i++)
        {
            V2d pos,p;
            while (true){
                pos = new V2d((random.nextFloat() - 0.5f) * height + width /2,(random.nextFloat() - 0.5f) * height + height / 2);
                p = V2d.sub(pos,new V2d(width/2,height/2));
                if(p.mag() < r)
                    break;
            }
            V2d vn = V2d.mult(new V2d(-p.y,p.x).normalize(),(float)Math.pow(p.mag(),-0.5) * 300);

            float mass = random.nextFloat() * (startBodyMass.y - startBodyMass.x) + startBodyMass.x;
            stars.add(new Body( mass,
                    pos.x,pos.y,
                    vn.x + random.nextFloat() * (startBodySpeed)-startBodySpeed/2,
                    vn.y + random.nextFloat() * (startBodySpeed)-startBodySpeed/2,
                    i + 1));
            total_mass += mass;
        }
    }
    public void paint(Graphics g) {
        if(iBuffer == null){
            iBuffer = createImage(width,height);
            gBuffer = iBuffer.getGraphics();
        }
        gBuffer.setColor(getBackground());
        gBuffer.fillRect(0, 0, 640, 800);
        try{
            super.paint(gBuffer);
            gBuffer.setColor(Color.black);
            gBuffer.fillRect(0,0,width,height);
            time2 = time1;
            time1 = System.currentTimeMillis();
            if(play){
                time_all += time1 - time2;
                update();
            }
            for(int i = trails.size()-1;i >= 0;i--){
                Trail trail = trails.get(i);
                if(show_trail)
                    trail.show(gBuffer);
                trail.t --;
                if(trail.t < 1)
                    trails.remove(i);
            }
            for(Body body:stars)
                body.show(gBuffer);
            if(!hide_all){
                gBuffer.setColor(Color.BLACK);
                gBuffer.fillRect(0,0,160,110);
                if(show_keys)
                    gBuffer.fillRect(0,110,160,90);
                gBuffer.setColor(Color.YELLOW);
                gBuffer.drawString("剩余天体数:" + stars.size(),10,50);
                gBuffer.drawString("主恒星质量比:" + (int)(main_star.mass * 100 / total_mass) + "%",10,65);
                gBuffer.drawString("逝去时间:" + show_time(),10,80);
                gBuffer.drawString("[H]" + (show_keys ? "隐藏": "显示") + "快捷键提示",10,95);
                if(show_keys){
                    gBuffer.drawString("[Space]" + (play ? "暂停": "继续"),10,110);
                    gBuffer.drawString("[C]" + "清除轨迹",10,125);
                    gBuffer.drawString("[M]" + (show_msg ? "隐藏": "显示") + "碰撞信息",10,140);
                    gBuffer.drawString("[Q]隐藏信息栏",10,155);
                    gBuffer.drawString("[X]" + (show_trail ? "隐藏": "显示") + "轨迹",10,170);
                    gBuffer.drawString("[Z]" + (show_content ? "隐藏": "显示") + "碰撞信息",10,185);
                }
            }
            if(show_msg){
                gBuffer.setColor(Color.BLACK);
                gBuffer.fillRect(0,height - 60,480,100);
                gBuffer.setColor(Color.YELLOW);
                gBuffer.drawString(msg.get(2),10,height - 50);
                gBuffer.drawString(msg.get(1),10,height - 35);
                gBuffer.drawString(msg.get(0),10,height - 20);
            }

            g.drawImage(iBuffer, 0, 0, this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void update(){
        ArrayList<Crash> crashes = new ArrayList<>();
        for(int j = 0;j < stars.size(); j++){
            Body body = stars.get(j);
            for(int i = j + 1; i < stars.size(); i++){
                Body other = stars.get(i);
                V2d dir = new V2d(body.pos.x - other.pos.x,body.pos.y - other.pos.y);
                if(dir.mag()<=body.r+other.r){//here
                    if(body.mass > other.mass)
                        crashes.add(new Crash(body.id,other.id));
                    else
                        crashes.add(new Crash(other.id,body.id));
                    continue;
                }

                float forcePower = G *(body.mass * other.mass)/dir.magSq();
                V2d force = V2d.mult(dir.normalize(),forcePower);
                other.vel.add(V2d.mult((V2d.div(force,other.mass)),(1f / FPS0)));
                body.vel.sub(V2d.mult((V2d.div(force,body.mass)),(1f / FPS0)));
            }
        }
        int index = 0;
        outer:
        while (crashes.size()>0){
            Crash c = crashes.get(index);
            for(Crash crash:crashes){
                if(c != crash && crash.s_id == c.l_id){
                    index ++;
                    continue outer;
                }
            }
            Body larger = getBodyById(c.l_id);
            Body smaller = getBodyById(c.s_id);
            assert larger != null;
            assert smaller != null;
            msg.add(c.l_id + "号天体(" + larger.mass +")吞噬了" + c.s_id + "号天体(" + smaller.mass +")");
            msg.remove(0);
            try{
                larger.vel = V2d.div(V2d.add(V2d.mult(larger.vel,larger.mass),
                        V2d.mult(smaller.vel,smaller.mass)),
                        larger.mass+smaller.mass);
                larger.mass += smaller.mass;
                crashes.remove(c);
                stars.remove(smaller);
                index = 0;
            }catch (NullPointerException ignored){}
        }


        for(Body body:stars)
            body.vel.sub(main_star.vel);
        for(Body body:stars){
            if(show_trail)
                trails.add(new Trail(body.pos,V2d.add(body.pos,V2d.div(body.vel,FPS0)),body.col));
            body.pos.add(V2d.div(body.vel,FPS0));
        }
    }
    private Body getBodyById(int i){
        for(Body body:stars)
            if(body.id == i)
                return body;
            return null;
    }
    private class KL implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == 32)//[Space]继续/暂停
                play = !play;
            else if(e.getKeyCode() == 82)//[R]重新开始
                init();
            else if(e.getKeyCode() == 67)//[C]清除轨迹
                trails = new ArrayList<>();
            else if(e.getKeyCode() == 88){//[X]显隐轨迹
                show_trail = !show_trail;
                if(!show_trail)
                    trails = new ArrayList<>();
            }else if(e.getKeyCode() == 90)//[Z]显隐天体信息
                show_content = !show_content;
            else if(e.getKeyCode() == 72)//[H]显隐快捷键
                show_keys = !show_keys;
            else if(e.getKeyCode() == 77)//[M]显隐碰撞信息
                show_msg = !show_msg;
            else if(e.getKeyCode() == 81)//[Q]显隐信息栏
                hide_all = !hide_all;
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
    private String show_time(){
        String r;
        int time = (int)time_all / 1000;
        r = to_two_digits(time % 60);
        time = time / 60;
        r = to_two_digits(time % 60) + ":" + r;
        time = time / 60;
        r = to_two_digits(time % 60) + ":" + r;
        return r;
    }
    private String to_two_digits(int i){
        if(i < 10)
            return "0" + i;
        else
            return "" + i;
    }
}
