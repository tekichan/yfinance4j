package self.tekichan.demo.yfinance4j.model;

import java.math.BigDecimal;

/**
 * A combination of BigDecimal and ScaleUnit
 * <p>
 *     e.g. 1.35B = 1.35 billion
 * </p>
 *
 * @author  Teki Chan
 * @since   1 Jul 2022
 * @param baseValue     Base numeric Value of the combined value
 * @param scaleUnit     Scale Unit of the combined value
 */
public record BigDecimalAndUnit(
        BigDecimal baseValue,
        ScaleUnit scaleUnit
) {
}
