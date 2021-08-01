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

@Controller
@RequestMapping("/user")
public class IIUserController implements IUserController<User> {
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
        if (userService.getService().save(user)) {
            redirectAttributes.addFlashAttribute("success", "Sửa thành công");
        } else {
            redirectAttributes.addFlashAttribute("error", "Sửa thất bại");
        }

        return "redirect:/user/show";
    }
}
