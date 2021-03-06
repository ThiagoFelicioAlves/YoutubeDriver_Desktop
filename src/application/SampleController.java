package application;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SampleController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    Label SeuIp;

    static List<String> Link_Videos = new ArrayList<String>();

    @FXML
    void initialize() throws UnknownHostException {


    	String Path = System.getenv("ProgramFiles")+"\\Youtube Driver\\chromedriver.exe";

        InetAddress inetAddress = InetAddress.getLocalHost();

        SeuIp.setText("Digite esse aqui no seu APP: \n"+ inetAddress. getHostAddress());


        Path =  "C:\\Users\\Thiago\\Desktop\\chromedriver.exe";
        Path = "C:\\Users\\redes\\Desktop\\chromedriver.exe";
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-infobars");
        options.addArguments("start-maximized");

        System.setProperty("webdriver.chrome.driver",Path);
        WebDriver driver = new ChromeDriver(options);
        driver.get("http://www.youtube.com");


        Thread t = new Thread(() -> {
            try {
                ServerSocket Servidor = new ServerSocket(7878);

                do {
                    try {
                        Socket C = Servidor.accept();
                        Scanner s = new Scanner(C.getInputStream());
                        String pesquisar = s.nextLine();

                        String[] Separa = pesquisar.split("@CodVIDEO==");

                        if (pesquisar.equals("@Cod==1")) {

                            Link_Videos.clear();
                            WebElement ok = driver.findElement(By.id("search-icon-legacy"));
                            ok.click();
                            Thread.sleep(3000);

                            String Title_Videos = "";
                            String Desc_Videos = "";
                            String Channel_Videos = "";
                            String DataYT_Videos = "";
                            String Thumb_Videos = "";


                            for (int i = 1; i < 21; i++) {
                                try {
                                    WebElement Videos = driver.findElement(By.xpath("//*[@id=\"contents\"]/ytd-video-renderer[" + i + "]"));
                                    WebElement InfosVideos = Videos.findElement(By.id("dismissable"));

                                    Title_Videos += ";" + InfosVideos.findElement(By.id("title-wrapper")).getText();

                                    Thumb_Videos += ";" + InfosVideos.findElement(By.id("img")).getAttribute("src");

                                    Desc_Videos += ";" + InfosVideos.findElement(By.id("description-text")).getText();

                                    Channel_Videos += ";" + InfosVideos.findElement(By.id("byline")).getText();

                                    String SeparaData = ";" + InfosVideos.findElement(By.id("metadata-line")).getText();

                                    String[] JuntaData = SeparaData.split("\n");

                                    DataYT_Videos += JuntaData[0]+" "+ JuntaData[1];


                                    Link_Videos.add(InfosVideos.findElement(By.id("thumbnail")).getAttribute("href"));


                                } catch (Exception e) {

                                }
                            }

                            try {
                                ServerSocket Servidor2 = new ServerSocket(7879);
                                Socket C2 = Servidor2.accept();
                                PrintWriter writer = new PrintWriter(C2.getOutputStream());
                                writer.write(Title_Videos+"\n");
                                writer.write(Thumb_Videos+"\n");
                                writer.write(Desc_Videos+"\n");
                                writer.write(Channel_Videos+"\n");
                                writer.write(DataYT_Videos+"\n");
                                writer.flush();
                                writer.close();
                                Servidor2.close();
                                System.out.println("Enviado");
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }else if (((pesquisar.length() > 10) && pesquisar.substring(0, 11).equals("@CodVIDEO==")) ) {
                            int index = Integer.parseInt(Separa[1]);
                            System.out.println(Separa[1]);
                            driver.navigate().to(Link_Videos.get(index));
                            WebElement element = driver.findElement(By.className("ytp-chrome-controls"));
                            List<WebElement> childs = element.findElements(By.xpath(".//*"));
                            for(WebElement a : childs) {
                                String z = a.getAttribute("class");
                                if (z != null && z.equals("ytp-fullscreen-button ytp-button")){
                                    a.click();
                                }
                            }


                        }
                        else {
                            WebElement element;
                            try {
                                element = driver.findElement(By.id("search"));
                            } catch (Exception e){
                                element = driver.findElement(By.id("masthead-search-term"));
                            }
                            driver.findElement(By.id("search")).sendKeys(Keys.chord(Keys.CONTROL, "a"));
                            element.sendKeys(pesquisar);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (true);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        t.start();




    }
}
