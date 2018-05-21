package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处罚重新加载路由策略
 */
@RestController
public class RefreshRouteController {
    @Autowired
    ApplicationEventPublisher publisher;

    @Autowired
    CustomRouteLocator routeLocator;

    @PostMapping("refresh")
    public void refreshRoute() {
        RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(routeLocator);
        publisher.publishEvent(routesRefreshedEvent);
    }
}
