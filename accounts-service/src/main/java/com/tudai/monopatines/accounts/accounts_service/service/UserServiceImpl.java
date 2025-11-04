package com.tudai.monopatines.accounts.accounts_service.service;

import com.tudai.monopatines.accounts.accounts_service.dto.CreateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UpdateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UserResponse;
import com.tudai.monopatines.accounts.accounts_service.entity.User;
import com.tudai.monopatines.accounts.accounts_service.exception.UserAlreadyExistsException;
import com.tudai.monopatines.accounts.accounts_service.exception.UserNotFoundException;
import com.tudai.monopatines.accounts.accounts_service.repository.UserRepository;
import com.tudai.monopatines.accounts.accounts_service.util.MapperUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para gestionar usuarios.
 * Contiene la lógica de negocio para todas las operaciones relacionadas con usuarios.
 * 
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    /**
     * Constructor del servicio.
     * 
     * @param userRepository Repositorio para acceder a la base de datos de usuarios
     * @param roleService Servicio para gestionar roles
     */
    public UserServiceImpl(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Verifica que no exista un usuario con el mismo email (debe ser único),
     * crea un nuevo usuario y lo guarda en la base de datos.
     */
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        // Verificar si ya existe un usuario con el mismo email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw UserAlreadyExistsException.forEmail(request.getEmail());
        }

        // Validar que el password esté presente al crear un usuario
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required when creating a user");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        
        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);
        
        // Asignar ROLE_USER por defecto a todos los usuarios nuevos
        roleService.assignRoleToUser(savedUser.getId(), "ROLE_USER");
        
        // Obtener roles para incluir en la respuesta
        List<String> roles = roleService.getRolesByUserId(savedUser.getId());
        UserResponse response = MapperUtil.mapUserToResponse(savedUser);
        response.setRoles(roles);
        return response;
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Busca el usuario en la base de datos por ID, obtiene sus roles
     * y lo convierte a DTO de respuesta incluyendo los roles.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        User user = userOptional.get();
        List<String> roles = roleService.getRolesByUserId(id);
        UserResponse response = MapperUtil.mapUserToResponse(user);
        response.setRoles(roles);
        return response;
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Busca el usuario en la base de datos por email (único), obtiene sus roles
     * y lo convierte a DTO de respuesta incluyendo los roles.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }
        User user = userOptional.get();
        List<String> roles = roleService.getRolesByUserId(user.getId());
        UserResponse response = MapperUtil.mapUserToResponse(user);
        response.setRoles(roles);
        return response;
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Obtiene todos los usuarios desde el repositorio,
     * obtiene los roles de cada usuario, los mapea a DTOs de respuesta
     * y retorna una lista con todos los resultados incluyendo los roles.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            UserResponse response = MapperUtil.mapUserToResponse(user);
            List<String> roles = roleService.getRolesByUserId(user.getId());
            response.setRoles(roles);
            responses.add(response);
        }
        return responses;
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Busca el usuario, verifica que el nuevo email no exista
     * (si cambió), actualiza los campos y guarda los cambios en la base de datos.
     * No actualiza el password (ese cambio se hace desde auth-service).
     */
    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        User user = userOptional.get();

        // Verificar si el email cambió y ya existe
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw UserAlreadyExistsException.forEmail(request.getEmail());
            }
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        
        // Obtener roles para incluir en la respuesta
        List<String> roles = roleService.getRolesByUserId(updatedUser.getId());
        UserResponse response = MapperUtil.mapUserToResponse(updatedUser);
        response.setRoles(roles);
        return response;
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Verifica que el usuario exista y lo elimina de la base de datos.
     */
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

}
