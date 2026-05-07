import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class FileCopier {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Source file: ");
        String sourcePath = scanner.nextLine();
        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists() || !sourceFile.isFile()) {
            System.out.println("Error");
            scanner.close();
            return;
        }

        System.out.print("Target file: ");
        String targetPath = scanner.nextLine();
        File targetFile = new File(targetPath);

        if (targetFile.exists()) {
            System.out.println("File already exists");
            scanner.close();
            return;
        }

        try (FileInputStream inputStream = new FileInputStream(sourceFile);
             FileOutputStream outputStream = new FileOutputStream(targetFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesCopied = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesCopied += bytesRead;
            }

            System.out.println("Total: " + totalBytesCopied);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}