package com.hubaccess.financial;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class FplCalculationService {

    // 2025 Federal Poverty Level guidelines (48 contiguous states)
    private static final Map<Integer, BigDecimal> FPL_2025 = Map.ofEntries(
        Map.entry(1, new BigDecimal("15650")),
        Map.entry(2, new BigDecimal("21150")),
        Map.entry(3, new BigDecimal("26650")),
        Map.entry(4, new BigDecimal("32150")),
        Map.entry(5, new BigDecimal("37650")),
        Map.entry(6, new BigDecimal("43150")),
        Map.entry(7, new BigDecimal("48650")),
        Map.entry(8, new BigDecimal("54150"))
    );
    private static final BigDecimal PER_ADDITIONAL = new BigDecimal("5550");

    public BigDecimal calculateFplPercentage(BigDecimal annualIncome, int householdSize) {
        if (householdSize < 1) throw new IllegalArgumentException("Household size must be >= 1");
        if (annualIncome == null || annualIncome.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Annual income must be >= 0");
        }

        BigDecimal fplAmount;
        if (householdSize <= 8) {
            fplAmount = FPL_2025.get(householdSize);
        } else {
            fplAmount = FPL_2025.get(8).add(PER_ADDITIONAL.multiply(BigDecimal.valueOf(householdSize - 8)));
        }

        return annualIncome.divide(fplAmount, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP);
    }
}
