package Intellect.com.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import Intellect.com.Repository.CityRepo;
import Intellect.com.Repository.CountryRepo;
import Intellect.com.Repository.StateRepo;
import Intellect.com.Repository.UserRepo;
import Intellect.com.Utils.EmailUtils;
import Intellect.com.dto.LoginDto;
import Intellect.com.dto.QuoteDto;
import Intellect.com.dto.RegisterDto;
import Intellect.com.dto.ResetPwdDto;
import Intellect.com.dto.UserDto;
import Intellect.com.entity.CityEntity;
import Intellect.com.entity.CountryEntity;
import Intellect.com.entity.StateEntity;
import Intellect.com.entity.UserEntity;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;
	@Autowired
	private CountryRepo countryRepo;

	@Autowired
	private StateRepo stateRepo;

	@Autowired
	private CityRepo cityRepo;

	@Autowired
	private EmailUtils emailUtils;

	private QuoteDto[] quotations =null;
	@Override
	public Map<Integer, String> getCountries() {
		Map<Integer, String> countryMap = new HashMap<>();
		List<CountryEntity> countryList = countryRepo.findAll();
		countryList.forEach(c -> {
			countryMap.put(c.getCountryId(), c.getCountryName());
		});

		return null;
	}

	@Override
	public Map<Integer, String> getStates(Integer cid) {

		Map<Integer, String> stateMap = new HashMap<>();

		/*
		 * Map<Integer, String >stateMap=new HashMap<>();
		 * 
		 * CountryEntity country=new CountryEntity();
		 * 
		 * country.setCountryId(cid);
		 * 
		 * StateEntity state=new StateEntity(); state.setCountry(country);
		 * Example<StateEntity> of = Example.of(state); List<StateEntity> stateList =
		 * stateRepo.findAll(of);
		 * stateList.forEach(c->{stateMap.put(c.getStateId(),c.getStateName());
		 * 
		 * }); return stateMap;
		 */

		List<StateEntity> statesList = stateRepo.getStates(cid);
		statesList.forEach(c -> {
			stateMap.put(c.getStateId(), c.getStateName());

		});
		return stateMap;
	}

	@Override
	public Map<Integer, String> getCities(Integer sid) {
		Map<Integer, String> cityMap = new HashMap<>();

		List<CityEntity> citiesList = cityRepo.getCities(sid);
		citiesList.forEach(p -> {
			cityMap.put(p.getCityId(), p.getCityName());

		});
		return cityMap;
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepo.findByEmail(email);
		/*
		 * UserDto dto=new UserDto();
		 *  BeanUtils.copyProperties(userEntity, dto); return
		 * dto;
		 */
		if(userEntity==null) {
			return null;  
		}
		
		ModelMapper mapper = new ModelMapper();
		UserDto userDto = mapper.map(userEntity, UserDto.class);
		return userDto;
	}

	@Override
	public boolean registerUser(RegisterDto regDto) {
		ModelMapper mapper = new ModelMapper();
		
		UserEntity entity = mapper.map(regDto, UserEntity.class);
		
		CountryEntity country = countryRepo.findById(regDto.getCountryId()).orElseThrow();
		StateEntity state = stateRepo.findById(regDto.getStateId()).orElseThrow();
		CityEntity city = cityRepo.findById(regDto.getCityId()).orElseThrow();
		
		  entity.setCountry(country);
		entity.setState(state);
		entity.setCity(city);
		
		entity.setPwd(generateRandom());
        entity.setPwdUpdated("NO");
		// user registration

		UserEntity saveEntity = userRepo.save(entity);

		String subject = "User Registration";

		String body = "your temporary Pwd is" + entity.getPwd();

		emailUtils.sendEmail(regDto.getEmail(), subject, body);

		return saveEntity.getUserId() != null;

	}

	@Override
	public UserDto getUser(LoginDto loginDto) {
		UserEntity userEntity = userRepo.findByEmailAndPwd(loginDto.getEmail(), loginDto.getPwd());
		if (userEntity == null) {
			return null;

		}

		ModelMapper mapper = new ModelMapper();

		return mapper.map(userEntity, UserDto.class);

	}

	@Override
	public boolean resetPwd(ResetPwdDto pwdDto) {
		UserEntity userEntity = userRepo.findByEmailAndPwd(pwdDto.getEmail(), pwdDto.getOldPwd());

		if (userEntity != null) {
			userEntity.setPwd(pwdDto.getNewPwd());
			userEntity.setPwdUpdated("YES");
			userRepo.save(userEntity);

			return true;
		}

		return false;

	}

	@Override
	public String getQuote() {
		
		if(quotations==null) {
			 String url="https://type.fit/api/quotes";
	          //webservice Call
	          
	          
	          RestTemplate req=new RestTemplate();
	          ResponseEntity<String> forEntity = req.getForEntity(url, String.class);
	          String responseBody = forEntity.getBody();
	          ObjectMapper mapper=new ObjectMapper();
	          
	          
	          try {

	              quotations = mapper.readValue(responseBody, QuoteDto[].class);
	              
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
         
          Random r=new Random();
          int index = r.nextInt(quotations.length-1);
          
		return quotations[index].getText();
	}

	private static String generateRandom() {
		String aToZ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

		Random rand = new Random();
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			int randIndex = rand.nextInt(aToZ.length());
			res.append(aToZ.charAt(randIndex));
		}
		return res.toString();
	}

}
