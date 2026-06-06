package com.aipo.weddingshop.controller;

import com.aipo.weddingshop.entity.Category;
import com.aipo.weddingshop.entity.Order;
import com.aipo.weddingshop.entity.Product;
import com.aipo.weddingshop.service.CategoryService;
import com.aipo.weddingshop.service.ProductService;
import com.aipo.weddingshop.service.BannerService;
import com.aipo.weddingshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BannerService bannerService;
    private final OrderService orderService; // Đảm bảo đã khai báo instance ở đây

    // 1. Home
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("activeBanners", bannerService.getActiveBannersForUser());
        model.addAttribute("categories", categoryService.findAll());
        return "customer/home";
    }

    // 2. All Categories
    @GetMapping("/categories")
    public String showAllCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "customer/categories";
    }

    // 3. Products by Category
    @GetMapping("/categories/{id}")
    public String showProductsByCategory(@PathVariable("id") Long categoryId, Model model) {
        Category currentCategory = categoryService.findById(categoryId);
        model.addAttribute("currentCategory", currentCategory);

        List<Product> products = productService.findByCategoryId(categoryId);
        model.addAttribute("products", products);

        return "customer/category-products";
    }

    // 4. Product Detail
    @GetMapping("/products/{id}")
    public String showProductDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "customer/product-detail";
    }

    // 5. Add to Cart (Xử lý tham số tránh cảnh báo "never used")
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("productSize") String productSize,
                            @RequestParam("quantity") Integer quantity) {

        // Tạm thời log ra console hoặc làm gì đó với biến để IntelliJ không báo lỗi "never used"
        System.out.println("Add Product ID: " + productId + " | Size: " + productSize + " | Qty: " + quantity);

        // TODO: Xử lý gọi CartService tại đây

        return "redirect:/customer/cart";
    }

    // 6. Order History (Sửa lỗi Static Context: gọi qua instance 'orderService')
    @GetMapping("/order-history")
    public String showOrderHistory(@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                                   Model model) {
        Long currentUserId = 1L;
        List<Order> orders;

        // SỬA TẠI ĐÂY: Thay vì gọi OrderService (Tên Class Static), ta gọi orderService (đối tượng được tiêm vào)
        if ("ALL".equalsIgnoreCase(status)) {
            orders = orderService.getOrdersByUserId(currentUserId);
        } else {
            orders = orderService.getOrdersByUserIdAndStatus(currentUserId, status);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);

        return "customer/order-history";
    }

    // 7. Order Detail (Sửa lỗi Static Context)
    @GetMapping("/order-detail/{id}")
    public String showOrderDetail(@PathVariable("id") Long orderId, Model model) {

        // SỬA TẠI ĐÂY: Gọi orderService.getOrderById thay vì Class static
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);

        return "customer/order-detail";
    }
}