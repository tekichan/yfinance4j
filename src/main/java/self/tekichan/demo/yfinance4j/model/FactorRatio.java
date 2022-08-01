package self.tekichan.demo.yfinance4j.model;

import java.math.BigDecimal;

/**
 * Factor ratio representation
 * <p>
 *     e.g. 1:3
 * </p>
 * @author Teki Chan
 * @since 1 Jul 2022
 * @param leftValue     Left value of factor ratio
 * @param rightValue    Right value of factor ratio
 */
public record FactorRatio(
        BigDecimal leftValue
        , BigDecimal rightValue
){
}
