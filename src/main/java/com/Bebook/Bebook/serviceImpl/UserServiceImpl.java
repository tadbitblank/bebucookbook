package com.Bebook.Bebook.serviceImpl;

import com.Bebook.Bebook.JWT.CustomerUsersDetailServices;
import com.Bebook.Bebook.JWT.JwtFilter;
import com.Bebook.Bebook.JWT.JwtUtil;
import com.Bebook.Bebook.POJO.User;
import com.Bebook.Bebook.constants.BookConstants;
import com.Bebook.Bebook.dao.UserDao;
import com.Bebook.Bebook.service.UserService;
import com.Bebook.Bebook.utils.BookUtils;
import com.Bebook.Bebook.utils.EmailUtils;
import com.Bebook.Bebook.wrapper.UserWrapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    EmailUtils emailUtils;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private CustomerUsersDetailServices customerUsersDetailServices;




    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup{}",  requestMap);
        try{
        if(validateSignUpMap(requestMap)){
            User user = userDao.findByEmailId(requestMap.get("email"));
            if(Objects.isNull(user)){
                userDao.save(getUserFromMap(requestMap));
                return BookUtils.getResponseEntity("Successfully Registered.", HttpStatus.OK);
            }else
            {
                return BookUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
            }

        }else {
            return BookUtils.getResponseEntity(BookConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }
    }catch(Exception ex){
        ex.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private boolean validateSignUpMap(Map<String, String> requestMap){
       if(requestMap.containsKey("name")
               && requestMap.containsKey("contactNumber")
               && requestMap.containsKey("email")
               && requestMap.containsKey("password"))
        {
            return true;
        }
        return false;
    }
    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }
    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
       log.info("Inside Login");
       try {
           Authentication auth = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(requestMap.get("email")
                   ,requestMap.get("password")));
           if(auth.isAuthenticated()){
               if(customerUsersDetailServices.getUserDetail().getStatus().equalsIgnoreCase("true")){
                   return new ResponseEntity<String>("{\"token\":\"" + jwtUtil.generateToken(customerUsersDetailServices.getUserDetail().getEmail(),
                           customerUsersDetailServices.getUserDetail().getRole()) + "\"}", HttpStatus.OK);
               }
               else {
                   return new ResponseEntity<String>("{\"message\":\""+"Wait for admin approval."+"\"}", HttpStatus.BAD_REQUEST);
               }
           }

       }catch (Exception ex){
           log.error("Exception during login process:", ex);
       }
       return new ResponseEntity<String>("{\"message\":\""+"Wrong Credentials."+"\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);

            }else{
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty()){
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
                    return BookUtils.getResponseEntity("User status updated Succesfully", HttpStatus.OK);

                }else{
                  return   BookUtils.getResponseEntity("User id does not exist", HttpStatus.OK);
                }

            }else {
                return BookUtils.getResponseEntity(BookConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private void sendMailToAllAdmin(String status,String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status != null && status.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER:- "+user +"\n is approved by \nADMIN:- " + jwtFilter.getCurrentUser(), allAdmin);

        }else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "USER:- "+user +"\n is disabled by \nADMIN:- " + jwtFilter.getCurrentUser(), allAdmin);
        }
    }
    @Override
    public ResponseEntity<String> checkToken() {
        return BookUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
            if(!userObj.equals(null)){
                if(userObj.getPassword().equals(requestMap.get("oldPassword"))){
                    userObj.setPassword(requestMap.get("newPassword"));
                    userDao.save(userObj);
                    return BookUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
                }
                return BookUtils.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);

            }
            return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user= userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail()))
                emailUtils.forgotMail(user.getEmail(), "Credential by Bebu CookBook", user.getPassword());

            return BookUtils.getResponseEntity("Check your mail for credentials.", HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookUtils.getResponseEntity(BookConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
