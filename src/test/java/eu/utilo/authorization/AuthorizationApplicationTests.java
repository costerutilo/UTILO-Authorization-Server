package eu.utilo.authorization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationApplicationTests {

	private static final String REDIRECT_URI = "http://127.0.0.1:9000/test/utilo";

	@Autowired
	MockMvc mvc;

	@BeforeEach
	public void setUp() {


	}

	@Test
	public void whenNotLoggedInAndRequestingTokenThenRedirectsToLogin() throws IOException {

		URI uri =  UriComponentsBuilder
				.fromPath("/oauth2/authorize")
				.scheme("http")
				.port(9000)
				.host("127.0.0.1")
				.queryParam("response_type", "code")
				.queryParam("client_id", "utilo-client")
				.queryParam("scope", "openid")
				.queryParam("redirect_uri", REDIRECT_URI)
				.build("true");

		try {

			mvc.perform(
					get(uri.toString()))
					.andExpect(redirectedUrl("http://127.0.0.1:9000/login"))
					.andExpect(status().is3xxRedirection()
					);

			// leite weiter zur Login Page mit gleich Parametern:
			uri =  UriComponentsBuilder
					.fromPath("/login")
					.scheme("http")
					.port(9000)
					.host("127.0.0.1")
					.queryParam("response_type", "code")
					.queryParam("client_id", "utilo-client")
					.queryParam("scope", "openid")
					.queryParam("redirect_uri", REDIRECT_URI)
					.build("true");

			// Login Form muss angezeigt worden sein
			mvc.perform(
					get(uri.toString()))
					.andExpect(content().string(containsString("username")))
					.andExpect(content().string(containsString("password"))
					);

			/*
			mvc.perform(
					formLogin("/oauth2/login").user("utilo").password("utilo")
							).andExpect(redirectedUrl(REDIRECT_URI));

			 */


		} catch (Exception e) {
			throw new RuntimeException(e);
		}		/*
				.andExpect(
						content().string(
								containsString("Username: user")))
				.andExpect(
						content().string(
								containsString("Authorities: [ROLE_USER]")))
				.andReturn() //
				.getResponse().getContentAsString();
		 */

	}

}
