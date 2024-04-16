package Intellect.com.Service;

import java.util.Map;

import Intellect.com.dto.LoginDto;
import Intellect.com.dto.RegisterDto;
import Intellect.com.dto.ResetPwdDto;
import Intellect.com.dto.UserDto;

public interface UserService {
	public Map<Integer,String> getCountries();

	public Map<Integer,String> getStates(Integer cid);

	public Map<Integer,String> getCities(Integer sid);

	public UserDto getUser(String email);

	public boolean registerUser(RegisterDto regDto);

	public UserDto getUser(LoginDto loginDto);

	public boolean resetPwd(ResetPwdDto pwdDto);

	public String getQuote();
}
