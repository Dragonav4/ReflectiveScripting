package scripts
savingsToInvestmentRatio = new double[LL];
averageRatio = new double[LL];
for (i = 0; i < LL; i++) {
    savingsToInvestmentRatio[i] = savings[i] / investments[i];
    averageRatio[i] = savingsToInvestmentRatio.sum() / LL;
}

