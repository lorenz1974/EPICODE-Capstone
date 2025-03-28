package capstone.be.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FilePathObject {
    private String fullPath;
    private String parentDirectory;
    private String fileNameWithExtension;
    private String fileNameWithoutExtension;
    private String finalExtension;
    private String extension;
}
