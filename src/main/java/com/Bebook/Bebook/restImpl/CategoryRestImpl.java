package com.Bebook.Bebook.restImpl;

import com.Bebook.Bebook.POJO.Category;
import com.Bebook.Bebook.constants.BookConstants;
import com.Bebook.Bebook.rest.CategoryRest;
import com.Bebook.Bebook.service.CategoryService;
import com.Bebook.Bebook.utils.BookUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryRestImpl implements CategoryRest {

    @Autowired
    CategoryService categoryService;


    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            return categoryService.addNewCategory(requestMap);
        }catch (Exception ex){

        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @Override
//    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
//        try {
//            categoryService.getAllCategory(filterValue);
//
//        }catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
//        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
public ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String filterValue) {
    try {
        List<Category> categories = categoryService.getAllCategory(filterValue).getBody();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    } catch (Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            return categoryService.updateCategory(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
