package com.pengfu.inote.config;

import com.pengfu.inote.domain.vo.common.ResultCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Knife4jConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false) // 去除默认状态码
                // 添加全局响应状态码
                .globalResponses(HttpMethod.GET, getGlobalResponse())
                .globalResponses(HttpMethod.POST, getGlobalResponse())
                .globalResponses(HttpMethod.PUT, getGlobalResponse())
                .globalResponses(HttpMethod.PATCH, getGlobalResponse())
                .globalResponses(HttpMethod.DELETE, getGlobalResponse());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("inote 接口文档")
                .description("")
                .contact(new Contact("PrideZH", "", "332842890@qq.com"))
                .version("1.0")
                .build();
    }

    // 全局响应状态码
    private List<Response> getGlobalResponse() {
        List<Response> responseList = new ArrayList<>();
        for (ResultCode resultCode : ResultCode.values()) {
            responseList.add(
                    new ResponseBuilder()
                    .code(String.valueOf(resultCode.code()))
                    .description(resultCode.message())
                    .build());
        }
        return responseList;
    }

}
