package cenamos.repository;

import java.util.List;

public interface PlacesRepository {

    boolean put(String fileId);

    boolean isEmpty();

    List<String> getAll();

    String getAny();
}
