package pt.uminho.di.aa.miradourum.services;

import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.ImagePontoInteresse;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;


public interface ImagePontoInteresseService {
    ImagePontoInteresse saveImage(String url, PontoInteresse ponto);
}
