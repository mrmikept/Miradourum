package pt.uminho.di.aa.miradourum.services;

import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.User;

import java.util.List;

public interface UserService {
    /**
     *
     * @return lista de utilizadores
     */
    List<User> getUsers();

    /**
     *
     * @param id do utilizador
     * @return dados de um utilizador
     */
    User getUserById(int id);

    /**
     *
     * @param email do utilizador
     * @return dados de um utilizador
     */
    User getUserByEmail(String email);

    /**
     * Atualiza os dados de um utilizador caso exista, ou atualiza as informações de um dado utilizador
     * @param user
     */
    void saveAndUpdateUser(User user);

    /**
     * Verifica se um email já existe na base de dados
     * @param email
     * @return true/false
     */
    boolean emailExists(String email);

    /**
     * Verifica se a password está correta
     * @param password
     * @return true/false
     */
    boolean checkPassword(String password, String encodedPassword);

    /**
     * Verifica se um utilizador tem estatuto premium
     * @param premium
     * @return true/false
     */
    boolean checkPremium(Long userid);

    /**
     *
     * @param userId
     * @return lista de pontos de interesse visitados por um dado utilizador
     */
    List<PontoInteresse> getPontosInteresse(Long userId);

    /**
     *
     * @param userId
     * @return lista de URLs das imagens que um utilizador adicionou nas suas reviews
     */
    List<String> getImagesURL(Long userId);

    /**
     * Guarda um user na base de dados, colocando a password encriptada
     * @param user
     */
    void saveUser(User user);
}