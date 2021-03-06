package com.example.project_exam.controllers.admin;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface IAdminController<T> {
    public String list(Model model, int page);
    public String add(Model model);
    public String edit(Model model, Long id);
    public String doAdd(T obj, RedirectAttributes redirectAttributes, MultipartFile file);
    public String doEdit(T obj,RedirectAttributes redirectAttributes);
    public String delete(Long id);
}
