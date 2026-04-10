package com.example.order.service;

import com.example.common.event.object.PaymentCompletedEvent;
import com.example.common.event.object.PaymentFailedEvent;
import com.example.order.dto.CustomerOrder;
import com.example.order.dto.ProductDto;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.event.publisher.OrderEventPublisher;
import com.example.order.repository.OrderRepository;
import com.example.order.type.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final RestTemplate restTemplate;
    private final OrderEventPublisher orderEventPublisher;


    @Override
    @Retryable(maxRetries = 3, //3 retry
            delay = 500, //0.5s
            multiplier = 2.0 //0.5s, 1s, 2s
    )
    public Order createOrder(CustomerOrder customerOrder) {
        Map<Long, Integer> orderMap = customerOrder.orderItemMap();
        //call product to validate and reserve stock
        Set<OrderItem> itemList = reserveItems(orderMap);
        //compute amount
        BigDecimal amount = itemList.stream().map(o -> o.getUnityPrice().multiply(BigDecimal.valueOf(o.getQuantity()))).reduce(BigDecimal::add).orElse(new BigDecimal(0));
        //persist order
        Order order = new Order(null, itemList, customerOrder.email(), OrderStatus.PENDING, amount, null, null);
        // Synchronize order and orderItem
        itemList.forEach(item -> item.setOrder(order));
        Order savedOrder =  orderRepository.save(order);
        //publish the event then return immediately
        this.orderEventPublisher.publishOrderCreated(savedOrder);
        return savedOrder;
    }

    @KafkaListener(topics = "${kafka.topics.payment-completed}",
            groupId = "order-group")
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Order %s not found",event.getOrderId())));
        //payment is successful. We validate the order
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    @KafkaListener(topics = "${kafka.topics.payment-failed}",
            groupId = "order-group")
    public void onPaymentFailed(PaymentFailedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Order %s not found",event.getOrderId())));
        //payment is unsuccessful. We invalidate the order
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        //we need to release reserved stock
        cancelOrder(order.getId());
    }


    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findOrderByIdWithItems(id);
    }

    @Override
    public Page<Order> getOrderByCustomer(int page, int size, String sorBy, String sortOrder, String email) {
        var pageable = getPageable(page, size, sorBy, sortOrder);
        return orderRepository.findAllOrdersByEmailWithItems(email, pageable);
    }

    @Override
    public Page<Order> getAllOrders(int page, int size, String sorBy, String sortOrder) {
        var pageable = getPageable(page, size, sorBy, sortOrder);
        return orderRepository.findAllOrdersWithItems(pageable);
    }


    @Override
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = this.orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        return this.orderRepository.save(order);
    }

    @Override
    @Retryable(maxRetries = 3, //3 retry
            delay = 500, //0.5s
            multiplier = 2.0 //0.5s, 1s, 2s
    )
    public void cancelOrder(Long id) {
        //fetch all orderitems for this order
        List<OrderItem> orderItemList = this.orderItemService.findAllByOrderId(id);
        //call product service to return products reserved
        Map<Long, Integer> cancelMap = orderItemList.stream().collect(Collectors.toMap(OrderItem::getId, OrderItem::getQuantity));
        HttpEntity<Map<Long, Integer>> body = new HttpEntity<>(cancelMap);
        ParameterizedTypeReference<List<ProductDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        restTemplate.exchange("http://PRODUCT/api/v1/product/cancel", HttpMethod.PUT, body, typeRef).getBody();

        //change status order
        updateOrderStatus(id, OrderStatus.CANCELLED);
    }

    /**
     * Reserve items required by the order
     *
     * @param orderMap map of quantity per id
     * @return Set of orderItems
     */
    private Set<OrderItem> reserveItems(Map<Long, Integer> orderMap) {

        //call product service to update stock
        HttpEntity<Map<Long, Integer>> body = new HttpEntity<>(orderMap);
        ParameterizedTypeReference<List<ProductDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        List<ProductDto> productList = restTemplate.exchange("http://PRODUCT/api/v1/product/reserve", HttpMethod.PUT, body, typeRef).getBody();
        //if stock is ok, we create list of items
        Set<OrderItem> orderItemList = new HashSet<>();
        for (Map.Entry<Long, Integer> oi : orderMap.entrySet()) {
            ProductDto prod = productList.stream().filter(p -> p.id() == oi.getKey()).findFirst().get();
            orderItemList.add(new OrderItem(null, null, prod.id(), prod.name(), prod.price(), oi.getValue()));
        }
        return orderItemList;
    }


    /**
     * Prepare the pageRequest for the query.
     *
     * @param page      starting page
     * @param size      number of elements per page
     * @param sortBy    fields used to sort
     * @param sortOrder order of sort (asc/desc)
     * @return a page request
     */
    private PageRequest getPageable(int page, int size, String sortBy, String sortOrder) {
        var sort = sortOrder.equalsIgnoreCase("Desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return pageable;
    }
}
