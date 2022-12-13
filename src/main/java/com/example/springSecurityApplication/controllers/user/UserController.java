package com.example.springSecurityApplication.controllers.user;

import com.example.springSecurityApplication.enumm.Status;
import com.example.springSecurityApplication.models.*;
import com.example.springSecurityApplication.repositories.CartRepository;
import com.example.springSecurityApplication.repositories.ImageRepository;
import com.example.springSecurityApplication.repositories.OrderRepository;
import com.example.springSecurityApplication.security.PersonDetails;
import com.example.springSecurityApplication.services.ProductService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    private final ProductService productService;
    private final ImageRepository imageRepository;

    public UserController(OrderRepository orderRepository, CartRepository cartRepository, ProductService productService,
                          ImageRepository imageRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.imageRepository = imageRepository;
    }

//    @GetMapping("/index")
    @GetMapping("/")
    public String index(Model model) {

        // Получаем объект аутентификации - > с помощью Spring SecurityContextHolder обращаемся к контексту и на нем вызываем метод аутентификации.
        // Из потока для текущего пользователя мы получаем объект, который был положен в сессию после аутентификации
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
//        String role = personDetails.getPerson().getRole();

//        if(role.equals("ROLE_ADMIN"))
//        {
//            return "redirect:/admin";
//        }

        model.addAttribute("products", productService.getAllProduct());
        return "user/index";
    }

    @GetMapping("/cart/add/{id}")
    public String addProductInCart(@PathVariable("id") int id, Model model){
        Product product = productService.getProductId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        Cart cart = new Cart(id_person, product.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productsList = new ArrayList<>();
        for (Cart cart: cartList) {
            productsList.add(productService.getProductId(cart.getProductId()));
        }

        float price = 0;
        for (Product product: productsList) {
            price += product.getPrice();
        }
        model.addAttribute("price", price);
        model.addAttribute("cart_product", productsList);
        return "user/cart";
    }

    @GetMapping("/cart/delete/{id}")
    public String deleteProductCart(Model model, @PathVariable("id") int id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        cartRepository.deleteCartByProductId(id);
        return "redirect:/cart";
    }

    @GetMapping("/order/create")
    public String order(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productsList = new ArrayList<>();
        // Получаем продукты из корзины по id
        for (Cart cart: cartList) {
            productsList.add(productService.getProductId(cart.getProductId()));
        }

        float price = 0;
        for (Product product: productsList){
            price += product.getPrice();
        }

        String uuid = UUID.randomUUID().toString();
        for (Product product: productsList){
            Order newOrder = new Order(uuid, product, personDetails.getPerson(), 1, product.getPrice(), Status.Принят);
            orderRepository.save(newOrder);
            cartRepository.deleteCartByProductId(product.getId());
        }
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String ordersUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());

        List<String> newOrders = new ArrayList<>();

        for (Order ord : orderList){

            if (!newOrders.contains(ord.getNumber())){
                newOrders.add(ord.getNumber());
            }

        }
//        System.out.println(newOrders);

        JSONArray jPordArr = new JSONArray();

        String Status = null;
        for (String order : newOrders) {
            LocalDateTime data = null;
            Status = null;
            Integer sumCount = 0;
            float sumPrice = 0;
            JSONObject jPordObj = new JSONObject();
            JSONArray jProd = new JSONArray();

            List<Order> lOrders = orderRepository.findOrderByNumber(order);
            for (Order or : lOrders) {
                data = or.getDateTime();
                Status = String.valueOf(or.getStatus());

                sumCount = sumCount + or.getCount();
                sumPrice = sumPrice + or.getPrice();

                List imgList = imageRepository.findByProductId(or.getProduct().getId());

                JSONObject ja = new JSONObject();
                ja.put("data", or.getDateTime());
                ja.put("id", or.getProduct().getId());
                ja.put("img", imgList);
                ja.put("name", or.getProduct().getTitle());
                ja.put("count", or.getCount());
                ja.put("price", or.getPrice());

                jProd.put(ja);
                ja = null;
            }

            jPordObj.put("num", order);
            jPordObj.put("date", data);
            jPordObj.put("Status", Status);
            jPordObj.put("sumCount", sumCount);
            jPordObj.put("sumPrice", sumPrice);
            jPordObj.put("products", jProd);
            data = null;
            Status = null;
            jProd = null;

            jPordArr.put(jPordObj);
            jPordObj = null;
        }

        model.addAttribute("ordersObj", jPordArr.toList());
        model.addAttribute("orders", orderRepository.findAll());




        model.addAttribute("orders", orderList);
        return "/user/orders";
    }
}
