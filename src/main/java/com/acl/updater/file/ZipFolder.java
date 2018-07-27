package com.acl.updater.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFolder implements BackupDirectory {

    private static final Logger LOGGER = LogManager.getLogger(MetadataFileUpdater.class);

    private List <String> fileList;
    private String filePath;
    private static final String OUTPUT_ZIP_FILE = "Backup.zip";

    public ZipFolder(String filePath) {
        this.filePath = filePath;
        fileList = new ArrayList <> ();
    }

    public void backupDirectory() {
        ZipFolder appZip = new ZipFolder(filePath);
        appZip.generateFileList(new File(filePath));
        appZip.zipIt();
    }
    public static void main(String[] args) {
        String sourceFolder = "C:\\Users\\alban\\Documents\\Bibliothèque calibre";
        ZipFolder appZip = new ZipFolder(sourceFolder);
        appZip.generateFileList(new File(sourceFolder));
        appZip.zipIt();
    }

    private void zipIt() {
        byte[] buffer = new byte[1024];
        String source = new File(filePath).getName();

        try (ZipOutputStream zos = new ZipOutputStream(
                new FileOutputStream( filePath + File.separator + OUTPUT_ZIP_FILE))) {

            LOGGER.info("Output to Zip : " + filePath + File.separator + OUTPUT_ZIP_FILE);

            for (String file: this.fileList) {
                LOGGER.info("File Added : " + file);
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);

                try (FileInputStream in  = new FileInputStream(filePath + File.separator + file)) {
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
            }

            zos.closeEntry();
            LOGGER.info("Folder successfully compressed");

        } catch (IOException ex) {
            LOGGER.error("Error during folder compression ", ex);
        }

    }

    private void generateFileList(File node) {
        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            if (subNote != null) {
                for (String filename: subNote) {
                    generateFileList(new File(node, filename));
                }
            }
        }
    }

    private String generateZipEntry(String file) {
        return file.substring(filePath.length() + 1, file.length());
    }
}
