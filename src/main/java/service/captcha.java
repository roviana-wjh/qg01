package service;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;
@WebServlet("/captcha")
public class captcha extends HttpServlet {
    private Producer captchaProducer;
    public void init() throws ServletException {
        Properties props = new Properties();
        props.setProperty("kaptcha.image.width", "150");
        props.setProperty("kaptcha.image.height", "50");
        props.setProperty("kaptcha.textproducer.char.length", "4");
        props.setProperty("kaptcha.textproducer.font.color", "black");
        props.setProperty("kaptcha.noise.color", "gray");
        captchaProducer = new Config(props).getProducerImpl();
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IOException {
        String captchaText = captchaProducer.createText();
        HttpSession session = req.getSession();
        session.setAttribute("captcha", captchaText.toLowerCase());
        BufferedImage image = captchaProducer.createImage(captchaText);
        resp.setContentType("image/png");
        ImageIO.write(image, "png", resp.getOutputStream());
    }
}
