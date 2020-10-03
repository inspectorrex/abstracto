package dev.sheldan.abstracto.core.models.database;

import dev.sheldan.abstracto.core.models.SnowFlake;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name="role")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ARole implements SnowFlake, Serializable {

    @Id
    @Getter
    @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    @JoinColumn(name = "role_server_id", nullable = false)
    private AServer server;

    @Getter
    @Setter
    private Boolean deleted;

    @Column(name = "created")
    private Instant created;

    @PrePersist
    private void onInsert() {
        this.created = Instant.now();
    }

    @Column(name = "updated")
    private Instant updated;

    @Transient
    private boolean fake;

    @PreUpdate
    private void onUpdate() {
        this.updated = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ARole role = (ARole) o;
        return Objects.equals(id, role.id) &&
                Objects.equals(server, role.server) &&
                Objects.equals(deleted, role.deleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, server, deleted);
    }

    public String getAsMention() {
        return "<@&" + getId() + '>';
    }
}
