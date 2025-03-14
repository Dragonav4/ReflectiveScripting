package models;

import annotations.Bind;
import interfaces.IModel;

public class Model2 implements IModel {
    @Bind private int LL; // number of years
    @Bind private double[] savingsRate; // savings rate for each year
    @Bind private double[] GDP; // Gross Domestic Product
    @Bind private double[] consumption; // consumption for each year

    @Bind private double[] savings; // savings for each year
    @Bind private double[] investments; // investments for each year

    public Model2() {}

    public void run() {
        // Initialize derived arrays
        savings = new double[LL];
        investments = new double[LL];

        // Calculate savings and investments
        for (int t = 0; t < LL; t++) {
            savings[t] = GDP[t] * savingsRate[t];
            investments[t] = GDP[t] - savings[t];
        }
    }
}
