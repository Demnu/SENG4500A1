public class TaxInfo implements Comparable<TaxInfo> {
    private double startIncome;
    private double endIncome;
    private double taxRate;     
    private double fixedTax;   

    public TaxInfo(double startIncome, double endIncome, double taxRate, double fixedTax) {
        this.startIncome = startIncome;
        this.endIncome = endIncome;
        this.taxRate = taxRate;
        this.fixedTax = fixedTax;
    }

    // Getters and Setters
    public double getEndIncome() {
        return endIncome;
    }
    public double getStartIncome() {
        return startIncome;
    }
    public void setEndIncome(double higherBound) {
        this.endIncome = higherBound;
    }
    public void setStartIncome(double lowerBound) {
        this.startIncome = lowerBound;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getFixedTax() {
        return fixedTax;
    }

    public void setFixedTax(double fixedTax) {
        this.fixedTax = fixedTax;
    }

    @Override
    public int compareTo(TaxInfo other) {
        return Double.compare(this.startIncome, other.startIncome);
    }
}
