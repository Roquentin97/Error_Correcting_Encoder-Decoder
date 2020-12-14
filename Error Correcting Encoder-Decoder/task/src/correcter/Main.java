package correcter;

import java.io.*;
import java.nio.file.Paths;
import java.rmi.server.ExportException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private  static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws IOException{

            final String ENCODE = "encode";
            final String SEND = "send";
            final String DECODE = "decode";


            final File SRC_FILE = new File("send.txt");
            //File SRC_FILE = new File("/home/roquentin/Documents/code/IO/test/src.txt");
            final File ENCODED_FILE = new File("encoded.txt");
            final File RECEIVED_FILE = new File("received.txt");
            final File DECODED_FILE = new File("decoded.txt");

          //  System.out.print("Write a mode: ");
            String mode = scanner.next();

            while (!mode.equals(ENCODE) && !mode.equals(SEND) && !mode.equals(DECODE)) {
                System.out.printf("Unknown mode. Please, type one of the next modes: '%s', '%s', '%s'.\n", ENCODE, SEND, DECODE);
                System.out.print("Write a mode: ");
                mode = scanner.next();
            }

            if (mode.equals(ENCODE)) {
                BitUtils.encodeData(SRC_FILE, ENCODED_FILE);
                BitUtils.printBitwiseHex(new BufferedInputStream(new FileInputStream(ENCODED_FILE)));

            } else if (mode.equals(SEND)) {
                SimulationUtil.simulateSend(ENCODED_FILE, RECEIVED_FILE);

            } else {
                BitUtils.decodeData(RECEIVED_FILE, DECODED_FILE);
                BitUtils.printBitwiseHex(new BufferedInputStream(new FileInputStream(SRC_FILE.getAbsolutePath())));
                BitUtils.printBitwiseHex(new BufferedInputStream(new FileInputStream(DECODED_FILE.getAbsolutePath())));
            }
        }

}

