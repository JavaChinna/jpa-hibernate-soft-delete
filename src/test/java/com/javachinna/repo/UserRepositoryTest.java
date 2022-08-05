package com.javachinna.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import com.javachinna.model.Role;
import com.javachinna.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataJpaTest
@Sql(scripts = "classpath:insert-scripts.sql")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	void testSoftDelete() {
		// Add a new user
		User user = new User();
		user.setDisplayName("Chinna");
		user.setEmail("java4chinna@gmail.com");
		// Add a role to the user
		Role adminRole = roleRepository.findByName(Role.ROLE_ADMIN);
		user.addRole(roleRepository.findByName(Role.ROLE_USER));
		user.addRole(adminRole);
		user = userRepository.saveAndFlush(user);
		assertEquals(2, user.getRoles().size());
		// Remove a role from the user		
		user.removeRole(adminRole);
		userRepository.saveAndFlush(user);

		user = userRepository.findByEmail("java4chinna@gmail.com");
		
		assertEquals(1, user.getRoles().size());
		log.info("User Roles after removal: {}", user.getRoles().stream().map(r -> r.getRole().getName()).collect(Collectors.toList()));
		userRepository.delete(user);
		assertEquals(0, userRepository.findAll().size());
	}
}
