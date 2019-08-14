package com.rsoudani.app.category.repository;

import com.rsoudani.app.category.model.Category;
import com.rsoudani.app.commontests.utils.DBCommandTransactionalExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static com.rsoudani.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class CategoryRepositoryUTest {
    private EntityManagerFactory emf;
    private EntityManager em;
    private CategoryRepository categoryRepository;
    private DBCommandTransactionalExecutor dbCommandTransactionalExecutor;

    @Before
    public void initTestCase(){
        emf = Persistence.createEntityManagerFactory("libraryPU");
        em = emf.createEntityManager();
        categoryRepository = new CategoryRepository();
        categoryRepository.em = em;
        dbCommandTransactionalExecutor = new DBCommandTransactionalExecutor(em);
    }

    @After
    public void closeEntityManager(){
        em.close();
        emf.close();
    }

    @Test
    public void addCategoryAndFindIt(){
        Long categoryAddedId = dbCommandTransactionalExecutor.executeCommand(
                () -> categoryRepository.add(java()).getId()
        );

        assertThat(categoryAddedId, is(notNullValue()));

        Category category = categoryRepository.findById(categoryAddedId);
        assertThat(category, is(notNullValue()));
        assertThat(category.getName(), is(equalTo(java().getName())));
    }

    @Test
    public void findCategoryByIdNotFound(){
        final Category category = categoryRepository.findById(999L);
        assertThat(category, is(nullValue()));
    }

    @Test
    public void findCategoryByIdWithNullId(){
        final Category category = categoryRepository.findById(null);
        assertThat(category, is(nullValue()));
    }

    @Test
    public void updateCategory() {
        Long categoryAddedId = dbCommandTransactionalExecutor.executeCommand(
                () -> categoryRepository.add(java()).getId()
        );

        Category categoryAfterAdd = categoryRepository.findById(categoryAddedId);
        em.detach(categoryAfterAdd);
        assertThat(categoryAfterAdd.getName(), is(equalTo(java().getName())));
        categoryAfterAdd.setName(cleanCode().getName());

        Category categoryAfterSet = categoryRepository.findById(categoryAfterAdd.getId());
        assertThat(categoryAfterSet.getName(), is(equalTo(java().getName())));

        dbCommandTransactionalExecutor.executeCommand(() -> {
            categoryRepository.update(categoryAfterAdd);
            return null;
        });

        Category categoryAfterUpdate = categoryRepository.findById(categoryAfterAdd.getId());
        assertThat(categoryAfterUpdate.getName(), is(equalTo(cleanCode().getName())));
    }

    @Test
    public void findAllCategories(){
        dbCommandTransactionalExecutor.executeCommand(() -> {
            allCategories().forEach(categoryRepository::add);
            return null;
        });

        List<Category> categories = categoryRepository.findAll("name");
        assertThat(categories.size(), is(equalTo(4)));
        assertThat(categories.get(0).getName(), is(equalTo(architecture().getName())));
        assertThat(categories.get(1).getName(), is(equalTo(cleanCode().getName())));
        assertThat(categories.get(2).getName(), is(equalTo(java().getName())));
        assertThat(categories.get(3).getName(), is(equalTo(networks().getName())));

    }

    @Test
    public void alreadyExistsForAdd(){
        dbCommandTransactionalExecutor.executeCommand(
                () -> categoryRepository.add(java()).getId()
        );
        assertThat(categoryRepository.alreadyExists(java()), is(equalTo(true)));
        assertThat(categoryRepository.alreadyExists(cleanCode()), is(equalTo(false)));
    }

    @Test
    public void alreadyExistsWithId(){
        Category java = dbCommandTransactionalExecutor.executeCommand(
                () -> {
                    categoryRepository.add(cleanCode());
                    return categoryRepository.add(java());
                }
        );

        assertThat(categoryRepository.alreadyExists(java), is(equalTo(false)));

        java.setName(cleanCode().getName());
        assertThat(categoryRepository.alreadyExists(java), is(equalTo(true)));

        java.setName(networks().getName());
        assertThat(categoryRepository.alreadyExists(java), is(equalTo(false)));
    }

    @Test
    public void existsById(){
        Long categoryAddedId = dbCommandTransactionalExecutor.executeCommand(
                () -> categoryRepository.add(java()).getId()
        );

        assertThat(categoryRepository.existById(categoryAddedId), is(equalTo(true)));
        assertThat(categoryRepository.existById(999L), is(equalTo(false)));
    }
}
