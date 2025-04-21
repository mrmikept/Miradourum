package pt.uminho.di.aa.miradourum.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById((long) id).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveAndUpdateUser(String username,String email,String password,String image) {
        User user = userRepository.findByEmail(email);

        if(username != null){
            user.setUsername(username);
        }
        if(password != null){
            user.setPassword(encodePassword(password));
        }
        if(image != null){
            user.setProfileImage(image);
        }

        userRepository.save(user);
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public void saveUser(User user){
        user.setPassword(encodePassword(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkPassword(String password, String encodedPassword) {
        return bCryptPasswordEncoder.matches(password, encodedPassword);
    }

    @Override
    public boolean checkPremium(Long userId) {
        return userRepository.checkPremium(userId);
    }

    @Override
    public List<PontoInteresse> getPontosInteresse(Long userId) {
        return userRepository.getPontosInteresse(userId);
    }

    @Override
    public List<String> getImagesURL(Long userId) {
        // reviews feitas pelo user nos pontos que ele visitou
        List<Review> reviews = userRepository.getUserReviewsOnVisitedPontos(userId);

        // magens das reviews de cima
        List<Image> images = userRepository.getImagesFromReviews(reviews);

        // urls
        return images.stream()
                .map(Image::getUrl)
                .filter(Objects::nonNull)
                .toList();
    }
}