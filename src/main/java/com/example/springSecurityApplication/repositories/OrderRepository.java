package com.example.springSecurityApplication.repositories;

import com.example.springSecurityApplication.models.Order;
import com.example.springSecurityApplication.models.Person;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByPerson(Person person);
    List<Order> findOrderByNumber(String number);
//    List<Order> getAllBy();
    @Query(value = "select number from orders group by \"number\"", nativeQuery = true)
    List<String> findAllGroupByNumber();
    @Query(value = "update orders set status = ?1 where number = '?2'", nativeQuery = true)
    List<String> editOrderStatus(String num, int status);
}
