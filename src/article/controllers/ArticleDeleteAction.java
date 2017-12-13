package article.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import article.models.ArticleDAO;
import article.models.ArticleDAOImpl;
import article.models.ArticleVO;

public class ArticleDeleteAction extends AbstractController {

	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
		long no = Long.parseLong(request.getParameter("no"));
		String pwd = request.getParameter("pwd");
		
		ArticleVO articleVO = new ArticleVO();
		articleVO.setNo(no);
		articleVO.setPwd(pwd);
		
		ArticleDAO articleDAO = ArticleDAOImpl.getInstance();
		ModelAndView mav = new ModelAndView("/WEB-INF/views/result.jsp");
        try {
            articleDAO.deleteArticle(articleVO);
            mav.addObject("msg","글삭제 완료");
            mav.addObject("url","list");
            
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("msg","비밀번호를 다시 확인 해주세요~");
            mav.addObject("url","javascript:history.back();" );
        }
					
		return mav;
	}

}
