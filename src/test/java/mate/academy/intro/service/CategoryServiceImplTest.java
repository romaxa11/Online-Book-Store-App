package mate.academy.intro.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Optional;
import mate.academy.intro.dto.category.CategoryDto;
import mate.academy.intro.dto.category.CreateCategoryRequestDto;
import mate.academy.intro.mapper.CategoryMapper;
import mate.academy.intro.model.Category;
import mate.academy.intro.repository.category.CategoryRepository;
import mate.academy.intro.service.category.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    @Test
    @DisplayName("""
            Verify create category method
            """)
    void save_ValidCreateCategoryRequestDto_ReturnsCategoryDto() {
        //Given
        CreateCategoryRequestDto categoryRequestDto = getCreateCategoryRequestDto();
        Category category = getCategoryByCreateCategoryRequestDto(categoryRequestDto);
        CategoryDto expected = getCategoryDtoByCategory(category);
        expected.setId(1L);

        Mockito.when(categoryMapper.toModel(categoryRequestDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);
        //When
        CategoryDto actual = categoryServiceImpl.save(categoryRequestDto);
        //Then
        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toModel(categoryRequestDto);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify get all categories method
            """)
    void getAll_ValidList_ReturnAllCategories_Success() {
        //Given
        Category category = getCategory();
        CategoryDto expected = getCategoryDtoByCategory(category);
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);
        //When
        List<CategoryDto> actual = categoryServiceImpl.findAll(pageable);
        //Then
        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("""
            Verify get category by id method
            """)
    void getCategoryById_ValidId_ReturnCategoryDto_Success() {
        //Given
        Category category = getCategory();
        CategoryDto expected = getCategoryDtoByCategory(category);
        Long categoryId = category.getId();

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);
        //When
        CategoryDto actual = categoryServiceImpl.getCategoryById(categoryId);
        //Then
        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(anyLong());
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify delete by id method
            """)
    void deleteById_ValidId_DoesNotThrowException() {
        // Then
        assertDoesNotThrow(() -> categoryServiceImpl.deleteById(anyLong()));
    }

    private Category getCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Adventure");
        category.setDescription("Adventure description");
        return category;
    }

    private CategoryDto getCategoryDtoByCategory(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription());
        return categoryDto;
    }

    private Category getCategoryByCreateCategoryRequestDto(
            CreateCategoryRequestDto createCategoryRequestDto
    ) {
        Category category = new Category();
        category.setName(createCategoryRequestDto.getName());
        category.setDescription(createCategoryRequestDto.getDescription());
        return category;
    }

    private CreateCategoryRequestDto getCreateCategoryRequestDto() {
        CreateCategoryRequestDto categoryRequestDto = new CreateCategoryRequestDto();
        categoryRequestDto
                .setName("Adventure")
                .setDescription("Adventure description");
        return categoryRequestDto;
    }
}
