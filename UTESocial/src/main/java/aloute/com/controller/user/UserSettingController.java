package aloute.com.controller.user;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import aloute.com.util.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserSettingController {
	@GetMapping("/setting")
	public String showUploadForm(Model model, HttpSession session)
	{
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
		return "user/setting";
	}

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/setting/images")
    @ResponseBody
    public ResponseEntity<String> uploadImages(
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
            @RequestParam(value = "cover", required = false) MultipartFile coverFile,
            HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");

            // =============================
            // 📂 Xử lý ảnh đại diện
            // =============================
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarDir = "uploads/avatars/" + user.getNameUser();
                Path avatarPath = Paths.get(avatarDir);
                if (!Files.exists(avatarPath)) Files.createDirectories(avatarPath);

                String avatarName = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
                Path avatarFilePath = avatarPath.resolve(avatarName);
                Files.write(avatarFilePath, avatarFile.getBytes());

                String avatarUrl = "/" + avatarDir + "/" + avatarName;
                user.setAvatar(avatarUrl);
            }

            // =============================
            // 📂 Xử lý ảnh bìa
            // =============================
            if (coverFile != null && !coverFile.isEmpty()) {
                String coverDir = "uploads/backgrounds/" + user.getNameUser();
                Path coverPath = Paths.get(coverDir);
                if (!Files.exists(coverPath)) Files.createDirectories(coverPath);

                String coverName = System.currentTimeMillis() + "_" + coverFile.getOriginalFilename();
                Path coverFilePath = coverPath.resolve(coverName);
                Files.write(coverFilePath, coverFile.getBytes());

                String coverUrl = "/" + coverDir + "/" + coverName;
                user.setBackground(coverUrl);
            }

            // ✅ Lưu DB và cập nhật session
            userRepository.save(user);
            session.setAttribute("user", user);

            return ResponseEntity.ok("Cập nhật ảnh thành công!");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Lỗi khi lưu ảnh: " + e.getMessage());
        }
    }
    @PostMapping("/setting/info")
    public String handleSetInfo(@RequestParam String fullName,
    							@RequestParam String introduce,
    							@RequestParam String location,
    							@RequestParam LocalDate birthday,
    							HttpSession session)
    {
    	User user = (User) session.getAttribute("user");
    	user.setFullName(fullName);
    	user.setIntroduce(introduce);
    	user.setLocation(location);
    	user.setBirthday(birthday);
    	userRepository.save(user);
    	return "redirect:/profile";
    }
    @PostMapping("/setting/password")
    public String handlePassword(@RequestParam String currentPassword,
    							@RequestParam String newPassword,
    							@RequestParam String confirmPassword,
    							HttpSession session,
    							RedirectAttributes redirectAttributes)
    {
    	User user = (User) session.getAttribute("user");
    	if (!PasswordUtil.hashPassword(currentPassword).equals(user.getPasswordHash()))
    	{
    		
    		redirectAttributes.addFlashAttribute("errorPass", "Mật khẩu không đúng vui lòng nhập lại!");
    		return "redirect:/setting#security";
    	}
    	if (!newPassword.equals(confirmPassword))
    	{
    		redirectAttributes.addFlashAttribute("errorPass", "Vui lòng nhập mật khẩu xác nhận đúng với mật khẩu đã nhập!");
    		return "redirect:/setting#security";    		
    	}
    	user.setPasswordHash(PasswordUtil.hashPassword(confirmPassword));
    	userRepository.save(user);
    	return "redirect:/profile";
    }
}
