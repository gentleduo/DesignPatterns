package org.duo.cor.servlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Chain of Responsibility
 * 责任链模式将一些处理请求的对象都串在一根绳子上，这个处理完，下一个处理完，直到所有的对象都处理完
 * <p>
 * 完全模拟servlet中的filter
 */
public class Main {

    public static void main(String[] args) {

        Request request = new Request();
        request.str = "大家好:)，<script>，欢迎访问 mashibing.com ，大家都是996 ";
        Response response = new Response();
        response.str = "response";

        FilterChain chain = new FilterChain();
        chain.add(new HTMLFilter()).add(new SensitiveFilter());
        chain.doFilter(request, response);
        System.out.println(request.str);
        System.out.println(response.str);
    }
}

interface Filter {
    void doFilter(Request request, Response response, FilterChain chain);
}

class HTMLFilter implements Filter {
    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        request.str = request.str.replaceAll("<", "[").replaceAll(">", "]") + "HTMLFilter()";
        chain.doFilter(request, response);
        response.str += "--HTMLFilter()";
    }
}

class Request {
    String str;
}

class Response {
    String str;
}

class SensitiveFilter implements Filter {
    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        request.str = request.str.replaceAll("996", "955") + " SensitiveFilter()";
        chain.doFilter(request, response);
        response.str += "--SensitiveFilter()";
    }
}


class FilterChain {

    List<Filter> filters = new ArrayList<>();
    int index = 0;

    public FilterChain add(Filter f) {

        filters.add(f);
        return this;
    }

    public void doFilter(Request request, Response response) {

        if (index == filters.size()) return;
        Filter f = filters.get(index);
        index++;
        f.doFilter(request, response, this);
    }
}