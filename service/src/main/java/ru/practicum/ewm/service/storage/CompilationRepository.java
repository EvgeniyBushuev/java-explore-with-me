package ru.practicum.ewm.service.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.model.Compilation;


@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("select c from Compilation c " +
            "where ((:pinned) is null or c.pinned = :pinned)")
    Page<Compilation> findAllByPublic(@Param("pinned") Boolean pinned,
                                      Pageable pageable);
}
