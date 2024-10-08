package edu.njnu.opengms.common.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName HandleTimeInterceptor
 * @Description 打印每个方法的执行时间
 * @Author sun_liber
 * @Date 2018/11/14
 * @Version 1.0.0
 */
@Slf4j
public class HandleTimeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (long) request.getAttribute("startTime");
        request.removeAttribute("startTime");
        long endTime = System.currentTimeMillis();
        request.setAttribute("handling Time:", endTime - startTime);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String queryString = request.getQueryString() != null ? request.getQueryString() : "";
        if (request.getAttribute("handling Time:") == null) {
            log.error(request.getMethod() + " " + request.getRequestURL().toString() + "?" + queryString + " error");
        } else {
            log.info("handlingTime: " + request.getRequestURL().toString() + "?" + queryString + " " + request.getAttribute("handling Time:") + "ms");
        }
    }
}
