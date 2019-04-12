package com.mahesh.Dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.mahesh.Model.BeanAdminHandle;
import com.mahesh.Model.BeanLibrarianHandle;
import com.mahesh.Model.BeanRegister;

@Repository
@Component
public class Dbconnect {

	@Autowired
	JdbcTemplate jdbcTemplate;
	private static String sql;

	public int insertUser(BeanRegister beanRegister) {
		sql = "INSERT INTO register(name,mobile,email,password) VALUES(?,?,?,?)";
		int value = jdbcTemplate.update(sql, new Object[] { beanRegister.getName(), beanRegister.getMobile(),
				beanRegister.getEmail(), beanRegister.getPassword() });
		return (value > 0) ? 1 : 0;
	}

	public boolean isValidUser(BeanRegister beanRegister) {
		boolean validUser = false;
		// isUserPresent() row count
		if (isUserPresent()) {
			sql = "SELECT * FROM register WHERE email=? and password=?";

			BeanRegister beanR = jdbcTemplate.queryForObject(sql,
					new Object[] { beanRegister.getEmail(), beanRegister.getPassword() }, new RegisterRowMapper());
			// user ne ghatalela data ani db madhala data same tar yes
			if (beanRegister.getEmail().equals(beanR.getEmail())
					&& (beanRegister.getPassword().equals(beanR.getPassword())))
				validUser = true;
			else
				validUser = false;
		} else {
			validUser = false;
		}
		return validUser;
	}

	private boolean isUserPresent() {
		sql = "SELECT COUNT(id) FROM register";
		int value = jdbcTemplate.queryForObject(sql, Integer.class);
		return (value > 0) ? true : false;
	}

	public boolean insertLibAction(BeanAdminHandle beanAdminHandle) {
		sql = "INSERT INTO addlib(name,email,password,mobile,aprv) VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id)";
		int insvalue = jdbcTemplate.update(sql, new Object[] { beanAdminHandle.getName(), beanAdminHandle.getEmail(),
				beanAdminHandle.getPassword(), beanAdminHandle.getMobile(), beanAdminHandle.getAprv() });
		return (insvalue > 0) ? true : false;
	}

	public List<BeanRegister> getListofLibrarian() {
		sql = "SELECT id,aprv,name,email,password,mobile FROM register";
		return jdbcTemplate.query(sql, new RegisterRowMapper());
	}

	public List<BeanLibrarianHandle> getListofBooks() {
		sql = "SELECT callNo,bookName,author,publisher,qty,issued,id FROM addbook";

		return jdbcTemplate.query(sql, new ListofBooksRowMapper());
	}

	public BeanRegister getEditlib(int id) {
		sql = "SELECT * FROM register WHERE id=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { id }, new RegisterRowMapper());
	}

	public int updateLib(BeanRegister beanRegister, int userId) {
		sql = "UPDATE register SET name=?,email=?,password=?,mobile=?,aprv=? WHERE id=?";
		System.out.println("name: " + beanRegister.getName() + " ema: " + beanRegister.getEmail() + "\n" + "pass: "
				+ beanRegister.getPassword() + " mob: " + beanRegister.getMobile() + " beanRegister.getAprv(): "
				+ beanRegister.getAprv() + " userId" + userId);
		return jdbcTemplate.update(sql, new Object[] { beanRegister.getName(), beanRegister.getEmail(),
				beanRegister.getPassword(), beanRegister.getMobile(), beanRegister.getAprv(), userId });
	}

	public int deleteLib(int userId) {
		sql = "DELETE FROM register WHERE id=?";
		int val = jdbcTemplate.update(sql, new Object[] { userId });
		return (val > 0) ? 1 : 0;
	}

	public int deletebook(int userId) {
		sql = "DELETE FROM addbook WHERE id=?";
		int val = jdbcTemplate.update(sql, new Object[] { userId });
		return (val > 0) ? 1 : 0;
	}

	public int insertbookAction(BeanLibrarianHandle librarianHandle) {
		sql = "INSERT INTO addbook(callNo,bookName,author,publisher,qty) VALUES(?,?,?,?,?)";
		int value = jdbcTemplate.update(sql, new Object[] { librarianHandle.getCallNo(), librarianHandle.getBookName(),
				librarianHandle.getAuthor(), librarianHandle.getPublisher(), librarianHandle.getQty() });
		return (value > 0) ? 1 : 0;
	}

