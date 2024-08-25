package com.Bebook.Bebook.restImpl;

import com.Bebook.Bebook.POJO.Bill;
import com.Bebook.Bebook.constants.BookConstants;
import com.Bebook.Bebook.rest.BillRest;
import com.Bebook.Bebook.service.BillService;
import com.Bebook.Bebook.utils.BookUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BillRestImpl implements BillRest {

    @Autowired
    BillService billService;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try {
            return billService.generateReport(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        try {
            return billService.getBills();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try {
            return billService.getPdf(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            return billService.deleteBill(id);

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
