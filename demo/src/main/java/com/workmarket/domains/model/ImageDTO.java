package com.workmarket.domains.model;

import com.workmarket.utility.MimeTypeUtilities;
import org.apache.commons.lang.StringUtils;

/**
 * Created by ianha on 7/14/14
 */
public class ImageDTO {
	private String url; // CDN url
	private String image; // Base 64 string
	private String filename;
	private ImageCoordinates coordinates;

	public ImageDTO() { }

	public ImageDTO(String url) {
		this(url, null, null, null);
	}

	public ImageDTO(String url, String image, String filename, ImageCoordinates coordinates) {
		this.url = url;
		this.image = image;
		this.filename = filename;
		this.coordinates = coordinates;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public ImageCoordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(ImageCoordinates coordinates) {
		this.coordinates = coordinates;
	}

	public boolean hasImage() {
		return StringUtils.isNotBlank(image);
	}

	public boolean hasFilename() {
		return StringUtils.isNotBlank(filename);
	}

	public String stripContentTypeFromImage() {
		String contentType = MimeTypeUtilities.guessMimeType(getFilename());

		if (contentType != null && getImage() != null) {
			String prefix = "data:" + contentType + ";base64,";
			if (getImage().startsWith(prefix)) {
				return getImage().substring(prefix.length());
			}
		}

		return getImage();
	}
}
