package com.neilos.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * MathUtils - Comprehensive mathematical utility class for NeilOS
 * Provides extensive mathematical operations, calculations, and utilities.
 * 
 * Features:
 * - Basic arithmetic operations
 * - Statistical calculations (mean, median, mode, variance, std dev)
 * - Combinatorics (factorial, permutations, combinations)
 * - Number theory (GCD, LCM, prime numbers, factorization)
 * - Geometry calculations
 * - Matrix operations
 * - Vector operations
 * - Complex number operations
 * - Financial calculations
 * - Random number generation
 * - Unit conversions
 * - Numerical integration
 * - Root finding
 * - Sorting and ranking
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class MathUtils {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Mathematical constants */
    public static final double PI = Math.PI;
    public static final double E = Math.E;
    public static final double GOLDEN_RATIO = (1 + Math.sqrt(5)) / 2;
    public static final double SQRT_2 = Math.sqrt(2);
    public static final double SQRT_3 = Math.sqrt(3);
    public static final double LN_2 = Math.log(2);
    public static final double LN_10 = Math.log(10);
    public static final double DEG_TO_RAD = PI / 180.0;
    public static final double RAD_TO_DEG = 180.0 / PI;
    
    /** Rounding modes */
    public static final int ROUND_HALF_UP = RoundingMode.HALF_UP.ordinal();
    public static final int ROUND_HALF_DOWN = RoundingMode.HALF_DOWN.ordinal();
    public static final int ROUND_CEILING = RoundingMode.CEILING.ordinal();
    public static final int ROUND_FLOOR = RoundingMode.FLOOR.ordinal();
    public static final int ROUND_HALF_EVEN = RoundingMode.HALF_EVEN.ordinal();
    
    /** Precision levels */
    public static final int PRECISION_LOW = 4;
    public static final int PRECISION_MEDIUM = 8;
    public static final int PRECISION_HIGH = 16;
    public static final int PRECISION_VERY_HIGH = 32;
    
    // ============================================================
    // BASIC ARITHMETIC
    // ============================================================
    
    /**
     * Clamps a value between a minimum and maximum
     * 
     * @param value The value to clamp
     * @param min The minimum value
     * @param max The maximum value
     * @return The clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Clamps a value between a minimum and maximum (integer)
     * 
     * @param value The value to clamp
     * @param min The minimum value
     * @param max The maximum value
     * @return The clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Returns the sign of a number (-1, 0, 1)
     * 
     * @param value The value
     * @return The sign
     */
    public static int sign(double value) {
        if (value > 0) return 1;
        if (value < 0) return -1;
        return 0;
    }
    
    /**
     * Checks if a number is even
     * 
     * @param value The value
     * @return true if even
     */
    public static boolean isEven(int value) {
        return value % 2 == 0;
    }
    
    /**
     * Checks if a number is odd
     * 
     * @param value The value
     * @return true if odd
     */
    public static boolean isOdd(int value) {
        return value % 2 != 0;
    }
    
    /**
     * Checks if a number is prime
     * 
     * @param value The value
     * @return true if prime
     */
    public static boolean isPrime(int value) {
        if (value <= 1) return false;
        if (value <= 3) return true;
        if (value % 2 == 0 || value % 3 == 0) return false;
        
        for (int i = 5; i * i <= value; i += 6) {
            if (value % i == 0 || value % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets the next prime number greater than or equal to n
     * 
     * @param n The starting number
     * @return The next prime
     */
    public static int nextPrime(int n) {
        if (n <= 2) return 2;
        int candidate = n % 2 == 0 ? n + 1 : n;
        while (!isPrime(candidate)) {
            candidate += 2;
        }
        return candidate;
    }
    
    /**
     * Gets the previous prime number less than or equal to n
     * 
     * @param n The starting number
     * @return The previous prime
     */
    public static int previousPrime(int n) {
        if (n <= 2) return 2;
        int candidate = n % 2 == 0 ? n - 1 : n;
        while (candidate > 2 && !isPrime(candidate)) {
            candidate -= 2;
        }
        return candidate;
    }
    
    // ============================================================
    // GCD & LCM
    // ============================================================
    
    /**
     * Calculates the greatest common divisor (GCD) of two numbers
     * 
     * @param a First number
     * @param b Second number
     * @return The GCD
     */
    public static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
    
    /**
     * Calculates the greatest common divisor (GCD) of multiple numbers
     * 
     * @param numbers The numbers
     * @return The GCD
     */
    public static int gcd(int... numbers) {
        if (numbers.length == 0) return 0;
        int result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result = gcd(result, numbers[i]);
        }
        return result;
    }
    
    /**
     * Calculates the least common multiple (LCM) of two numbers
     * 
     * @param a First number
     * @param b Second number
     * @return The LCM
     */
    public static int lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return Math.abs(a * b) / gcd(a, b);
    }
    
    /**
     * Calculates the least common multiple (LCM) of multiple numbers
     * 
     * @param numbers The numbers
     * @return The LCM
     */
    public static int lcm(int... numbers) {
        if (numbers.length == 0) return 0;
        int result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result = lcm(result, numbers[i]);
        }
        return result;
    }
    
    // ============================================================
    // FACTORIAL & COMBINATORICS
    // ============================================================
    
    /**
     * Calculates the factorial of a number (n!)
     * 
     * @param n The number
     * @return The factorial
     */
    public static long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n > 20) throw new IllegalArgumentException("n > 20 would overflow long");
        
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    
    /**
     * Calculates the factorial as a BigInteger (for large numbers)
     * 
     * @param n The number
     * @return The factorial as BigInteger
     */
    public static BigInteger factorialBig(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
    
    /**
     * Calculates the number of permutations (nPr)
     * 
     * @param n Total items
     * @param r Items to arrange
     * @return Number of permutations
     */
    public static long permutations(int n, int r) {
        if (n < 0 || r < 0 || r > n) return 0;
        long result = 1;
        for (int i = n - r + 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    
    /**
     * Calculates the number of combinations (nCr)
     * 
     * @param n Total items
     * @param r Items to choose
     * @return Number of combinations
     */
    public static long combinations(int n, int r) {
        if (n < 0 || r < 0 || r > n) return 0;
        if (r > n - r) r = n - r;
        long result = 1;
        for (int i = 1; i <= r; i++) {
            result = result * (n - r + i) / i;
        }
        return result;
    }
    
    /**
     * Calculates the binomial coefficient (n choose k)
     * 
     * @param n Total items
     * @param k Items to choose
     * @return The binomial coefficient
     */
    public static BigInteger binomial(int n, int k) {
        if (k < 0 || k > n) return BigInteger.ZERO;
        if (k > n - k) k = n - k;
        
        BigInteger result = BigInteger.ONE;
        for (int i = 1; i <= k; i++) {
            result = result.multiply(BigInteger.valueOf(n - k + i))
                           .divide(BigInteger.valueOf(i));
        }
        return result;
    }
    
    // ============================================================
    // STATISTICAL CALCULATIONS
    // ============================================================
    
    /**
     * Calculates the mean (average) of an array of numbers
     * 
     * @param numbers The numbers
     * @return The mean
     */
    public static double mean(double... numbers) {
        if (numbers.length == 0) return 0;
        return sum(numbers) / numbers.length;
    }
    
    /**
     * Calculates the mean (average) of an array of integers
     * 
     * @param numbers The numbers
     * @return The mean
     */
    public static double mean(int... numbers) {
        if (numbers.length == 0) return 0;
        return sum(numbers) / (double) numbers.length;
    }
    
    /**
     * Calculates the median of an array of numbers
     * 
     * @param numbers The numbers
     * @return The median
     */
    public static double median(double... numbers) {
        if (numbers.length == 0) return 0;
        double[] sorted = numbers.clone();
        Arrays.sort(sorted);
        int middle = sorted.length / 2;
        if (sorted.length % 2 == 0) {
            return (sorted[middle - 1] + sorted[middle]) / 2.0;
        }
        return sorted[middle];
    }
    
    /**
     * Calculates the median of an array of integers
     * 
     * @param numbers The numbers
     * @return The median
     */
    public static double median(int... numbers) {
        if (numbers.length == 0) return 0;
        int[] sorted = numbers.clone();
        Arrays.sort(sorted);
        int middle = sorted.length / 2;
        if (sorted.length % 2 == 0) {
            return (sorted[middle - 1] + sorted[middle]) / 2.0;
        }
        return sorted[middle];
    }
    
    /**
     * Calculates the mode (most frequent value) of an array of numbers
     * 
     * @param numbers The numbers
     * @return The mode(s)
     */
    public static double[] mode(double... numbers) {
        if (numbers.length == 0) return new double[0];
        
        Map<Double, Integer> frequency = new HashMap<>();
        for (double num : numbers) {
            frequency.put(num, frequency.getOrDefault(num, 0) + 1);
        }
        
        int maxFreq = Collections.max(frequency.values());
        return frequency.entrySet().stream()
            .filter(e -> e.getValue() == maxFreq)
            .mapToDouble(Map.Entry::getKey)
            .toArray();
    }
    
    /**
     * Calculates the variance of an array of numbers
     * 
     * @param numbers The numbers
     * @return The variance
     */
    public static double variance(double... numbers) {
        if (numbers.length < 2) return 0;
        double mean = mean(numbers);
        double sumSq = 0;
        for (double num : numbers) {
            sumSq += Math.pow(num - mean, 2);
        }
        return sumSq / numbers.length;
    }
    
    /**
     * Calculates the sample variance of an array of numbers
     * 
     * @param numbers The numbers
     * @return The sample variance
     */
    public static double sampleVariance(double... numbers) {
        if (numbers.length < 2) return 0;
        double mean = mean(numbers);
        double sumSq = 0;
        for (double num : numbers) {
            sumSq += Math.pow(num - mean, 2);
        }
        return sumSq / (numbers.length - 1);
    }
    
    /**
     * Calculates the standard deviation of an array of numbers
     * 
     * @param numbers The numbers
     * @return The standard deviation
     */
    public static double stdDev(double... numbers) {
        return Math.sqrt(variance(numbers));
    }
    
    /**
     * Calculates the sample standard deviation of an array of numbers
     * 
     * @param numbers The numbers
     * @return The sample standard deviation
     */
    public static double sampleStdDev(double... numbers) {
        return Math.sqrt(sampleVariance(numbers));
    }
    
    /**
     * Calculates the sum of an array of numbers
     * 
     * @param numbers The numbers
     * @return The sum
     */
    public static double sum(double... numbers) {
        double sum = 0;
        for (double num : numbers) {
            sum += num;
        }
        return sum;
    }
    
    /**
     * Calculates the sum of an array of integers
     * 
     * @param numbers The numbers
     * @return The sum
     */
    public static long sum(int... numbers) {
        long sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }
    
    /**
     * Calculates the product of an array of numbers
     * 
     * @param numbers The numbers
     * @return The product
     */
    public static double product(double... numbers) {
        double product = 1;
        for (double num : numbers) {
            product *= num;
        }
        return product;
    }
    
    /**
     * Calculates the product of an array of integers
     * 
     * @param numbers The numbers
     * @return The product
     */
    public static long product(int... numbers) {
        long product = 1;
        for (int num : numbers) {
            product *= num;
        }
        return product;
    }
    
    /**
     * Calculates the range (max - min) of an array of numbers
     * 
     * @param numbers The numbers
     * @return The range
     */
    public static double range(double... numbers) {
        if (numbers.length == 0) return 0;
        double min = numbers[0];
        double max = numbers[0];
        for (double num : numbers) {
            if (num < min) min = num;
            if (num > max) max = num;
        }
        return max - min;
    }
    
    /**
     * Calculates the quartiles of an array of numbers
     * 
     * @param numbers The numbers
     * @return Array [Q1, Q2 (median), Q3]
     */
    public static double[] quartiles(double... numbers) {
        if (numbers.length == 0) return new double[]{0, 0, 0};
        double[] sorted = numbers.clone();
        Arrays.sort(sorted);
        
        int n = sorted.length;
        double q2 = median(sorted);
        double[] lower = Arrays.copyOfRange(sorted, 0, n / 2);
        double[] upper = Arrays.copyOfRange(sorted, (n + 1) / 2, n);
        
        return new double[]{median(lower), q2, median(upper)};
    }
    
    /**
     * Calculates the interquartile range (IQR) of an array of numbers
     * 
     * @param numbers The numbers
     * @return The IQR
     */
    public static double iqr(double... numbers) {
        double[] q = quartiles(numbers);
        return q[2] - q[0];
    }
    
    // ============================================================
    // ROUNDING & PRECISION
    // ============================================================
    
    /**
     * Rounds a number to the specified number of decimal places
     * 
     * @param value The value to round
     * @param decimals The number of decimal places
     * @return The rounded value
     */
    public static double round(double value, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(value * scale) / scale;
    }
    
    /**
     * Rounds a number to the specified number of decimal places with a rounding mode
     * 
     * @param value The value to round
     * @param decimals The number of decimal places
     * @param roundingMode The rounding mode
     * @return The rounded value
     */
    public static double round(double value, int decimals, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(decimals, RoundingMode.valueOf(roundingMode));
        return bd.doubleValue();
    }
    
    /**
     * Rounds a number to the nearest integer
     * 
     * @param value The value to round
     * @return The rounded value
     */
    public static int roundToInt(double value) {
        return (int) Math.round(value);
    }
    
    /**
     * Rounds a number up to the nearest integer (ceiling)
     * 
     * @param value The value to ceil
     * @return The ceiling value
     */
    public static int ceilToInt(double value) {
        return (int) Math.ceil(value);
    }
    
    /**
     * Rounds a number down to the nearest integer (floor)
     * 
     * @param value The value to floor
     * @return The floor value
     */
    public static int floorToInt(double value) {
        return (int) Math.floor(value);
    }
    
    // ============================================================
    // NUMBER THEORY
    // ============================================================
    
    /**
     * Gets the factors of a number
     * 
     * @param n The number
     * @return List of factors
     */
    public static List<Integer> getFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        for (int i = 1; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                factors.add(i);
                if (i != n / i) {
                    factors.add(n / i);
                }
            }
        }
        Collections.sort(factors);
        return factors;
    }
    
    /**
     * Gets the prime factors of a number
     * 
     * @param n The number
     * @return List of prime factors
     */
    public static List<Integer> primeFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        int num = n;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            while (num % i == 0) {
                factors.add(i);
                num /= i;
            }
        }
        if (num > 1) {
            factors.add(num);
        }
        return factors;
    }
    
    /**
     * Gets all divisors of a number
     * 
     * @param n The number
     * @return List of divisors
     */
    public static List<Integer> getDivisors(int n) {
        return getFactors(n);
    }
    
    /**
     * Gets the sum of divisors of a number
     * 
     * @param n The number
     * @return Sum of divisors
     */
    public static int sumOfDivisors(int n) {
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            if (n % i == 0) sum += i;
        }
        return sum;
    }
    
    /**
     * Checks if a number is a perfect number
     * 
     * @param n The number
     * @return true if perfect
     */
    public static boolean isPerfectNumber(int n) {
        return n > 0 && sumOfDivisors(n) == 2 * n;
    }
    
    /**
     * Checks if a number is an Armstrong number
     * 
     * @param n The number
     * @return true if Armstrong
     */
    public static boolean isArmstrongNumber(int n) {
        int num = n;
        int digits = String.valueOf(n).length();
        int sum = 0;
        while (num > 0) {
            int digit = num % 10;
            sum += Math.pow(digit, digits);
            num /= 10;
        }
        return sum == n;
    }
    
    /**
     * Checks if a number is a palindrome
     * 
     * @param n The number
     * @return true if palindrome
     */
    public static boolean isPalindrome(int n) {
        String str = String.valueOf(n);
        return str.equals(new StringBuilder(str).reverse().toString());
    }
    
    // ============================================================
    // MATRIX OPERATIONS
    // ============================================================
    
    /**
     * Transposes a matrix
     * 
     * @param matrix The matrix to transpose
     * @return The transposed matrix
     */
    public static double[][] transpose(double[][] matrix) {
        if (matrix.length == 0) return new double[0][0];
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }
    
    /**
     * Adds two matrices
     * 
     * @param a First matrix
     * @param b Second matrix
     * @return Sum of matrices
     */
    public static double[][] add(double[][] a, double[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) {
            throw new IllegalArgumentException("Matrices must have same dimensions");
        }
        int rows = a.length;
        int cols = a[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }
    
    /**
     * Subtracts two matrices
     * 
     * @param a First matrix
     * @param b Second matrix
     * @return Difference of matrices
     */
    public static double[][] subtract(double[][] a, double[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) {
            throw new IllegalArgumentException("Matrices must have same dimensions");
        }
        int rows = a.length;
        int cols = a[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }
    
    /**
     * Multiplies two matrices
     * 
     * @param a First matrix
     * @param b Second matrix
     * @return Product of matrices
     */
    public static double[][] multiply(double[][] a, double[][] b) {
        if (a[0].length != b.length) {
            throw new IllegalArgumentException("Matrices cannot be multiplied");
        }
        int rows = a.length;
        int cols = b[0].length;
        int n = a[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += a[i][k] * b[k][j];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }
    
    /**
     * Multiplies a matrix by a scalar
     * 
     * @param matrix The matrix
     * @param scalar The scalar
     * @return Scaled matrix
     */
    public static double[][] multiply(double[][] matrix, double scalar) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j] * scalar;
            }
        }
        return result;
    }
    
    /**
     * Calculates the determinant of a matrix
     * 
     * @param matrix The matrix
     * @return The determinant
     */
    public static double determinant(double[][] matrix) {
        int n = matrix.length;
        if (n == 1) return matrix[0][0];
        if (n == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        
        double det = 0;
        for (int j = 0; j < n; j++) {
            det += Math.pow(-1, j) * matrix[0][j] * determinant(minor(matrix, 0, j));
        }
        return det;
    }
    
    /**
     * Gets the minor of a matrix (removes row and column)
     * 
     * @param matrix The matrix
     * @param row The row to remove
     * @param col The column to remove
     * @return The minor matrix
     */
    private static double[][] minor(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] minor = new double[n - 1][n - 1];
        int r = 0;
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            int c = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                minor[r][c] = matrix[i][j];
                c++;
            }
            r++;
        }
        return minor;
    }
    
    /**
     * Calculates the inverse of a matrix
     * 
     * @param matrix The matrix
     * @return The inverse matrix
     */
    public static double[][] inverse(double[][] matrix) {
        double det = determinant(matrix);
        if (Math.abs(det) < 1e-10) {
            throw new ArithmeticException("Matrix is singular (det = 0)");
        }
        int n = matrix.length;
        double[][] inverse = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double[][] minor = minor(matrix, i, j);
                inverse[j][i] = Math.pow(-1, i + j) * determinant(minor) / det;
            }
        }
        return inverse;
    }
    
    // ============================================================
    // VECTOR OPERATIONS
    // ============================================================
    
    /**
     * Calculates the dot product of two vectors
     * 
     * @param a First vector
     * @param b Second vector
     * @return The dot product
     */
    public static double dotProduct(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have same length");
        }
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }
    
    /**
     * Calculates the cross product of two 3D vectors
     * 
     * @param a First vector
     * @param b Second vector
     * @return The cross product
     */
    public static double[] crossProduct(double[] a, double[] b) {
        if (a.length != 3 || b.length != 3) {
            throw new IllegalArgumentException("Vectors must be 3D");
        }
        return new double[]{
            a[1] * b[2] - a[2] * b[1],
            a[2] * b[0] - a[0] * b[2],
            a[0] * b[1] - a[1] * b[0]
        };
    }
    
    /**
     * Calculates the magnitude (length) of a vector
     * 
     * @param vector The vector
     * @return The magnitude
     */
    public static double magnitude(double[] vector) {
        double sum = 0;
        for (double v : vector) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }
    
    /**
     * Normalizes a vector (divides by its magnitude)
     * 
     * @param vector The vector
     * @return The normalized vector
     */
    public static double[] normalize(double[] vector) {
        double mag = magnitude(vector);
        if (mag < 1e-10) return vector.clone();
        double[] result = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] / mag;
        }
        return result;
    }
    
    /**
     * Calculates the angle between two vectors in radians
     * 
     * @param a First vector
     * @param b Second vector
     * @return The angle in radians
     */
    public static double angleBetween(double[] a, double[] b) {
        double dot = dotProduct(a, b);
        double magA = magnitude(a);
        double magB = magnitude(b);
        if (magA < 1e-10 || magB < 1e-10) return 0;
        return Math.acos(clamp(dot / (magA * magB), -1, 1));
    }
    
    // ============================================================
    // COMPLEX NUMBERS
    // ============================================================
    
    /**
     * Complex number class
     */
    public static class Complex {
        private final double real;
        private final double imaginary;
        
        public Complex(double real, double imaginary) {
            this.real = real;
            this.imaginary = imaginary;
        }
        
        public double getReal() { return real; }
        public double getImaginary() { return imaginary; }
        
        public Complex add(Complex other) {
            return new Complex(real + other.real, imaginary + other.imaginary);
        }
        
        public Complex subtract(Complex other) {
            return new Complex(real - other.real, imaginary - other.imaginary);
        }
        
        public Complex multiply(Complex other) {
            double r = real * other.real - imaginary * other.imaginary;
            double i = real * other.imaginary + imaginary * other.real;
            return new Complex(r, i);
        }
        
        public Complex divide(Complex other) {
            double denominator = other.real * other.real + other.imaginary * other.imaginary;
            double r = (real * other.real + imaginary * other.imaginary) / denominator;
            double i = (imaginary * other.real - real * other.imaginary) / denominator;
            return new Complex(r, i);
        }
        
        public Complex conjugate() {
            return new Complex(real, -imaginary);
        }
        
        public double magnitude() {
            return Math.sqrt(real * real + imaginary * imaginary);
        }
        
        public double phase() {
            return Math.atan2(imaginary, real);
        }
        
        @Override
        public String toString() {
            if (imaginary == 0) return String.valueOf(real);
            if (real == 0) return imaginary + "i";
            return real + (imaginary < 0 ? " - " : " + ") + Math.abs(imaginary) + "i";
        }
    }
    
    // ============================================================
    // FINANCIAL CALCULATIONS
    // ============================================================
    
    /**
     * Calculates simple interest
     * 
     * @param principal The principal amount
     * @param rate The interest rate (as percentage)
     * @param time The time in years
     * @return The simple interest
     */
    public static double simpleInterest(double principal, double rate, double time) {
        return principal * rate * time / 100;
    }
    
    /**
     * Calculates compound interest
     * 
     * @param principal The principal amount
     * @param rate The interest rate (as percentage)
     * @param time The time in years
     * @param compoundingPeriods The number of compounding periods per year
     * @return The compound interest
     */
    public static double compoundInterest(double principal, double rate, double time, int compoundingPeriods) {
        return principal * Math.pow(1 + (rate / 100) / compoundingPeriods, compoundingPeriods * time);
    }
    
    /**
     * Calculates EMI (Equated Monthly Installment)
     * 
     * @param principal The loan amount
     * @param rate The annual interest rate (as percentage)
     * @param months The loan tenure in months
     * @return The EMI
     */
    public static double emi(double principal, double rate, int months) {
        double monthlyRate = rate / (12 * 100);
        return principal * monthlyRate * Math.pow(1 + monthlyRate, months) / 
               (Math.pow(1 + monthlyRate, months) - 1);
    }
    
    /**
     * Calculates the future value of an investment
     * 
     * @param presentValue The present value
     * @param rate The interest rate (as percentage)
     * @param periods The number of periods
     * @return The future value
     */
    public static double futureValue(double presentValue, double rate, int periods) {
        return presentValue * Math.pow(1 + rate / 100, periods);
    }
    
    /**
     * Calculates the present value of an investment
     * 
     * @param futureValue The future value
     * @param rate The interest rate (as percentage)
     * @param periods The number of periods
     * @return The present value
     */
    public static double presentValue(double futureValue, double rate, int periods) {
        return futureValue / Math.pow(1 + rate / 100, periods);
    }
    
    // ============================================================
    // RANDOM NUMBER GENERATION
    // ============================================================
    
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Random RANDOM = new Random();
    
    /**
     * Generates a random integer between min (inclusive) and max (inclusive)
     * 
     * @param min The minimum value
     * @param max The maximum value
     * @return A random integer
     */
    public static int randomInt(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }
    
    /**
     * Generates a random double between min (inclusive) and max (exclusive)
     * 
     * @param min The minimum value
     * @param max The maximum value
     * @return A random double
     */
    public static double randomDouble(double min, double max) {
        return RANDOM.nextDouble() * (max - min) + min;
    }
    
    /**
     * Generates a random boolean
     * 
     * @return A random boolean
     */
    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }
    
    /**
     * Generates a random secure integer
     * 
     * @param min The minimum value
     * @param max The maximum value
     * @return A secure random integer
     */
    public static int secureRandomInt(int min, int max) {
        return SECURE_RANDOM.nextInt(max - min + 1) + min;
    }
    
    /**
     * Generates a random secure double
     * 
     * @param min The minimum value
     * @param max The maximum value
     * @return A secure random double
     */
    public static double secureRandomDouble(double min, double max) {
        return SECURE_RANDOM.nextDouble() * (max - min) + min;
    }
    
    /**
     * Generates a random string of specified length
     * 
     * @param length The length of the string
     * @param includeDigits Whether to include digits
     * @param includeLetters Whether to include letters
     * @param includeSpecial Whether to include special characters
     * @return A random string
     */
    public static String randomString(int length, boolean includeDigits, 
                                      boolean includeLetters, boolean includeSpecial) {
        StringBuilder chars = new StringBuilder();
        if (includeDigits) chars.append("0123456789");
        if (includeLetters) chars.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        if (includeSpecial) chars.append("!@#$%^&*()_+-=[]{}|;:,.<>?");
        
        if (chars.length() == 0) {
            throw new IllegalArgumentException("At least one character type must be included");
        }
        
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return result.toString();
    }
    
    /**
     * Generates a random password
     * 
     * @param length The length of the password
     * @return A random password
     */
    public static String randomPassword(int length) {
        return randomString(length, true, true, true);
    }
    
    /**
     * Shuffles an array
     * 
     * @param array The array to shuffle
     */
    public static void shuffle(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    
    /**
     * Shuffles an array
     * 
     * @param array The array to shuffle
     */
    public static void shuffle(double[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            double temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    
    /**
     * Shuffles a list
     * 
     * @param list The list to shuffle
     */
    public static <T> void shuffle(List<T> list) {
        Collections.shuffle(list, RANDOM);
    }
    
    /**
     * Generates a random sample from a list
     * 
     * @param list The list to sample from
     * @param sampleSize The size of the sample
     * @return A random sample
     */
    public static <T> List<T> randomSample(List<T> list, int sampleSize) {
        if (sampleSize > list.size()) {
            throw new IllegalArgumentException("Sample size exceeds list size");
        }
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy, RANDOM);
        return copy.subList(0, sampleSize);
    }
    
    // ============================================================
    // UNIT CONVERSIONS
    // ============================================================
    
    /**
     * Converts degrees to radians
     * 
     * @param degrees The value in degrees
     * @return The value in radians
     */
    public static double degToRad(double degrees) {
        return degrees * DEG_TO_RAD;
    }
    
    /**
     * Converts radians to degrees
     * 
     * @param radians The value in radians
     * @return The value in degrees
     */
    public static double radToDeg(double radians) {
        return radians * RAD_TO_DEG;
    }
    
    /**
     * Converts Celsius to Fahrenheit
     * 
     * @param celsius The temperature in Celsius
     * @return The temperature in Fahrenheit
     */
    public static double celsiusToFahrenheit(double celsius) {
        return celsius * 9.0 / 5.0 + 32;
    }
    
    /**
     * Converts Fahrenheit to Celsius
     * 
     * @param fahrenheit The temperature in Fahrenheit
     * @return The temperature in Celsius
     */
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5.0 / 9.0;
    }
    
    /**
     * Converts kilometers to miles
     * 
     * @param km The distance in kilometers
     * @return The distance in miles
     */
    public static double kmToMiles(double km) {
        return km * 0.621371;
    }
    
    /**
     * Converts miles to kilometers
     * 
     * @param miles The distance in miles
     * @return The distance in kilometers
     */
    public static double milesToKm(double miles) {
        return miles * 1.60934;
    }
    
    /**
     * Converts pounds to kilograms
     * 
     * @param pounds The weight in pounds
     * @return The weight in kilograms
     */
    public static double poundsToKg(double pounds) {
        return pounds * 0.453592;
    }
    
    /**
     * Converts kilograms to pounds
     * 
     * @param kg The weight in kilograms
     * @return The weight in pounds
     */
    public static double kgToPounds(double kg) {
        return kg * 2.20462;
    }
    
    // ============================================================
    // NUMERICAL INTEGRATION
    // ============================================================
    
    /**
     * Integrates a function using the trapezoidal rule
     * 
     * @param f The function to integrate
     * @param a The lower limit
     * @param b The upper limit
     * @param n The number of intervals
     * @return The integral
     */
    public static double integrate(DoubleUnaryOperator f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = (f.applyAsDouble(a) + f.applyAsDouble(b)) / 2.0;
        for (int i = 1; i < n; i++) {
            sum += f.applyAsDouble(a + i * h);
        }
        return h * sum;
    }
    
    /**
     * Integrates a function using Simpson's rule
     * 
     * @param f The function to integrate
     * @param a The lower limit
     * @param b The upper limit
     * @param n The number of intervals (must be even)
     * @return The integral
     */
    public static double simpson(DoubleUnaryOperator f, double a, double b, int n) {
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n must be even");
        }
        double h = (b - a) / n;
        double sum = f.applyAsDouble(a) + f.applyAsDouble(b);
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            sum += (i % 2 == 0 ? 2 : 4) * f.applyAsDouble(x);
        }
        return h * sum / 3.0;
    }
    
    // ============================================================
    // ROOT FINDING
    // ============================================================
    
    /**
     * Finds a root of a function using the bisection method
     * 
     * @param f The function
     * @param a The lower bound
     * @param b The upper bound
     * @param tolerance The tolerance
     * @param maxIterations The maximum iterations
     * @return The root
     */
    public static double bisection(DoubleUnaryOperator f, double a, double b, 
                                   double tolerance, int maxIterations) {
        double fa = f.applyAsDouble(a);
        double fb = f.applyAsDouble(b);
        if (fa * fb > 0) {
            throw new IllegalArgumentException("f(a) and f(b) must have opposite signs");
        }
        
        for (int i = 0; i < maxIterations; i++) {
            double c = (a + b) / 2;
            double fc = f.applyAsDouble(c);
            if (Math.abs(fc) < tolerance || (b - a) / 2 < tolerance) {
                return c;
            }
            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }
        return (a + b) / 2;
    }
    
    /**
     * Finds a root of a function using Newton's method
     * 
     * @param f The function
     * @param df The derivative of the function
     * @param x0 The initial guess
     * @param tolerance The tolerance
     * @param maxIterations The maximum iterations
     * @return The root
     */
    public static double newton(DoubleUnaryOperator f, DoubleUnaryOperator df, 
                                double x0, double tolerance, int maxIterations) {
        double x = x0;
        for (int i = 0; i < maxIterations; i++) {
            double fx = f.applyAsDouble(x);
            double dfx = df.applyAsDouble(x);
            if (Math.abs(dfx) < 1e-10) break;
            double x1 = x - fx / dfx;
            if (Math.abs(x1 - x) < tolerance) {
                return x1;
            }
            x = x1;
        }
        return x;
    }
    
    // ============================================================
    // STATISTICAL TESTS
    // ============================================================
    
    /**
     * Performs a linear regression on a set of points
     * 
     * @param x The x values
     * @param y The y values
     * @return Array [slope, intercept, r^2]
     */
    public static double[] linearRegression(double[] x, double[] y) {
        if (x.length != y.length || x.length < 2) {
            throw new IllegalArgumentException("Invalid input");
        }
        
        int n = x.length;
        double sumX = sum(x);
        double sumY = sum(y);
        double sumXY = 0;
        double sumX2 = 0;
        double sumY2 = 0;
        
        for (int i = 0; i < n; i++) {
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
            sumY2 += y[i] * y[i];
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        
        // Calculate R^2
        double meanY = sumY / n;
        double ssTotal = 0;
        double ssResidual = 0;
        for (int i = 0; i < n; i++) {
            ssTotal += Math.pow(y[i] - meanY, 2);
            ssResidual += Math.pow(y[i] - (slope * x[i] + intercept), 2);
        }
        double rSquared = 1 - (ssResidual / ssTotal);
        
        return new double[]{slope, intercept, rSquared};
    }
    
    /**
     * Calculates the correlation coefficient between two datasets
     * 
     * @param x The x values
     * @param y The y values
     * @return The correlation coefficient
     */
    public static double correlation(double[] x, double[] y) {
        if (x.length != y.length || x.length < 2) {
            throw new IllegalArgumentException("Invalid input");
        }
        
        double meanX = mean(x);
        double meanY = mean(y);
        double numerator = 0;
        double denomX = 0;
        double denomY = 0;
        
        for (int i = 0; i < x.length; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            numerator += dx * dy;
            denomX += dx * dx;
            denomY += dy * dy;
        }
        
        return numerator / (Math.sqrt(denomX) * Math.sqrt(denomY));
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of MathUtils
     */
    public static void main(String[] args) {
        System.out.println("📐 MathUtils Demo");
        System.out.println("═".repeat(60));
        
        // Basic arithmetic
        System.out.println("\n📌 Basic Arithmetic:");
        System.out.println("  clamp(5, 0, 10): " + clamp(5, 0, 10));
        System.out.println("  sign(-5): " + sign(-5));
        System.out.println("  isPrime(17): " + isPrime(17));
        System.out.println("  nextPrime(20): " + nextPrime(20));
        
        // GCD & LCM
        System.out.println("\n📌 GCD & LCM:");
        System.out.println("  gcd(12, 18): " + gcd(12, 18));
        System.out.println("  gcd(12, 18, 24): " + gcd(12, 18, 24));
        System.out.println("  lcm(12, 18): " + lcm(12, 18));
        
        // Combinatorics
        System.out.println("\n📌 Combinatorics:");
        System.out.println("  factorial(5): " + factorial(5));
        System.out.println("  permutations(5, 2): " + permutations(5, 2));
        System.out.println("  combinations(5, 2): " + combinations(5, 2));
        System.out.println("  binomial(10, 3): " + binomial(10, 3));
        
        // Statistics
        System.out.println("\n📌 Statistics:");
        double[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println("  Data: " + Arrays.toString(data));
        System.out.println("  Mean: " + mean(data));
        System.out.println("  Median: " + median(data));
        System.out.println("  Mode: " + Arrays.toString(mode(data)));
        System.out.println("  Variance: " + variance(data));
        System.out.println("  Std Dev: " + stdDev(data));
        System.out.println("  Range: " + range(data));
        System.out.println("  Quartiles: " + Arrays.toString(quartiles(data)));
        System.out.println("  IQR: " + iqr(data));
        
        // Rounding
        System.out.println("\n📌 Rounding:");
        System.out.println("  round(3.14159, 2): " + round(3.14159, 2));
        System.out.println("  roundToInt(3.7): " + roundToInt(3.7));
        System.out.println("  ceilToInt(3.2): " + ceilToInt(3.2));
        System.out.println("  floorToInt(3.8): " + floorToInt(3.8));
        
        // Number theory
        System.out.println("\n📌 Number Theory:");
        System.out.println("  factors of 36: " + getFactors(36));
        System.out.println("  prime factors of 36: " + primeFactors(36));
        System.out.println("  divisors of 28: " + getDivisors(28));
        System.out.println("  isPerfectNumber(28): " + isPerfectNumber(28));
        System.out.println("  isArmstrongNumber(153): " + isArmstrongNumber(153));
        System.out.println("  isPalindrome(12321): " + isPalindrome(12321));
        
        // Matrix operations
        System.out.println("\n📌 Matrix Operations:");
        double[][] m1 = {{1, 2}, {3, 4}};
        double[][] m2 = {{5, 6}, {7, 8}};
        System.out.println("  Matrix 1: " + Arrays.deepToString(m1));
        System.out.println("  Matrix 2: " + Arrays.deepToString(m2));
        System.out.println("  Determinant: " + determinant(m1));
        
        // Vector operations
        System.out.println("\n📌 Vector Operations:");
        double[] v1 = {1, 2, 3};
        double[] v2 = {4, 5, 6};
        System.out.println("  v1: " + Arrays.toString(v1));
        System.out.println("  v2: " + Arrays.toString(v2));
        System.out.println("  Dot product: " + dotProduct(v1, v2));
        System.out.println("  Magnitude of v1: " + magnitude(v1));
        System.out.println("  Normalized v1: " + Arrays.toString(normalize(v1)));
        System.out.println("  Angle between: " + Math.toDegrees(angleBetween(v1, v2)) + "°");
        
        // Complex numbers
        System.out.println("\n📌 Complex Numbers:");
        Complex c1 = new Complex(3, 4);
        Complex c2 = new Complex(1, -2);
        System.out.println("  c1: " + c1);
        System.out.println("  c2: " + c2);
        System.out.println("  c1 + c2: " + c1.add(c2));
        System.out.println("  c1 - c2: " + c1.subtract(c2));
        System.out.println("  c1 * c2: " + c1.multiply(c2));
        System.out.println("  c1 / c2: " + c1.divide(c2));
        System.out.println("  conjugate(c1): " + c1.conjugate());
        System.out.println("  magnitude(c1): " + c1.magnitude());
        System.out.println("  phase(c1): " + c1.phase() + " rad");
        
        // Financial
        System.out.println("\n📌 Financial:");
        System.out.println("  Simple Interest (1000, 5%, 2y): " + simpleInterest(1000, 5, 2));
        System.out.println("  Compound Interest (1000, 5%, 2y, 12): " + compoundInterest(1000, 5, 2, 12));
        System.out.println("  EMI (100000, 10%, 24): " + emi(100000, 10, 24));
        System.out.println("  Future Value (1000, 5%, 5): " + futureValue(1000, 5, 5));
        
        // Random
        System.out.println("\n📌 Random:");
        System.out.println("  randomInt(1, 10): " + randomInt(1, 10));
        System.out.println("  randomDouble(0, 1): " + randomDouble(0, 1));
        System.out.println("  randomPassword(12): " + randomPassword(12));
        
        // Unit conversions
        System.out.println("\n📌 Unit Conversions:");
        System.out.println("  180° to rad: " + degToRad(180));
        System.out.println("  π rad to deg: " + radToDeg(Math.PI));
        System.out.println("  100°C to °F: " + celsiusToFahrenheit(100));
        System.out.println("  212°F to °C: " + fahrenheitToCelsius(212));
        System.out.println("  10 km to miles: " + kmToMiles(10));
        System.out.println("  10 miles to km: " + milesToKm(10));
        
        // Linear regression
        System.out.println("\n📌 Linear Regression:");
        double[] x = {1, 2, 3, 4, 5};
        double[] y = {2, 4, 6, 8, 10};
        double[] regression = linearRegression(x, y);
        System.out.println("  Slope: " + regression[0]);
        System.out.println("  Intercept: " + regression[1]);
        System.out.println("  R²: " + regression[2]);
        System.out.println("  Correlation: " + correlation(x, y));
        
        System.out.println("\n✅ Demo completed!");
    }
}