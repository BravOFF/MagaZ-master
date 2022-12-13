package com.example.springSecurityApplication.repositories;

import com.example.springSecurityApplication.models.Order;
import com.example.springSecurityApplication.models.Person;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.List;

@Repository
@Transactional
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByPerson(Person person);
    List<Order> findOrderByNumber(String number);
//    List<Order> getAllBy();
    @Query(value = "select number from orders group by \"number\"", nativeQuery = true)
    List<String> findAllGroupByNumber();
    @Query(value = "select number from orders where number like %?1% group by \"number\"", nativeQuery = true)
    List<String> findByQueryNum(String qyery);

    @Modifying
    @Query(value = "update orders set status = ?1 where number = ?2", nativeQuery = true)
    int editOrderStatus(int status, String num);
}
