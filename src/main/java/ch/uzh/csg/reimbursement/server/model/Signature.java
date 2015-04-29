package ch.uzh.csg.reimbursement.server.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Signature")
public class Signature {

	//	@Transient
	//	private final static Logger LOG = Logger.getLogger(Signature.class.getName());

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Column(nullable = false, updatable = true, unique = false, name = "content_type")
	private String contentType;

	@Column(nullable = false, updatable = true, unique = false, name = "file_size")
	private long fileSize;

	@Column(nullable = false, updatable = true, unique = false, name = "content", columnDefinition = "blob")
	private byte[] content;

	@Column(nullable = false, updatable = true, unique = false, name = "cropped_content", columnDefinition = "blob")
	private byte[] croppedContent;

	@Column(nullable = true, updatable = true, unique = false, name = "crop_width")
	private int cropWidth;

	@Column(nullable = true, updatable = true, unique = false, name = "crop_height")
	private int cropHeight;

	@Column(nullable = true, updatable = true, unique = false, name = "crop_top")
	private int cropTop;

	@Column(nullable = true, updatable = true, unique = false, name = "crop_left")
	private int cropLeft;

	public Signature(String contentType, long fileSize, byte[] content) {
		this.contentType = contentType;
		this.fileSize = fileSize;
		this.content = content;
		this.croppedContent = content;
	}

	public void addCropping(int width, int height, int top, int left) {
		this.cropWidth = width;
		this.cropHeight = height;
		this.cropTop = top;
		this.cropLeft = left;
		this.croppedContent = cropImage();
	}

	private byte[] cropImage() {
		byte[] croppedImageInByte = null;

		try {
			InputStream inputStream = new ByteArrayInputStream(content);
			BufferedImage image = ImageIO.read(inputStream);
			int originalHeight = image.getHeight();
			int originalWidth = image.getWidth();

			if(cropHeight > originalHeight || cropWidth > originalWidth || (cropLeft+cropWidth) > originalWidth || (cropTop+cropHeight) > originalHeight){
				cropHeight = originalHeight - cropTop;
				cropWidth = originalWidth - cropLeft;
			}

			BufferedImage croppedImage = image.getSubimage(cropLeft, cropTop, cropWidth, cropHeight);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(croppedImage, contentType.replace("image/", ""), outputStream);
			outputStream.flush();

			croppedImageInByte = outputStream.toByteArray();

			outputStream.close();

		} catch (IOException e) {
			// TODO sebi | create a reasonable exception handling here
			e.printStackTrace();
		}

		return croppedImageInByte;
	}

	public byte[] getCroppedContent() {
		return croppedContent;
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected Signature() {

	}
}
