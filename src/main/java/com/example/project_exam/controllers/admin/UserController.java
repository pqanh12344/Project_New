package com.example.project_exam.controllers.admin;

import com.example.project_exam.models.User;
import com.example.project_exam.services.AbService;
import com.example.project_exam.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/admin/user")
public class UserController implements IAdminController<User>{
	public static final Pattern p = Pattern.compile("/^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$/");
	
    @Value("${config.upload_folder}")
    String UPLOADED_FOLDER;

    @Autowired
    UserService userService;

    @Override
    @GetMapping("")
    public String list(Model model, @RequestParam(name = "page", defaultValue = "1") int page) {
        AbService.PagingResult pageResult = userService.getService().getPaginate(page);
        model.addAttribute("pageResult", pageResult);
        return "admin/user/list";
    }

    @Override
    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("obj", new User());
        return "admin/user/add";
    }

    @Override
    @GetMapping("/edit")
    public String edit(Model model, @RequestParam Long id) {
        model.addAttribute("obj", userService.getService().getById(id));
        return "admin/user/edit";
    }

    @Override
    @PostMapping("/do-add")
    public String doAdd(User user, RedirectAttributes redirectAttributes, @RequestParam(name = "file") MultipartFile file) {
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
                            //Pattern p = Pattern.compile("/^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$/");
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
														try {

															Date date = new Date();
															LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
															int year = localDate.getYear();
															int month = localDate.getMonthValue();

															String saveFolder = UPLOADED_FOLDER + month + "_" + year + "/";

															File dir = new File(saveFolder);
															if (dir.isFile() || !dir.exists()) {
																dir.mkdir();
															}

															String filename = System.currentTimeMillis() + file.getOriginalFilename();

															byte[] bytes = file.getBytes();
															Path path = Paths.get(dir.getAbsolutePath() + "/" + filename);
															Files.write(path, bytes);
															user.setEmoji(saveFolder.replace(UPLOADED_FOLDER, "") + filename);
															userService.getService().save(user);
															redirectAttributes.addFlashAttribute("success", "Đăng ký thành công");
														} catch (Exception e) {
															e.printStackTrace();
															redirectAttributes.addFlashAttribute("error", "Đăng ký thất bại");
														}
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
        return "redirect:/admin/user/add";
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

        return "redirect:/admin/user/";
    }

    @Override
    @GetMapping("/delete")
    public String delete(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/user/";
    }
}
