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
    // 1. HIỂN THỊ TRANG GIỎ HÀNG (cart.html)
    @GetMapping
    public String viewCart(Model model, Principal principal, jakarta.servlet.http.HttpSession session) { // 🌟 Thêm HttpSession vào tham số
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

        // 🌟 DÒNG QUAN TRỌNG: Ném con số thực này vào Session để Navbar ở đâu cũng đọc được!
        session.setAttribute("totalCartQuantity", totalQuantity);

        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalQuantity", totalQuantity);

        return "customer/cart";
    }

    // 2. HÀM XỬ LÝ KHI BẤM "THÊM VÀO GIỎ HÀNG" (CẬP NHẬT TRẢ VỀ SỐ LƯỢNG THỰC)
    @PostMapping("/add")
    @ResponseBody
    public Object addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "productSize", required = false, defaultValue = "M") String productSize,
                            @RequestParam(value = "quantity", required = false, defaultValue = "1") Integer quantity,
                            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                            Principal principal) {

        User user = getLoggedInUser(principal);
        if (user == null) {
            if ("XMLHttpRequest".equals(requestedWith)) {
                return org.springframework.http.ResponseEntity.status(401).body("Chưa đăng nhập");
            }
            return "redirect:/login";
        }

        // 1. Thực hiện thêm vào giỏ hàng
        cartService.addToCart(user, productId, productSize, quantity);

        // 2. Lấy giỏ hàng mới nhất để tính tổng số lượng thực tế
        Cart cart = cartService.getCartByUser(user);
        int totalQuantity = cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        // 🌟 BẮN SỐ THỰC VỀ CHO AJAX: Nếu đúng là request từ JavaScript
        if ("XMLHttpRequest".equals(requestedWith) || requestedWith != null) {
            return org.springframework.http.ResponseEntity.ok(totalQuantity); // Trả về con số, ví dụ: 3, 4, 5
        }

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