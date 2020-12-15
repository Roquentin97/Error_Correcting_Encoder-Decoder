package correcter;

import java.io.*;

public class BitUtils {



    public static void encodeData(File src, File dst, boolean append) throws IOException {

        if (!src.isFile()) {
            throw  new IllegalArgumentException(src.getAbsolutePath() + " is not a file!");
        }

        try(
                BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dst, append))
        ) {

            int bytee, encoded1, encoded2;
            while ((bytee = input.read()) != -1) {
                encoded1 = encoded2 = 0;

                // set parity bits
                encoded1 = setNsBit(encoded1, 0, calculateParity(bytee, 0,1,3));
                encoded1 = setNsBit(encoded1, 1, calculateParity(bytee, 0,2,3));
                encoded1 = setNsBit(encoded1, 3, calculateParity(bytee, 1,2,3));

                encoded2 = setNsBit(encoded2, 0, calculateParity(bytee, 4,5,7));
                encoded2 = setNsBit(encoded2, 1, calculateParity(bytee, 4,6,7));
                encoded2 = setNsBit(encoded2, 3, calculateParity(bytee, 5,6,7));


                // write regular bits
                encoded1 = setNsBit(encoded1, 2, getNsBit(bytee, 0));
                encoded1 = setNsBit(encoded1, 4, getNsBit(bytee, 1 ));
                encoded1 = setNsBit(encoded1, 5, getNsBit(bytee,2));
                encoded1 = setNsBit(encoded1, 6, getNsBit(bytee, 3));

                encoded2 = setNsBit(encoded2, 2, getNsBit(bytee, 4));
                encoded2 = setNsBit(encoded2, 4, getNsBit(bytee, 5));
                encoded2 = setNsBit(encoded2, 5, getNsBit(bytee, 6));
                encoded2 = setNsBit(encoded2, 6, getNsBit(bytee, 7));

                output.write(encoded1);
                output.write(encoded2);
                }
        }




    }


    public static void encodeData(File src, File dst) throws IOException {
        encodeData(src, dst, false);
    }

    public static void decodeData(File src, File dst, boolean append) throws IOException {
        try (
                BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dst, append))
        ) {
            int bytee;
            int decodedByte;
            while ((bytee = input.read()) != -1) {
                bytee = fixByte(bytee);
                decodedByte = 0;
                decodedByte = setNsBit(decodedByte, 0, getNsBit(bytee, 2));
                decodedByte = setNsBit(decodedByte, 1, getNsBit(bytee, 4));
                decodedByte = setNsBit(decodedByte, 2, getNsBit(bytee, 5));
                decodedByte = setNsBit(decodedByte, 3, getNsBit(bytee, 6));

                bytee = input.read();
                if (bytee == -1) return;
                bytee = fixByte(bytee);


                decodedByte = setNsBit(decodedByte, 4, getNsBit(bytee, 2));
                decodedByte = setNsBit(decodedByte, 5, getNsBit(bytee, 4));
                decodedByte = setNsBit(decodedByte, 6, getNsBit(bytee, 5));
                decodedByte = setNsBit(decodedByte, 7, getNsBit(bytee, 6));

                output.write(decodedByte);


            }





        }
    }

    public static void decodeData(File src, File dst) throws IOException {
        decodeData(src, dst, false);
    }

    public static void printBitwiseBinary(InputStream input) throws IOException {
        int bytee;
        while ((bytee = input.read()) != -1) {
            String binary = String.format("%8s", Integer.toBinaryString(bytee)).replaceAll(" ", "0");
            System.out.print(binary + " ");
        }
        System.out.println("\n");
    }

    public static void printBitwiseHex(InputStream input) throws IOException {
        int bytee;
        while ((bytee = input.read()) != -1) {
            System.out.print(Integer.toHexString(bytee) + " ");
        }
        System.out.println();
    }


    private static int calculateParity(int bytee, int... indexes){
        int sum = 0;
        for (int i : indexes) {
            sum += getNsBit(bytee, i);
        }

        return sum % 2 == 0 ? 0 : 1;
    }

    private static int fixByte(int bytee) {
        boolean p1, p2, p4;

        p1 = getBitsSum(bytee, 2, 4, 6) % 2 == getNsBit(bytee, 0);
        p2 = getBitsSum(bytee, 2, 5, 6) % 2 == getNsBit(bytee, 1);
        p4 = getBitsSum(bytee, 4, 5, 6) % 2 == getNsBit(bytee, 3);

        if (p1 && p2 && p4) return bytee;

        int counter = -1;
        if (!p1) counter += 1;
        if (!p2) counter += 2;
        if (!p4) counter += 4;

        return bytee ^ (1 << (7 - counter));

    }


    private static int getBitsSum(int bytee, int... indexes) {
        int counter = 0;
        for (int i : indexes) {
            counter += getNsBit(bytee, i);
        }
        return counter;
    }


    private static int setNsBit(int bytee, int index, int bit) {
        if (index > 7 || index < 0) {
            throw  new IndexOutOfBoundsException();
        }
        return bytee | (bit << (7 - index));
    }

    private static int getNsBit(int bytee, int index) {
        if (index > 7 || index < 0) {
            throw new IndexOutOfBoundsException();
        }

        return (bytee & (1 << (7 - index))) == 0 ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {

        File src = new File("/home/roquentin/Documents/code/IO/test/src.txt");
        File dstEncoded = new File("/home/roquentin/Documents/code/IO/test/encoded.txt");
        File received = new File("/home/roquentin/Documents/code/IO/test/received.txt");
        File  dstDecoded = new File("/home/roquentin/Documents/code/IO/test/decoded" + src.getName().substring(src.getName().indexOf(".")));

        encodeData(src, dstEncoded);
        SimulationUtil.simulateSend(dstEncoded, received);
        decodeData(received, dstDecoded);


        try (BufferedInputStream inputSrc = new BufferedInputStream(new FileInputStream(src));
             BufferedInputStream inputEncoded = new BufferedInputStream(new FileInputStream(dstEncoded));
             BufferedInputStream inputReceived = new BufferedInputStream(new FileInputStream(received));
             BufferedInputStream inputDecoded = new BufferedInputStream(new FileInputStream(dstDecoded))

        )  {

            printBitwiseBinary(inputSrc);
            printBitwiseBinary(inputEncoded);
            printBitwiseBinary(inputReceived);
            printBitwiseBinary(inputDecoded);

        }

    }
}
