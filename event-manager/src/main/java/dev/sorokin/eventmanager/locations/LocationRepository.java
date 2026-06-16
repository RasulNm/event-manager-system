package dev.sorokin.eventmanager.locations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    boolean existsByName(String name);

    @Modifying
    @Query("""
            UPDATE LocationEntity l
            SET l.name = :name,
            l.address = :address,
            l.capacity = :capacity,
            l.description = :description
            WHERE l.id = :id
            """)
    int updateLocation(
            @Param("id") Long locationId,
            @Param("name") String name,
            @Param("address") String address,
            @Param("capacity") Integer capacity,
            @Param("description") String description
    );
}