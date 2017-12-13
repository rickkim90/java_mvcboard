package article.models;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ArticleDAOImpl implements ArticleDAO {
	private static ArticleDAOImpl articleDAO = null;
	
	private String driver = null;
	private String url = null;
	private String username = null;
	private String password = null;
	
	private ArticleDAOImpl() {
		Properties pr = new Properties();
		String props =
			this.getClass().getResource("").getPath() + "/database.properties";
		try {
			pr.load(new FileInputStream(props));
			
			driver = pr.getProperty("driver");
			url = pr.getProperty("url");
			username = pr.getProperty("username");
			password = pr.getProperty("password");
			
			Class.forName(driver);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
	
	public static ArticleDAOImpl getInstance() {
		if (articleDAO == null) {
			articleDAO = new ArticleDAOImpl();
		}
		return articleDAO;
	}

	private void dbClose(PreparedStatement ps, Connection cn) {
		if (ps != null) try{ps.close();}catch(Exception e){}
		if (cn != null) try{cn.close();}catch(Exception e){}
	}
	
	private void dbClose(ResultSet rs, PreparedStatement ps, Connection cn) {
		if (rs != null) try{rs.close();} catch(Exception e){}
		if (ps != null) try{ps.close();} catch(Exception e){}
		if (cn != null) try{cn.close();} catch(Exception e){}
	}
	@Override
	public void insertArticle(ArticleVO articleVO) throws Exception {
		Connection cn = null;
		PreparedStatement ps = null;

		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO tb_article(no,title,name,pwd,content)");
		sql.append("Values(seq_article.nextval, ?,?,?,?)");

		try {
			cn = getConnection();
			ps = cn.prepareStatement(sql.toString());
			ps.setString(1, articleVO.getTitle());
			ps.setString(2, articleVO.getName());
			ps.setString(3, articleVO.getPwd());
			ps.setString(4, articleVO.getContent());
			ps.executeUpdate();
		} finally {
			dbClose(ps, cn);
		}

	}
	@Override
	public List<ArticleVO> getArticleList() throws Exception {
		
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<ArticleVO> list = new ArrayList<ArticleVO>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT no, title, name, regdate, viewcount");
		sql.append(" FROM tb_article");
		sql.append(" ORDER BY no DESC");
		
		try {
			cn = getConnection();
			ps = cn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				ArticleVO articleVO = new ArticleVO();
				articleVO.setNo(rs.getLong("no"));
				articleVO.setTitle(rs.getString("title"));
				articleVO.setName(rs.getString("name"));
				articleVO.setRegdate(rs.getDate("regdate"));
				articleVO.setViewcount(rs.getInt("viewcount"));
				list.add(articleVO);
							}
			
		} finally {
			dbClose(rs,ps,cn);
		}
		return list;
		
	}
	@Override
	public List<ArticleVO> getArticleList(PageVO pageVO) throws Exception {
		
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<ArticleVO> list = new ArrayList<ArticleVO>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT B.*");
		sql.append(" FROM(SELECT rownum AS rnum, A.*");
		sql.append(" 	 FROM (SELECT no, title, name, regdate, viewcount");
		sql.append(" 		   FROM tb_article ORDER BY no DESC) A) B");
		sql.append(" WHERE rnum between ? and ?");
				
		try {
			cn = getConnection();
			ps = cn.prepareStatement(sql.toString());
			ps.setLong(1, pageVO.getStartnum());
			ps.setLong(2, pageVO.getEndnum());
			rs = ps.executeQuery();
			
			while(rs.next()){
				ArticleVO articleVO = new ArticleVO();
				articleVO.setNo(rs.getLong("no"));
				articleVO.setTitle(rs.getString("title"));
				articleVO.setName(rs.getString("name"));
				articleVO.setRegdate(rs.getDate("regdate"));
				articleVO.setViewcount(rs.getInt("viewcount"));
				list.add(articleVO);
							}			
		} finally {
			dbClose(rs,ps,cn);
		}
		return list;		
		}

	@Override
	public ArticleVO getDetail(long no) throws Exception {
		ArticleVO articleVO = null;
		
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT no, title, name, regdate, viewcount, content");
		sql.append(" FROM tb_article");
		sql.append(" WHERE no=?");
		
		try {
			cn = getConnection();
			ps = cn.prepareStatement(sql.toString());
			ps.setLong(1,  no);
			rs = ps.executeQuery();
			
			if(rs.next()){
				articleVO = new ArticleVO();
				articleVO.setNo(rs.getLong("no"));
				articleVO.setTitle(rs.getString("title"));
				articleVO.setName(rs.getString("name"));
				articleVO.setRegdate(rs.getDate("regdate"));
				articleVO.setViewcount(rs.getInt("viewcount"));
				articleVO.setContent(rs.getString("content"));
			}else {
				throw new RuntimeException("잘못된 접근 혹은 삭제된 글입니다.");
			}
		} finally {
			dbClose(rs,ps,cn);
		}
		return articleVO;
	}
	@Override
	public void updateViewcount(long no) throws Exception{
		Connection cn = null;
		PreparedStatement ps = null;
		
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE tb_article set");
		sql.append(" viewcount = viewcount+1");
		sql.append(" where no = ?");
		
		try {
			cn = getConnection();
			ps = cn.prepareStatement(sql.toString());
			ps.setLong(1, no);
			if(ps.executeUpdate()==0){
				throw new RuntimeException(no+"번 게시물이 존재하지 않습니다.");
			}
		} finally {
			dbClose(ps, cn);
		}
	}
		
		@Override
		public void updateArticle(ArticleVO articleVO) throws Exception {
			Connection cn = null;
			PreparedStatement ps = null;

			StringBuffer sql = new StringBuffer();
			sql.append(" UPDATE tb_article SET");
			sql.append(" name=?");
			sql.append(" ,title=?");
			sql.append(" ,content=? ");
			sql.append(" where no = ? AND pwd = ?");
			
			try {
				cn = getConnection();
				ps = cn.prepareStatement(sql.toString());
				
				ps.setString(1, articleVO.getName());
				ps.setString(2, articleVO.getTitle());
				ps.setString(3, articleVO.getContent());
				ps.setLong(4, articleVO.getNo());
				ps.setString(5, articleVO.getPwd());
				if(ps.executeUpdate()==0){
					throw new RuntimeException("게시물이 존재하지 않거나, 비밀번호가 잘못되었습니다.");
				}
			} finally {
				dbClose(ps, cn);
			}


	}
		@Override
	public void deleteArticle(ArticleVO articleVO) throws Exception {
			Connection cn = null;
			PreparedStatement ps = null;

			StringBuffer sql = new StringBuffer();
			sql.append(" DELETE FROM tb_article");
			sql.append(" WHERE no = ? AND pwd = ?");
			
			try {
				cn = getConnection();
				ps = cn.prepareStatement(sql.toString());
				
				ps.setLong(1, articleVO.getNo());
				ps.setString(2, articleVO.getPwd());
				if(ps.executeUpdate()==0){
					throw new RuntimeException("게시물이 존재하지 않거나, 비밀번호가 잘못되었습니다.");
				}
			} finally {
				dbClose(ps, cn);
			}
		
	}
	@Override
	public Long getTotalCount() throws Exception {
		
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT count(*) cnt");
		sql.append(" FROM tb_article");
		
		long cnt = 0;
		
		try {
			cn = getConnection();
			ps = cn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			
			if(rs.next()){
				cnt = rs.getLong("cnt");				
			}else {
				throw new RuntimeException("잘못된 접근 혹은 삭제된 글입니다.");
			}
		} finally {
			dbClose(rs,ps,cn);
		}
		return cnt;
		

	}
	
}
