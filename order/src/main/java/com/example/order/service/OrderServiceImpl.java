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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;


    @Transactional
    @Override
    public Order createOrder(CustomerOrder customerOrder) {
        Map<Long, Integer> orderMap = customerOrder.orderItemMap();
        //call product to validate and reserve stock
        Set<OrderItem> itemList = validAndReserveItems(orderMap);
        //compute amount
        BigDecimal amount = itemList.stream().map(o -> o.getUnityPrice().multiply(BigDecimal.valueOf(o.getQuantity()))).reduce(BigDecimal::add).orElse(new BigDecimal(0));
        //persist order
        return orderRepository.save(new Order(null, itemList, customerOrder.email(), OrderStatus.PENDING, amount, null, null));
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

    private Set<OrderItem> validAndReserveItems(Map<Long, Integer> orderMap) {
        //query product list by ids to validate the stock availability
        List<ProductDto> productList = getProductList(orderMap.keySet().stream().toList());
        //call product service to update stock
        HttpEntity<Map<Long, Integer>> body = new HttpEntity<>(orderMap);
        ParameterizedTypeReference<List<ProductDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        restTemplate.exchange("http://localhost:8081/api/v1/product/reserve", HttpMethod.PUT, body, typeRef);
        List<ProductDto> updatedProdList = new ArrayList<>();
        //if stock is ok, we create list of items
        Set<OrderItem> orderItemList = new HashSet<>();
        for (Map.Entry<Long,Integer> oi : orderMap.entrySet()) {
            ProductDto prod = productList.stream().filter(p -> p.id() == oi.getKey()).findFirst().get();
            orderItemList.add(new OrderItem(null, null, prod.id(), prod.name(), prod.price(), oi.getValue()));
        }
        return orderItemList;
    }

    /**
     * Fetch a list of products from product-service then validate the quantity available
     *
     * @param idList Id list of required items from the order
     * @return List of products
     */
    private List<ProductDto> getProductList(List<Long> idList) {
        HttpEntity<List<Long>> body = new HttpEntity<>(idList);
        ParameterizedTypeReference<List<ProductDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<ProductDto>> response = restTemplate.exchange("http://localhost:8081/api/v1/product/list", HttpMethod.POST, body, typeRef);
        List<ProductDto> productList = response.getBody();
        if (productList == null || productList.size() < idList.size()) {
            throw new IllegalStateException("Error: One or more orderItems do not exist in product list");
        }
        return productList;
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
