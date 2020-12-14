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

            int b, j = 0, encoded = 0;
            int size = input.available() - 1;

            while ((b = input.read()) != -1 ) {
                for (int i = 0; i < 8; i++, j++) {

                    encoded = (setNsBitPair(encoded, j % 3, getNsBit(b, i) ));

                    // encoded is fully filled: write and reset
                    if (j % 3 == 2) {
                        output.write(encoded);
                        encoded = 0;
                    }

                }

            }

            if (j % 3 != 1) {
                encoded = setNsBitPair(encoded, 2, 0);
                if (encoded != 0)
                    output.write(encoded);
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
            int decodedByte = 0, dIndex = 0;
            while ((bytee = input.read()) != -1) {
                bytee = fixByte(bytee);

                //read obe bit from each pair except the parity pair
                for (int i = 0; i < 5; i += 2, dIndex++) {
                    decodedByte = setNsBit(decodedByte, dIndex % 8, getNsBit(bytee, i));
                    if (dIndex % 8 == 7) {
                        output.write(decodedByte);
                        decodedByte = 0;
                    }
                }

            }

            if (decodedByte != 0)
                output.write(decodedByte);




        }
    }

    public static void decodeData(File src, File dst) throws IOException {
        decodeData(src, dst, false);
    }

    private static int setNsBitPair(int bytee, int index, boolean state) {
        if (index == 0) {
            return state ?
                    bytee | 0b11000000 :
                    bytee & 0b00111111;
        } else if (index == 1) {
            return state ?
                    bytee | 0b00110000 :
                    bytee & 0b11001111;
        } else if (index == 2) {
            bytee = state ?
                    bytee | 0b00001100 :
                    bytee & 0b11110011;

            // count the parity bit: the sum of the data bits modulo 2
            int counter = 0;
            if ((bytee | 0b00111111) == 0b11111111)
                counter++;
            if ((bytee | 0b11001111) == 0b11111111)
                counter++;
            if ((bytee | 0b11110011) == 0b11111111)
                counter++;

            return (counter % 2 == 0)  ? bytee : bytee | 0b00000011;
        } else {
            throw new IllegalArgumentException("Only first three pairs may be set (starting from 0)");
        }
    }
    private static int setNsBitPair(int bytee, int index, int bit) {
        return bit == 1 ?
                setNsBitPair(bytee, index, true) :
                setNsBitPair(bytee, index, false);
    }

    private static int fixByte(int bytee) {
        int parityMask = bytee | 0b11111100;
        int parityFlag;
        int ordinal = 0, counter = 0;

        if (parityMask == 0b11111111) {
            parityFlag = 1;
        } else if (parityMask == 0b11111100) {
            parityFlag = 0;
        } else {
            return bytee;
        }

        int mask = bytee | 0b00111111;
        if (mask != 0b11111111 && mask != 0b00111111) {
            ordinal = 0;

        } else if (mask == 0b11111111) {
            counter++;
        }
        mask = bytee | 0b11001111;
        if (mask != 0b11111111 && mask != 0b11001111) {
            ordinal = 1;
        } else if (mask == 0b11111111) {
            counter++;
        }
        mask = bytee | 0b11110011;
        if (mask != 0b11111111 && mask != 0b11110011) {
            ordinal = 2;
        } else if (mask == 0b11111111) {
            counter++;
        }

        if (parityFlag == 1) {
            switch (ordinal) {
                case 0:
                    return (counter % 2 == 0) ?
                            bytee | 0b11000000 :
                            bytee & 0b00111111 ;

                case 1:
                    return (counter % 2 == 0) ?
                            bytee | 0b00110000 :
                            bytee & 0b11001111 ;

                default:
                    return (counter % 2 == 0) ?
                            bytee | 0b00001100 :
                            bytee & 0b11110011 ;

            }
        } else {
            switch (ordinal) {
                case 0:
                    return (counter % 2 == 0) ?
                            bytee & 0b00111111 :
                            bytee | 0b11000000 ;
                case 1:
                    return (counter % 2 == 0) ?
                            bytee & 0b11001111 :
                            bytee | 0b00110000 ;
                default:
                    return (counter % 2 == 0) ?
                            bytee & 0b11110011 :
                            bytee | 0b00001100 ;
            }
        }

    }


    private static int setNsBit(int bytee, int index, boolean state) {
        return  setNsBit(bytee, index, state ? 1 : 0);
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
             BufferedInputStream inputDecoded = new BufferedInputStream(new FileInputStream(dstDecoded));

        )  {

            printBitwiseBinary(inputSrc);
            printBitwiseBinary(inputEncoded);
            printBitwiseBinary(inputReceived);
            printBitwiseBinary(inputDecoded);

        }

    }
}
