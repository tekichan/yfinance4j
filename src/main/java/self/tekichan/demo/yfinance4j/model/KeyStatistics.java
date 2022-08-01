package self.tekichan.demo.yfinance4j.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Key Statistics of Stock
 * @author Teki Chan
 * @since 1 Jul 2022
 * @param symbol            Stock symbol
 * @param downloadDateTime  Date/Time of downloading the key statistics
 * @param companyName   Company Name
 * @param stockExchange Stock exchange (abbr)
 * @param currencyCode  Currency code used by the stock
 * @param marketCap Market Cap (intraday)
 * @param enterpriseValue   Enterprise Value
 * @param trailingPe    Trailing P/E
 * @param forwardPe Forward P/E
 * @param pegRatio  PEG Ratio (5 yr expected)
 * @param priceSalesRatio   Price/Sales (Trailing Twelve Months)
 * @param priceBookRatio    Price/Book (Most Recent Quarter)
 * @param enterpriseValueRevenueRatio   Enterprise Value/Revenue
 * @param enterpriseValueEbitaRatio     Enterprise Value/EBITDA
 * @param fiscalYearEnds    Fiscal Year Ends
 * @param mostRecentQuarter Most Recent Quarter (Most Recent Quarter)
 * @param profitMarginPercent   Profit Margin
 * @param operatingMarginPercent    Operating Margin (Trailing Twelve Months)
 * @param returnOnAssetsPercent Return on Assets (Trailing Twelve Months)
 * @param returnOnEquityPercent Return on Equity (Trailing Twelve Months)
 * @param revenue   Revenue (Trailing Twelve Months)
 * @param revenuePerShare   Revenue Per Share (Trailing Twelve Months)
 * @param quarterlyRevenueGrowthPercent Quarterly Revenue Growth (Year Over Year)
 * @param grossProfit   Gross Profit (Trailing Twelve Months)
 * @param ebitda    EBITDA
 * @param netIncomeAviToCommon  Net Income Avi to Common (Trailing Twelve Months)
 * @param dilutedEps    Diluted EPS (Trailing Twelve Months)
 * @param quarterlyEarningsGrowthPercent    Quarterly Earnings Growth (Year Over Year)
 * @param totalCash Total Cash (Most Recent Quarter)
 * @param totalCashPerShare Total Cash Per Share (Most Recent Quarter)
 * @param totalDebt Total Debt (Most Recent Quarter)
 * @param totalDebtEquityRatio  Total Debt/Equity (Most Recent Quarter)
 * @param currentRatio  Current Ratio (Most Recent Quarter)
 * @param bookValuePerShare Book Value Per Share (Most Recent Quarter)
 * @param operatingCashFlow Operating Cash Flow (Trailing Twelve Months)
 * @param leveredFreeCashFlow   Levered Free Cash Flow (Trailing Twelve Months)
 * @param beta  Beta (5Y Monthly)
 * @param fiftyTwoWeekChangePercent 52-Week Change (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param snp50052WeekChangePercent SnP500 52-Week Change (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param fiftyTwoWeekHigh  52 Week High (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param fiftyTwoWeekLow   52 Week Low (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param fiftyDayMovingAverage 50-Day Moving Average (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param twoHundredDayMovingAverage    200-Day Moving Average (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param avgVol3Month  Avg Vol (3 month) (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param avgVol10Day   Avg Vol (10 day) (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param sharesOutstanding Shares Outstanding (Shares outstanding is taken from the most recently filed quarterly or annual report and Market Cap is calculated using shares outstanding.)
 * @param impliedSharesOutstanding  Implied Shares Outstanding (Implied Shares Outstanding of common equityassuming the conversion of all convertible subsidiary equity into common.)
 * @param sharesFloat   Float (A company's float is a measure of the number of shares available for trading by the public. It's calculated by taking the number of issued and outstanding shares minus any restricted stockwhich may not be publicly traded.)
 * @param heldByInsidersPercent Percentage Held by Insiders
 * @param heldByInstitutions    Percentage Held by Institutions
 * @param sharesShort   Shares Short
 * @param shortRatio    Short Ratio
 * @param shortPercentOfFloat   Short percentage of Float
 * @param sharesOutstandingShortPercent Short percentage of Shares Outstanding
 * @param sharesShortPriorMonth Shares Short Prior Month
 * @param forwardAnnualDividendRate Forward Annual Dividend Rate
 * @param forwardAnnualDividendYieldPercent Forward Annual Dividend Yield
 * @param trailingAnnualDividendRate    Trailing Annual Dividend Rate (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param trailingAnnualDividendYieldPercent    Trailing Annual Dividend Yield (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param fiveYearAverageDividendYieldPercent   5 Year Average Dividend Yield
 * @param payoutRatioPercent    Payout Ratio
 * @param dividendDate  Dividend Date (Data derived from multiple sources or calculated by Yahoo Finance.)
 * @param exDividendDate    Ex-Dividend Date
 * @param lastSplitFactor   Last Split Factor
 * @param lastSplitDate Last Split Date
 */
public record KeyStatistics(
        // Identity Information of Stock
        /** Stock symbol */
        String symbol,
        /** Date/Time of downloading the key statistics */
        LocalDateTime downloadDateTime,
        /** Company Name */
        String companyName,
        /** Stock exchange (abbr) */
        String stockExchange,
        /** Currency code used by the stock */
        String currencyCode,
        // Valuation Measures
        /** Market Cap (intraday) */
        BigDecimalAndUnit marketCap,
        /** Enterprise Value */
        BigDecimalAndUnit enterpriseValue,
        /** Trailing P/E */
        BigDecimal trailingPe,
        /** Forward P/E */
        BigDecimal forwardPe,
        /** PEG Ratio (5 yr expected) */
        BigDecimal pegRatio,
        /** Price/Sales (Trailing Twelve Months) */
        BigDecimal priceSalesRatio,
        /** Price/Book (Most Recent Quarter) */
        BigDecimal priceBookRatio,
        /** Enterprise Value/Revenue */
        BigDecimal enterpriseValueRevenueRatio,
        /** Enterprise Value/EBITDA */
        BigDecimal enterpriseValueEbitaRatio,
        // Fiscal Year
        /** Fiscal Year Ends */
        LocalDate fiscalYearEnds,
        /** Most Recent Quarter (mrq) */
        LocalDate mostRecentQuarter,
        // Profitability
        /** Profit Margin */
        BigDecimal profitMarginPercent,
        /** Operating Margin (Trailing Twelve Months) */
        BigDecimal operatingMarginPercent,
        // Management Effectiveness
        /** Return on Assets (Trailing Twelve Months) */
        BigDecimal returnOnAssetsPercent,
        /** Return on Equity (Trailing Twelve Months) */
        BigDecimal returnOnEquityPercent,
        // Income Statement
        /** Revenue (Trailing Twelve Months) */
        BigDecimalAndUnit revenue,
        /** Revenue Per Share (Trailing Twelve Months) */
        BigDecimal revenuePerShare,
        /** Quarterly Revenue Growth (Year Over Year) */
        BigDecimal quarterlyRevenueGrowthPercent,
        /** Gross Profit (Trailing Twelve Months) */
        BigDecimalAndUnit grossProfit,
        /** EBITDA */
        BigDecimalAndUnit ebitda,
        /** Net Income Avi to Common (Trailing Twelve Months) */
        BigDecimalAndUnit netIncomeAviToCommon,
        /** Diluted EPS (Trailing Twelve Months) */
        BigDecimal dilutedEps,
        /** Quarterly Earnings Growth (Year Over Year) */
        BigDecimal quarterlyEarningsGrowthPercent,
        // Balance Sheet
        /** Total Cash (Most Recent Quarter) */
        BigDecimalAndUnit totalCash,
        /** Total Cash Per Share (Most Recent Quarter) */
        BigDecimal totalCashPerShare,
        /** Total Debt (Most Recent Quarter) */
        BigDecimalAndUnit totalDebt,
        /** Total Debt/Equity (Most Recent Quarter) */
        BigDecimal totalDebtEquityRatio,
        /** Current Ratio (Most Recent Quarter) */
        BigDecimal currentRatio,
        /** Book Value Per Share (Most Recent Quarter) */
        BigDecimal bookValuePerShare,
        // Cash Flow Statement
        /** Operating Cash Flow (Trailing Twelve Months) */
        BigDecimalAndUnit operatingCashFlow,
        /** Levered Free Cash Flow (Trailing Twelve Months) */
        BigDecimalAndUnit leveredFreeCashFlow,
        // Stock Price History
        /** Beta (5Y Monthly) */
        BigDecimal beta,
        /** 52-Week Change (Data derived from multiple sources or calculated by Yahoo Finance.) */
        BigDecimal fiftyTwoWeekChangePercent,
        /** S&P500 52-Week Change (Data derived from multiple sources or calculated by Yahoo Finance.) */
        BigDecimal snp50052WeekChangePercent,
        /** 52 Week High (Data derived from multiple sources or calculated by Yahoo Finance.) */
        BigDecimal fiftyTwoWeekHigh,
        /** 52 Week Low (Data derived from multiple sources or calculated by Yahoo Finance.) */
        BigDecimal fiftyTwoWeekLow,
        /** 50-Day Moving Average (Data derived from multiple sources or calculated by Yahoo Finance.) */
        BigDecimal fiftyDayMovingAverage,
        /** 200-Day Moving Average (Data derived from multiple sources or calculated by Yahoo Finance.) */
        BigDecimal twoHundredDayMovingAverage,
        // Share Statistics
        /** Avg Vol (3 month) (Data derived from multiple sources or calculated by Yahoo Finance.) */
        BigDecimalAndUnit avgVol3Month,
        /** Avg Vol (10 day) (Data derived from multiple sources or calculated by Yahoo Finance.) */
        BigDecimalAndUnit avgVol10Day,
        /** Shares Outstanding (Shares outstanding is taken from the most recently filed quarterly or annual report and Market Cap is calculated using shares outstanding.) */
        BigDecimalAndUnit sharesOutstanding,
        /** Implied Shares Outstanding (Implied Shares Outstanding of common equityassuming the conversion of all convertible subsidiary equity into common.) */
        BigDecimalAndUnit impliedSharesOutstanding,
        /** Float (A company's float is a measure of the number of shares available for trading by the public. It's calculated by taking the number of issued and outstanding shares minus any restricted stockwhich may not be publicly traded.) */
        BigDecimalAndUnit sharesFloat,
        /** % Held by Insiders */
        BigDecimal heldByInsidersPercent,
        /** % Held by Institutions */
        BigDecimal heldByInstitutions,
        /** Shares Short */
        BigDecimalAndUnit sharesShort,
        /** Short Ratio */
        BigDecimal shortRatio,
        /** Short % of Float */
        BigDecimal shortPercentOfFloat,
        /** Short % of Shares Outstanding */
        BigDecimal sharesOutstandingShortPercent,
        /** Shares Short Prior Month */
        BigDecimalAndUnit sharesShortPriorMonth,
        // Dividends & Splits
        /** Forward Annual Dividend Rate */
        BigDecimal forwardAnnualDividendRate,
        /** Forward Annual Dividend Yield */
        BigDecimal forwardAnnualDividendYieldPercent,
        /** Trailing Annual Dividend Rate (Data derived from multiple sources or calculated by Yahoo Finance.) */
        BigDecimal trailingAnnualDividendRate,
        /** Trailing Annual Dividend Yield (Data derived from multiple sources or calculated by Yahoo Finance.) */
        BigDecimal trailingAnnualDividendYieldPercent,
        /** 5 Year Average Dividend Yield */
        BigDecimal fiveYearAverageDividendYieldPercent,
        /** Payout Ratio */
        BigDecimal payoutRatioPercent,
        /** Dividend Date (Data derived from multiple sources or calculated by Yahoo Finance.) */
        LocalDate dividendDate,
        /** Ex-Dividend Date */
        LocalDate exDividendDate,
        /** Last Split Factor */
        FactorRatio lastSplitFactor,
        /** Last Split Date */
        LocalDate lastSplitDate
) implements IStockQuote {
    @Override
    public String getDescription() {
        return "%1$s - %2$s".formatted(this.symbol, this.companyName);
    }
}
