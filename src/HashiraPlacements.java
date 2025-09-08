import java.io.*;
import java.math.BigInteger;
import java.util.*;
import com.google.gson.*;

public class HashiraPlacements {

    public static void main(String[] args) throws Exception {
        // Check if filename argument is provided
        if (args.length < 1) {
            System.out.println("Usage: java HashiraPlacements <input-file.json>");
            return;
        }

        // Read JSON file from argument
        String filename = args[0];
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        String jsonInput = sb.toString();

        // Parse JSON
        JsonObject root = JsonParser.parseString(jsonInput).getAsJsonObject();

        // Extract n and k
        JsonObject keys = root.getAsJsonObject("keys");
        int n = keys.get("n").getAsInt();
        int k = keys.get("k").getAsInt();

        List<BigInteger> xVals = new ArrayList<>();
        List<BigInteger> yVals = new ArrayList<>();

        // Extract roots and decode values
        for (int i = 1; i <= n; i++) {
            String key = Integer.toString(i);
            if (!root.has(key)) {
                continue;
            }
            JsonObject obj = root.getAsJsonObject(key);
            String baseStr = obj.get("base").getAsString();
            String valStr = obj.get("value").getAsString();
            int base = Integer.parseInt(baseStr);

            BigInteger y = new BigInteger(valStr, base);
            xVals.add(BigInteger.valueOf(i));  // Use root index as x
            yVals.add(y);
        }

        // Calculate constant term using Lagrange interpolation at 0
        BigInteger constantTerm = lagrangeAtZero(xVals.subList(0, k), yVals.subList(0, k));

        // Print the constant term
        System.out.println(constantTerm.toString());
    }

    // Lagrange interpolation at x=0 to find constant term
    static BigInteger lagrangeAtZero(List<BigInteger> x, List<BigInteger> y) {
        int n = x.size();
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < n; i++) {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                numerator = numerator.multiply(x.get(j).negate()); // (0 - x_j)
                denominator = denominator.multiply(x.get(i).subtract(x.get(j)));
            }

            BigInteger term = y.get(i).multiply(numerator).divide(denominator);
            result = result.add(term);
        }
        return result;
    }
}
