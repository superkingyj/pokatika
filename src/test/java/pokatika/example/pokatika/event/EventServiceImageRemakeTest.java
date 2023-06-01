package pokatika.example.pokatika.event;

import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

@Data
public class EventServiceImageRemakeTest {

    private static String localeImagePath;
    private static String localeSavePath;

    private static String ipfsDomain;
    private static String ipfsCid;
    private static String ipfsSavePath;

    private static String webImagePath;
    private static String webSavePath;

    private ConfigurableEnvironment environment;

    Font font;

    @BeforeEach
    void beforeEach() {
        environment = new StandardEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/application-test.yml"));
            System.out.println(properties);
            PropertiesPropertySource propertySource = new PropertiesPropertySource("testProperties", properties);
            propertySources.addLast(propertySource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        localeImagePath = environment.getProperty("locale.image-path");
        localeSavePath = environment.getProperty("locale.save-path");
        ipfsDomain = environment.getProperty("ipfs.domain");
        ipfsCid = environment.getProperty("ipfs.cid");
        ipfsSavePath = environment.getProperty("ipfs.save-path");
        webSavePath = environment.getProperty("web.save-path");
        webImagePath = environment.getProperty("web.image-path");
    }

    @Test
    void 로컬_이미지를_불러와_그_위에_글자_쓰고_로컬에_저장(){
        File localFileInput = new File(localeImagePath);
        BufferedImage localImage = null;
        try {
            localImage = ImageIO.read(localFileInput);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeString(localImage, localeSavePath);
    }

    @Test
    void 웹_링크의_이미지를_불러와_그_위에_글자_쓰기_테스트(){
        String imageUrl = webImagePath;
        BufferedImage webImage = null;
        try {
             webImage = ImageIO.read(new URL(imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeString(webImage, webSavePath);
    }

    @Test
    void IPFS의_이미지를_불러와_그_위에_글자_쓰기_테스트(){
        String domain = ipfsDomain;
        String cid = ipfsCid;
        BufferedImage ipfsImage = null;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new BufferedImageHttpMessageConverter());
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "image/png");
        System.out.println(domain + cid);

        HttpEntity<?> responseEntity = new HttpEntity<>(headers);
        ResponseEntity<BufferedImage> response = restTemplate.exchange(
                URI.create(domain + cid),
                HttpMethod.GET,
                responseEntity,
                BufferedImage.class
        );

        ipfsImage = response.getBody();
        System.out.println(ipfsImage);
        writeString(ipfsImage, ipfsSavePath);
    }

    void initFont(){
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:fonts/SK_Pupok_Solid_400.ttf");
        try {
            InputStream inputStream = resource.getInputStream();
            this.font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            this.font = this.font.deriveFont(15f);
            inputStream.close();
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    void writeString(BufferedImage bufferedImage, String savePath){
        initFont();

        try {
            Graphics graphics = bufferedImage.getGraphics();

            graphics.setFont(this.font);
            graphics.setColor(Color.blue);
            graphics.drawString("minji99", 10, 25);
            graphics.dispose();

            File localFileOutput = new File(savePath);
            ImageIO.write(bufferedImage, "png", localFileOutput);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
