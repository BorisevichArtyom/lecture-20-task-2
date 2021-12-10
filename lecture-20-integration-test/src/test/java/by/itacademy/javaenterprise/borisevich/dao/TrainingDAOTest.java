package by.itacademy.javaenterprise.borisevich.dao;

import by.itacademy.javaenterprise.borisevich.config.PersistanceConfig;
import by.itacademy.javaenterprise.borisevich.entity.DiaryUser;
import by.itacademy.javaenterprise.borisevich.entity.Training;
import by.itacademy.javaenterprise.borisevich.exception.DAOException;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class TrainingDAOTest {
    private static MySQLContainer<?> mysqlOldVersion;

    private AnnotationConfigApplicationContext applicationContext =
            new AnnotationConfigApplicationContext(PersistanceConfig.class);
    private TrainingDAO trainingDAO = applicationContext.getBean(TrainingDAO.class);

    @Test
    public void testFindAllTrainings() throws DAOException {
        assertNotNull(trainingDAO.findAll());
    }


    @Test
    public void testSaveTraining() throws DAOException {
        Training training = Training.builder().trainingDate(Instant.now()).user(DiaryUser.builder().id(1L).build()).build();
        trainingDAO.saveOrUpdate(training);
        assertNotNull(training.getId());
        trainingDAO.deleteById(training.getId());
    }

    @Test
    public void testSaveTrainingNull() {
        assertThrows(NullPointerException.class, () -> trainingDAO.saveOrUpdate(null));
    }

    @Test
    public void testSaveAndGetIdTraining() throws DAOException {
        Training training = Training.builder().trainingDate(Instant.now()).user(DiaryUser.builder().id(1L).build()).build();
        trainingDAO.saveOrUpdate(training);
        assertNotNull(trainingDAO.findById(training.getId()));
        trainingDAO.deleteById(training.getId());
    }

    @Test
    public void testUpdateTraining() throws DAOException {
        Training training = Training.builder().trainingDate(Instant.now()).user(DiaryUser.builder().id(1L).build()).build();
        trainingDAO.saveOrUpdate(training);
        Long id1 = training.getId();
        training.setUser(DiaryUser.builder().id(2L).build());
        trainingDAO.saveOrUpdate(training);
        Long id2 = training.getId();
        assertEquals(id1, id2);
        trainingDAO.deleteById(training.getId());
    }

    @Test
    public void testUpdateTrainingInvalid() throws DAOException {
        assertThrows(DataIntegrityViolationException.class, () -> {
            trainingDAO.saveOrUpdate(new Training());
        });
    }
    @Test
    public void testDeleteTraining() throws DAOException {
        Training training = Training.builder().trainingDate(Instant.now()).user(DiaryUser.builder().id(1L).build()).build();
        trainingDAO.saveOrUpdate(training);
        trainingDAO.deleteById(training.getId());
        assertNull(trainingDAO.findById(training.getId()));
    }

}
