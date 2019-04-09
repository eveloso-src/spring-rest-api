package com.eduit.spring.ws.repository;

import com.eduit.spring.ws.model.Item;
import org.springframework.data.repository.CrudRepository;


public interface ItemRepository extends CrudRepository<Item, Long> {


}