	private static class AdminHandleRowMapper implements RowMapper<BeanAdminHandle> {
		public BeanAdminHandle mapRow(ResultSet res, int row) throws SQLException {

			return new BeanAdminHandle(res.getInt("id"), res.getBoolean("aprv"), res.getString("mobile"),
					res.getString("email"), res.getString("name"), res.getString("password"));
		}
	}

	public int issuebookAction(BeanLibrarianHandle librarianHandle) {
		sql = "INSERT INTO issueBook(callNo,studid,studname,studmobile,qty) VALUES(?,?,?,?,?)";
		int value = jdbcTemplate.update(sql, new Object[] { librarianHandle.getCallNo(), librarianHandle.getStudid(),
				librarianHandle.getStudname(), librarianHandle.getStudmobile(), librarianHandle.getQty() });
		return (value > 0) ? 1 : 0;
	}

	public List<BeanLibrarianHandle> getListofIssuedLibrarian(BeanLibrarianHandle beanLibrarian) {
//		sql = "SELECT i.callNo,i.studid,i.studname,i.studmobile,ab.issued FROM addbook as ab left join issuebook as i on i.callNo=ab.callNo and ab.issued=1 where i.callNo is not null ";
		sql = "SELECT i.callNo,i.studid,i.studname,i.studmobile,ab.issued FROM addbook as ab left join issuebook as i on i.callNo=ab.callNo and ab.issued=1 "
				+ " where i.callNo is not null ";
		System.out.println("=dddd> " + sql);
		return jdbcTemplate.query(sql, new LibrarianIssuedRowMapper());
	}

	private static class RegisterRowMapper implements RowMapper<BeanRegister> {
		public BeanRegister mapRow(ResultSet res, int row) throws SQLException {
			// int id, boolean aprv, String mobile, String email, String name, String
			// password
			return new BeanRegister(res.getInt("id"), res.getBoolean("aprv"), res.getString("mobile"),
					res.getString("email"), res.getString("name"), res.getString("password"));
		}
	}

	public int issuereturnbookAction(BeanLibrarianHandle librarianHandle) {
		sql = "INSERT INTO bookreturn(callNo,studid) VALUES(?,?)";
		int value = jdbcTemplate.update(sql, new Object[] { librarianHandle.getCallNo(), librarianHandle.getStudid() });
		return (value > 0) ? 1 : 0;
	}

	private static class LibrarianIssuedRowMapper implements RowMapper<BeanLibrarianHandle> {
		public BeanLibrarianHandle mapRow(ResultSet res, int row) throws SQLException {
			BeanLibrarianHandle librarianHandle = new BeanLibrarianHandle();
			librarianHandle.setCallNo(res.getString("callNo"));
			librarianHandle.setStudid(res.getInt("studid"));
			librarianHandle.setStudname(res.getString("studname"));
			librarianHandle.setStudmobile(res.getString("studmobile"));
			librarianHandle.setStatus(res.getInt("issued"));
			System.out.println("147: issued ==> " + res.getInt("issued"));
			return librarianHandle;

		}
	}

	private static final class ListofBooksRowMapper implements RowMapper<BeanLibrarianHandle> {

		public BeanLibrarianHandle mapRow(ResultSet res, int row) throws SQLException {
			BeanLibrarianHandle handle = new BeanLibrarianHandle();

			handle.setCallNo(res.getString("callNo"));
			handle.setBookName(res.getString("bookName"));
			handle.setAuthor(res.getString("author"));
			handle.setPublisher(res.getString("publisher"));
			handle.setQty(res.getInt("qty"));
			handle.setIssued(res.getInt("issued"));
			handle.setId(res.getInt("id"));

			return handle;
		}

	}

	public boolean updateviewBook(BeanLibrarianHandle librarianHandle) {
		sql = "UPDATE addbook SET issued=1,qty=qty-? WHERE callNo=?";
		int value = jdbcTemplate.update(sql, new Object[] { librarianHandle.getQty(), librarianHandle.getCallNo() });
		return (value > 0) ? true : false;
	}

	public boolean updateRetBooks(BeanLibrarianHandle librarianHandle) {
		sql = "UPDATE addbook SET issued=0 WHERE callNo=?";
		int value = jdbcTemplate.update(sql, new Object[] { librarianHandle.getCallNo() });
		return (value > 0) ? true : false;
	}

	public String isaprove(BeanRegister beanRegister) {
		sql = "SELECT aprv FROM register WHERE email=? and password=?";
		int value = jdbcTemplate.queryForObject(sql,
				new Object[] { beanRegister.getEmail(), beanRegister.getPassword() }, Integer.class);
		if (value > 0)
			return (value == 2) ? "admin" : "lib";
		else
			return "notaprv";
	}

}
