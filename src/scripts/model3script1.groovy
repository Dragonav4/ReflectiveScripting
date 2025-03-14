package scripts

// Modify the inflation rate
for (i = 0; i < LL; i++) {
    inflationRate[i] += 0.5; // Increase inflation rate by 0.5%
}

// Recalculate real GDP with updated inflation rates
for (i = 0; i < LL; i++) {
    realGDP[i] = baseGDP[i] * (1 - inflationRate[i] / 100);
}