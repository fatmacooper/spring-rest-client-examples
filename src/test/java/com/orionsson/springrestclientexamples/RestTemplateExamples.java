package com.orionsson.springrestclientexamples;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RestTemplateExamples {
    public static final String API_ROOT="https://api.predic8.de:443/shop/";
    @Test
    public void getCategories() throws Exception{
        String apiUrl = API_ROOT + "categories/";
        RestTemplate restTemplate = new RestTemplate();
        JsonNode jsonNode = restTemplate.getForObject(apiUrl, JsonNode.class);
        System.out.println("Response");
        System.out.println(jsonNode.toString());
    }
    @Test
    public void getCustomers() throws Exception{
        String apiUrl = API_ROOT + "/customers/";
        RestTemplate restTemplate = new RestTemplate();
        JsonNode jsonNode = restTemplate.getForObject(apiUrl,JsonNode.class);
        System.out.println("Response");
        System.out.println(jsonNode.toString());
    }
    @Test
    public void createCustomer() throws Exception{
        String apiUrl = API_ROOT + "customers/";
        RestTemplate restTemplate = new RestTemplate();
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("firstname","Betul");
        objectMap.put("lastname","Cooper");
        JsonNode jsonNode = restTemplate.postForObject(apiUrl,objectMap,JsonNode.class);

       // System.out.println("Response");
       // System.out.println(jsonNode.toString());

        String customerUrl = jsonNode.get("customer_url").textValue();
        String id = customerUrl.split("/")[3];
        JsonNode result = restTemplate.getForObject(apiUrl + id,JsonNode.class);
        System.out.println(result);
    }
    @Test
    public void updateCustomer() throws Exception{
        //create customer
        String apiUrl = API_ROOT + "customers/";
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("firstname","Necip");
        objectMap.put("lastname","Cooper");
        RestTemplate restTemplate = new RestTemplate();
        JsonNode jsonNode = restTemplate.postForObject(apiUrl,objectMap,JsonNode.class);
        String id = jsonNode.get("customer_url").textValue().split("/")[3];
        System.out.println("Customer id: " + id);
        //update customer
        objectMap.put("firstname","Necip 2");
        objectMap.put("lastname","Cooper 2");
        restTemplate.put(apiUrl+id,objectMap);
        JsonNode updatedNode = restTemplate.getForObject(apiUrl+id,JsonNode.class);
        System.out.println(updatedNode.toString());
    }

    @Test
    public void updateCustomerUsingPatch() throws Exception{
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("firstname","ahmet");
        objectMap.put("lastname","arik");
        RestTemplate restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(1000);
        httpRequestFactory.setReadTimeout(1000);

        restTemplate.setRequestFactory(httpRequestFactory);

        String apiUrl = API_ROOT + "customers/";
        JsonNode jsonNode = restTemplate.postForObject(apiUrl,objectMap,JsonNode.class);

        String id = jsonNode.get("customer_url").textValue().split("/")[3];
        System.out.println("Customer id: " + id);

        objectMap.put("firstname","ahmet 2");
        objectMap.put("lastname","arik 2");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(objectMap,headers);
        JsonNode updatedNode = restTemplate.patchForObject(apiUrl+id,entity,JsonNode.class);

        System.out.println(updatedNode.toString());
    }

    @Test(expected = ResourceAccessException.class)
    public void updateCustomerUsingPatchSunHttp() throws Exception {

        //create customer to update
        String apiUrl = API_ROOT + "/customers/";

        RestTemplate restTemplate = new RestTemplate();

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("firstname", "Sam");
        postMap.put("lastname", "Axe");

        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());

        String customerUrl = jsonNode.get("customer_url").textValue();

        String id = customerUrl.split("/")[3];

        System.out.println("Created customer id: " + id);

        postMap.put("firstname", "Sam 2");
        postMap.put("lastname", "Axe 2");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(postMap, headers);

        //fails due to sun.net.www.protocol.http.HttpURLConnection not supporting patch
        JsonNode updatedNode = restTemplate.patchForObject(apiUrl + id, entity, JsonNode.class);

        System.out.println(updatedNode.toString());
    }

    @Test(expected = HttpClientErrorException.class)
    public void deleteCustomer() throws Exception{
        String apiUrl = API_ROOT + "customers/";
        RestTemplate restTemplate = new RestTemplate();
        Map<String,Object> postMap = new HashMap<>();
        postMap.put("firstname","fatma");
        postMap.put("lastname","copur");
        JsonNode jsonNode = restTemplate.postForObject(apiUrl,postMap,JsonNode.class);
        String customerId = jsonNode.get("customer_url").textValue().split("/")[3];

        restTemplate.delete(apiUrl+customerId);
        System.out.println("Customer is deleted.");

        restTemplate.getForObject(apiUrl+customerId,JsonNode.class);
    }

}
