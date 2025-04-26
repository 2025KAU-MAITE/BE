package maite.maite.web.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SocialSignupController {

    @GetMapping("/auth/additional-info")
    public String showAdditionalInfoPage(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String provider,
            Model model) {

        // 모델에 속성 추가
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        model.addAttribute("provider", provider);

        // 템플릿 이름 반환 (확장자 제외)
        return "additional-info-form";
    }
}