package models;

import annotations.Bind;
import interfaces.IModel;

public class Model3 implements IModel {
    @Bind
    private int LL; //number of years
    @Bind
    private double[] ZDEKS; // export


    public Model3() {
    }

    public void run() {
        for (int t = 1; t < LL; t++) {
            ZDEKS[t] = t+1000;
        }
    }
}
