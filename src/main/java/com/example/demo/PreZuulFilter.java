package com.example.demo;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 前置拦截
 */
@Component
public class PreZuulFilter extends ZuulFilter {
    private Logger logger = LoggerFactory.getLogger(PreZuulFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String auth = ctx.getRequest().getHeader("auth");
        try {
            //鉴权
            if (auth(auth)) {
                logger.info("URL 鉴权失败 ：{}",ctx.getRequest().getRequestURL().toString());
                ctx.setSendZuulResponse(false);// 不对齐进行再次路由
                ctx.getResponse().sendError(500, "鉴权失败");
            }
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
        return null;
    }

    private boolean auth(String cook) {
        if (StringUtils.isEmpty(cook)) {
            return true;
        }
        return false;
    }
}
