
package com.eduit.spring.ws.resource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eduit.spring.ws.resource.request.ItemRequest;
import com.eduit.spring.ws.resource.response.ItemResponse;
import com.eduit.spring.ws.resource.response.MessageResponse;
import com.eduit.spring.ws.service.ItemService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;


@RestController
@RequestMapping("/v1")
public class ItemResource {

    @Autowired
    private ItemService itemService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemResource.class);

    @RequestMapping(value = "/item/{id}",method = RequestMethod.GET,produces="application/json")
    public @ResponseBody  ResponseEntity<?> getById(@PathVariable("id") Long id){
        LOGGER.info("getting item id {} ",id);
        if(!itemService.exists(id)){
            LOGGER.info("item id {} not found",id);
            return new ResponseEntity(new MessageResponse("item "+ id + " not found "), HttpStatus.BAD_REQUEST);
        }
        ItemResponse itemResponse = itemService.getById(id);
        LOGGER.info("item found ",itemResponse);
        return new ResponseEntity(itemResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/item",method = RequestMethod.GET,produces="application/json")
    public @ResponseBody ResponseEntity<?> get(){
        LOGGER.info("getting all items ");
        List<ItemResponse> itemResponses = itemService.getAll();
        LOGGER.info("items founds {} ", itemResponses);
        return new ResponseEntity(itemResponses, HttpStatus.OK);
    }

    @RequestMapping(value = "/docker",method = RequestMethod.GET,produces="application/json")
    public @ResponseBody ResponseEntity<?> getDocker(){
        LOGGER.info("DOCKER ");
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        List<Container> containers = dockerClient.listContainersCmd().exec();
        LOGGER.info("containers " + containers.size());
        return new ResponseEntity(new Object(), HttpStatus.OK);
    }

    
    @RequestMapping(value = "/item",method = RequestMethod.POST,produces="application/json", consumes="application/json")
    public @ResponseBody ResponseEntity<?> updateItem(@RequestBody ItemRequest itemRequest){
        LOGGER.info("incoming message {} ",itemRequest);
        if(!itemService.exists(itemRequest.getId())){
            LOGGER.info("item id {} not found",itemRequest.getId());
            return new ResponseEntity(new MessageResponse("item "+ itemRequest.getId() + " not found "), HttpStatus.BAD_REQUEST);
        }
        ItemResponse itemResponse = itemService.saveOrUpdate(itemRequest);
        LOGGER.info("response message {} ",itemResponse);
        return new ResponseEntity(itemResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/item",method = RequestMethod.PUT,produces="application/json", consumes="application/json")
    public @ResponseBody ResponseEntity<ItemResponse> saveItem(@RequestBody ItemRequest itemRequest){
        LOGGER.info("incoming message {} ",itemRequest);
        if(itemRequest.getId() != null ){
            LOGGER.info("item id {}must be null",itemRequest.getId());
            return new ResponseEntity(new MessageResponse(" id " + itemRequest.getId() + " must be null "), HttpStatus.BAD_REQUEST);
        }
        ItemResponse itemResponse = itemService.saveOrUpdate(itemRequest);
        LOGGER.info("response message {} ",itemResponse);
        return new ResponseEntity(itemResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/item/{id}",method = RequestMethod.DELETE ,produces="application/json")
    public @ResponseBody  ResponseEntity<?> delete(@PathVariable("id") Long id){
        LOGGER.info("deleting item id {} ",id);
        if(id == null ){
            LOGGER.info("item id {} is mandatory",id);
            return new ResponseEntity(new MessageResponse(" id " + id + " is mandatory "), HttpStatus.BAD_REQUEST);
        }
        itemService.delete(id);
        LOGGER.info("item id {} deleted successfully ",id);
        return new ResponseEntity(new MessageResponse("deleted"), HttpStatus.OK);
    }


}
