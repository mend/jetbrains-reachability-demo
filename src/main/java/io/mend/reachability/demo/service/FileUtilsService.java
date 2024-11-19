package io.mend.reachability.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class FileUtilsService {

    public void saveFileAsJson(Object fileToSave, String filePath) {

        File file;
        FileOutputStream fos = null;

        try {
            file = new File(filePath);
            file.getParentFile().mkdirs();
            file.createNewFile();
            fos = new FileOutputStream(file);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            fos.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(fileToSave));

        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: {}", e.getMessage(), e);
        } catch (IOException e) {
            // in some cases on Windows the FileNotFoundException is not used
            // instead only the message "The system cannot find the path specified"
            // is printed without the path
            log.error("IOException for {}: {}", filePath, e.getMessage(), e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                //Do nothing
            }
        }
    }


}
