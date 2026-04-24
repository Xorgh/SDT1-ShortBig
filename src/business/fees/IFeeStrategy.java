package business.fees;

public interface IFeeStrategy {
  double calculate(double pricePerShare, int numberOfShares);
}
