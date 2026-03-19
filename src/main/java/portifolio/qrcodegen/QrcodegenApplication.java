package portifolio.qrcodegen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class QrcodegenApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrcodegenApplication.class, args);
	}

}
