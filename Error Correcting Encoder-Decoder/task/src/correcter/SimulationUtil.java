package correcter;

import java.io.*;
import java.util.Random;

public class SimulationUtil {

    private static final  Random random = new Random();

    public static String simulateCharReplacement(String text, int frequency) {
        char[] array = text.toCharArray();
        int n = text.length() - text.length() % frequency;
        int ordinal;
        char symb;
        for (int i = 0; i < n; i += 3) {
            do {
                ordinal = random.nextInt(3) + i;
                switch (random.nextInt(4)) {
                    case 0:
                        symb = randomLowerCaseEngAlphabet();
                        break;
                    case 1:
                        symb = randomUpperCaseEngAlphabet();
                        break;
                    case 2:
                        symb = randomDigit();
                        break;
                    default:
                        symb = ' ';
                }
            } while (array[ordinal] == symb);
            array[ordinal] = symb;
        }
        return new String(array);
    }

    public static int simulateBitReplacement(int data) {
        int mask = (int) Math.pow(2, random.nextInt(8));
        return ((mask & data) == 0) ? data + mask : data - mask;
    }

    public static void simulateSend(File src, File dst) throws IOException {
        try (
                BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
                BufferedOutputStream output = new BufferedOutputStream((new FileOutputStream(dst)))
        ) {
            int bytee;
            while ((bytee = input.read()) != -1) {
                output.write(simulateBitReplacement(bytee));
            }
        }
    }

    private static char randomLowerCaseEngAlphabet() {
        return (char) (random.nextInt(123 - 97) + 97);
    }

    private static char randomUpperCaseEngAlphabet() {
        return (char) (random.nextInt(91 - 65) + 65);
    }

    private static char randomDigit() {
        return (char) (random.nextInt(10) + 48);
    }

}
