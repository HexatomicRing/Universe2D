public class V2d {
    float x,y;
    public V2d(float x,float y){
        this.x = x;
        this.y = y;
    }
    public float mag(){
        return (float) Math.sqrt(x*x + y*y);
    }
    public float magSq(){
        return x*x + y*y;
    }
    public static V2d mult(V2d v,float f){
        return new V2d(v.x * f,v.y * f);
    }
    public static V2d div(V2d v,float f){
        return new V2d(v.x / f,v.y / f);
    }
    public static V2d add(V2d v1,V2d v2){
        return new V2d(v1.x + v2.x,v1.y + v2.y);
    }
    public static V2d sub(V2d v1,V2d v2){
        return new V2d(v1.x - v2.x,v1.y - v2.y);
    }
    public void add(V2d v){
        x += v.x;
        y += v.y;
    }
    public void sub(V2d v){
        x -= v.x;
        y -= v.y;
    }
    public V2d normalize(){
        return div(this,mag());
    }
}
