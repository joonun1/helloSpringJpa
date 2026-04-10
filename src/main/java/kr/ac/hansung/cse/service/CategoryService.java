package kr.ac.hansung.cse.service;

import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + id));
    }

    @Transactional
    public Category createCategory(String name) {
        categoryRepository.findByName(name)
                .ifPresent(c -> { throw new DuplicateCategoryException(name); });
        return categoryRepository.save(new Category(name));
    }
    // 카테고리 삭제: 연결된 상품이 있으면 삭제 불가
    @Transactional
    public void deleteCategory(Long id) {
        long count = categoryRepository.countProductsByCategoryId(id);
        if (count > 0) throw new IllegalStateException(
                "상품 " + count + "개가 연결되어 있어 삭제할 수 없습니다.");
        categoryRepository.delete(id);
    }
}
