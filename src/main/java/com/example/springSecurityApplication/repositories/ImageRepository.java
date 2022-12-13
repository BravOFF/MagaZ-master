package com.example.springSecurityApplication.repositories;

import com.example.springSecurityApplication.models.Image;
import com.example.springSecurityApplication.models.Order;
import com.example.springSecurityApplication.models.Person;
import com.example.springSecurityApplication.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ImageRepository extends JpaRepository<Image, Integer> {


    List<Product> findByProduct(Product product);

    @Query(value = "select file_name from image where product_id = ?1", nativeQuery = true)
    List findByProductId(int id);
}