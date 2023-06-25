package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class BuildW11List {

    private static final String BUILD_PROPERTIES = "D:/BuildW11List/wc11FileList.properties";

//    private static final String LCFC_DIRECTORY_PATH = "D:/eclipse-workspace/LCFC/";
    
    private static final String LCFC_DIRECTORY_PATH = "C:/Users/eric.lin/git/serverplmdev/";

    private static final String OUTPUT_DIRECTORY_PATH = "D:/BuildW11List/completeList/";

    private static final String DEPLOY_DIRECTORY_PATH = "D:/BuildW11List/completeList/deploy/";

    private static final String DEPLOY_DIRECTORY_ZIP_PATH = "D:/BuildW11List/completeList/deploy.zip";

    public static void main(String[] args) throws Exception {
        BuildW11List buildW11List = new BuildW11List();
        buildW11List.run();
    }

    private void run() throws Exception {
        deleteOldDirectory();
        copyFileToDirectory();
        zipDirectory();
    }

    private void deleteOldDirectory() throws IOException {
        FileUtils.deleteDirectory(new File(OUTPUT_DIRECTORY_PATH));
    }

    private void copyFileToDirectory() throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(BUILD_PROPERTIES);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String filePath = null;
        while ((filePath = bufferedReader.readLine()) != null) {
            System.out.println("filePath:" + filePath);
            if (StringUtils.startsWith(filePath, "--"))
                continue;

            File file = new File(LCFC_DIRECTORY_PATH + filePath);
            File directory = new File(DEPLOY_DIRECTORY_PATH + StringUtils.substringBeforeLast(filePath, "/"));
            FileUtils.copyFileToDirectory(file, directory);
            System.out.println(filePath + " -> Build Success!\n");
        }
    }

    private void zipDirectory() throws Exception {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(DEPLOY_DIRECTORY_ZIP_PATH));
        addDirectoryToZip(new File(DEPLOY_DIRECTORY_PATH), zipOutputStream);
        zipOutputStream.close();
        System.out.println("zipDirectory Success!");
    }

    private void addDirectoryToZip(File folder, ZipOutputStream zipOutputStream) throws Exception {
        File[] fileArray = folder.listFiles();
        for (File file : fileArray) {
            if (file.isDirectory()) {
                addDirectoryToZip(file, zipOutputStream);
                continue;
            }

            String name = file.getAbsolutePath().substring(OUTPUT_DIRECTORY_PATH.length());
            zipOutputStream.putNextEntry(new ZipEntry(name));
            IOUtils.copy(new FileInputStream(file), zipOutputStream);
            zipOutputStream.closeEntry();
        }
    }

}
