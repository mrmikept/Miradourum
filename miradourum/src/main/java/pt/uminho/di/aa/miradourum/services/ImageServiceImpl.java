package pt.uminho.di.aa.miradourum.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.repositories.ImageRepository;

import java.util.List;


@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public Image saveImage(String url, byte[] imagedata, Review review) {
        Image image = new Image();
        image.setUrl(url);
        image.setReview(review);
        //Todo Logica de guardar no minio
        imageRepository.save(image);
        return image;
    }
}
