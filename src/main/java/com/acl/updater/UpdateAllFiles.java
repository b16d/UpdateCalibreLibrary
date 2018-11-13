package com.acl.updater;


import com.acl.updater.database.DatabaseUpdater;
import com.acl.updater.database.MetadataDatabaseUpdater;
import com.acl.updater.file.BackupDirectory;
import com.acl.updater.file.FileUpdater;
import com.acl.updater.file.MetadataFileUpdater;
import com.acl.updater.file.ZipFolder;

public class UpdateAllFiles {

    private DatabaseUpdater databaseUpdater;

    private FileUpdater fileUpdater;

    private BackupDirectory zipFolder;

    private boolean isBbackup = false;

    public UpdateAllFiles(String filePath, boolean isBackup) {
        init(filePath, isBackup);
    }

    private void init(String filePath, boolean backup) {
        zipFolder = new ZipFolder(filePath);
        databaseUpdater = new MetadataDatabaseUpdater(filePath);
        fileUpdater = new MetadataFileUpdater(filePath);
        isBbackup = backup;
    }

    public void update() {
        if (isBbackup) {
            zipFolder.backupDirectory();
        }
        databaseUpdater.updateData();
        fileUpdater.updateDirectory();
    }
    public static void main(String[] args) {
        UpdateAllFiles main = new UpdateAllFiles("C:\\Users\\alban\\Documents\\Bibliothèque calibre", false);
        main.update();
    }

    public void setZipFolder(BackupDirectory zipFolder) {
        this.zipFolder = zipFolder;
    }
}
