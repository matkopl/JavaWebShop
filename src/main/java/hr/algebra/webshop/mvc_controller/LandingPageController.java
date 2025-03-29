package hr.algebra.webshop.mvc_controller;

import hr.algebra.webshop.dto.ProductDto;
import hr.algebra.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class LandingPageController {

    private final ProductService productService;

    @GetMapping("/")
    public String showLandingPage(Model model) {
        List<ProductDto> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "landing";
    }
}
