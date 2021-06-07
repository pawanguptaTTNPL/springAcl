package com.example.springacl.Controllers;


        import com.example.springacl.entities.Product;
        import com.example.springacl.repositories.ProductRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.PathVariable;
        import org.springframework.web.bind.annotation.RequestParam;
        import org.springframework.web.bind.annotation.RestController;

        import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/all")
    public List<Product> findAllProducts() {

        return productRepository.findAll();
    }


    @GetMapping("/product/{id}")
    public Product findOne(@PathVariable int id) {

        return productRepository.findById(id);
    }

}