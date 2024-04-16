package Intellect.com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Intellect.com.entity.UserEntity;

public interface UserRepo  extends JpaRepository<UserEntity, Integer> {

	public UserEntity findByEmail(String email);
	
	public UserEntity findByEmailAndPwd(String email,String pwd);
}
