package com.Bebook.Bebook.restImpl;

import com.Bebook.Bebook.JWT.JwtFilter;
import com.Bebook.Bebook.constants.BookConstants;
import com.Bebook.Bebook.rest.ProductRest;
import com.Bebook.Bebook.service.ProductService;
import com.Bebook.Bebook.utils.BookUtils;
import com.Bebook.Bebook.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ProductRestImpl implements ProductRest {

   @Autowired
    ProductService productService;
    @Autowired
    private JwtFilter jwtFilter;


    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            return productService.addNewProduct(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
            return productService.getAllProduct();

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
      try {
         return productService.updateProduct(requestMap);

      }catch (Exception ex) {
          ex.printStackTrace();
      }
      return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            return productService.deleteProduct(id);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){

            }else{
                return BookUtils.getResponseEntity(BookConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            return productService.updateStatus(requestMap);

        }catch (Exception e){
            e.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try {
            return productService.getByCategory(id);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try {
            return productService.getProductById(id);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
