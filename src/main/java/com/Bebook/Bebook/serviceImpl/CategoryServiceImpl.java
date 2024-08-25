package com.Bebook.Bebook.serviceImpl;

import com.Bebook.Bebook.JWT.JwtFilter;
import com.Bebook.Bebook.POJO.Category;
import com.Bebook.Bebook.constants.BookConstants;
import com.Bebook.Bebook.dao.CategoryDao;
import com.Bebook.Bebook.service.CategoryService;
import com.Bebook.Bebook.utils.BookUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDao categoryDao;
    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
       try {
           if (jwtFilter.isAdmin()){
               if (validateCategoryMap(requestMap, false)){
                   categoryDao.save(getCategoryFromMap(requestMap, false));
                   return BookUtils.getResponseEntity("Category Added Succesfully", HttpStatus.OK);
                   
               }

           }else {
               return BookUtils.getResponseEntity(BookConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
           }

       }catch (Exception ex){

       }
       return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id")){
                return true;
            }else if (!validateId) {
                return true;
            }
        }
        return false;
    }
    private Category getCategoryFromMap(Map<String, String> requestMap, boolean isAdd){
        Category category = new Category();
        if (isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

//    @Override
//    public ResponseEntity<List<Category>>getAllCategory(String filterValue) {
//        try {
//            if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
//                log.info("Inside if");
//                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(), HttpStatus.OK);
//            }
//            return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
//
//        }catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
//        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
@Override
public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
    List<Category> categories;
    if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
        categories = categoryDao.getAllCategory();
    } else {
        categories = categoryDao.findAll();
    }
    log.info("Fetched Categories: {}", categories);
    return new ResponseEntity<>(categories, HttpStatus.OK);
}

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                if (validateCategoryMap(requestMap, true)){
                    Optional optional =categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (!optional.isEmpty()){
                        categoryDao.save(getCategoryFromMap(requestMap, true));
                        return BookUtils.getResponseEntity("Category Updated Succesfully", HttpStatus.OK);

                    }else {
                        return BookUtils.getResponseEntity("Category id does not exist", HttpStatus.OK);
                    }
                }
                return BookUtils.getResponseEntity(BookConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);

            }else {
                return BookUtils.getResponseEntity(BookConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
