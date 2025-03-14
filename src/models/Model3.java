package models;

import annotations.Bind;
import interfaces.IModel;

public class Model3 implements IModel {
    @Bind private int LL; // Number of years
    @Bind private double[] baseGDP; // Base GDP values for each year
    @Bind private double[] inflationRate; // Inflation rate (modifiable by the script)

    @Bind private double[] realGDP; // Real GDP after applying inflation

    public Model3() {}
    public void run() {
        for (int t = 0; t < LL; t++) {
            realGDP[t] = baseGDP[t] * (1 - inflationRate[t] / 100);
        }
    }
}