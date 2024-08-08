package be.intec.kazernemediaplayer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = "be.intec.kazernemediaplayer", exclude = { SecurityAutoConfiguration.class })
public class KazerneMediaPLayer {

    public static void main(String[] args) {
        SpringApplication.run(KazerneMediaPLayer.class, args);
    }
}