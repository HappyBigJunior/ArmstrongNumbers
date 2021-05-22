package Armstrong;

import java.util.*;

public class ArmstrongNumbers {
    private static final long[][] digitsExponentiationTable = new long[11][20]; // digitsExponentiationTable[i][j] = Math.pow(i, j)
    private static final int[] digitsCount = new int[10];
    private static long numberWithAscendingDigits; // examples: "123", "1122225669"; examples of increments: 13 -> 14 .. 128 -> 129, 129 -> 133, 133 -> 134 ...
    private static int numberLength = 1; // count of digits in numberWithAscendingDigits

    private static List<Long> getArmstrongNumbers(long numbersBorder) {
        List<Long> numbersList = new ArrayList<>();
        Set<Long> numbersSet = new HashSet<>(); // does not have duplicates
        numbersSet.add(0L);

        final long maxAllowedNumber = Math.min(numbersBorder, 8_999_999_999_999_999_999L);

        while (numberWithAscendingDigits < maxAllowedNumber) {
            getDigitAfterIncrementInSpecifiedPlace(1); // returning digit is used for recursion only

            int maxAdditionalZerosCount = switch (numberLength) {
                case 1, 19 -> 0;
                case 2, 3, 4, 18 -> 1;
                case 5, 6, 7, 8, 9, 17 -> 2;
                default -> 3;
            };

            for (int i = 0; i <= maxAdditionalZerosCount; i++) { // 123, 1230, 12300, 123000
                numbersSet.add(getArmstrongNumberOrZero(numberWithAscendingDigits, i));
            }
        }

        for (Long n : numbersSet) {
            if (n <= numbersBorder) {
                numbersList.add(n);
            }
        }

        Collections.sort(numbersList);

        return numbersList;
    }

    private static long getDigitAfterIncrementInSpecifiedPlace(int digitPlaceFromEnd) {
        long coveringTailMask = digitsExponentiationTable[10][digitPlaceFromEnd - 1];
        long digit = numberWithAscendingDigits / coveringTailMask % 10;

        if (digit < 9) {
            numberWithAscendingDigits += coveringTailMask;

            return digit + 1;
        }

        if (digitPlaceFromEnd + 1 > numberLength) {
            numberLength = digitPlaceFromEnd + 1;
        }

        long previousDigit = getDigitAfterIncrementInSpecifiedPlace(digitPlaceFromEnd + 1);
        numberWithAscendingDigits -= (9 - previousDigit) * coveringTailMask;

        return previousDigit;
    }

    private static long getArmstrongNumberOrZero(long inputNumber, int additionalZerosCount) {
        int numberLength = ArmstrongNumbers.numberLength + additionalZerosCount;
        long number = inputNumber * digitsExponentiationTable[10][additionalZerosCount];
        Arrays.fill(digitsCount, 0);
        long multipliedDigitsSum = 0;

        while (number > 0) {
            int lastDigitInNumber = (int) (number % 10);
            number /= 10;
            digitsCount[lastDigitInNumber]++;
            multipliedDigitsSum += digitsExponentiationTable[lastDigitInNumber][numberLength];
        }

        long number2 = multipliedDigitsSum;

        while (number2 > 0) {
            int lastDigitInNumber = (int) (number2 % 10);
            number2 /= 10;
            digitsCount[lastDigitInNumber]--;
        }

        for (int j : digitsCount) {
            if (j != 0) {
                return 0; // not equivalent in composition in digits
            }
        }

        return multipliedDigitsSum; // inputNumber == 135 -> multipliedDigitsSum = (5 * 5 * 5) + (3 * 3 * 3) + (1 * 1 * 1) = 153 this is Armstrong number
    }

    public static void main(String[] args) {
        long numbersBorder = Long.MAX_VALUE;
        long startTimeInMillisecond = System.currentTimeMillis();

        for (int i = 1; i <= 10; i++) {
            digitsExponentiationTable[i][0] = 1;

            for (int j = 1; j < 20; j++) {
                digitsExponentiationTable[i][j] = i;

                for (int k = 1; k < j; k++) { // Math.pow no good for long
                    digitsExponentiationTable[i][j] *= i;
                }
            }
        }

        List<Long> numbersList = getArmstrongNumbers(numbersBorder);
        System.out.printf("Количество найденых чисел Армстронга: %d%n", numbersList.size());
        System.out.println(numbersList);

        final double divisorForConvertingToSeconds = 1000.0;
        double executionTimeInSeconds = (System.currentTimeMillis() - startTimeInMillisecond) / divisorForConvertingToSeconds;
        System.out.printf("Время исполнения = %.2f сек.%n", executionTimeInSeconds);
    }
}
