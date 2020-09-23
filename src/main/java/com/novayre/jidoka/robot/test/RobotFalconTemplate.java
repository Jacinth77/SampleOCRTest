


package com.novayre.jidoka.robot.test;

import java.awt.*;
import java.awt.Color;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;

import com.novayre.jidoka.falcon.api.IFalconProcess;
import com.novayre.jidoka.falcon.ocr.api.*;
import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.io.FilenameUtils;

import com.novayre.jidoka.client.api.IJidokaServer;
import com.novayre.jidoka.client.api.IRobot;
import com.novayre.jidoka.client.api.JidokaFactory;
import com.novayre.jidoka.client.api.annotations.Robot;
import com.novayre.jidoka.client.api.multios.IClient;
import com.novayre.jidoka.falcon.api.IFalcon;
import com.novayre.jidoka.falcon.api.IFalconImage;
import org.w3c.dom.Element;

/**
 * The Class RobotFalconTemplate.
 */
@Robot
public class RobotFalconTemplate implements IRobot {

	/** The server. */
	private IJidokaServer< ? > server;
	
	/** The falcon. */
	private IFalcon falcon;
	
	/** The client. */
	private IClient client;
	
	private JidokaImageSupport images;

	public String fileNameinput;
	static BufferedImage image;
	
	/**
	 * Initialize the modules
	 * @throws IOException 
	 */
	public void start() throws IOException {
		
		server = JidokaFactory.getServer();
		client = IClient.getInstance(this);
		falcon = IFalcon.getInstance(this, client);
		images = JidokaImageSupport.getInstance(this);
		IFalconProcess falconProcess = falcon.getFalconProcess();
		
		server.setNumberOfItems(1);
		server.setCurrentItem(1, images.getTestPng().getDescription());
	}
	
	/**
	 * Search image.
	 * @throws IOException 
	 * @throws AWTException 
	 */
	public void searchImage() throws IOException, AWTException {
		
		IFalconImage testImage = images.getTestPng().search();
		
		// Uncomment to apply 25% noise and color tolerance 
//		testImage.setTolerance(.25f);
//		testImage.getOptions().colorTolerance(.25f);
		
		server.info("Searching the image: " + testImage.getDescription());
		
		// Sends the image to the trace
		falcon.sendImage(testImage.getImage(), "Test image");
		
		server.info("Desktop capture");
		server.sendScreen("Current desktop");
		
		// Image search on the desktop
		if (testImage.search().found()) {
			
			server.info("Image found at: " + testImage.getPointsWhereFound().get(0));

			// Draw a rectangle where the image was found
			drawRectangle(testImage);
			
			// Single left click over the image
			testImage.clickOnCenter();
			
			server.setCurrentItemResultToOK();
			
		} else {
			
			server.warn("Image not found");
			server.setCurrentItemResultToWarn();
		}
	}
	
	/**
	 * Draw rectangle over screenshot capture to show where the image was found
	 *
	 * @param imageFound the image found
	 * @return the string
	 * @throws AWTException 
	 */
	private String drawRectangle(IFalconImage imageFound) throws AWTException {

		try {
			
			Path screenshot = Paths.get(server.getCurrentDir(), "screenshot.png");
			ImageIO.write(server.getScreen(), "png", screenshot.toFile());
			
			BufferedImage img = ImageIO.read(screenshot.toFile());
			Graphics2D g2d = img.createGraphics();

			Point p = imageFound.getPointsWhereFound().get(0);

			g2d.setStroke(new BasicStroke(3));
			g2d.setColor(Color.RED);
			g2d.drawRect(p.x, p.y, imageFound.getRectangle().width, imageFound.getRectangle().height);
			g2d.dispose();

			String output = addSuffix(screenshot.toFile().getAbsolutePath(), "_mod");

			File f = new File(output);

			ImageIO.write(img, "png", f);
			server.info("Save image modified: " + output);
			
			falcon.sendImage(ImageIO.read(new File(output)), output);

			return output;
		} catch (IOException e) {
			return null;
		}

	}
	
	/**
	 * Adds the suffix to a filename
	 *
	 * @param filename the filename
	 * @param suffix the suffix
	 * @return the string
	 */
	private String addSuffix(String filename, String suffix) {

		String baseName = FilenameUtils.getBaseName(filename);
		baseName += suffix;

		String output = FilenameUtils.getFullPath(filename) + File.separator + baseName + "."
				+ FilenameUtils.getExtension(filename);

		return output;
	}


