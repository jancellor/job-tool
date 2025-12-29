package uk.jchancellor.jobtool.searches;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchRepository {
    public List<Search> findAll() {
        return List.of(
                Search.builder()
                        .boardName("totaljobs")
                        .query("java")
                        .type("contract")
                        .remote(true)
                        .build());
    }
}
