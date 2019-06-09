package cenamos.repository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Repository
public class MemoryPlacesRepo implements PlacesRepository {

    private List<String> places = new ArrayList<>();

    @Override
    public boolean put(String fileId) {
        places.add(fileId);
        return true;
    }

    @Override
    public boolean isEmpty() {
        return !places.isEmpty();
    }

    @Override
    public List<String> getAll() {
        return Collections.unmodifiableList(places);
    }

    @Override
    public String getAny() {
        int index = new Random().nextInt(places.size());
        return places.get(index);
    }

}
