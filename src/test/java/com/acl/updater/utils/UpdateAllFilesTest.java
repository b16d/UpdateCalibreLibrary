package com.acl.updater.utils;

import com.acl.updater.UpdateAllFiles;
import com.acl.updater.database.DatabaseUpdater;
import com.acl.updater.file.BackupDirectory;
import com.acl.updater.file.FileUpdater;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;


public class UpdateAllFilesTest {

    @Mock
    BackupDirectory backupDirectory;

    @Mock
    FileUpdater fileUpdater;

    @Mock
    DatabaseUpdater databaseUpdater;

    @Test
    public void testUpdateWithoutBackup() throws IllegalAccessException, NoSuchFieldException {
        UpdateAllFiles updateAllFiles = new UpdateAllFiles("", false);
        Field field = UpdateAllFiles.class.getDeclaredField("zipFolder");
        field.setAccessible(true);
        field.set(updateAllFiles,backupDirectory);

        updateAllFiles.update();
        Mockito.verify(backupDirectory,Mockito.never()).backupDirectory();
    }

    @Test
    public void testUpdateWithBackup() throws IllegalAccessException, NoSuchFieldException {
        UpdateAllFiles updateAllFiles = new UpdateAllFiles("", true);

        Field field = UpdateAllFiles.class.getDeclaredField("zipFolder");
        field.setAccessible(true);
        field.set(updateAllFiles,backupDirectory);


        updateAllFiles.update();
        Mockito.verify(backupDirectory,Mockito.times(1)).backupDirectory();
    }

   @BeforeEach
    public void setUp() {
       MockitoAnnotations.initMocks(this);
   }
}
