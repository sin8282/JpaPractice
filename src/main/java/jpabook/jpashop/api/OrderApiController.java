package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("api/v1/orders") // v1은 Lazy Loding을 전체 초기화시켜주기 위해 한번씩 실행해주는 예제입니다.
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all){
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @Getter // No serializer found error 뜨면 Getter가 없어서 그런것.
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //private List<OrderItem> orderItems; // DTO 수준에서 api를 설계하는 것이므로 Entity가 아닌 DTO로 따로 빼야한다.
        private List<OrderItemDto> orderItems;

        public OrderDto(Order o){
            orderId = o.getId();
            name = o.getMember().getName();
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            address = o.getDelivery().getAddress();
            //orderItems = o.getOrderItems();
            //orderItems.stream().forEach(oi -> oi.getItem().getName());
            orderItems = o.getOrderItems().stream()
                    .map(oi -> new OrderItemDto(oi))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto{

        private String itemName;
        private int orderPrice;
        private int count;
        public OrderItemDto(OrderItem oi) {
            itemName = oi.getItem().getName();
            orderPrice = oi.getOrderPrice();
            count = oi.getCount();
        }
    }
}
