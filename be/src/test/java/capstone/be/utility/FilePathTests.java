package capstone.be.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FilePathTests {

    @Test
    void testFilePathObject() {
        String testPath = "/home/user/documents/example.tar.gz";
        FilePath filePath = new FilePath(testPath);
        FilePathObject filePathObject = filePath.getFilePathObject();

        assertEquals(testPath, filePathObject.getFullPath());
        assertEquals("/home/user/documents", filePathObject.getParentDirectory());
        assertEquals("example.tar.gz", filePathObject.getFileNameWithExtension());
        assertEquals("example", filePathObject.getFileNameWithoutExtension());
        assertEquals("gz", filePathObject.getFinalExtension());
        assertEquals("tar.gz", filePathObject.getExtension());
    }

    @Test
    void testFilePathObjectNoExtension() {
        String testPath = "/home/user/documents/example";
        FilePath filePath = new FilePath(testPath);
        FilePathObject filePathObject = filePath.getFilePathObject();

        assertEquals(testPath, filePathObject.getFullPath());
        assertEquals("/home/user/documents", filePathObject.getParentDirectory());
        assertEquals("example", filePathObject.getFileNameWithExtension());
        assertEquals("example", filePathObject.getFileNameWithoutExtension());
        assertEquals("", filePathObject.getFinalExtension());
        assertEquals("", filePathObject.getExtension());
    }

    @Test
    void testFilePathObjectOneExtension() {
        String testPath = "/home/user/documents/example.txt";
        FilePath filePath = new FilePath(testPath);
        FilePathObject filePathObject = filePath.getFilePathObject();

        assertEquals(testPath, filePathObject.getFullPath());
        assertEquals("/home/user/documents", filePathObject.getParentDirectory());
        assertEquals("example.txt", filePathObject.getFileNameWithExtension());
        assertEquals("example", filePathObject.getFileNameWithoutExtension());
        assertEquals("txt", filePathObject.getFinalExtension());
        assertEquals("txt", filePathObject.getExtension());
    }
}
