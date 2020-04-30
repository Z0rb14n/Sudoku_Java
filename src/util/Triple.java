/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author adminasaurus
 */
public class Triple {
    protected byte num;
    protected int row;
    protected int col;
    protected int x;
    protected int y;
    protected Triple(){
    }
    public Triple(byte n, int x, int y){
        num = n;
        row = x;
        this.x = x;
        col = y;
        this.y = y;
    }
    public byte getNum(){return num;}
    public void setNum(byte l){this.num = l;}
    public int getRow(){return row;}
    public void setRow(int l){this.row = l;}
    public int getCol(){return col;}
    public void setCol(int l){this.col = l;}
    public int getX(){return x;}
    public void setX(int l){this.x = l;}
    public int getY(){return y;}
    public void setY(int l){this.y = l;}
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Triple)) return false;
        Triple lol = (Triple) o;
        return lol.num == num && lol.row == row && lol.col == col && lol.x == x && lol.y == y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.num;
        hash = 13 * hash + this.row;
        hash = 13 * hash + this.col;
        hash = 13 * hash + this.x;
        hash = 13 * hash + this.y;
        return hash;
    }
}
