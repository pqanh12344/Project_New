package com.example.project_exam.controllers.user;

import com.example.project_exam.models.User;
import com.example.project_exam.services.AbService;
import com.example.project_exam.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/user")
public class IIUserController implements IUserController<User> {
	public static final Pattern p = Pattern.compile("/^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$/");
	
    @Autowired
    UserService userService;

    @Override
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("obj", new User());
        return "user/login";
    }

    @Override
    @PostMapping("/do-login")
    public String doLogin(Long h, Model model, User user, RedirectAttributes redirectAttributes, @RequestParam(name = "page", defaultValue = "1") int page) {
        AbService.PagingResult pageResult = userService.getService().getPaginate(page);
        ArrayList<User> users = pageResult.getList();
        int k = 0;
        for(int i = 0; i < users.size(); i++){
            if((users.get(i).getName().compareTo(user.getName()) == 0) && (users.get(i).getId_number().compareTo(user.getId_number()) == 0)){
                h = users.get(i).getId();
                k++;
                redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công");
                break;
            }
        }
        if(k == 0){
            redirectAttributes.addFlashAttribute("error", "Đăng nhập thất bại");
        }
        return "redirect:/user/HomePage?id=" + h;
    }

    @Override
    @GetMapping("/edit")
    public String edit(Model model, @RequestParam Long id) {
        model.addAttribute("obj3", userService.getService().getById(id));
        return "user/edit";
    }

    @Override
    @PostMapping("/do-edit")
    public String doEdit(User user, RedirectAttributes redirectAttributes) {
        String len = user.getId_number();
        if(len.length() >= 8){
            int s1 = 0, s2 = 0, s3 = 0;
            for(int i = 0; i < len.length(); i++){
                if(len.charAt(i) >= 'a' && len.charAt(i) <= 'z'){
                    s1++;
                }else if(len.charAt(i) >= 'A' && len.charAt(i) <= 'Z'){
                    s2++;
                }else if(len.charAt(i) >= '0' && len.charAt(i) <= '9'){
                    s3++;
                }
            }
            if((s1 >= 1) && (s2 >= 1) && (s3 >= 1)){
                String name = user.getName();
                if(name.length() > 0){
                    String temp = user.getPassword();
                    int g1 = 0, g2 = 0, g3 = 0, g4 = 0;
                    if(temp.length() >= 8){
                        for(int j = 0; j < temp.length(); j++){
                            if(temp.charAt(j) >= 'a' && temp.charAt(j) <= 'z'){
                                g1++;
                            }else if(temp.charAt(j) >= 'A' && temp.charAt(j) <= 'Z'){
                                g2++;
                            }else if(temp.charAt(j) >= '0' && temp.charAt(j) <= '9'){
                                g3++;
                            }else if(temp.charAt(j) == '!' || temp.charAt(j) == '@' || temp.charAt(j) == '#' || temp.charAt(j) == '$'
                                || temp.charAt(j) == '%' || temp.charAt(j) == '^' || temp.charAt(j) == '&' || temp.charAt(j) == '*'
                                || temp.charAt(j) == '.' || temp.charAt(j) == '(' || temp.charAt(j) == ')' || temp.charAt(j) == '+'
                                    || temp.charAt(j) == '-' || temp.charAt(j) == '[' || temp.charAt(j) == ']' || temp.charAt(j) == '{'
                                    || temp.charAt(j) == '}' || temp.charAt(j) == '<' || temp.charAt(j) == '>' || temp.charAt(j) == '?'
                                    || temp.charAt(j) == ';' || temp.charAt(j) == ':'){
                                g4++;
                            }
                        }
                        if((g1 >= 1) && (g2 >= 1) && (g3 >= 1) && (g4 >= 1)){
                            String mail = user.getEmail();
                            Matcher matcher = p.matcher(mail);
                                        if(!matcher.find()){
                                            redirectAttributes.addFlashAttribute("error4", "Mail không hợp lệ!");
                                        }else{
                                            String res = user.getPhone_number();
                                            if(res.length() == 13){
                                                int y = 0;
                                                if(res.charAt(3) != '-' || res.charAt(8) != '-') y++;
                                                for(int r = 0; r < res.length(); r++){
                                                    if(r != 3 && r != 8){
                                                        if(res.charAt(r) >= '0' && res.charAt(r) <= '9'){
                                                            continue;
                                                        }else y++;
                                                    }
                                                }
                                                if(y > 0){
                                                    redirectAttributes.addFlashAttribute("error5", "SĐT có phải dạng 010-0000-0000");
                                                }else{
                                                    String dc = user.getAddress();
                                                    if(dc.length() > 0){
														
															if(userService.getService().save(user))
															     redirectAttributes.addFlashAttribute("success", "Sửa thành công");
															else redirectAttributes.addFlashAttribute("error", "Sửa thất bại");
                                                    }else{
                                                        redirectAttributes.addFlashAttribute("error5", "Địa chỉ không được bỏ trống!");
                                                    }
                                                }
                                            }else{
                                                redirectAttributes.addFlashAttribute("error5", "SĐT có phải dạng 010-0000-0000");
                                            }
                                        }
                        }else{
                            redirectAttributes.addFlashAttribute("error3", "Password Phải chứa ít nhất 8 ký " +
                                    "tự ban gồm ít nhất 1 chữ thường , 1 chữ hoa và 1 số và 1 ký tự đặc biệt.");
                        }
                    }
                }else{
                    redirectAttributes.addFlashAttribute("error2", "Tên người dùng không được để trống.");
                }
            }else{
                redirectAttributes.addFlashAttribute("error1", "ID Phải chứa ít nhất 8 ký " +
                        "tự ban gồm ít nhất 1 chữ thường , 1 chữ hoa và 1 số");
            }
        }else{
            redirectAttributes.addFlashAttribute("error1", "ID Phải chứa ít nhất 8 ký " +
                                           "tự ban gồm ít nhất 1 chữ thường , 1 chữ hoa và 1 số");
        }

        return "redirect:/user/show";
    }
}
