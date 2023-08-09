package jpabook.jpashop.domain.springjpa;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity{

    // Spring Auditing은 직접적으로 Column에 선언하고 적용이 가능하다.
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDt;

    @LastModifiedDate
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
