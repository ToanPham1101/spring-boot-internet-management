package item.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import item.model.CreateOrderCommand;
import item.model.SearchOrdersQuery;
import item.model.SearchOrdersResult;
import item.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/search")
    public SearchOrdersResult searchOrders(@ParameterObject SearchOrdersQuery query) {
        return orderService.searchOrders(query);
    }

    @PostMapping
    public void createOrder(@RequestBody CreateOrderCommand command) {
        orderService.createOrder(command);
    }
}

