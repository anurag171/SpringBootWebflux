package com.reactivespring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MovieApplicationTests {


	@Test
	void testOneEqualOne(){
		Assertions.assertEquals(1,1);
	}

}
