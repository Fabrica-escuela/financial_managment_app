package com.udea.financial.infrastructure.driven.persistence.adapter;

import com.udea.financial.domain.model.User;
import com.udea.financial.infrastructure.driven.persistence.entity.UserEntity;
import com.udea.financial.infrastructure.driven.persistence.mapper.UserMapper;
import com.udea.financial.infrastructure.driven.persistence.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepositoryAdapter — unit tests")
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    private User domainUser(Long id, String email) {
        return User.builder().idUser(id).name("Test").email(email).password("hash").build();
    }

    private UserEntity entityUser(Long id, String email) {
        return UserEntity.builder().idUser(id).name("Test").email(email).password("hash").build();
    }

    @Nested
    @DisplayName("save()")
    class Save {
        @Test
        @DisplayName("Mapea el dominio a entity y llama a userRepository.saveAndFlush()")
        void save_mapsToDomainAndDelegatesToJpa() {
            User user = domainUser(null, "new@example.com");
            UserEntity entity = entityUser(null, "new@example.com");
            when(userMapper.toEntity(user)).thenReturn(entity);

            adapter.save(user);

            verify(userMapper).toEntity(user);
            // Corregido: se usa saveAndFlush para coincidir con la implementación
            verify(userRepository).saveAndFlush(entity);
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {
        @Test
        @DisplayName("Mapea el dominio a entity y llama a userRepository.saveAndFlush() (upsert)")
        void update_mapsToDomainAndDelegatesToJpa() {
            User user = domainUser(1L, "update@example.com");
            UserEntity entity = entityUser(1L, "update@example.com");
            when(userMapper.toEntity(user)).thenReturn(entity);

            adapter.update(user);

            verify(userMapper).toEntity(user);
            // Corregido: se usa saveAndFlush para coincidir con la implementación
            verify(userRepository).saveAndFlush(entity);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {
        @Test
        @DisplayName("ID existente retorna Optional con el usuario mapeado al dominio")
        void findById_existingId_returnsMappedDomain() {
            UserEntity entity = entityUser(1L, "found@example.com");
            User expected = domainUser(1L, "found@example.com");
            when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(userMapper.toDomain(entity)).thenReturn(expected);

            Optional<User> result = adapter.findById(1L);

            assertThat(result).isPresent().contains(expected);
        }

        @Test
        @DisplayName("ID inexistente retorna Optional.empty()")
        void findById_nonExistingId_returnsEmpty() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            Optional<User> result = adapter.findById(99L);
            assertThat(result).isEmpty();
            verify(userMapper, never()).toDomain(any());
        }
    }

    @Nested
    @DisplayName("findByEmail()")
    class FindByEmail {
        @Test
        @DisplayName("Email existente retorna Optional con el usuario mapeado al dominio")
        void findByEmail_existingEmail_returnsMappedDomain() {
            UserEntity entity = entityUser(2L, "email@example.com");
            User expected = domainUser(2L, "email@example.com");
            when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(entity));
            when(userMapper.toDomain(entity)).thenReturn(expected);

            Optional<User> result = adapter.findByEmail("email@example.com");

            assertThat(result).isPresent().contains(expected);
        }

        @Test
        @DisplayName("Email inexistente retorna Optional.empty()")
        void findByEmail_nonExistingEmail_returnsEmpty() {
            when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());
            Optional<User> result = adapter.findByEmail("nobody@example.com");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteById {
        @Test
        @DisplayName("Delega la eliminación en el JPA repository")
        void deleteById_delegatesToJpaRepository() {
            adapter.deleteById(5L);
            verify(userRepository).deleteById(5L);
        }
    }

    @Nested
    @DisplayName("allUsers()")
    class AllUsers {
        @Test
        @DisplayName("Mapea cada entity al dominio y retorna la lista completa")
        void allUsers_mappsAllEntitiesAndReturnsFullList() {
            UserEntity e1 = entityUser(1L, "a@example.com");
            UserEntity e2 = entityUser(2L, "b@example.com");
            User u1 = domainUser(1L, "a@example.com");
            User u2 = domainUser(2L, "b@example.com");

            when(userRepository.findAll()).thenReturn(List.of(e1, e2));
            when(userMapper.toDomain(e1)).thenReturn(u1);
            when(userMapper.toDomain(e2)).thenReturn(u2);

            List<User> result = adapter.allUsers();

            assertThat(result).hasSize(2).containsExactly(u1, u2);
        }

        @Test
        @DisplayName("Sin usuarios en BD retorna lista vacía")
        void allUsers_emptyDb_returnsEmptyList() {
            when(userRepository.findAll()).thenReturn(List.of());
            List<User> result = adapter.allUsers();
            assertThat(result).isEmpty();
            verify(userMapper, never()).toDomain(any());
        }
    }
}