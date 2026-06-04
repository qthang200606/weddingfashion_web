package com.aipo.weddingshop.controller.customer;

import com.aipo.weddingshop.entity.Cart;
import com.aipo.weddingshop.entity.CartItem;
import com.aipo.weddingshop.entity.User;
import com.aipo.weddingshop.service.CartService;
import com.aipo.weddingshop.service.UserService; // 🌟 Import dịch vụ mới tạo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/customer/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService; // 🌟 Inject UserService vào đây

    // Hàm bổ trợ lấy User từ Spring Security thông qua Email định danh
    private User getLoggedInUser(Principal principal) {
        if (principal == null) {
            return null; // Trả về null nếu chưa đăng nhập
        }
        String email = principal.getName(); // Lấy email từ đối tượng Principal
        return userService.findByEmail(email); // Tìm thực thể User đầy đủ từ DB
    }

    // 1. HIỂN THỊ TRANG GIỎ HÀNG (cart.html)
    @GetMapping
    public String viewCart(Model model, Principal principal) {
        User user = getLoggedInUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartService.getCartByUser(user);

        double totalPrice = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        int totalQuantity = cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalQuantity", totalQuantity);

        return "customer/cart";
    }

    // 2. HÀM XỬ LÝ KHI BẤM "THÊM VÀO GIỎ HÀNG"
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("productSize") String productSize,
                            @RequestParam("quantity") Integer quantity,
                            Principal principal) { // 🌟 Dùng Principal thay cho HttpSession
        User user = getLoggedInUser(principal);
        if (user == null) {
            return "redirect:/login"; // Sẽ không bị đá về nữa nếu bạn đã login
        }

        cartService.addToCart(user, productId, productSize, quantity);
        return "redirect:/customer/cart";
    }

    // 3. CẬP NHẬT TĂNG/GIẢM SỐ LƯỢNG NGAY TẠI GIAO DIỆN GIỎ HÀNG
    @PostMapping("/update")
    public String updateCart(@RequestParam("cartItemId") Long cartItemId,
                             @RequestParam("action") String action,
                             Principal principal) {
        if (getLoggedInUser(principal) == null) return "redirect:/login";

        cartService.updateQuantity(cartItemId, action);
        return "redirect:/customer/cart";
    }

    // 4. XÓA BỎ HOÀN TOÀN MỘT MẪU VÁY KHỎI GIỎ HÀNG
    @GetMapping("/delete/{id}")
    public String deleteCartItem(@PathVariable("id") Long id, Principal principal) {
        if (getLoggedInUser(principal) == null) return "redirect:/login";

        cartService.deleteCartItem(id);
        return "redirect:/customer/cart";
    }
}