	public void ImgPreprocessing() throws Exception {
		String inputFilePath ="C:\\Users\\Jacinth.A\\Documents\\714336.jpg";
		SetDPI(inputFilePath);
		String DPI = "D:\\Output file\\1.jpg";

		extractTextFromOCR();

        //
		//

		BufferedImage defaultImage1 =
				ImageIO.read(Paths.get(DPI).toFile());

		server.info(defaultImage1.getWidth()+"     "+ defaultImage1.getHeight());
		server.info(defaultImage1.getMinX()+"  "+defaultImage1.getMinY());


		BufferedImage defaultImage = new BufferedImage(defaultImage1.getWidth(), defaultImage1.getHeight(), defaultImage1.getType());

		//BufferedImage dest1 = defaultImage1.getSubimage(320, 40,120, 15);
		//BufferedImage dest1 = defaultImage1.getSubimage(400, 30,150, 35);
		String one = "D:\\Output file\\1.jpg";

		if (server.getParameters().get("docFile").toString().contains("1")) {
			BufferedImage dest1 = defaultImage1.getSubimage(0, 389, 200, 49);
			ImageIO.write(dest1, "jpg", new File(one));
		}
		else if(server.getParameters().get("docFile").toString().contains("2")){
			BufferedImage dest1=defaultImage1.getSubimage(395,20,200,30);
			ImageIO.write(dest1, "jpg", new File(one));
		}
		else{
			BufferedImage dest1=defaultImage1.getSubimage(0,390,810,100);
			ImageIO.write(dest1, "jpg", new File(one));
		}


		/*BufferedImage dest2 = defaultImage1.getSubimage(0, 286,350, 44);
		String two = "D:\\Output file\\1two.jpg";
		ImageIO.write(dest2, "jpg", new File(two));
		BufferedImage dest3 = defaultImage1.getSubimage(0, 280,350, 45);
		String three = "D:\\Output file\\1three.jpg";
		ImageIO.write(dest3, "jpg", new File(three));
		BufferedImage dest4 = defaultImage1.getSubimage(0, 279,350, 50);
		String four = "D:\\Output file\\1four.jpg";
		ImageIO.write(dest4, "jpg", new File(four));*/




		extractTextFromOCR();
		SetDPI(one);

		//String inputFilebw = "D:\\Output file\\714336.jpg";
		BufferedImage image = ImageIO.read(new File(one));

		server.info(image.getRGB(1,2));

		//sharpen();
		extractTextFromOCR();

		blackwhite(image);

		brightness();

		extractTextFromOCR();

		blackwhite(image);

		BufferedImage defaultImage2 =
				ImageIO.read(Paths.get(DPI).toFile());
		falconprocess(defaultImage2);




	}

	public void extractTextFromOCR () throws Exception{

		String inputFile ="D:\\Output file\\1.jpg";
		Tesseract tesseract = new Tesseract();
		tesseract.setDatapath("D:\\TessData");
		tesseract.setLanguage("eng");
		String ExtractedText = tesseract.doOCR(new File(inputFile));
		server.info("Text :" + ExtractedText);

	}


	public void SetDPI(String Path) throws IOException {


		// String inputFilePath = Paths.get(server.getCurrentDir(), fileNameinput).toString();
		BufferedImage image = ImageIO.read(new File(Path));
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(0.95f);
		IIOMetadata metadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(image), param);
		Element tree = (Element) metadata.getAsTree("javax_imageio_jpeg_image_1.0");
		Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
		jfif.setAttribute("Xdensity", Integer.toString(300));
		jfif.setAttribute("Ydensity", Integer.toString(300));
		jfif.setAttribute("resUnits", "1"); // In pixels-per-inch units
		metadata.mergeTree("javax_imageio_jpeg_image_1.0", tree);
		String filename = "D:\\Output file\\1.jpg";

