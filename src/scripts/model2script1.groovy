package scripts

// Initialize savings-to-investment ratio array
savingsToInvestmentRatio = new double[LL];
averageRatio = new double[LL];

// Calculate the savings-to-investment ratio
for (i = 0; i < LL; i++) {
    savingsToInvestmentRatio[i] = savings[i] / investments[i];
    averageRatio[i] = savingsToInvestmentRatio.sum() / LL;
}

// Calculate the average ratio
