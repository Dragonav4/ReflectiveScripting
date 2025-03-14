package scripts


cumulativeSavings = new double[LL];

// Calculate cumulative savings
for (i = 0; i < LL; i++) {
    if (i == 0) {
        cumulativeSavings[i] = savings[i];
    } else {
        cumulativeSavings[i] = cumulativeSavings[i - 1] + savings[i];
    }
}