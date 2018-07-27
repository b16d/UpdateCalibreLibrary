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

    public UpdateAllFiles(String filePath) {
        init(filePath);
    }

    private void init(String filePath) {
        zipFolder = new ZipFolder(filePath);
        databaseUpdater = new MetadataDatabaseUpdater(filePath);
        fileUpdater = new MetadataFileUpdater(filePath);
    }

    public void update() {
        zipFolder.backupDirectory();
        databaseUpdater.updateData();
        fileUpdater.updateDirectory();
    }
    public static void main(String[] args) {
        UpdateAllFiles main = new UpdateAllFiles("C:\\Users\\alban\\Documents\\Bibliothèque calibre");
        main.update();
    }
}
