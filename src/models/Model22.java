package models;

import annotations.Bind;
import interfaces.IModel;

public class Model22 implements IModel {
    @Bind
    private int LL; //number of years
    @Bind
    private double[] twKI; // the growth rate of private consumption
    @Bind
    private double[] twKS; // the growth rate of public consumption
    @Bind
    private double[] twINW; // investment growth
    @Bind
    private double[] twEKS; // export growth
    @Bind
    private double[] twIMP; // import growth

    @Bind
    private double[] KI; //private consumption
    @Bind
    private double[] KS; // public consumption
    @Bind
    private double[] INW; // investments
    @Bind
    private double[] EKS; // export
    @Bind
    private double[] IMP; // import
    @Bind
    private double[] PKB; //GDP


    private double temp; // this field is not associated with the data model or with the results

    public Model22() {
    }

    public void run() {
        for (int t = 1; t < LL; t++) {
            KI[t] = 1+t;
            KS[t] = 2+t;
            INW[t] = 3+t;
            EKS[t] = EKS[t]/1000;
            IMP[t] = IMP[t]/10000;
            PKB[t] = PKB[t]/3000;
        }
    }
}
