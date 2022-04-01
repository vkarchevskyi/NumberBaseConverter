import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    private static String decToBase(final BigDecimal number, final int base) {
        final StringBuilder result = new StringBuilder();

        BigInteger num = new BigInteger(number.toBigInteger().toString());
        while (num.compareTo(BigInteger.ONE) >= 0) { // number >= 1
            BigInteger remainder = num.remainder(BigInteger.valueOf(base));
            if (remainder.compareTo(BigInteger.TEN) >= 0) { // remainder >= 10
                result.append((char) remainder.add(BigInteger.valueOf('a' - 10)).intValue()); // (char) (remainder - 10 + 'A')
            } else {
                result.append(remainder);
            }
            num = num.divide(BigInteger.valueOf(base));
        }

        String fractPartStr = number.toPlainString().replaceFirst(number.toBigInteger().toString(), "");
        if (!"".equals(fractPartStr)) {
            BigDecimal fractionalPart = new BigDecimal(fractPartStr);
            result.reverse();
            if ("".equals(result.toString())) {
                result.append('0');
            }
            result.append('.');

            int digitsAfterComa = 0;

            while (digitsAfterComa < 5 && !"0".equals(fractionalPart.stripTrailingZeros().toString())) {
                fractionalPart = fractionalPart.multiply(BigDecimal.valueOf(base));
                BigInteger integerPart = fractionalPart.toBigInteger();
                if (integerPart.compareTo(BigInteger.valueOf(10)) >= 0) {
                    result.append((char) integerPart.add(BigInteger.valueOf('a' - 10)).intValue());
                } else {
                    result.append(integerPart);
                }
                digitsAfterComa++;
                fractionalPart = fractionalPart.subtract(new BigDecimal(integerPart));
            }

            while (digitsAfterComa < 5) {
                result.append("0");
                digitsAfterComa++;
            }

            return result.toString();
        } else {
            return result.reverse().toString();
        }
    }

    private static String toDec(final String number, final int base) {
        BigDecimal dec = BigDecimal.ZERO;
        boolean fractions = false;

        int index = number.indexOf(".");
        int power = (index == -1) ? (number.length() - 1) : (index - 1);

        for (int i = 0, len = number.length(); i < len; i++, power--) {

            char character = number.charAt(i);
            int num;
            if (character >= 'A' && character <= 'Z') {
                num = 10 + (character - 'A');
            } else if (character >= '0' && character <= '9') {
                num = character - '0';
            } else if (character == '.') {
                fractions = true;
                power = 0;
                continue;
            } else {
                return "-1";
            }

            BigDecimal baseNum;
            if (fractions) {
                baseNum = BigDecimal.ONE.divide(BigDecimal.valueOf(Math.pow(base, -power)), 12, RoundingMode.HALF_DOWN);
            } else {
                baseNum = BigDecimal.valueOf(base).pow(power);
            }

            dec = dec.add(BigDecimal.valueOf(num).multiply(baseNum));
        }
        return dec.stripTrailingZeros().toString();
    }

    private static String toBase(String number, int sourceBase, int targetBase) {
        BigDecimal dec = new BigDecimal(toDec(number, sourceBase));
        if (number.contains(".") && dec.scale() < 5) {
            dec = dec.setScale(5, RoundingMode.HALF_UP);
        }
        return decToBase(dec, targetBase);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        do {
            System.out.print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ");
            String[] inputStr = scanner.nextLine().strip().toUpperCase(Locale.US).replaceAll(" +", " ").split(" ");
            int sourceBase;
            int targetBase;

            try {
                if ("/EXIT".equals(inputStr[0])) {
                    break;
                }
                sourceBase = Integer.parseInt(inputStr[0]);
                targetBase = Integer.parseInt(inputStr[1]);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("You can type only two numbers or /exit command");
                continue;
            }

            do {
                System.out.printf("Enter number in base %d to convert to base %d (To go back type /back) ", sourceBase, targetBase);
                String input = scanner.nextLine().strip().toUpperCase(Locale.US);
                if ("/BACK".equals(input)) {
                    break;
                }
                System.out.printf("Conversion result: %s%n", toBase(input, sourceBase, targetBase));
            } while (true);
        } while (true);
    }
}
