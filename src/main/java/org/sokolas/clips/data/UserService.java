package org.sokolas.clips.data;

import lombok.RequiredArgsConstructor;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;

import java.util.ArrayList;
import java.util.List;

import static org.dizitart.no2.objects.filters.ObjectFilters.eq;

@RequiredArgsConstructor
public class UserService {
    private final ObjectRepository<User> repository;

    public List<String> list() {
        Cursor<User> users = repository.find();
        ArrayList<String> result = new ArrayList<>();
        users.forEach(user -> result.add(user.getId()));
        return result;
    }

    public String listAsString() {
        Cursor<User> cursor = repository.find();
        StringBuilder sb = new StringBuilder();
        cursor.forEach(user -> sb.append(user.getId()).append("\n"));
        return sb.toString();
    }

    public void insert(String userId) {
        repository.update(eq("id", userId), new User(userId));
    }
}
