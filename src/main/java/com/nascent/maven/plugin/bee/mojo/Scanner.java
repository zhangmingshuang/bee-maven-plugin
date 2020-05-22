package com.nascent.maven.plugin.bee.mojo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Scanner.
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/6
 */
public interface Scanner {

    /**
     * Scan Files.
     *
     * @param folder Scan Folder
     * @return the files in the folder and subdirectory.
     */
    List<Path> scan(Path folder) throws IOException;
}
