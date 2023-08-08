package jpabook.jpashop.domain.springjpa;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createDt = now;
        updateDt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updateDt = LocalDateTime.now();
    }
}
