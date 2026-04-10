package business.services.queries;

import java.time.LocalDateTime;

public enum PriceHistoryRange
{
  LAST_HOUR("Last Hour", () -> LocalDateTime.now().minusHours(1)),
  LAST_DAY("Last Day", () -> LocalDateTime.now().minusDays(1)),
  LAST_WEEK("Last Week", () -> LocalDateTime.now().minusWeeks(1)),
  ALL_TIME("All Time", () -> LocalDateTime.MIN);

  private final String label;
  private final java.util.function.Supplier<LocalDateTime> cutoff;

  PriceHistoryRange(String label, java.util.function.Supplier<LocalDateTime> cutoff)
  {
    this.label = label;
    this.cutoff = cutoff;
  }

  public String getLabel()
  {
    return label;
  }

  public LocalDateTime getCutoff()
  {
    return cutoff.get();
  }
}