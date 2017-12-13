package article.models;

import java.util.List;

public interface ArticleDAO {
	void insertArticle(ArticleVO articleVO) throws Exception;
	List<ArticleVO> getArticleList()throws Exception;
	ArticleVO getDetail(long no) throws Exception;
	void updateViewcount(long no)throws Exception;
	void updateArticle(ArticleVO articleVO)throws Exception;
	void deleteArticle(ArticleVO articleVO)throws Exception;
	List<ArticleVO> getArticleList(PageVO pageVO)throws Exception;
	Long getTotalCount() throws Exception;

}