		try (FileImageOutputStream output = new FileImageOutputStream(new File(filename))) {
			writer.setOutput(output);
			IIOImage iioImage = new IIOImage(image, null, metadata);
			writer.write(metadata, iioImage, param);
			writer.dispose();
		}
	}

	public void  blackwhite(BufferedImage img) throws Exception {
		try {
			//BufferedImage img = ImageIO.read(new File("http://i.stack.imgur.com/yhCnH.png"));
			BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g = gray.createGraphics();
			g.drawImage(img, 0, 0, null);

			File outputfile = new File("D:\\Output file\\1.jpg");
			ImageIO.write(gray, "jpg", outputfile);

			Tesseract it = new Tesseract();
			it.setDatapath("D:\\TessData");
			it.setLanguage("eng");
			String str = it.doOCR(outputfile);
			server.info("Blackand white"+str);
		}

		//System.out.println(colors.size());
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void brightness(){
		try{
			String filename = "D:\\Output file\\1.jpg";
			//reading image data from file
			BufferedImage src=ImageIO.read(new File(filename));
			/* passing source image and brightening by 50%-value of 1.0f means original brightness */
			BufferedImage dest=changeBrightness(src,1.5f);
			//drawing new image on panel
			//writing new image to a file in jpeg format
			String output = "D:\\Output file\\1.jpg";
			ImageIO.write(dest,"jpeg",new File(output));

			/*dest.setRGB(0,255,255);
			String output1 = "D:\\Output file\\7143366.jpg";
			ImageIO.write(dest,"jpeg",new File(output1));
			Tesseract it = new Tesseract();
			it.setDatapath("D:\\TessData");
			it.setLanguage("eng");
			String str = it.doOCR(new File(output1));
			server.info("Brightness"+str);*/

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public BufferedImage changeBrightness(BufferedImage src,float val){
		RescaleOp brighterOp = new RescaleOp(val, 0, null);
		return brighterOp.filter(src,null); //filtering
	}


	public void sharpen() throws Exception {


		String inputFile = "D:\\Output file\\1.jpg";
		BufferedImage inputFile1 =
				ImageIO.read(Paths.get(inputFile).toFile());
		Image sourceImage = Toolkit.getDefaultToolkit().getImage(inputFile);
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
		GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
		image = graphicsConfiguration.createCompatibleImage(inputFile1.getWidth(), inputFile1.getHeight(), Transparency.BITMASK);
		Graphics graphics = image.createGraphics();
		graphics.drawImage(sourceImage, 0, 0, null);
		graphics.dispose();
		Kernel kernel = new Kernel(3, 3,
				new float[]{
						-1, -1, -1,
						-1, 9, -1,
						-1, -1, -1});

		BufferedImageOp op = new ConvolveOp(kernel);
		//image = op.filter(image, null);
		image = op.filter(inputFile1, null);

		File outputfile = new File("D:\\Output file\\1.jpg");
		ImageIO.write(image, "jpg", outputfile);



	}

	public void falconprocess(BufferedImage img) throws IOException {
		IFalconProcess falconProcess = falcon.getFalconProcess();
		StartParameters s = new StartParameters();
		s.setLogImages(true);
		s.setLogIntermediateMessages(true);
		s.setImageDescription("Original");

		falconProcess.start(img, s);
		falconProcess.morphTool(new MorphToolParameters()
				.width(img.getWidth())
				.height(img.getHeight())
				.shape(MorphToolParameters.EShape.RECT));
		//falconProcess.restoreMatrix(new RestoreMatrixParameters().id("jacie"));
		falconProcess.ocr(new OCRParameters()
				.languageInImage("eng")
				.configuration(null)
				.textFormInImage(1));
		falconProcess.morphology(new MorphologyParameters().operation(MorphologyParameters.EOperation.BLACK_HAT));
		//File blackhat = new File("D:\\Output file\\1.jpg");
		//ImageIO.write(falconProcess.images().get(0), "jpg", blackhat);
		falconProcess.ocr(new OCRParameters()
				.languageInImage("eng")
				.configuration(null)
				.textFormInImage(1));

		falconProcess.adaptiveThreshold(new AdaptiveThresholdParameters().type(AdaptiveThresholdParameters.EType.BINARY).method(AdaptiveThresholdParameters.EMethod.GAUSSIAN_C).blockSize(1024*1024).maxVal(255).subMatrixes(true));
		falconProcess.ocr(new OCRParameters()
				.languageInImage("eng")
				.configuration(null)
				.textFormInImage(1));


	}

	/**
	 * End.
	 */
	public void end() {
		
	}
}
