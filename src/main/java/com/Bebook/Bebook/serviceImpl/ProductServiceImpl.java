package com.Bebook.Bebook.serviceImpl;

import com.Bebook.Bebook.JWT.JwtFilter;
import com.Bebook.Bebook.POJO.Category;
import com.Bebook.Bebook.POJO.Product;
import com.Bebook.Bebook.constants.BookConstants;
import com.Bebook.Bebook.dao.ProductDao;
import com.Bebook.Bebook.service.ProductService;
import com.Bebook.Bebook.utils.BookUtils;
import com.Bebook.Bebook.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    ProductDao productDao;


    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap, false)){
                    productDao.save(getProductFromMap(requestMap, false));
                    return BookUtils.getResponseEntity("Product Added SuccessFully", HttpStatus.OK);

                }
                return BookUtils.getResponseEntity(BookConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);

            }
            return BookUtils.getResponseEntity(BookConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);

        }catch (Exception ex){
            ex.printStackTrace();

        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);

    }




    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId){
                return true;
            } else if (!validateId) {
                return true;
                
            }
        }
        return false;
    }
    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {

        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));

        Product product = new Product();
        if (isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;

    }
    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
            return new ResponseEntity<>(productDao.getAllProduct(), HttpStatus.OK);

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap, true)){
                    Optional<Product> optional =productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (!optional.isEmpty()){
                        Product product = getProductFromMap(requestMap, true);
                        product.setStatus(optional.get().getStatus());
                        productDao.save(product);
                        return BookUtils.getResponseEntity("Product Update Successfully", HttpStatus.OK);

                    }else {
                        return BookUtils.getResponseEntity("Product Id does not exist", HttpStatus.OK);
                    }

                }else {
                    return BookUtils.getResponseEntity(BookConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }

            }else {
                return BookUtils.getResponseEntity(BookConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if (jwtFilter.isAdmin()){
               Optional optional= productDao.findById(id);
               if (!optional.isEmpty()){
                   productDao.deleteById(id);
                   return BookUtils.getResponseEntity("Product deleted successfully.", HttpStatus.OK);
               }
               return BookUtils.getResponseEntity("Product id does not exist",HttpStatus.OK);

            }else {
                BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
               Optional optional= productDao.findById(Integer.parseInt(requestMap.get("id")));
               if (!optional.isEmpty()){
                   productDao.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                   return BookUtils.getResponseEntity("Product Status Updated Successfully", HttpStatus.OK);

               }
               return BookUtils.getResponseEntity("Product id does not exist", HttpStatus.OK);

            }else {
                return BookUtils.getResponseEntity(BookConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductByCategory(id), HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductById(id), HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
