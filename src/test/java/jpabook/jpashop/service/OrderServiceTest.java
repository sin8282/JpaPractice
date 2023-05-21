package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //
        Member member = new Member();
        member.setName("회원");
        member.setAddress(new Address("서울", "강", "12345"));
        em.persist(member);

        Book book = new Book();
        book.setName("Jpa 기본");
        book.setPrice(30000);
        book.setStockQuantity(10);
        em.persist(book);

        int orderCount = 2;

        //
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order getOrder = orderRepository.findOne(orderId);

        //
        Assertions.assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문 상태");
        Assertions.assertEquals(1, getOrder.getOrderItems().size(), "주문 상품 종류 갯수");
        Assertions.assertEquals(30000*orderCount, getOrder.getTotalPrice(), "주문 상품 금액");
        Assertions.assertEquals(8, book.getStockQuantity());

    }

    @Test
    public void 주문초과() throws Exception {
        //
        Member member = new Member();
        member.setName("회원");
        member.setAddress(new Address("서울", "강", "12345"));
        em.persist(member);

        Book book = new Book();
        book.setName("Jpa 기본");
        book.setPrice(30000);
        book.setStockQuantity(10);
        em.persist(book);

        int orderCount = 11;

        //
        NotEnoughStockException thrown =
                Assertions.assertThrows(
                        NotEnoughStockException.class,
                        () -> orderService.order(member.getId(), book.getId(), orderCount)
                );
        Assertions.assertEquals(
                "need more stock",
                thrown.getMessage(),
                "재고 초과해서 주문할 수 없다.."
        );
    }

    @Test
    public void 주문취소() throws Exception {
        //
        Member member = new Member();
        member.setName("회원");
        member.setAddress(new Address("서울", "강", "12345"));
        em.persist(member);

        Book book = new Book();
        book.setName("Jpa 기본");
        book.setPrice(30000);
        book.setStockQuantity(10);
        em.persist(book);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //
        orderService.cancelOrder(orderId);

        //
        Order order = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.CANCEL, order.getStatus(), "주문 상태");
        Assertions.assertEquals(10, book.getStockQuantity(), "상품 재고 채워넣기");
    }
}