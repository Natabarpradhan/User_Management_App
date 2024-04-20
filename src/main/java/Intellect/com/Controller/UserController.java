package Intellect.com.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import Intellect.com.Service.UserService;
import Intellect.com.Utils.AppConstants;
import Intellect.com.Utils.AppProperties;
import Intellect.com.dto.LoginDto;
import Intellect.com.dto.RegisterDto;
import Intellect.com.dto.ResetPwdDto;
import Intellect.com.dto.UserDto;

@Controller
public class UserController {
	
	@Autowired
	private AppProperties props;

	@Autowired
	private UserService userService;

	@GetMapping("/register")
	public String registerPage(Model model) {

		model.addAttribute("registerDto", new RegisterDto());
		model.addAttribute("countries", userService.getCountries());

		return "registerView";
	}

	@GetMapping("/states/{cid}")
	@ResponseBody
	public Map<Integer, String> getStates(@PathVariable("cid") Integer cid) {

		return userService.getStates(cid);
	}

	@GetMapping("/cities/{sid}")
	@ResponseBody
	public Map<Integer, String> getCities(@PathVariable("sid") Integer sid) {
		return userService.getCities(sid);
	}

	@PostMapping("/register")
	public String register(RegisterDto regDto, Model model) {
		model.addAttribute("countries", userService.getCountries());
		Map<String, String> messages = props.getMessages();
		
		UserDto user = userService.getUser(regDto.getEmail());
		if (user != null) {
			model.addAttribute(AppConstants.ERROR_MSG, messages.get(AppConstants.DUP_EMAIL));
			return "registerView";
		}
		boolean registerUser = userService.registerUser(regDto);
		if (registerUser) {
			
			model.addAttribute(AppConstants.SUCC_MSG, messages.get(AppConstants.REG_SUCC));
		} else {
			model.addAttribute(AppConstants.ERROR_MSG, messages.get(AppConstants.REG_FAIL));

		}
		
		return "registerView";
	}

	@GetMapping("/")
	public String loginPage(Model model) {
		
		model.addAttribute("loginDto", new LoginDto());
		return "index";
	}

	@PostMapping("/login")
	public String login(LoginDto loginDto, Model model) {
		Map<String, String> messages= props.getMessages();
		UserDto user = userService.getUser(loginDto);

		if (user == null) {
			model.addAttribute(AppConstants.ERROR_MSG, messages.get("invalidCredentials"));
			return "index";
		}

		if ("YES".equals(user.getPwdUpdated())) {
			// pwd already updated go to dashboard

			return "redirect:dashboard";
		} else {
			// pwd not updated go to reset pwd page
			ResetPwdDto resetPwdDto = new ResetPwdDto();
			resetPwdDto.setEmail(user.getEmail());

			model.addAttribute("resetPwdDto", resetPwdDto);
			return AppConstants.RESET_PWD_VIEW;
		}

	}

	@PostMapping("/resetPwt")
	public String resetPwd(ResetPwdDto pwdDto, Model model) {
		Map<String, String> messages= props.getMessages();
		if (!pwdDto.getNewPwd().equals(pwdDto.getConfirmPwd())) {
			model.addAttribute(AppConstants.ERROR_MSG,messages.get("pwdMatchErr"));
			return AppConstants.RESET_PWD_VIEW;

		}
		UserDto user = userService.getUser(pwdDto.getEmail());
		if (user.getPwd().equals(pwdDto.getOldPwd())) {
			boolean resetPwd = userService.resetPwd(pwdDto);
			if (resetPwd) {
				return "redirect:dashboard";

			} else {
				model.addAttribute(AppConstants.ERROR_MSG, messages.get(" pwdUpdateErr"));
				return AppConstants.RESET_PWD_VIEW;
			}
		} else {
			model.addAttribute(AppConstants.ERROR_MSG, messages.get(" oldPwdErr"));
			return AppConstants.RESET_PWD_VIEW;
		}

	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {

		String quote = userService.getQuote();
		model.addAttribute("quote", quote);

		return "dashboardView";
	}

	@GetMapping("/logout")
	public String logout() {
		return "redirect:/";
	}
}
