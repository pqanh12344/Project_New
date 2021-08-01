package com.example.project_exam.controllers.user;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface IUserController<T> {
    public String login(Model model);
    public String doLogin(Long h, Model model, T obj, RedirectAttributes redirectAttributes, int page);
    public String edit(Model model, Long id);
    public String doEdit(T obj,RedirectAttributes redirectAttributes);
}
