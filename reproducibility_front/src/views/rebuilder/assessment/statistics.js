export function calculatePCorrelationCoefficient(initialArry, reproducedArry) {
    let PCorrelationCoefficient = ss.sampleCorrelation(
        initialArry,
        reproducedArry
    );
    console.log(PCorrelationCoefficient);
    return PCorrelationCoefficient;
}
