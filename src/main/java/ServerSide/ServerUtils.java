package ServerSide;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import net.lingala.zip4j.*;


import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ServerUtils {

    //todo this should be two classes serverutils and databaseutils but ill change that later

    public static String pathToTemp = "src/main/java/ServerSide/temp";
    public static String pathToLanding = "src/main/java/ServerSide/ActiveGameServer";
    public static String pathToDataBase = "src/main/java/ServerSide/Database";
    public static String pathToPacking = "src/main/java/ServerSide/packing";

    public static String getRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public static void deleteFolderContents(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolderContents(f.getAbsolutePath());
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void createFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void MoveAllFiles(String sourcePath, String destinationPath) throws IOException {

        File sourceFolder = new File(sourcePath);
        File destinationFolder = new File(destinationPath);

        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        for (File file : sourceFolder.listFiles()) {
            Path source = file.toPath();
            Path target = new File(destinationFolder.getPath() + "/" + file.getName()).toPath();
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static String GetPathToInnerFile(String outerFilePath) throws IOException {
        Path path = Paths.get(outerFilePath + "/META-INF");
        Path unknownFolder = Files.list(path.getParent())
                .filter(p -> !p.equals(path))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown folder not found"));
        return unknownFolder.toString();
    }


    public static void UnzipFolderContents(String zipString, String destDir) throws IOException, InterruptedException {
        zipString =  zipString + "/server.zip";
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipString));
        try {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File entryDestination = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            baos.write(buffer, 0, len);
                        }
                        byte[] bytes = baos.toByteArray();
                        FileOutputStream fos = new FileOutputStream(entryDestination);
                        fos.write(bytes);
                        fos.close();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Zip(String sourceDirPath, String zipFilePath) throws IOException {
        File sourceDir = new File(sourceDirPath);
        FileOutputStream fos = new FileOutputStream(zipFilePath+"/server.zip");
        ArchiveOutputStream aos = new ZipArchiveOutputStream(fos);
        addFilesToZip(sourceDir, sourceDir, aos);
    }

    private static void addFilesToZip(File rootDir, File currentDir, ArchiveOutputStream aos) throws IOException {
        byte[] buffer = new byte[1024];
        for (File file : currentDir.listFiles()) {
            if (file.isDirectory()) {
                addFilesToZip(rootDir, file, aos);
            } else {
                String entryName = file.getAbsolutePath().substring(rootDir.getAbsolutePath().length() + 1);
                ArchiveEntry entry = new ZipArchiveEntry(entryName);
                aos.putArchiveEntry(entry);
                try (FileInputStream fis = new FileInputStream(file)) {
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        aos.write(buffer, 0, len);
                    }
                }
                aos.closeArchiveEntry();
            }
        }
    }



    public static void MoveZip(String target, String dest) throws IOException {
        Files.move(Paths.get(target + "/server.zip"), Paths.get(dest + "/server.zip"), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void CopyZip(String target, String dest) throws IOException {
        Files.copy(Paths.get(target + "/server.zip"), Paths.get(dest + "/server.zip"), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void DeleteZip(String path){
        File file = new File(path + "/server.zip");
        if (file.delete()) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("Failed to delete the file");
        }
    }

}
