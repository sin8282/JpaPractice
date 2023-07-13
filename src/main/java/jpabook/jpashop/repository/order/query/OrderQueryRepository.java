package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * findOrderQueryDtos : 핵심은 ToOne을 먼저 조회해서 컬럼의 조회를 최소화시키고(최선, fetch join 아님) ToMany를 나중에 join하는 방식이다.
 * findAllByDtoOptimizing : where in 절을 이용해서 한번에 조회하는 방법을 이용한다.
 * findAllByDtoFlat : 한번에 조회는 가능하다. join 해서 가져오며, 다수에 맞춰서 작동하므로 orderRepository의 findAllWithItem와 같은 이유로 페이징 안됨.
**/
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrders(); // member와 delivery는 하나씩 밖에 없다.
        result.forEach(o->{ // forEach로 컬렉션을 돌리듯, toMany조건이니 따로 처리한다.
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();

    }

    public List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }


    public List<OrderQueryDto> findAllByDtoOptimizing() {
        List<OrderQueryDto> result = findOrders();

        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemsMap = orderItems.stream()
                .collect(Collectors.groupingBy(dto -> dto.getOrderId()));

        result.forEach(o-> o.setOrderItems(orderItemsMap.get(o.getOrderId())));
        return result;
    }

    public List<OrderFlatDto> findAllByDtoFlat() {
        return em.createQuery(
                "select distinct new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) " +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i",OrderFlatDto.class)
                .getResultList();

    }
}
