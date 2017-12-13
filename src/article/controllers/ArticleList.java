package article.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import article.models.ArticleDAO;
import article.models.ArticleDAOImpl;
import article.models.ArticleVO;
import article.models.PageVO;

public class ArticleList extends AbstractController{

	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
		long pg = 1;
		try{
			pg = Long.parseLong(request.getParameter("pg"));
		}catch(Exception e){
			
		}
		int pageSize=10;// 페이지당 출력할 게시물 수
		long startnum = (pg-1)*pageSize+1;
		long endnum = pg*pageSize;
		
		PageVO pageVO = new PageVO(startnum, endnum);
		
		ArticleDAO articleDAO = ArticleDAOImpl.getInstance();
		
		ModelAndView mav = new ModelAndView();
		try {
			List<ArticleVO> list = articleDAO.getArticleList(pageVO);
			
			Long TotalCount =articleDAO.getTotalCount();
			Long TotalPage = (TotalCount+pageSize-1)/pageSize;
			Long startPage = (pg-1)/pageSize*pageSize+1;
			Long endPage = (pg-1)/pageSize*pageSize+10;
			Long prevPage = startPage-10;
			Long nextPage = endPage+10;
					
			
			if(TotalPage < endPage){
				endPage = TotalPage;
			}
			if(prevPage < 1){
				prevPage = (long) 1;
			}
			if(nextPage > TotalPage){
				nextPage = TotalPage;
			}
			
						
			
			mav.setViewName("/WEB-INF/views/article/list.jsp");
			mav.addObject("list", list);
			mav.addObject("TotalPage", TotalPage);
			mav.addObject("startPage", startPage);
			mav.addObject("endPage", endPage);
			mav.addObject("prevPage", prevPage);
			mav.addObject("nextPage", nextPage);
			mav.addObject("pg", pg);
			
		} catch (Exception e) {
			e.printStackTrace();
			mav.setViewName("/WEB-INF/views/result.jsp");
			mav.addObject("msg","게시물 리스트 출력에서\n 관리자에게 문의하세요");
			mav.addObject("url","test");
		}

		return mav;
	}

}
