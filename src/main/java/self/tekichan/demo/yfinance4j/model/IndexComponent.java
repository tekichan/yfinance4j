package self.tekichan.demo.yfinance4j.model;

import java.math.BigDecimal;

/**
 * Information of a component stock of a stock index
 * @author Teki Chan
 * @since 1 Jul 2022
 * @param symbol        Stock symbol
 * @param companyName   Company name
 * @param lastPrice     Last price
 * @param change        Price change
 * @param percentChange     Price change percentage
 * @param volume        Volume
 */
public record IndexComponent (
        String symbol
        , String companyName
        , BigDecimal lastPrice
        , BigDecimal change
        , BigDecimal percentChange
        , Long volume
){
    /**
     * Construct IndexComponent from String array
     * @param args  String array of IndexComponent parameters in order
     */
    public IndexComponent(String... args) {
        this(
                args[0]
                , args[1]
                , new BigDecimal(args[2])
                , new BigDecimal(args[3])
                , new BigDecimal(args[4])
                , Long.valueOf(args[5])
        );
    }
}
