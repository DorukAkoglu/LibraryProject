package studyLibrary.project;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryManager {
    private Cloudinary cloudinary;

    public CloudinaryManager() throws IOException {
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/config.properties"));  

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", props.getProperty("cloud_name"),
            "api_key", props.getProperty("api_key"),
            "api_secret", props.getProperty("api_secret")));
    }

    public String uploadImage(File file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }
    
}
