/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;

/**
 *
 * @author adminasaurus
 */
public class Pair {
    protected int row;
    protected int col;
    protected int x;
    protected int y;
    protected Pair(){
    }
    public Pair(ArrayList<Byte> lol){
        if (lol == null || lol.size() != 2) throw new IllegalArgumentException("Pair initializer called with invalid arraylist param.");
        row = lol.get(0);
        col = lol.get(1);
        x = lol.get(0);
        y = lol.get(1);
    }
    public Pair (int x, int y){
        row = x;
        col = y;
        this.x = x;
        this.y = y;
    }
    public int getRow(){return row;}
    public void setRow(int l){this.row = l;}
    public int getCol(){return col;}
    public void setCol(int l){this.col = l;}
    public int getX(){return x;}
    public void setX(int l){this.x = l;}
    public int getY(){return y;}
    public void setY(int l){this.y = l;}
    @Override
    public String toString(){
        return "(" + x + "," + y + ")";
    }
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Pair)) return false;
        Pair temp = (Pair) o;
        return temp.row == row && temp.col == col;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.row;
        hash = 67 * hash + this.col;
        hash = 67 * hash + this.x;
        hash = 67 * hash + this.y;
        return hash;
    }
}
