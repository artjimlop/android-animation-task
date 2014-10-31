package domain;


import com.google.gson.annotations.SerializedName;

public class Square {

    @SerializedName("x")
    int mX;
    @SerializedName("y")
    int mY;
    @SerializedName("colour")
    String mColour;
    @SerializedName("size")
    int mSize;

    public Square(){

    }

    public Square(int x, int y, String colour, int size){
        mX = x;
        mY = y;
        mColour = colour;
        mSize = size;
    }

    public int getX(){
        return this.mX;
    }

    public int getY(){
        return this.mY;
    }

    public String getColour(){
        return this.mColour;
    }

    public int getSize(){
        return this.mSize;
    }

    public void setX(int x) {
        mX = x;
    }

    public void setY(int y) {
        mY = y;
    }

    public void setColour(String colour) {
        mColour = colour;
    }

    public void setSize(int size) {
        mSize = size;
    }
}
