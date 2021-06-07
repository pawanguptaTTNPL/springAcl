package com.example.springacl.repositories;



        import com.example.springacl.entities.Product;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.security.access.prepost.PostAuthorize;
        import org.springframework.security.access.prepost.PostFilter;

        import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @PostFilter("hasPermission(filterObject, 'READ')")
    List<Product> findAll();

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    Product findById(int id);

}