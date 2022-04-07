package com.model2.mvc.service.product.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.*;


public class ProductDAO {
	
	public ProductDAO() {
	}

	public void insertProduct(Product product) throws Exception{
		
		System.out.println("insertproductDAO start");
		
		Connection con = DBUtil.getConnection();
		
		String sql = "INSERT INTO PRODUCT VALUES (seq_product_prod_no.nextval,?,?,?,?,?,sysdate)";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, product.getProdName());
		stmt.setString(2, product.getProdDetail());
		stmt.setString(3, product.getManufDay().replace("-", ""));
		stmt.setInt(4, product.getPrice());
		stmt.setString(5, product.getImgFile());
	
		stmt.executeUpdate();
		
		System.out.println(sql);
		
		con.close();
		
		System.out.println("insertproductDAO end");
	}
	
	public Product findProduct(int prodNo) throws Exception {
		
		System.out.println("findProduct start");
		
		Connection con = DBUtil.getConnection();

		String sql = "SELECT * FROM product WHERE prod_no=? ";
		System.out.println(sql);
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setInt(1, prodNo);
		
		System.out.println(prodNo);
		
		ResultSet rs = stmt.executeQuery();
		System.out.println(rs);

		Product product = null;
		while (rs.next()) {
			product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			product.setProdName(rs.getString("prod_name"));
			product.setProdDetail(rs.getString("prod_detail"));
			product.setManufDay(rs.getString("manufacture_day"));
			product.setPrice(rs.getInt("price"));
			product.setImgFile(rs.getString("image_file"));
			product.setRegDate(rs.getDate("reg_date"));
		}
		
		System.out.println(sql);
		rs.close();
		stmt.close();
		con.close();
		
		System.out.println("findProduct end");
		return product;
		
	}

	public Map<String, Object> getProductList(Search search) throws Exception {
		
		Map<String, Object> map = new HashMap<String, Object> ();

		System.out.println("getP.DAOMap start");
		
		Connection con = DBUtil.getConnection();
		System.out.println(search.getSearchCondition()+search.getSearchKeyword());

		String sql = "SELECT prod_no, prod_name, price, reg_date FROM product ";
		if (search.getSearchCondition() != null) {
			if (search.getSearchCondition().equals("0") && !search.getSearchKeyword().equals("")) {
				sql += " WHERE prod_no LIKE '%"  + search.getSearchKeyword() + "%'";
			} else if (search.getSearchCondition().equals("1") && !search.getSearchKeyword().equals("")) {
				sql += " WHERE prod_name LIKE '%" + search.getSearchKeyword() + "%'";
			} else if (search.getSearchCondition().equals("2") && !search.getSearchKeyword().equals("")) {
				sql += " WHERE price LIKE '%" + search.getSearchKeyword() + "%'";
			} else if (search.getSearchCondition().equals("3") && !search.getSearchKeyword().equals("")) {
				sql += " WHERE reg_date LIKE '%" + search.getSearchKeyword() + "%'";
			}
		}
		
		sql += " ORDER BY prod_no";
		
		System.out.println("ProductDAO :: Original SQL :: " + sql);
		
		int totalCount = this.getTotalCount(sql);
		System.out.println("ProductDAO :: totalCount :: "+ totalCount);
		
		sql = makeCurrentPageSql(sql, search);
		PreparedStatement stmt = con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		System.out.println(search);
		
		List<Product> list = new ArrayList<Product>();
		
		while(rs.next()){
			Product product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			product.setProdName(rs.getString("prod_name"));
			product.setPrice(rs.getInt("price"));
			System.out.println(rs.getInt("price"));
			product.setRegDate(rs.getDate("reg_date"));
			
			list.add(product);
		}
		
		//==> totalCount ���� ����
		map.put("totalCount", new Integer(totalCount));
		//==> currentPage �� �Խù� ���� ���� List ����
		map.put("list", list);
		
		rs.close();
		stmt.close();
		con.close();
	
		return map;

	}
	
	private int getTotalCount(String sql) throws Exception {
		// TODO Auto-generated method stub
		sql = "SELECT COUNT(*) "+
		          "FROM ( " +sql+ ") countTable";
		
		Connection con = DBUtil.getConnection();
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		int totalCount = 0;
		if( rs.next() ){
			totalCount = rs.getInt(1);
		}
		
		pStmt.close();
		con.close();
		rs.close();
		
		return totalCount;
	}
	
	private String makeCurrentPageSql(String sql, Search search) {
		// TODO Auto-generated method stub
		sql = 	"SELECT * "+ 
				"FROM (		SELECT inner_table. * ,  ROWNUM AS row_seq " +
								" 	FROM (	"+sql+" ) inner_table "+
								"	WHERE ROWNUM <="+search.getCurrentPage()*search.getPageSize()+" ) " +
				"WHERE row_seq BETWEEN "+((search.getCurrentPage()-1)*search.getPageSize()+1) +" AND "+search.getCurrentPage()*search.getPageSize();
	
		System.out.println("ProductDAO :: make SQL :: "+ sql);	
	
		return sql;
	}

	public void updateProduct(Product product) throws Exception {
		
		System.out.println("updateProduct start");
		Connection con = DBUtil.getConnection();

		String sql = "UPDATE product SET prod_name=?, prod_detail=?, manufacture_day=?, price=?, image_file=? WHERE prod_no=?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		
		stmt.setString(1, product.getProdName());
		stmt.setString(2, product.getProdDetail());
		stmt.setString(3, product.getManufDay());
		stmt.setInt(4, product.getPrice());
		stmt.setString(5, product.getImgFile());
		
		stmt.setInt(6, product.getProdNo());
		
		
		stmt.executeUpdate();
		
		System.out.println("updateProduct end");
		con.close();
	}
	
	
}
