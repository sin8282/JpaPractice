package jpabook.jpashop.domain.springjpa;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AutoCloseable.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSpring implements Persistable<String> {

    //JPA는 내부적으로 이 객체가 새로운것인지 아닌지를 @Id값으로 구분한다.
    //@GeneratedValue를 사용할땐 객체를 new 할때 바로 id값이 생기는게 아니라 db에 연결시점에 id가 들어오게 된다.
    //즉 JPA가 동작할때는 NULL이라는 이야기이다.
    //하지만 @GenratedValue가 아니라면 내가 직접 id를 setter해줘야하고, 이경우 객체 생성 단계에서 이미 값이 있다고 판단하고,
    //JPA는 영속성을 확인하기 위해 DB에 SELECT * FROM TABLE WHERE ID = @id 로 검색을 해보게된다.
    //그리고 없는걸 확인하고 진짜 없으면 EntityManager.save(new())를 진행하게 된다.
    //이 과정을 EntityManager.merge()라고 한다.
    //하지만 우리가 원하는것은 pk값을 커스텀하면서, save()만 실행되게 하는 것이기 때문에
    //임의로 isNew() 보다 뒤에 실행되는 @CreatedDate를 이용하여, 마치 @GeneratedValue처럼 새로운객체인지 아닌지를 판가름하게 하는것이다.
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createDt;

    public ItemSpring(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return createDt == null;
    }
}
