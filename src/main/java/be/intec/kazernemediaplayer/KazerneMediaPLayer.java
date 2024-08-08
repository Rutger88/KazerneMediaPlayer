package be.intec.kazernemediaplayer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "be.intec.kazernemediaplayer")
public class KazerneMediaPLayer {

    public static void main(String[] args) {
        SpringApplication.run(KazerneMediaPLayer.class, args);
    }
}