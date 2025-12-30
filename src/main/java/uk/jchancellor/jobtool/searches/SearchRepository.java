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
                        .employmentType("contract")
                        .remote(true)
                        .build(),
                Search.builder()
                        .boardName("cwjobs")
                        .query("java")
                        .employmentType("contract")
                        .remote(true)
                        .build());
    }
}
