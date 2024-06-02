package sudokujava.algorithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class SudokuSumRestriction {
    private final ArrayList<Rational[]> mat = new ArrayList<>();

    public SudokuSumRestriction() {
        // each row is 45
        for (int row = 0; row < 9; row++) {
            Rational[] arr = new Rational[82];
            Arrays.fill(arr, Rational.ZERO);
            Arrays.fill(arr, row * 9, row * 9 + 9, Rational.ONE);
            arr[81] = new Rational(45);
            mat.add(arr);
        }

        // each col is 45
        for (int col = 0; col < 9; col++) {
            Rational[] arr = new Rational[82];
            Arrays.fill(arr, Rational.ZERO);
            for (int row = 0; row < 9; row++) {
                arr[row * 9 + col] = Rational.ONE;
            }
            arr[81] = new Rational(45);
            mat.add(arr);
        }

        // each grid cell is 45
        for (int gridCell = 0; gridCell < 9; gridCell++) {
            Rational[] arr = new Rational[82];
            Arrays.fill(arr, Rational.ZERO);
            int startRow = gridCell / 3;
            int startCol = (gridCell % 3) * 3;
            for (int row = startRow; row < startRow + 3; row++) {
                for (int col = startCol; col < startCol + 3; col++) {
                    arr[row * 9 + col] = Rational.ONE;
                }
            }
            arr[81] = new Rational(45);
            mat.add(arr);
        }
    }

    private void multAddSet(int targetRow, int addedRow, Rational addedRowScale) {
        for (int i = 0; i < 82; i++) {
            mat.get(targetRow)[i] = mat.get(targetRow)[i].add(mat.get(addedRow)[i].mult(addedRowScale));
        }
    }

    private void scaleRow(int targetRow, Rational scale) {
        for (int i = 0; i < 82; i++) {
            mat.get(targetRow)[i] = mat.get(targetRow)[i].mult(scale);
        }
    }

    private void swapRow(int targetRow, int otherRow) {
        Rational[] temp = mat.get(targetRow);
        mat.set(targetRow, mat.get(otherRow));
        mat.set(otherRow, temp);
    }

    private void addRow(Rational[] row) {
        mat.add(row);
    }

    private void sortColRow(int startRow, int colTest) {
        for (int row = startRow; row < mat.size(); row++) {
            if (!mat.get(row)[colTest].isZero()) {
                swapRow(startRow, row);
                return;
            }
        }
    }

    public void rref() {
        int col = 0;
        for (int row = 0; row < mat.size(); row++) {
            while (col < 81 && mat.get(row)[col].isZero()) {
                sortColRow(row, col);
                if (mat.get(row)[col].isZero()) col++;
            }
            if (col == 81) break;
            Rational leadingCoef = mat.get(row)[col];
            assert !leadingCoef.isZero();
            scaleRow(row, leadingCoef.reciprocal());
            for (int i = 0; i < mat.size(); i++) {
                if (i == row) continue;
                multAddSet(i, row, mat.get(i)[col].mult(-1));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mat.size(); i++) {
            if (i != 0) sb.append('\n');
            for (int j = 0; j < 81; j++) {
                double val = mat.get(i)[j].getDouble();
                sb.append(String.format("%1.1f", val)).append("  ");
            }
            sb.append(" |  ");
            double val = mat.get(i)[81].getDouble();
            sb.append(String.format("%1.1f", val));
        }
        return sb.toString();
    }

    public void printSummary() {
        for (Rational[] rationals : mat) {
            StringBuilder left = new StringBuilder();
            StringBuilder right = new StringBuilder();
            for (int j = 0; j < 81; j++) {
                Rational val = rationals[j];
                if (val.isZero()) continue;
                int row = j / 9;
                int col = j % 9;
                StringBuilder sb = val.ltZero() ? right : left;
                if (sb.length() > 0) sb.append(" + ");
                sb.append(String.format("%1.1f(%d, %d)", Math.abs(val.getDouble()), row, col));
            }
            Rational con = rationals[81];
            if (con.ltZero()) {
                if (left.length() > 0) left.append(" + ");
                left.append(Math.abs(con.getDouble()));
                System.out.println(left + " = " + right);
            } else {
                String modifier = right.length() > 0 ? " + " : "";
                System.out.println(left + " = " + con.getDouble() + modifier + right);
            }
        }
    }

    public void addGroup(int sum, int... location) {
        Rational[] arr = new Rational[82];
        Arrays.fill(arr, Rational.ZERO);
        arr[81] = new Rational(sum);
        for (int i = 0; i < location.length; i += 2) {
            arr[location[i] * 9 + location[i + 1]] = Rational.ONE;
        }
        addRow(arr);
    }

    public static void main(String[] args) {
        SudokuSumRestriction sm = new SudokuSumRestriction();
        sm.addGroup(16, 0, 0, 0, 1, 1, 0);
        sm.addGroup(20, 0, 2, 1, 2, 2, 2, 2, 3);
        sm.addGroup(11, 0, 3, 1, 3);
        sm.addGroup(12, 0, 4, 1, 4, 2, 4);
        sm.addGroup(15, 0, 5, 0, 6);
        sm.addGroup(9, 0, 7, 1, 7);
        sm.addGroup(7, 0, 8, 1, 8);
        sm.addGroup(15, 1, 1, 2, 1, 3, 1);
        sm.addGroup(11, 1, 5, 1, 6);
        sm.addGroup(10, 2, 0, 3, 0);
        sm.addGroup(10, 2, 5, 2, 6);
        sm.addGroup(12, 2, 7, 2, 8);
        sm.addGroup(6, 3, 2);
        sm.addGroup(18, 3, 3, 3, 4, 4, 4, 4, 5);
        sm.addGroup(21, 3, 5, 3, 6, 3, 7, 4, 7);
        sm.addGroup(8, 3, 8, 4, 8);
        sm.addGroup(7, 4, 0);
        sm.addGroup(6, 4, 1, 5, 1);
        sm.addGroup(20, 4, 2, 4, 3, 5, 3);
        sm.addGroup(8, 4, 6, 5, 6);
        sm.addGroup(17, 5, 0, 6, 0, 6, 1);
        sm.addGroup(11, 5, 2, 6, 2, 6, 3);
        sm.addGroup(19, 5, 4, 6, 4, 7, 4);
        sm.addGroup(16, 5, 5, 6, 5, 7, 5);
        sm.addGroup(11, 5, 7, 6, 7);
        sm.addGroup(13, 5, 8, 6, 8);
        sm.addGroup(8, 6, 6, 7, 6);
        sm.addGroup(3, 7, 0);
        sm.addGroup(16, 7, 1, 7, 2, 8, 2);
        sm.addGroup(11, 7, 3, 8, 3, 8, 4);
        sm.addGroup(7, 7, 7, 7, 8);
        sm.addGroup(9, 8, 0, 8, 1);
        sm.addGroup(11, 8, 5, 8, 6);
        sm.addGroup(11, 8, 7, 8, 8);
        //System.out.println(sm);
        sm.rref();
        sm.printSummary();
    }

    static class Rational {
        public static final Rational ONE = new Rational(1, 1);
        public static final Rational ZERO = new Rational(0, 1);
        private final int num;
        private final int denom;

        public Rational(int num) {
            this(num, 1);
        }

        public Rational(int num, int denom) {
            this.num = num;
            this.denom = denom;
        }

        public Rational getReduced() {
            int betterNum = num;
            int betterDenom = denom;
            if (denom < 0) {
                betterNum *= -1;
                betterDenom *= -1;
            }
            BigInteger b1 = BigInteger.valueOf(betterNum);
            BigInteger b2 = BigInteger.valueOf(betterDenom);
            BigInteger gcd = b1.gcd(b2);
            return new Rational(betterNum / gcd.intValueExact(), betterDenom / gcd.intValueExact());
        }

        public Rational add(Rational other) {
            return new Rational(num * other.denom + other.num * denom, other.denom * denom).getReduced();
        }

        public Rational mult(int other) {
            return new Rational(num * other, denom).getReduced();
        }

        public Rational mult(Rational other) {
            return new Rational(num * other.num, denom * other.denom).getReduced();
        }

        public Rational reciprocal() {
            assert !isZero();
            return new Rational(denom, num).getReduced();
        }


        public double getDouble() {
            return (double) num / denom;
        }

        public boolean isZero() {
            return this.num == 0;
        }

        public boolean ltZero() {
            return this.num < 0;
        }

        @Override
        public String toString() {
            return num + "/" + denom;
        }
    }
}
