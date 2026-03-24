package com.example.order.service;

import com.example.order.dto.CustomerOrder;
import com.example.order.dto.ProductDto;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;


    @Override
    public Order createOrder(CustomerOrder customerOrder) {
        //call product to validate and reserve stock
        //compute amount
        //persist order
        return null;
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
        return null;
    }

    @Override
    public void cancelOrder(Long id) {

    }

    private boolean reserveItems(Set<OrderItem> orderItemSet) {
        List<Long> idList = orderItemSet.stream().map(OrderItem::getId).collect(Collectors.toList());
        HttpEntity<List<Long>> body = new HttpEntity<>(idList);
        ParameterizedTypeReference<List<ProductDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<ProductDto>> response = restTemplate.exchange("http://localhost:8081/api/v1/product/list", HttpMethod.POST, body, typeRef);
        List<ProductDto> productList = response.getBody();
        if (productList == null || productList.size() < idList.size()) {
            throw new IllegalStateException("Error: One or more orderItems do not exist in product list");
        }

        for (OrderItem o : orderItemSet) {
            ProductDto prod = productList.stream().filter(p -> p.id() == o.getId()).findFirst().get();
            if (o.getQuantity() > prod.stock()) {
                log.info("Not enough quantity available");
                throw new IllegalStateException(String.format("Error: Not enough quantity available: {id:%s, available:%s, required:%s}", o.getId(), o.getUnityPrice(), prod.id(), prod.price()));
            }
        }
        //TODO call product service to update stock
        return true;
    }

    private PageRequest getPageable(int page, int size, String sorBy, String sortOrder) {
        var sort = sortOrder.equalsIgnoreCase("Desc") ?
                Sort.by(sorBy).descending() : Sort.by(sorBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return pageable;
    }
}
