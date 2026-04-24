package integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

public class IntegrationTestUtilities
{
  public static String createTestDir() throws IOException
  {
    String path = "data/integrationtests/test-" + UUID.randomUUID() + "/";
    Files.createDirectory(Path.of(path));
    return path;
  }

  public static void cleanupTestDir(String dirPath) throws IOException
  {
    if (Files.exists(Path.of(dirPath)))
    {
      try (Stream<Path> files = Files.list(Path.of(dirPath)))
      {
        files.forEach(file -> {
          try { Files.delete(file); }
          catch (IOException e) { throw new RuntimeException("Failed to delete file: " + file, e); }
        });
      }
      Files.delete(Path.of(dirPath));
    }
  }
}