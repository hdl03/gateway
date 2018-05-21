package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现简单的动态路由,并刷新
 */

public class CustomRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {
    public final static Logger logger = LoggerFactory.getLogger(CustomRouteLocator.class);


    private JdbcTemplate jdbcTemplate;
    private ZuulProperties properties;
    private ServerProperties server;

    public CustomRouteLocator(ServerProperties server, ZuulProperties properties) {
        super(server.getServletPath(), properties);
        this.properties = properties;
        this.server = server;
        logger.info("servletPath:{},properties : {}", server, properties);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //数据库数据刷新,实现动态加载路由网关
    @Override
    public void refresh() {
        doRefresh();
    }

    /**
     * 加载路由策略
     *
     * @return
     */
    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<String, ZuulProperties.ZuulRoute>();
        //从application.yml中加载路由信息
        routesMap.putAll(super.locateRoutes());
        //从db中加载路由信息,实现动态加载
        routesMap.putAll(locateRoutesFromDB());
        //优化一下配置
        LinkedHashMap<String, ZuulProperties.ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            // Prepend with slash if not already present.
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (StringUtils.hasText(this.properties.getPrefix())) {
                path = this.properties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
            values.put(path, entry.getValue());
        }
        logger.info("----------打印动态路由数据-----------{}", values);
        return values;
    }

    /**
     * 从数据库中加载，路由
     *
     * @return
     */
    private Map<String, ZuulProperties.ZuulRoute> locateRoutesFromDB() {
        Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();
        //模拟数据库数据
//        List<ZuulRouteVO> results = new LinkedList<>();
//        ZuulRouteVO zuulRouteVO = new ZuulRouteVO();
//        zuulRouteVO.setId(UUID.randomUUID().toString().replace("-",""));
//        zuulRouteVO.setPath("/user/**");
//        zuulRouteVO.setServiceId("http://127.0.0.1:8080");
//        results.add(zuulRouteVO);
        List<ZuulRouteVO> results = jdbcTemplate.query("select * from zuul_route_vo", new BeanPropertyRowMapper<>(ZuulRouteVO.class));
        for (ZuulRouteVO result : results) {
            ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
            if (StringUtils.isEmpty(result.getPath())) {
                logger.warn("动态配置,配置有误,请检查： {}", result);
            } else {
                BeanUtils.copyProperties(result, zuulRoute);
                routes.put(zuulRoute.getPath(), zuulRoute);
            }
        }
        return routes;
    }

}

