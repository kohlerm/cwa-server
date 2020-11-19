package app.coronawarn.server.services.distribution.statistics.keyfigurecard.factory;

import static java.time.ZoneOffset.UTC;

import app.coronawarn.server.common.protocols.internal.stats.CardHeader;
import app.coronawarn.server.common.protocols.internal.stats.KeyFigureCard;
import app.coronawarn.server.services.distribution.statistics.StatisticsJsonStringObject;
import app.coronawarn.server.services.distribution.statistics.keyfigurecard.ValueTrendCalculator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class HeaderCardFactory {

  protected final ValueTrendCalculator valueTrendCalculator;

  protected HeaderCardFactory(ValueTrendCalculator valueTrendCalculator) {
    this.valueTrendCalculator = valueTrendCalculator;
  }

  /**
   * Create KeyFigureCard object. Calls the children method `buildKeyFigureCard` for card specific
   * properties. This method adds the generic CardHeader that all KeyFigureCards must have.
   *
   * @param stats JSON Object statistics
   * @return KeyFigureCard .
   */
  public KeyFigureCard makeKeyFigureCard(StatisticsJsonStringObject stats) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate dateTime = LocalDate.parse(stats.getEffectiveDate(), formatter);
    KeyFigureCard.Builder keyFigureBuilder = KeyFigureCard.newBuilder()
        .setHeader(CardHeader.newBuilder()
            .setCardId(this.getCardId())
            .setUpdatedAt(dateTime.atStartOfDay(UTC).toEpochSecond())
            .build()
        );

    return this.buildKeyFigureCard(stats, keyFigureBuilder);
  }

  protected abstract Integer getCardId();

  protected abstract KeyFigureCard buildKeyFigureCard(StatisticsJsonStringObject stats,
      KeyFigureCard.Builder keyFigureBuilder);

}
