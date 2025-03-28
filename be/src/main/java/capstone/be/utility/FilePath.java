package capstone.be.utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FilePath {
    private Path path;

    public FilePath(String filePath) {
        this.path = Paths.get(filePath);
    }

    public FilePathObject getFilePathObject() {
        return new FilePathObject(
                getFullPath(),
                getParentDirectory(),
                getFileNameWithExtension(),
                getFileNameWithoutExtension(),
                getFinalExtension(),
                getExtension());
    }

    private String getFullPath() {
        return path.toString().replace("\\", "/"); // Windows and Linux compatibility
    }

    private String getParentDirectory() {
        return path.getParent().toString().replace("\\", "/"); // Windows and Linux compatibility
    }

    private String getFileNameWithExtension() {
        return path.getFileName().toString();
    }

    private String getFileNameWithoutExtension() {
        String fileName = getFileNameWithExtension();
        int firstDotIndex = fileName.indexOf('.');
        return (firstDotIndex == -1) ? fileName : fileName.substring(0, firstDotIndex);
    }

    private String getFinalExtension() {
        String fileName = getFileNameWithExtension();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private String getExtension() {
        String fileName = getFileNameWithExtension();
        int firstDotIndex = fileName.indexOf('.');
        return (firstDotIndex == -1) ? "" : fileName.substring(firstDotIndex + 1);
    }
}
