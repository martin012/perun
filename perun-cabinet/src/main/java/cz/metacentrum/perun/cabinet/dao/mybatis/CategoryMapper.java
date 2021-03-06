package cz.metacentrum.perun.cabinet.dao.mybatis;

import cz.metacentrum.perun.cabinet.model.Category;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CategoryMapper {
	
	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	int countByExample(CategoryExample example);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	int deleteByExample(CategoryExample example);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	int insert(Category record);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	int insertSelective(Category record);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	List<Category> selectByExample(CategoryExample example);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	Category selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	int updateByExampleSelective(@Param("record") Category record, @Param("example") CategoryExample example);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	int updateByExample(@Param("record") Category record, @Param("example") CategoryExample example);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	int updateByPrimaryKeySelective(Category record);

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table CATEGORY
	 *
	 * @mbggenerated Wed Nov 02 13:52:56 CET 2011
	 */
	int updateByPrimaryKey(Category record);
	
}