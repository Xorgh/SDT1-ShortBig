package persistence.fileimplementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileStorageInitializer
{
  public static void ensureFilesExist(String directoryPath)
  {
    List<String> files = List.of(
        directoryPath + "ownedstocks.txt",
        directoryPath + "portfolio.txt",
        directoryPath + "stocks.txt",
        directoryPath + "stockpricehistories.txt",
        directoryPath + "transactions.txt"
    );

    for (String path : files)
    {
      try
      {
        Path filepath = Paths.get(path);
        if (!Files.exists(filepath))
        {
          Files.createFile(filepath);
        }
      }
      catch (IOException e)
      {
        throw new RuntimeException("Failed to create file: " + path, e);
      }
    }
  }

}
