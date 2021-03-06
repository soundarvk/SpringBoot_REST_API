package com.ibm.rest;

import static springfox.documentation.builders.PathSelectors.regex;

import java.io.InputStream;
import java.text.DateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.rest.account.AccountDetailsOutput;
import com.ibm.rest.account.AccountInput;
import com.ibm.rest.transaction.input.TransactionInput;
import com.ibm.rest.transaction.output.AcctTrnInqRs;
import com.ibm.rest.transaction.output.TransactionOutput;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableAutoConfiguration
@RestController
@EnableSwagger2
public class HelloworldApplication {
	
	static final Logger logger = LogManager.getLogger();

	@Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("springboot")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/.*"))
                .build();
    }
     
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SpringBoot REST API with Swagger")
                .description("SpringBoot REST API with Swagger")
                .termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
                .contact("sanketsw@au1.ibm.com")
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/IBM-Bluemix/news-aggregator/blob/master/LICENSE")
                .version("2.0")
                .build();
    }

	@RequestMapping(value = "/hello", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ApiOperation(value = "sayHello", nickname = "sayHello")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = String.class),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure") })
	public String hello() {
		return "Hello World from " + Result.IMPLEMENTATION;
	}

	@RequestMapping(value = "/operate/add/{left}/{right}", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "addNumbers", nickname = "addNumbers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Result.class),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"), 
			@ApiResponse(code = 500, message = "Failure") })
	public Result add(@PathVariable("left") int left, @PathVariable("right") int right) {
		logger.info("Executing GET /operate/add/left/right API for {} and {}", left, right);
		return new Result("" + (left + right));
	}
	
	@RequestMapping(value = "/operate/add/{left}/{right}", method = RequestMethod.DELETE, produces = "application/json")
	@ApiOperation(value = "addNumbers", nickname = "addNumbers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Result.class),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"), 
			@ApiResponse(code = 500, message = "Failure") })
	public Result add1(@PathVariable("left") int left, @PathVariable("right") int right) {
		logger.info("Executing DELETE /operate/add/left/right API for {} and {}", left, right);
		return new Result("" + (left + right));
	}
	
	@RequestMapping(value = "/operate/addJSON", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "addJSON", nickname = "addJSON")
	//@ApiParam(value = "Numbers to be added", required = true)
	//@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Result.class)})
	public @ResponseBody Result add(@RequestBody NumbersInput input) {
		System.out.println(input);
		return new Result("" + (input.getLeft() + input.getRight()));
	}
	
	@RequestMapping(value = "/transactions", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "addJSON", nickname = "addJSON")
	public @ResponseBody Object add(@RequestBody TransactionInput input) throws Exception {
		logger.info("Executing /transactions API for {}", input);
		TransactionOutput output = null;
		InputStream io = this.getClass().getResourceAsStream("output.json");
		if(input.getAcctTrnInqRq().getANZAcctId().getAcctId() == null || input.getAcctTrnInqRq().getANZAcctId().getAcctId().equalsIgnoreCase("5000")) {
			throw new Exception("Invalid Account ID");
		} else if(input.getAcctTrnInqRq().getANZAcctId().getAcctId().equalsIgnoreCase("6000")) {
			return new BusinessError("80064", "Mobile Number in use");
		}
		String json =  org.apache.commons.io.IOUtils.toString(io);
		Gson gson = new GsonBuilder()
			     .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
			     .create();
		output = gson.fromJson(json, TransactionOutput.class);
		logger.info("Output is {}", output);
		return output;
	}
	
	@RequestMapping(value = "/acctbalance", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "addJSON", nickname = "addJSON")
	public @ResponseBody Object getBalanace(@RequestBody AccountInput input) throws Exception {
		logger.info("Executing /acctbalance API for {}", input);
		AccountDetailsOutput output = null;
		if(input == null || input.getCustId()==null || input.getCustId().equalsIgnoreCase("5000")) {
			throw new Exception("Invalid Customer ID");
		} else if(input.getCustId().equalsIgnoreCase("6000")) {
			return new BusinessError("80064", "Mobile Number in use");
		}
		InputStream io = this.getClass().getResourceAsStream("accountbalance.json");
		String json =  org.apache.commons.io.IOUtils.toString(io);
		Gson gson = new GsonBuilder().create();
		output = gson.fromJson(json, AccountDetailsOutput.class);
		logger.info("Output is {}", output);
		return output;
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloworldApplication.class, args);
	}
	
	
}
