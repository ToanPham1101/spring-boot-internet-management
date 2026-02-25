package internet.management.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import internet.management.model.CreateOrderCommand;
import internet.management.model.SearchOrdersQuery;
import internet.management.model.SearchOrdersResult;
import internet.management.service.OrderService;

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

