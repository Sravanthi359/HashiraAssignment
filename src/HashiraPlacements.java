import java.io.*;
import java.math.BigInteger;
import java.util.*;
import com.google.gson.*;

public class HashiraPlacements {

    public static void main(String[] args) throws Exception {
        
        if (args.length < 1) {
            System.out.println("Usage: java HashiraPlacements <input-file.json>");
            return;
        }

        String filename = args[0];
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        String jsonInput = sb.toString();

        JsonObject root = JsonParser.parseString(jsonInput).getAsJsonObject();

        JsonObject keys = root.getAsJsonObject("keys");
        int n = keys.get("n").getAsInt();
        int k = keys.get("k").getAsInt();

        List<BigInteger> xVals = new ArrayList<>();
        List<BigInteger> yVals = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            String key = Integer.toString(i);
            if (!root.has(key)) continue;

            JsonObject obj = root.getAsJsonObject(key);
            BigInteger y = decodeValue(obj);

            xVals.add(BigInteger.valueOf(i));
            yVals.add(y);
        }

        BigInteger constantTerm = lagrangeAtZero(xVals.subList(0, k), yVals.subList(0, k));
        String resultStr = constantTerm.toString();
        char lastDigit = resultStr.charAt(resultStr.length() - 1);
        int lastDigitInt = lastDigit - '0'; 
        System.out.println(lastDigitInt);
    }

    static BigInteger decodeValue(JsonObject obj) {
        String baseStr = obj.get("base").getAsString();
        String valStr = obj.get("value").getAsString();
        int base = Integer.parseInt(baseStr);
        return new BigInteger(valStr, base);
    }

    static BigInteger lagrangeAtZero(List<BigInteger> x, List<BigInteger> y) {
        int n = x.size();
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < n; i++) {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            BigInteger xi = x.get(i);

            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                BigInteger xj = x.get(j);
                numerator = numerator.multiply(xj.negate());
                denominator = denominator.multiply(xi.subtract(xj));
            }

            BigInteger absDenominator = denominator.abs();
            if (!numerator.mod(absDenominator).equals(BigInteger.ZERO)) {
                throw new ArithmeticException("Numerator not divisible by denominator exactly.");
            }

            BigInteger term = y.get(i).multiply(numerator).divide(denominator);
            result = result.add(term);
        }
        return result;
    }
}
