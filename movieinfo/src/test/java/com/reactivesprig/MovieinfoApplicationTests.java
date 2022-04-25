package com.reactivesprig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
@ActiveProfiles("test")
class MovieinfoApplicationTests {

	@Test
	void testOneEqualOne(){
		Assertions.assertEquals(1,1);
	}
}
