package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

@ControllerAdvice
public class Jsonp implements ResponseBodyAdvice {

    private static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final Charset UTF8 = Charset.forName("UTF-8");
    private Charset charset;
    private SerializerFeature[] features;
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
        HttpServletResponse response = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();
        response.setContentType("text/html;charset=UTF-8");
        String value = servletRequest.getParameter("callback");
        if(value !=null){
            if (this.isValidJsonpQueryParam(value)) {
                JSONPObject jsonp = new JSONPObject(value, o);
                String text = JSON.toJSONString(jsonp.getJson(), this.features);
                String jsonpText = new StringBuilder(jsonp.getFunction()).append("(").append(text).append(")").toString();
                byte[] bytes = jsonpText.getBytes(this.charset);
                OutputStream out = null;
                try {
                    out = response.getOutputStream();
                    out.write(bytes);
                    out.flush();
                    out.close();
                } catch (IOException e) {

                }
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Ignoring invalid jsonp parameter value: " + value);
            }
        }
        return o;
    }


    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }
    protected boolean isValidJsonpQueryParam(String value) {
        return CALLBACK_PARAM_PATTERN.matcher(value).matches();
    }
    public Jsonp() {
        super();
        this.charset = UTF8;
        this.features = new SerializerFeature[0];
    }


}