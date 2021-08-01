package com.example.project_exam.controllers.admin;

import com.example.project_exam.models.User;
import com.example.project_exam.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {
    @Autowired
    UserService userService;

    @GetMapping("/admin/layout")
    public String getLayout(){
        return "admin/layout";
    }

    @GetMapping("/user/HomePage")
    public String HomePage(Model model, @RequestParam Long id){
        model.addAttribute("obj1", userService.getService().getById(id));
        return "user/HomePage";
    }

    @GetMapping("/user/show")
    public String Show(Model model, @RequestParam Long id){
        model.addAttribute("obj2", userService.getService().getById(id));
        return "user/show";
    }
}
