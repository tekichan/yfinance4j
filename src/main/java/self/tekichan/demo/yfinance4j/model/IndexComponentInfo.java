package self.tekichan.demo.yfinance4j.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Collection of all Index components information
 * @author Teki Chan
 * @since 1 Jul 2022
 * @param symbol    Index symbol
 * @param downloadDateTime  Date/time of downloading the information
 * @param currencyCode      Currency code
 * @param componentList     List of stock components
 */
public record IndexComponentInfo(
        String symbol
        , LocalDateTime downloadDateTime
        , String currencyCode
        , List<IndexComponent> componentList
){
}
