package org.example.rocksdb.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rocksdb.model.Product;
import org.example.rocksdb.model.User;
import org.example.rocksdb.service.RocksDbService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebController {

  private final RocksDbService rocksDbService;

  @GetMapping("/")
  public String index(Model model) {
    long userCount = rocksDbService.countUsers();
    long productCount = rocksDbService.countProducts();

    model.addAttribute("userCount", userCount);
    model.addAttribute("productCount", productCount);

    return "index";
  }

  // User 관리 화면
  @GetMapping("/users")
  public String userList(Model model, @RequestParam(required = false) String prefix) {
    List<User> users;
    if (prefix != null && !prefix.isEmpty()) {
      users = rocksDbService.getUsersByPrefix(prefix);
      model.addAttribute("prefix", prefix);
    } else {
      users = rocksDbService.getAllUsers();
    }
    model.addAttribute("users", users);
    model.addAttribute("user", new User());
    return "users";
  }

  @PostMapping("/users/save")
  public String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
    try {
      if (user.getId() == null || user.getId().trim().isEmpty()) {
        redirectAttributes.addFlashAttribute("message", "ID는 필수 입력 항목입니다.");
        redirectAttributes.addFlashAttribute("messageType", "error");
        return "redirect:/users";
      }
      if (user.getName() == null || user.getName().trim().isEmpty()) {
        redirectAttributes.addFlashAttribute("message", "이름은 필수 입력 항목입니다.");
        redirectAttributes.addFlashAttribute("messageType", "error");
        return "redirect:/users";
      }
      rocksDbService.saveUser(user.getId(), user);
      redirectAttributes.addFlashAttribute("message", "사용자가 저장되었습니다.");
      redirectAttributes.addFlashAttribute("messageType", "success");
    } catch (Exception e) {
      log.error("Failed to save user", e);
      redirectAttributes.addFlashAttribute("message", "저장 실패: " + e.getMessage());
      redirectAttributes.addFlashAttribute("messageType", "error");
    }
    return "redirect:/users";
  }

  @GetMapping("/users/delete/{id}")
  public String deleteUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
    try {
      rocksDbService.deleteUser(id);
      redirectAttributes.addFlashAttribute("message", "사용자가 삭제되었습니다.");
      redirectAttributes.addFlashAttribute("messageType", "success");
    } catch (Exception e) {
      log.error("Failed to delete user", e);
      redirectAttributes.addFlashAttribute("message", "삭제 실패: " + e.getMessage());
      redirectAttributes.addFlashAttribute("messageType", "error");
    }
    return "redirect:/users";
  }

  // Product 관리 화면
  @GetMapping("/products")
  public String productList(Model model, @RequestParam(required = false) String prefix) {
    List<Product> products;
    if (prefix != null && !prefix.isEmpty()) {
      products = rocksDbService.getProductsByPrefix(prefix);
      model.addAttribute("prefix", prefix);
    } else {
      products = rocksDbService.getAllProducts();
    }
    model.addAttribute("products", products);
    model.addAttribute("product", new Product());
    return "products";
  }

  @PostMapping("/products/save")
  public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
    try {
      if (product.getId() == null || product.getId().trim().isEmpty()) {
        redirectAttributes.addFlashAttribute("message", "ID는 필수 입력 항목입니다.");
        redirectAttributes.addFlashAttribute("messageType", "error");
        return "redirect:/products";
      }
      if (product.getName() == null || product.getName().trim().isEmpty()) {
        redirectAttributes.addFlashAttribute("message", "상품명은 필수 입력 항목입니다.");
        redirectAttributes.addFlashAttribute("messageType", "error");
        return "redirect:/products";
      }
      rocksDbService.saveProduct(product.getId(), product);
      redirectAttributes.addFlashAttribute("message", "상품이 저장되었습니다.");
      redirectAttributes.addFlashAttribute("messageType", "success");
    } catch (Exception e) {
      log.error("Failed to save product", e);
      redirectAttributes.addFlashAttribute("message", "저장 실패: " + e.getMessage());
      redirectAttributes.addFlashAttribute("messageType", "error");
    }
    return "redirect:/products";
  }

  @GetMapping("/products/delete/{id}")
  public String deleteProduct(@PathVariable String id, RedirectAttributes redirectAttributes) {
    try {
      rocksDbService.deleteProduct(id);
      redirectAttributes.addFlashAttribute("message", "상품이 삭제되었습니다.");
      redirectAttributes.addFlashAttribute("messageType", "success");
    } catch (Exception e) {
      log.error("Failed to delete product", e);
      redirectAttributes.addFlashAttribute("message", "삭제 실패: " + e.getMessage());
      redirectAttributes.addFlashAttribute("messageType", "error");
    }
    return "redirect:/products";
  }
}
