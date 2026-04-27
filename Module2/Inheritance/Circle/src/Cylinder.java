public class Cylinder extends Circle {
    private float height;

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getVolume() {
        return getArea() * height;
    }

     @Override
     public String toString() {
         return super.toString();
     }

}
